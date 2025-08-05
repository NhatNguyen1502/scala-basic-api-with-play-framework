package models

import java.time.LocalDateTime
import java.util.UUID

case class Product(
  id: UUID = UUID.randomUUID(),
  name: String,
  description: Option[String] = None,
  imageUrl: String,
  price: BigDecimal,
  categoryId: UUID,
  quantity: Int = 0,
  isFeatured: Boolean = false,
  createdAt: LocalDateTime = LocalDateTime.now(),
  updatedAt: LocalDateTime = LocalDateTime.now(),
  createdBy: UUID,
  updatedBy: UUID,
  isDeleted: Boolean = false
)
