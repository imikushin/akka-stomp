package com.heromq.akka

import grizzled.slf4j.Logging
import javax.jms.Message

trait ProducerActor extends MqActor with Logging {thisEnv: MessagingStyle =>

  lazy val messageProducer = session.createProducer(destination)

  protected def createMessage: PartialFunction[Any, Any] = {
    case content: String => session.createTextMessage(content) -> content
    case content: Serializable => session.createObjectMessage(content) -> content
  }

  protected def sendMessage: Receive = {
    case (message: Message, content: Any) => {
      messageProducer.send(message)
      debug("sent to broker: " + message.getJMSMessageID + ": " + content)
    }
  }

  protected def receive = createMessage andThen sendMessage

}
