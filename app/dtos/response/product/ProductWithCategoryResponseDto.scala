package dtos.response.product

import play.api.libs.json.{Json, OFormat}

import java.time.LocalDateTime
import java.util.UUID

case class ProductWithCategoryResponseDto(
  id: UUID,
  name: String,
  description: Option[String],
  imageUrl: String,
  price: BigDecimal,
  category: CategoryShortDto,
  quantity: Int,
  isFeatured: Boolean,
  createdAt: LocalDateTime,
  updatedAt: LocalDateTime,
  createdBy: UUID,
  updatedBy: UUID
)
object ProductWithCategoryResponseDto {
  implicit val format: OFormat[ProductWithCategoryResponseDto] =
    Json.format[ProductWithCategoryResponseDto]
}

case class CategoryShortDto(id: UUID, name: String)
object CategoryShortDto {
  implicit val format: OFormat[CategoryShortDto] = Json.format[CategoryShortDto]
}
