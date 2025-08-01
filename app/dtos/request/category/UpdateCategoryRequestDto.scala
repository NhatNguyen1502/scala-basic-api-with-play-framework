package dtos.request.category

import play.api.libs.json.{JsPath, Json, OWrites, Reads}
import validations.CustomValidators.requiredField

case class UpdateCategoryRequestDto(
  name: String
)

object UpdateCategoryRequestDto {
  implicit val reads: Reads[UpdateCategoryRequestDto] =
    (JsPath \ "name")
      .read[String](requiredField("Name is required"))
      .map(UpdateCategoryRequestDto.apply)

  implicit val writes: OWrites[UpdateCategoryRequestDto] =
    Json.writes[UpdateCategoryRequestDto]
}
