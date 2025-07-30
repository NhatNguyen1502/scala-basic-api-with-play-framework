package dtos.response.auth

import dtos.response.user.UserResponseDto
import play.api.libs.json.{Format, Json}

case class LoginResponseDto(
  accessToken: String,
  refreshToken: String,
  user: UserResponseDto
)

object LoginResponseDto {
  implicit val format: Format[LoginResponseDto] = Json.format[LoginResponseDto]
}
