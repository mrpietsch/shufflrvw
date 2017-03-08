/*
 * Copyright 2016 codecentric AG
 */

package io.shufflr

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{ FileIO, Flow }
import akka.util.ByteString
import io.shufflr.AlphabeticFraming.Frame
import java.nio.file.Paths
import scala.util.Random

object ShuffleWords {

  def apply(): Flow[ByteString, ByteString, NotUsed] =
    Flow[ByteString]
      .map(_.utf8String)
      .via(AlphabeticFraming())
      .map { case Frame(s, a) => ByteString(if (a) shuffleWord(s) else s) }

  def shuffleWord(word: String): String =
    if (word.length < 4)
      word
    else
      word.head +
      Random.shuffle(word.substring(1, word.length - 1): Seq[Char]).mkString +
      word.last
}
