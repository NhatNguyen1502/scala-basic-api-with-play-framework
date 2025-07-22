package utils

import play.api.libs.json.{JsPath, JsValue, Json, JsonValidationError}
import exceptions.AppException

/**
 * Utility object for formatting error messages as JSON. Provides methods to
 * convert JSON validation errors and custom application exceptions into a
 * standardized JSON format suitable for API responses.
 */
object ErrorFormatter {

  /**
   * Formats a sequence of Play JSON validation errors into a JSON response
   * object.
   *
   * @param errors
   *   A sequence of tuples, where each tuple contains a JSON path and a
   *   sequence of validation errors for that path.
   * @return
   *   A JsValue representing the formatted error response in the following
   *   format:
   *   {{{
   * {
   *   "success": false,
   *   "message": "Validation failed",
   *   "errors": [
   *     {
   *       "field": "fieldName",
   *       "message": "error message"
   *     }
   *   ]
   * }
   *   }}}
   */
  def formatJsErrors(
    errors: Seq[(JsPath, Seq[JsonValidationError])]
  ): JsValue = {
    val errorList = errors.flatMap {
      case (path, validationErrors) =>
        validationErrors.map {
          err =>
            Json.obj(
              "field" -> path.toJsonString.stripPrefix("."),
              "message" -> err.message
            )
        }
    }

    Json.obj(
      "success" -> false,
      "message" -> "Validation failed",
      "errors" -> errorList
    )
  }

  /**
   * Formats a custom AppException into a JSON error response.
   *
   * @param error
   *   The AppException containing the error message.
   * @return
   *   A JsValue representing the error in the following format:
   *   {{{
   * {
   *   "success": false,
   *   "message": "error message"
   * }
   *   }}}
   */
  def formatAppException(error: AppException): JsValue = Json.obj(
    "success" -> false,
    "message" -> error.errorCode.message
  )
}
