/*
 * Copyright 2016 codecentric AG
 */

package io.shufflr

import akka.http.scaladsl.model.ContentTypes.`application/octet-stream`
import akka.http.scaladsl.model.HttpEntity
import akka.http.scaladsl.model.StatusCodes.{ Conflict, Created, NoContent, NotFound }
import akka.http.scaladsl.model.headers.Location
import akka.http.scaladsl.server.directives.Credentials
import akka.http.scaladsl.server.directives.Credentials.Provided
import akka.http.scaladsl.server.{ Directives, Route }
import de.heikoseeberger.akkahttpcirce.CirceSupport

object Api {

  final case class AddUser(username: String, email: String)

  def route(userRepository: UserRepository): Route = {
    import CirceSupport._
    import Directives._
    import UserRepository._
    import io.circe.generic.auto._

    def authenticate(credentials: Credentials) =
      credentials match {
        case c @ Provided(username) if c.verify(username) => userRepository.getUser(username)
        case _                                            => None
      }

    def usersWithoutAuth =
      pathPrefix("users") {
        post {
          entity(as[AddUser]) { addUser =>
            userRepository.addUser(addUser.username, addUser.email) match {
              case Right(user @ User(username, _)) =>
                extractUri { uri =>
                  complete(Created, Vector(Location(uri.withPath(uri.path / username))), user)
                }
              case Left(UsernameTaken(username)) =>
                complete(Conflict, s"Username $username taken!")
            }
          }
        }
      }

    def usersWithAuth =
      authenticateBasic("shufflr", authenticate) { _ =>
        pathPrefix("users") {
          pathEnd {
            get {
              complete(userRepository.getUsers)
            }
          } ~
          path(Segment) { username =>
            get {
              userRepository.getUser(username) match {
                case Some(user) => complete(user)
                case None       => complete(NotFound, s"Username $username unknown!")
              }
            } ~
            delete {
              userRepository.removeUser(username) match {
                case Right(_) => complete(NoContent)
                case Left(_)  => complete(NotFound, s"Username $username unknown!")
              }
            }
          }
        }
      }

    def shuffle =
      authenticateBasic("shufflr", authenticate) { _ =>
        path("shuffle") {
          post {
            extractRequest { request =>
              val data = request.entity.dataBytes.via(ShuffleWords())
              complete(HttpEntity(`application/octet-stream`, data))
            }
          }
        }
      }

    usersWithoutAuth ~ usersWithAuth ~ shuffle
  }
}
