package models

import java.time.LocalDateTime

case class Role(
  id: Int = 0,
  name: String,
  description: Option[String] = None,
  createdAt: LocalDateTime = LocalDateTime.now(),
  updatedAt: LocalDateTime = LocalDateTime.now()
)
