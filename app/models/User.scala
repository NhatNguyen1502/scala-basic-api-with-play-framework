package models

import java.time.LocalDateTime
import java.util.UUID

case class User(
  id: UUID = UUID.randomUUID(),
  email: String,
  firstName: String,
  lastName: String,
  address: String,
  phoneNumber: String,
  password: String,
  age: Option[Int] = None,
  isActive: Boolean = true,
  isVerified: Boolean = false,
  roleId: Int = 0,
  createdAt: LocalDateTime = LocalDateTime.now(),
  updatedAt: LocalDateTime = LocalDateTime.now()
)
