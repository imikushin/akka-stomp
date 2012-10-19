# akka-stomp
A simple Akka based framework enabling actors to communicate with STOMP message brokers.

**ConsumerActor** - consumes messages from a broker.  
**ProducerActor** - produces messages to a broker.  
**MQ** - messaging style mixin enabling consumers and producers to work with message queues.  
**PubSub** - messaging style mixin enabling consumers and producers to work with pubsub topics. 

## Utility Actors
**ContainerActor** - simple actor with empty `receive`. Used for, well, containing other actors.  
**RecoverableActor** - hands over the current message to the next incarnation of the current actor while restarting. Used to prevent message loss on actor restart. 

Example:
```scala
class Resender(broker: String, qFrom: String, qTo: String) extends ContainerActor {

  val producer = context.actorOf(Props(newProducer))
  val worker = context.actorOf(Props(newWorker(producer)))
  val consumer = context.actorOf(Props(newConsumer(worker)))

  def newConsumer(next: ActorRef) = new ConsumerActor with MQ {
    def brokerUri: String = broker
    def worker: ActorRef = next
    def destinationName: String = qFrom
  }

  def newWorker(producer: ActorRef) = new Actor {
    protected def receive = {
      case msg: AnyRef => {
        producer ! msg
        sender ! Status.Success(msg)
      }
    }
  }

  def newProducer = new ProducerActor with MQ {
    def brokerUri: String = broker
    def destinationName: String = qTo
  }

}
```
