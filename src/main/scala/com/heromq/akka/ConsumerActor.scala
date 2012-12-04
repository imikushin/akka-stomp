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

  case object Tick
  case object Finished

}

import ConsumerActor._

trait ConsumerActor extends MqActor with MessagingStyle with FSM[State, Data] with Logging {

  val messageConsumer = session.createConsumer(destination)

  startWith(Initial, Empty)

  when(Initial) {
    case Event(SendTo(ref), _) => goto(Sending) using Target(ref)
  }

  when(Sending) {
    case Event(SendTo(ref), _) => goto(Sending) using Target(ref)
    case Event(Tick, Target(ref)) => goto(Sending) using Target(ref)
    case Event(Finished, _) => goto(Initial) using Empty
  }

  whenUnhandled {
    case Event(ufo, data) => stay() using {
      warn("Unhandled: state = " + this.stateName + ", msg = " + ufo)
      data
    }
  }

  onTransition {
    case _ -> Sending => {
      trace("trying to consume message...")
      nextStateData match {
        case Target(ref) => {
          Option(messageConsumer.receiveNoWait) match {
            case Some(message) => {
              val content = message match {
                case message: TextMessage => message.getText
                case message: ObjectMessage => message.getObject
              }
              ref ! content
              message.acknowledge()
              info("Sent ACK for message == '" + content + "', MessageID == " + message.getJMSMessageID)
              self ! Finished
            }
            case None => {
              setTimer(self.toString(), Tick, 1 second, repeat = false)
            }
          }
        }
        case ufo => {
          warn("OMG! UFO: " + ufo)
        }
      }
    }
  }

  initialize

}
