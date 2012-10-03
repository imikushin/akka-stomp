package com.heromq.akka

import akka.actor.Actor
import grizzled.slf4j.Logging

trait RecoverableActor extends Actor with Logging {

  override def preRestart(reason: Throwable, message: Option[Any]) {
    debug("Actor restarting, msg = " + message, reason)
    super.preRestart(reason, message)
    message map {self forward _}
  }

}
