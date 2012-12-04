package com.heromq.akka

import akka.actor.{FSM, ActorRef}
import akka.util.duration._
import grizzled.slf4j.Logging
import javax.jms._

object ConsumerActor {

  sealed trait State
  case object Initial extends State
  case object Sending extends State

  sealed trait Data
  case object Empty extends Data
  case class Target(ref: ActorRef) extends Data

  case class SendTo(ref: ActorRef)

}

import ConsumerActor._

trait ConsumerActor extends MqActor with MessagingStyle with FSM[State, Data] with Logging {

  val messageConsumer = session.createConsumer(destination)

  startWith(Initial, Empty)

  when(Initial) {
    case Event(SendTo(ref), _) => consumeAndMaybeSend(ref)
  }

  when(Sending, 1 second) {
    case Event(StateTimeout, Target(ref)) => consumeAndMaybeSend(ref)
    case Event(SendTo(ref), _) => consumeAndMaybeSend(ref)
  }

  whenUnhandled {
    case Event(ufo, data) => {
      warn("Unhandled: state = " + stateName + ", msg = " + ufo)
      stay()
    }
  }

  def consume(): Option[Any] = Option(messageConsumer.receiveNoWait) match {
    case Some(message) => {
      message.acknowledge()
      info("Consumed: MessageID == " + message.getJMSMessageID)
      message match {
        case message: TextMessage => Some(message.getText)
        case message: ObjectMessage => Some(message.getObject)
        case _ => None
      }
    }
    case _ => None
  }

  def consumeAndMaybeSend(ref: ActorRef) = consume() match {
    case Some(msg) => {
      ref ! msg
      goto(Initial) using Empty
    }
    case None => goto(Sending) using Target(ref)
  }

  onTransition {
    case _ -> Sending => {
      trace("trying to consume message...")
    }
  }

  initialize

}
