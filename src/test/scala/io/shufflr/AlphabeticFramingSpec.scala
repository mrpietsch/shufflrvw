/*
 * Copyright 2016 codecentric AG
 */

package io.shufflr

import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import akka.stream.testkit.scaladsl.TestSink
import org.scalatest.{ Matchers, WordSpec }

final class AlphabeticFramingSpec extends WordSpec with Matchers with AkkaSpec {
  import AlphabeticFraming._

  private implicit val mat = ActorMaterializer()

  "buildFrames" should {
    "correctly build frames" in {
      buildFrames("", None) shouldBe 'empty
      buildFrames("", Some(Frame("z", isAlphabetic = true))) shouldBe Vector(
        Frame("z", isAlphabetic = true)
      )

      buildFrames("abc", None) shouldBe Vector(Frame("abc", isAlphabetic = true))
      buildFrames("abc", Some(Frame("z", isAlphabetic = true))) shouldBe Vector(
        Frame("zabc", isAlphabetic = true)
      )
      buildFrames(" ", Some(Frame("z", isAlphabetic = true))) shouldBe Vector(
        Frame("z", isAlphabetic = true),
        Frame(" ", isAlphabetic = false)
      )

      buildFrames(" a b", Some(Frame("z", isAlphabetic = true))) shouldBe Vector(
        Frame("z", isAlphabetic = true),
        Frame(" ", isAlphabetic = false),
        Frame("a", isAlphabetic = true),
        Frame(" ", isAlphabetic = false),
        Frame("b", isAlphabetic = true)
      )

      buildFrames("a b ", Some(Frame("z", isAlphabetic = true))) shouldBe Vector(
        Frame("za", isAlphabetic = true),
        Frame(" ", isAlphabetic = false),
        Frame("b", isAlphabetic = true),
        Frame(" ", isAlphabetic = false)
      )
    }
  }

  "AlphabeticFraming" should {
    "emit frames either fully alphabetic or fully non-alphabetic" in {
      Source
        .single("This is  a test \nfor\nalphabetic-framing.")
        .via(AlphabeticFraming())
        .runWith(TestSink.probe)
        .request(99)
        .expectNext(Frame("This", isAlphabetic = true))
        .expectNext(Frame(" ", isAlphabetic = false))
        .expectNext(Frame("is", isAlphabetic = true))
        .expectNext(Frame("  ", isAlphabetic = false))
        .expectNext(Frame("a", isAlphabetic = true))
        .expectNext(Frame(" ", isAlphabetic = false))
        .expectNext(Frame("test", isAlphabetic = true))
        .expectNext(Frame(" \n", isAlphabetic = false))
        .expectNext(Frame("for", isAlphabetic = true))
        .expectNext(Frame("\n", isAlphabetic = false))
        .expectNext(Frame("alphabetic", isAlphabetic = true))
        .expectNext(Frame("-", isAlphabetic = false))
        .expectNext(Frame("framing", isAlphabetic = true))
        .expectNext(Frame(".", isAlphabetic = false))
        .expectComplete()
    }
  }
}
