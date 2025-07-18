package dtos.request.user

import play.api.libs.functional.syntax._
import play.api.libs.json.{ JsPath, JsonValidationError, Reads}

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
    (JsPath \ "email").read[String](Reads.email) and
      (JsPath \ "password").read[String] and
      (JsPath \ "age").readNullable[Int](Reads.min(1))
    )(CreateUserRequestDto.apply _)
}