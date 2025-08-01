package dtos.request.category

import play.api.libs.json._
import validations.CustomValidators.requiredField

case class CreateCategoryRequestDto(
  name: String
)

object CreateCategoryRequestDto {
  implicit val reads: Reads[CreateCategoryRequestDto] =
    (JsPath \ "name")
      .read[String](requiredField("Name is required"))
      .map(CreateCategoryRequestDto.apply)

  implicit val writes: OWrites[CreateCategoryRequestDto] =
    Json.writes[CreateCategoryRequestDto]
}
