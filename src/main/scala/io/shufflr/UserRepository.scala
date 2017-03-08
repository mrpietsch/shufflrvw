/*
 * Copyright 2016 codecentric AG
 */

package io.shufflr

import java.util.concurrent.ConcurrentHashMap
import scala.collection.JavaConverters

object UserRepository {

  final case class UsernameTaken(username: String)
  final case class UsernameUnknown(username: String)

  final case class User(username: String, email: String)
}

final class UserRepository(initialUsers: Map[String, UserRepository.User] = Map.empty) {
  import JavaConverters._
  import UserRepository._

  private val users = new ConcurrentHashMap[String, User](initialUsers.asJava)

  def getUsers: Set[User] =
    users.asScala.valuesIterator.to[Set]

  def getUser(username: String): Option[User] =
    Option(users.get(username)) // We all love `null`!

  def addUser(username: String, email: String): Either[UsernameTaken, User] = {
    val user = User(username, email)
    if (users.putIfAbsent(username, user) == null) Right(user) else Left(UsernameTaken(username))
  }

  def removeUser(username: String): Either[UsernameUnknown, User] = {
    val user = users.remove(username)
    if (user != null) Right(user) else Left(UsernameUnknown(username))
  }
}
