package dtos.request.user

import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json.{JsPath, JsonValidationError, Reads}

case class UpdateUserRequestDto(age: Option[Int], isActive: Option[Boolean])

object UpdateUserRequestDto {

  implicit val reads: Reads[UpdateUserRequestDto] = (
    (JsPath \ "age").readNullable[Int](
      Reads
        .of[Int]
        .filter(JsonValidationError("Minimum age is 1"))(_ >= 1)
        .filter(JsonValidationError("Maximum age is 100"))(_ <= 100)
    ) and
      (JsPath \ "isActive").readNullable[Boolean]
  )(UpdateUserRequestDto.apply _)
}
