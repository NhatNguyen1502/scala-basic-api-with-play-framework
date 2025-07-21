package dtos.response.user

import models.User
import play.api.libs.json.{Format, Json}

import java.time.LocalDateTime
import java.util.UUID

case class UserResponseDto(
  id: UUID,
  email: String,
  age: Option[Int],
  isActive: Boolean,
  createdAt: LocalDateTime,
  updatedAt: LocalDateTime
)

object UserResponseDto {
  implicit val format: Format[UserResponseDto] = Json.format[UserResponseDto]

  def fromUser(user: User): UserResponseDto = UserResponseDto(
    id = user.id,
    email = user.email,
    age = user.age,
    isActive = user.isActive,
    createdAt = user.createdAt,
    updatedAt = user.updatedAt
  )
}
