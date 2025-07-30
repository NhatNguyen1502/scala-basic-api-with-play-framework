package dtos.request.auth

import constants.RegexConstants.EmailRegex
import play.api.libs.functional.syntax.{
  toApplicativeOps,
  toFunctionalBuilderOps
}
import play.api.libs.json.{JsPath, Json, OWrites, Reads}
import validations.CustomValidators.{
  minLength,
  nonEmpty,
  regexMatch,
  requiredField
}

case class LoginRequestDto(email: String, password: String)

object LoginRequestDto {
  implicit val reads: Reads[LoginRequestDto] = (
    (JsPath \ "email").read[String](
      requiredField("Email is required") keepAnd regexMatch(
        EmailRegex,
        "Invalid email format"
      ) keepAnd nonEmpty("Email is not empty")
    ) and
      (JsPath \ "password").read[String](
        requiredField("Password is required") keepAnd minLength(
          6,
          "Password is at least 6 characters"
        ) keepAnd nonEmpty("Password is not empty")
      )
  )(LoginRequestDto.apply _)

  // Json serialization, it necessary for unit test
  implicit val writes: OWrites[LoginRequestDto] =
    Json.writes[LoginRequestDto]
}
