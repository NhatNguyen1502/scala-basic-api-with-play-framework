package dtos.response

import play.api.libs.json.{Json, JsValue, Writes}

case class ErrorResponse(
                          success: Boolean = false,
                          message: String,
                          errors: Option[JsValue] = None
                        )

object ErrorResponse {
  implicit val writes: Writes[ErrorResponse] = Json.writes[ErrorResponse]
}