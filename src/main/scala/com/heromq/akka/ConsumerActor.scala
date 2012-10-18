package com.heromq.akka

import akka.actor.{Actor, Props, ActorRef}
import akka.util.duration._
import grizzled.slf4j.Logging
import javax.jms._
import scala.collection.mutable

object ConsumerActor {

  case object Begin

}

trait ConsumerActor extends MqActor with Logging {thisEnv: MessagingStyle =>

  import ConsumerActor._

  val limit = 10
  protected val consumedMap = mutable.Map[Long, Message]()
  protected val processedMap = mutable.Map[Long, Message]()
  var count: Long = 0

  def worker: ActorRef

  val messageConsumer = session.createConsumer(destination)

  override def preStart() {
    connection.start()
    self ! Begin
    info("started receiving")
  }

  protected def receive = (fetchContent andThen processAndAcknowledge) orElse receivePlain

  protected def fetchContent: PartialFunction[Any, Any] = {
    case message: TextMessage => message -> message.getText
    case message: ObjectMessage => message -> message.getObject
  }

  protected def processAndAcknowledge: Receive = {
    case (message: Message, content: Any) => {
      count += 1
      val msgIndex = count
      debug("consumed: msgIndex == " + msgIndex + ", MessageID == " + message.getJMSMessageID)
      consumedMap += (msgIndex -> message)
      info("Processing: " + message.getJMSMessageID + ": " + content)
      context.actorOf(Props(new Actor {
        worker ! content
        protected def receive = {
          case s => {
            info("Processing: " + message.getJMSMessageID + ": " + content + "  ...  Done.")
            context.parent ! msgIndex
            context.stop(self)
          }
        }
      }))
      if (consumedMap.size + processedMap.size < limit) {
        self ! Begin
      }
    }
  }

  def receivePlain: Receive = {
    case Begin => {
      trace("trying to consume message...")
      Option(messageConsumer.receiveNoWait) map {self ! _} getOrElse {
        context.system.scheduler.scheduleOnce(1 second) {
          self ! Begin
        }
      }
    }
    case msgIndex: Long => {
      consumedMap.remove(msgIndex) map {message =>
        debug("processed msgIndex == " + msgIndex + ", MessageID == " + message.getJMSMessageID)
        processedMap += (msgIndex -> message)
      }
      debug("consumedMap.keySet.min == " + (consumedMap.keySet reduceOption {_ min _}))
      if (consumedMap.isEmpty || consumedMap.keySet.min > msgIndex) {
        val sortedKeys = processedMap.keySet.toSeq.sorted
        val diff = sortedKeys.head
        val prefix = sortedKeys.zipWithIndex takeWhile {case (e, i) => e - i == diff} map {_._1}
        val ackMsgIndex = prefix.last
        val message = processedMap(ackMsgIndex)
        prefix foreach {processedMap.remove(_)}
        message.acknowledge()
        info("Sent ACK for msgIndex == " + ackMsgIndex + ", MessageID == " + message.getJMSMessageID)
      }
      if (consumedMap.size + processedMap.size < limit) {
        self ! Begin
      }
    }
  }

}
