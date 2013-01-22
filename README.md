# akka-stomp
A simple Akka based framework enabling actors to communicate with STOMP message brokers.

## Framework Traits
**com.heromq.akka.ConsumerActor** - consumes messages from a STOMP broker. Send it a SendTo(actorRef):
ConsumerActor will consume a message from the STOMP broker and send its payload to the passed actorRef.

**com.heromq.akka.ProducerActor** - produces messages to a STOMP broker.

**com.heromq.akka.MQ** - messaging style mixin enabling consumers and producers to work with message queues.

**com.heromq.akka.PubSub** - messaging style mixin enabling consumers and producers to work with pubsub topics.

## Utility Traits
**com.heromq.akka.ContainerActor** - simple actor with empty `receive`. Used for, well, containing other actors.
**com.heromq.akka.RecoverableActor** - hands over the current message to the next incarnation of the current actor while restarting.
Used to prevent message loss on actor restart.

## Example
```scala
package com.example

import akka.actor.{Actor, Props}
import com.heromq.akka.ConsumerActor.SendTo
import com.heromq.akka.ContainerActor
import com.heromq.client.{MQConsumerActor, MQProducerActor}

class Repeater(broker: String, qFrom: String, qTo: String) extends ContainerActor {

  val consumer = context.actorOf(Props(new MQConsumerActor(broker, qFrom)))
  val producer = context.actorOf(Props(new MQProducerActor(broker, qTo)))

  val worker = context.actorOf(Props(newWorker))

  protected def newWorker = new Actor {

    consumer ! SendTo(self)

    protected def receive = {
      case msg => {
        try {
          producer ! msg
        } finally {
          consumer ! SendTo(self)
        }
      }
    }

  }

}
```
