package com.example

import akka.actor.{Actor, Props}
import com.heromq.akka.ConsumerActor.SendTo
import com.heromq.client.{MQConsumerActor, MQProducerActor}

class Repeater(broker: String, qFrom: String, qTo: String) extends Actor {

  val consumer = context.actorOf(Props(new MQConsumerActor(broker, qFrom)))
  val producer = context.actorOf(Props(new MQProducerActor(broker, qTo)))

  consumer ! SendTo(self)

  def receive = {
    case msg => try {producer ! msg} finally {consumer ! SendTo(self)}
  }

}
