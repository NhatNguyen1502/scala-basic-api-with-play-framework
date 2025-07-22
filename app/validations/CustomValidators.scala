package validations

import play.api.libs.json.{
  JsError,
  JsString,
  JsSuccess,
  JsonValidationError,
  Reads
}

/**
 * Utility object containing custom Play JSON validators for common input
 * constraints.
 */
object CustomValidators {

  /**
   * Validates that a string is not empty.
   *
   * @param msg
   *   The error message to display if the string is empty.
   * @return
   *   A `Reads[String]` that fails if the input string is empty.
   */
  def nonEmpty(msg: String): Reads[String] =
    Reads.StringReads.filter(JsonValidationError(msg))(_.nonEmpty)

  /**
   * Validates that a string has at least the specified minimum length.
   *
   * @param min
   *   The minimum required length.
   * @param msg
   *   The error message to display if the string is too short.
   * @return
   *   A `Reads[String]` that fails if the string's length is less than `min`.
   */
  def minLength(min: Int, msg: String): Reads[String] =
    Reads.StringReads.filter(JsonValidationError(msg))(_.length >= min)

  /**
   * Validates that a string matches the specified regular expression pattern.
   *
   * @param pattern
   *   The regular expression pattern to match.
   * @param msg
   *   The error message to display if the string does not match.
   * @return
   *   A `Reads[String]` that fails if the string does not match the pattern.
   */
  def regexMatch(pattern: String, msg: String): Reads[String] =
    Reads.StringReads.filter(JsonValidationError(msg))(_.matches(pattern))

  /**
   * Wraps a base `Reads[T]` validator with `optionWithNull` to support optional
   * fields.
   *
   * @param baseReads
   *   The validator to apply when the value is present.
   * @tparam T
   *   The type being validated.
   * @return
   *   A `Reads[Option[T]]` that validates when present, allows null or missing
   *   values.
   */
  def optionalWith[T](baseReads: Reads[T]): Reads[Option[T]] =
    Reads.optionWithNull[T](baseReads)

  def requiredField(errorMessage: String): Reads[String] =
    Reads[String] {
      case JsString(s) if s.trim.nonEmpty => JsSuccess(s.trim)
      case _                              => JsError(errorMessage)
    }
}
