package com.heromq.akka

import akka.actor.Actor
import grizzled.slf4j.Logging

trait ContainerActor extends Actor with Logging {

  def receive = {
    case m => unhandled(m)
  }

}
