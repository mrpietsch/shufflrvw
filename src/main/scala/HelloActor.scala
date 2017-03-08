/*
 * Copyright 2016 codecentric AG
 */

import akka.actor.{ Actor, ActorLogging, ActorSystem, Props }

object HelloActor {

  final val Name = "hello-actor"

  def props: Props =
    Props(new HelloActor)
}

final class HelloActor extends Actor with ActorLogging {

  log.info("Hello, World!")

  override def receive = Actor.emptyBehavior
}

object HelloActorMain {

  def main(args: Array[String]): Unit = {
    val system = ActorSystem()
    system.actorOf(HelloActor.props, HelloActor.Name)
  }
}
