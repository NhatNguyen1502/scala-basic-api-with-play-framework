package models

import java.time.LocalDateTime
import java.util.UUID

case class Category(
  id: UUID,
  name: String,
  createdAt: LocalDateTime = LocalDateTime.now(),
  updatedAt: LocalDateTime = LocalDateTime.now(),
  createdBy: UUID,
  updatedBy: UUID,
  isDeleted: Boolean
)
