package dtos.request.product

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Reads}
import validations.CustomValidators.requiredField

import java.util.UUID

case class CreateProductRequestDto(
  name: String,
  description: Option[String],
  price: BigDecimal,
  categoryId: UUID,
  quantity: Int,
  isFeatured: Boolean
)

object CreateProductRequestDto {
  implicit val reads: Reads[CreateProductRequestDto] = (
    (JsPath \ "name").read[String](
      requiredField("Product name is required")
    ) and
      (JsPath \ "description").readNullable[String] and
      (JsPath \ "price")
        .read[BigDecimal]
        .orElse(
          (JsPath \ "price")
            .read[String](
              requiredField("Price is required")
            )
            .map(BigDecimal(_))
        )
      and
      (JsPath \ "categoryId")
        .read[String](
          requiredField("Quantity is required")
        )
        .map(
          UUID.fromString
        ) and
      (JsPath \ "quantity")
        .read[Int]
        .orElse(
          (JsPath \ "quantity")
            .read[String](
              requiredField("Quantity is required")
            )
            .map(_.toInt)
        ) and
      (JsPath \ "isFeatured").read[Boolean]
  )(CreateProductRequestDto.apply _)
}
