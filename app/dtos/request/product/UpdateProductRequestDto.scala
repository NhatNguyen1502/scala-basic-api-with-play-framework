package dtos.request.product

import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json.{JsPath, Reads}

import java.util.UUID

case class UpdateProductRequestDto(
  name: Option[String],
  description: Option[String],
  price: Option[BigDecimal],
  categoryId: Option[UUID],
  quantity: Option[Int],
  isFeatured: Option[Boolean]
)
object UpdateProductRequestDto {
  implicit val reads: Reads[UpdateProductRequestDto] = (
    (JsPath \ "name").readNullable[String] and
      (JsPath \ "description").readNullable[String] and
      (JsPath \ "price")
        .readNullable[BigDecimal]
        .orElse(
          (JsPath \ "price").readNullable[String].map(_.map(BigDecimal(_)))
        ) and
      (JsPath \ "categoryId")
        .readNullable[String]
        .map(_.map(UUID.fromString)) and
      (JsPath \ "quantity")
        .readNullable[Int]
        .orElse(
          (JsPath \ "quantity").readNullable[String].map(_.map(_.toInt))
        ) and
      (JsPath \ "isFeatured").readNullable[Boolean]
  )(UpdateProductRequestDto.apply _)
}
