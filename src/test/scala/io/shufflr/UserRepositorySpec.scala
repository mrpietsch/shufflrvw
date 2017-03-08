/*
 * Copyright 2016 codecentric AG
 */

package io.shufflr

import org.scalatest.{ Matchers, WordSpec }

final class UserRepositorySpec extends WordSpec with Matchers {
  import UserRepository._

  private val user = User("user", "user@shufflr.io")

  "UserRepository" should {
    "correctly handle adding and removing users" in {
      import user._

      val userRepository = new UserRepository

      userRepository.getUsers shouldBe 'empty
      userRepository.getUser(username) shouldBe None

      userRepository.addUser(username, email) shouldBe Right(user)
      userRepository.getUsers shouldBe Set(user)
      userRepository.getUser(username) shouldBe Some(user)

      userRepository.addUser(username, email) shouldBe Left(UsernameTaken(username))

      userRepository.removeUser(username) shouldBe Right(user)
      userRepository.getUsers shouldBe 'empty
      userRepository.getUser(username) shouldBe None

      userRepository.removeUser(username) shouldBe Left(UsernameUnknown(username))
    }
  }
}
