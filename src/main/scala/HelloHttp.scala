/*
 * Copyright 2016 codecentric AG
 */

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.server.{ Directives, Route }
import akka.stream.ActorMaterializer
import de.heikoseeberger.akkahttpcirce.CirceSupport
import scala.util.{ Failure, Success }

object HelloHttp {

  final case class Hello(message: String)

  def route: Route = {
    import CirceSupport._
    import Directives._
    import io.circe.generic.auto._

    pathSingleSlash {
      get {
        complete(Hello("Hello, World!"))
      }
    }
  }
}

object HelloHttpMain {

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem()
    implicit val mat    = ActorMaterializer()
    import system.dispatcher

    val address = "0.0.0.0"
    val port    = 8000

    Http()
      .bindAndHandle(HelloHttp.route, address, port)
      .onComplete {
        case Success(ServerBinding(address)) =>
          println(s"Listening on $address")
        case Failure(cause) =>
          println(s"Can't bind to $address:$port because of ${cause.getMessage}")
          system.terminate()
      }
  }
}
