package dtos.response

import play.api.libs.json.{Json, Writes}

case class SuccessResponse[T](
                               success: Boolean = true,
                               message: String,
                               data: Option[T] = None
                             )

object SuccessResponse {
  implicit def writes[T: Writes]: Writes[SuccessResponse[T]] = Json.writes[SuccessResponse[T]]
}