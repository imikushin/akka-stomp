package com.heromq.client

import com.heromq.akka.{ProducerActor, PubSub, ConsumerActor, MQ}

class MQConsumerActor(val brokerUri: String, val destinationName: String) extends ConsumerActor with MQ
class PubSubConsumerActor(val brokerUri: String, val destinationName: String) extends ConsumerActor with PubSub
class MQProducerActor(val brokerUri: String, val destinationName: String) extends ProducerActor with MQ
class PubSubProducerActor(val brokerUri: String, val destinationName: String) extends ProducerActor with PubSub
