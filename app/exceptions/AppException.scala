package exceptions

import play.api.http.Status

/**
 * Represents a custom application exception that wraps a predefined
 * [[ErrorCode]] along with an associated HTTP status code.
 *
 * This exception can be thrown when the application encounters a known,
 * structured error, and it helps standardize how errors are propagated and
 * serialized in API responses.
 *
 * @param errorCode
 *   The structured error code containing both a unique code and a descriptive
 *   message.
 * @param httpStatus
 *   The HTTP status code to be returned to the client. Defaults to `500
 *   Internal Server Error` if not explicitly specified.
 */
class AppException(
  val errorCode: ErrorCode,
  val httpStatus: Int = Status.INTERNAL_SERVER_ERROR
) extends RuntimeException(errorCode.message)
