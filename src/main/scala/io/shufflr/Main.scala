/*
 * Copyright 2016 codecentric AG
 */

package io.shufflr

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.stream.ActorMaterializer
import scala.util.{ Failure, Success }

object Main {

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem()
    implicit val mat    = ActorMaterializer()
    import system.dispatcher

    val config         = system.settings.config
    val address        = config.getString("shufflr.api.address")
    val port           = config.getInt("shufflr.api.port")
    val userRepository = new UserRepository

    Http()
      .bindAndHandle(Api.route(userRepository), address, port)
      .onComplete {
        case Success(ServerBinding(address)) =>
          println(s"Listening on $address")
        case Failure(cause) =>
          println(s"Can't bind to $address:$port because of ${cause.getMessage}")
          system.terminate()
      }
  }
}
