/*
 * Copyright 2016 codecentric AG
 */

package io.shufflr

import akka.http.scaladsl.model.StatusCodes.{ Conflict, Created, NoContent, NotFound, OK }
import akka.http.scaladsl.model.headers.{ BasicHttpCredentials, HttpChallenges }
import akka.http.scaladsl.server.AuthenticationFailedRejection
import akka.http.scaladsl.server.AuthenticationFailedRejection.CredentialsMissing
import akka.http.scaladsl.testkit.ScalatestRouteTest
import de.heikoseeberger.akkahttpcirce.CirceSupport
import org.scalatest.{ Matchers, WordSpec }
import scala.collection.breakOut

final class ApiSpec extends WordSpec with Matchers with ScalatestRouteTest {
  import CirceSupport._
  import UserRepository._
  import io.circe.generic.auto._

  private val user        = User("user", "user@shufflr.io")
  private val credentials = BasicHttpCredentials(user.username, user.username)
  private val authRejection =
    AuthenticationFailedRejection(CredentialsMissing, HttpChallenges.basic("shufflr"))

  "Api's route" should {
    "reject an unauthenticated GET /users with an AuthenticationFailedRejection" in {
      Get("/users") ~> Api.route(userRepository(user)) ~> check {
        rejections should contain(authRejection)
      }
    }

    "respond to an authenticated GET /users with an OK and a correct Set[User]" in {
      val request = Get("/users") ~> addCredentials(credentials)
      request ~> Api.route(userRepository(user)) ~> check {
        status shouldBe OK
        responseAs[Set[User]] shouldBe Set(user)
      }
    }

    "respond to a POST /users with a taken username with a Conflict" in {
      Post("/users", User(user.username, "some@email")) ~> Api.route(userRepository(user)) ~> check {
        status shouldBe Conflict
        responseAs[String] shouldBe s"Username ${user.username} taken!"
      }
    }

    "respond to a POST /users with an available username with a Created a correct User" in {
      Post("/users", User(user.username, user.email)) ~> Api.route(userRepository()) ~> check {
        status shouldBe Created
        responseAs[User] shouldBe user
      }
    }

    "reject an unauthenticated GET /users/<username> with an AuthenticationFailedRejection" in {
      Get(s"/users/${user.username}") ~> Api.route(userRepository(user)) ~> check {
        rejections should contain(authRejection)
      }
    }

    "respond to an authenticated GET /users/<username> with an unknown username with a NotFound" in {
      val request = Get("/users/unknown") ~> addCredentials(credentials)
      request ~> Api.route(userRepository(user)) ~> check {
        status shouldBe NotFound
      }
    }

    "respond to an authenticated GET /users/<username> with a known username with an OK and a correct User" in {
      val request = Get(s"/users/${user.username}") ~> addCredentials(credentials)
      request ~> Api.route(userRepository(user)) ~> check {
        status shouldBe OK
        responseAs[User] shouldBe user
      }
    }

    "reject an unauthenticated DELETE /users/<username> with an AuthenticationFailedRejection" in {
      Delete(s"/users/${user.username}") ~> Api.route(userRepository(user)) ~> check {
        rejections should contain(authRejection)
      }
    }

    "respond to an authenticated DELETE /users/<username> with an unknown username with a NotFound" in {
      val request = Delete("/users/unknown") ~> addCredentials(credentials)
      request ~> Api.route(userRepository(user)) ~> check {
        status shouldBe NotFound
        responseAs[String] shouldBe s"Username unknown unknown!"
      }
    }

    "respond to an authenticated DELETE /users/<username> with a known username with a NoContent" in {
      val request = Delete(s"/users/${user.username}") ~> addCredentials(credentials)
      request ~> Api.route(userRepository(user)) ~> check {
        status shouldBe NoContent
      }
    }
  }

  private def userRepository(users: User*) =
    new UserRepository(users.map(user => user.username -> user)(breakOut))
}
