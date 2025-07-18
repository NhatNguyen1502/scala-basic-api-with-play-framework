package dtos.request.user

import constants.RegexConstants.EmailRegex
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, JsonValidationError, Reads}
import validations.CustomValidators.{minLength, minValue, nonEmpty, optionalWith, requiredField}

case class CreateUserRequestDto(
                                 email: String,
                                 password: String,
                                 age: Option[Int]
                               )

/**
 * Defines an implicit JSON formatter using Play JSON's macro-based `Json.format`.
 * This formatter allows automatic serialization and deserialization of
 * CreateUserRequestDto instances to and from JSON.
 *
 * This is required for:
 *
 * - Reading CreateUserRequestDto from a JSON request body (`validate[CreateUserRequestDto]`)
 *
 * - Writing CreateUserRequestDto to JSON when needed (e.g., logging, debugging)
 *
 * This is a combination of:
 *
 * - implicit val reads = Json.reads[CreateUserRequestDto]
 *
 * - implicit val writes = Json.writes[CreateUserRequestDto]
 *
 * The implicit `format` is automatically picked up by Play's JSON library
 * wherever an implicit `Format[CreateUserRequestDto]` is needed.
 */
object CreateUserRequestDto {
  //  implicit val format: Format[CreateUserRequestDto] = Json.format[CreateUserRequestDto]
  implicit val reads: Reads[CreateUserRequestDto] = (
    requiredField(
      JsPath \ "email",
      nonEmpty("Email must not be empty")
        .filter(JsonValidationError("Invalid email format"))(_.matches(EmailRegex)),
      "Email is required"
    ) and
    requiredField(
      JsPath \ "password",
      nonEmpty("Password must not be empty")
        .andKeep(minLength(6, "Password must be at least 6 characters")),
      "Password is required"
    ) and
    (JsPath \ "age").read[Option[Int]](
      optionalWith(minValue(1, "Age must be at least 1"))
    )
  )(CreateUserRequestDto.apply _)
}