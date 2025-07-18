package models

import java.time.LocalDateTime
import java.util.UUID

case class User(
  id: UUID = UUID.randomUUID(),
  email: String,
  password: String,
  age: Option[Int] = None,
  isActive: Boolean = true,
  createdAt: LocalDateTime = LocalDateTime.now(),
  updatedAt: LocalDateTime = LocalDateTime.now()
)
