/*
 * Copyright 2016 codecentric AG
 */

package io.shufflr

import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import akka.util.ByteString
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{ AsyncWordSpec, Matchers }

final class ShuffleWordsSpec
    extends AsyncWordSpec
    with Matchers
    with GeneratorDrivenPropertyChecks
    with AkkaSpec {
  import ShuffleWords._

  private implicit val mat = ActorMaterializer()

  "shuffleWord" should {
    "keep the first and last character and somehow shuffle the others" in {
      forAll { (word: String) =>
        if (word.length < 4)
          shuffleWord(word) shouldBe word
        else {
          val shuffledWord = shuffleWord(word)
          shuffledWord.head shouldBe word.head
          shuffledWord.last shouldBe word.last
          shuffledWord.init.tail.toList.sorted shouldBe word.init.tail.toList.sorted
        }
      }
    }
  }

  "ShuffleWords" should {
    "correctly shuffle words" in {
      Source
        .single(ByteString("a bc def \nword"))
        .via(ShuffleWords())
        .runFold("")(_ + _.utf8String)
        .map(_ should (be("a bc def \nword") or be("a bc def \nwrod")))
    }
  }
}
