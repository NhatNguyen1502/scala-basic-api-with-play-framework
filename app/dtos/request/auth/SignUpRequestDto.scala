package dtos.request.auth

import constants.RegexConstants.{EmailRegex, PhoneNumberRegex}
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

case class SignUpRequestDto(
  email: String,
  firstName: String,
  lastName: String,
  address: String,
  phoneNumber: String,
  password: String
)

object SignUpRequestDto {
  implicit val reads: Reads[SignUpRequestDto] = (
    (JsPath \ "email").read[String](
      requiredField("Email is required") keepAnd regexMatch(
        EmailRegex,
        "Invalid email format"
      ) keepAnd nonEmpty("Email is not empty")
    ) and
      (JsPath \ "firstName").read[String](
        requiredField("First name is required") keepAnd nonEmpty(
          "First name is not empty"
        )
      ) and
      (JsPath \ "lastName").read[String](
        requiredField("Last name is required") keepAnd nonEmpty(
          "Last name is not empty"
        )
      ) and
      (JsPath \ "address").read[String](
        requiredField("Address is required") keepAnd nonEmpty(
          "Address is not empty"
        )
      ) and
      (JsPath \ "phoneNumber").read[String](
        requiredField("Phone number is required") keepAnd regexMatch(
          PhoneNumberRegex,
          "Invalid phone number format"
        ) keepAnd nonEmpty(
          "Phone number is not empty"
        )
      ) and
      (JsPath \ "password").read[String](
        requiredField("Password is required") keepAnd minLength(
          6,
          "Password is at least 6 characters"
        ) keepAnd nonEmpty("Password is not empty")
      )
  )(SignUpRequestDto.apply _)

  // Json serialization, it necessary for unit test
  implicit val writes: OWrites[SignUpRequestDto] =
    Json.writes[SignUpRequestDto]
}
