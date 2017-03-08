/*
 * Copyright 2016 codecentric AG
 */

package io.shufflr

import akka.actor.ActorSystem
import org.scalatest.{ BeforeAndAfterAll, Suite }
import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

trait AkkaSpec extends BeforeAndAfterAll { this: Suite =>

  protected implicit val system = ActorSystem()

  override protected def afterAll() = {
    Await.ready(system.terminate(), 42.seconds)
    super.afterAll()
  }
}
