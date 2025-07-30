package dtos.request.user

import constants.RegexConstants.EmailRegex
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Json, JsonValidationError, OWrites, Reads}
import validations.CustomValidators.{
  minLength,
  nonEmpty,
  regexMatch,
  requiredField
}

/**
 * Represents the request body structure for creating a new user.
 *
 * Fields:
 *
 *   - `email`: The user's email address. Must be non-empty and match a valid
 *     email format.
 *   - `password`: The user's password. Must be non-empty and at least 6
 *     characters long.
 *   - `age`: An optional age field. If provided, must be between 1 and 100.
 */
case class CreateUserRequestDto(
  email: String,
  password: String,
  firstName: String,
  lastName: String,
  address: String,
  phoneNumber: String,
  role: String,
  age: Option[Int]
)

/**
 * Companion object containing a custom JSON deserializer (`Reads`) for
 * [[CreateUserRequestDto]].
 *
 * This implicit `Reads` provides detailed validation logic:
 *
 *   - `email` is required, must not be empty, and must match a valid email
 *     pattern.
 *   - `password` is required, must not be empty, and must be at least 6
 *     characters.
 *   - `age` is optional, but if present must be between 1 and 100.
 *
 * This is used by Play Framework to:
 *
 *   - Automatically validate and parse incoming JSON requests via
 *     `validate[CreateUserRequestDto]`.
 *   - Provide readable error messages when input JSON fails validation.
 *
 * Note:
 *
 *   - This uses custom validation combinators defined in `CustomValidators` for
 *     reusability and clarity.
 */
object CreateUserRequestDto {

  implicit val reads: Reads[CreateUserRequestDto] = (
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
        requiredField("Phone number is required") keepAnd nonEmpty(
          "Phone number is not empty"
        )
      ) and
      (JsPath \ "role").read[String](
        requiredField("Role is required") keepAnd regexMatch(
          "(USER|ADMIN)",
          "Invalid role"
        )
          keepAnd nonEmpty("Role is not empty")
      ) and
      (JsPath \ "age").readNullable[Int](
        Reads
          .of[Int]
          .filter(JsonValidationError("Minimum age is 1"))(_ >= 1)
          .filter(JsonValidationError("Maximum age is 100"))(_ <= 100)
      )
  )(CreateUserRequestDto.apply _)

  // Json serialization, it necessary for unit test
  implicit val writes: OWrites[CreateUserRequestDto] =
    Json.writes[CreateUserRequestDto]
}
