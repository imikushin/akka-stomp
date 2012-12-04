package com.heromq.client

import com.heromq.akka.{ConsumerActor, MQ}

class MQConsumerActor(val brokerUri: String, val destinationName: String) extends ConsumerActor with MQ
