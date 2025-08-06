package dtos.response

import play.api.libs.json.{Json, OFormat}

case class PagedResponse[T](
  items: Seq[T],
  currentPage: Int,
  size: Int,
  totalElements: Long,
  totalPages: Int
)
object PagedResponse {
  implicit def format[T : OFormat]: OFormat[PagedResponse[T]] =
    Json.format[PagedResponse[T]]
}
