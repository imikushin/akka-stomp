package com.heromq.akka

import akka.actor.Actor
import grizzled.slf4j.Logging

trait ContainerActor extends Actor with Logging {

  protected def receive = {
    case m => unhandled(m)
  }

}
