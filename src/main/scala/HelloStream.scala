/*
 * Copyright 2016 codecentric AG
 */

import akka.Done
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{ Flow, Keep, RunnableGraph, Sink, Source }
import scala.concurrent.Future

object HelloStream {

  def apply(): RunnableGraph[Future[Done]] = {
    val helloSource = Source.single("Hello, World!")
    val capitalize  = Flow[String].map(_.toUpperCase)
    val printlnSink = Sink.foreach(println)
    helloSource.via(capitalize).toMat(printlnSink)(Keep.right)
  }
}

object HelloStreamMain {

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem()
    implicit val mat    = ActorMaterializer()
    import system.dispatcher

    HelloStream().run().onComplete(_ => system.terminate())
  }
}
