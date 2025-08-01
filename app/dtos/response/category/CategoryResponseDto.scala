package dtos.response.category

import play.api.libs.json.{Format, Json}

import java.time.LocalDateTime
import java.util.UUID

case class CategoryResponseDto(
  id: UUID,
  name: String,
  createdAt: LocalDateTime,
  updatedAt: LocalDateTime
)

object CategoryResponseDto {
  implicit val format: Format[CategoryResponseDto] =
    Json.format[CategoryResponseDto]

  val tupled: (
    (UUID, String, LocalDateTime, LocalDateTime)
  ) => CategoryResponseDto = (apply _).tupled
}
