package dtos.response.user

import play.api.libs.json.{Format, Json}

import java.util.UUID

case class UserWithRoleResponseDto(
  id: UUID,
  email: String,
  role: String
)

object UserWithRoleResponseDto {
  implicit val format: Format[UserWithRoleResponseDto] =
    Json.format[UserWithRoleResponseDto]
}
