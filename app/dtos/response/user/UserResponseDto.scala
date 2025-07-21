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

  /**
   * Implicit JSON formatter for UserResponseDto using Play JSON.
   */
  implicit val format: Format[UserResponseDto] = Json.format[UserResponseDto]

  /**
   * A tupled version of the apply method for use in functional contexts, such
   * as Slick projections.
   */
  val tupled: (
    (UUID, String, Option[Int], Boolean, LocalDateTime, LocalDateTime)
  ) => UserResponseDto = (apply _).tupled

  /**
   * Maps a domain User model to a UserResponseDto.
   *
   * @param user
   *   The domain model representing a user.
   * @return
   *   A corresponding UserResponseDto for API responses.
   */
  def fromUser(user: User): UserResponseDto = UserResponseDto(
    id = user.id,
    email = user.email,
    age = user.age,
    isActive = user.isActive,
    createdAt = user.createdAt,
    updatedAt = user.updatedAt
  )
}
