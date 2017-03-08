/*
 * Copyright 2016 codecentric AG
 */

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.FileIO
import io.shufflr.ShuffleWords
import java.nio.file.Paths

object ShuffleWordsMain {

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem()
    implicit val mat    = ActorMaterializer()
    import system.dispatcher

    val in  = FileIO.fromPath(Paths.get("episode7.in"))
    val out = FileIO.toPath(Paths.get("episode7.out"))

    in.via(ShuffleWords()).runWith(out).onComplete(_ => system.terminate())
  }
}
