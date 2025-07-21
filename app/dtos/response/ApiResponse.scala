package dtos.response

import play.api.libs.json.{Json, Writes}

case class ApiResponse[T](
  success: Boolean,
  message: String,
  data: Option[T] = None,
  errors: Option[List[FieldError]] = None
)
object ApiResponse {
  // JSON serialization
  implicit def writes[T](implicit w: Writes[T]): Writes[ApiResponse[T]] =
    Json.writes[ApiResponse[T]]
}

// Field error of dto validation
case class FieldError(field: String, message: String)
object FieldError {
  implicit val writes: Writes[FieldError] = Json.writes[FieldError]
}
