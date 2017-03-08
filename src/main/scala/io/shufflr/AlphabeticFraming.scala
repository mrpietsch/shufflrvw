/*
 * Copyright 2016 codecentric AG
 */

package io.shufflr

import akka.stream.stage.{ GraphStage, GraphStageLogic, InHandler, OutHandler }
import akka.stream.{ Attributes, FlowShape, Inlet, Outlet }

object AlphabeticFraming {

  final case class Frame(value: String, isAlphabetic: Boolean)

  private final val alphabet = 'A'.to('Z').to[Set]

  def apply(): AlphabeticFraming =
    new AlphabeticFraming

  def buildFrames(s: String, previous: Option[Frame]): Vector[Frame] = {
    def isAlphabetic(c: Char) = alphabet.contains(c.toUpper)
    s.foldLeft(previous.toVector) {
      case (frames @ _ :+ last, c) if last.isAlphabetic ^ isAlphabetic(c) =>
        frames :+ Frame(c.toString, isAlphabetic(c))
      case (init :+ last, c) =>
        init :+ last.copy(last.value + c)
      case (_, c) =>
        Vector(Frame(c.toString, isAlphabetic(c)))
    }
  }
}

final class AlphabeticFraming private
    extends GraphStage[FlowShape[String, AlphabeticFraming.Frame]] {
  import AlphabeticFraming._

  override val shape =
    FlowShape(Inlet[String]("alphabeticFraming.in"), Outlet[Frame]("alphabeticFraming.out"))

  override def createLogic(attributes: Attributes) =
    new GraphStageLogic(shape) {
      import shape._

      private var lastFrame = Option.empty[Frame]

      setHandler(
        in,
        new InHandler {
          override def onPush() = {
            val frames = buildFrames(grab(in), lastFrame)
            if (frames.nonEmpty) {
              emitMultiple(out, frames.init)
              lastFrame = Some(frames.last)
            }
          }

          override def onUpstreamFinish() = {
            emitMultiple(out, lastFrame.iterator)
            super.onUpstreamFinish()
          }
        }
      )

      setHandler(out, new OutHandler {
        override def onPull() = pull(in)
      })
    }
}
