package exceptions

/**
 * Represents a structured error code used throughout the application. Each
 * error code consists of an integer identifier and a human-readable message.
 */
sealed trait ErrorCode {

  /** A unique integer representing the error code. */
  def code: Int

  /** A human-readable message describing the error. */
  def message: String
}

/**
 * Companion object for ErrorCode, containing all predefined error codes.
 */
object ErrorCode {
  case object UncategorizeException extends ErrorCode {
    val code = 1001
    val message = "An unexpected error has occurred"
  }

  case object EmailAlreadyExists extends ErrorCode {
    val code = 1002
    val message = "Email already exists"
  }

  case object UserNotFound extends ErrorCode {
    val code = 1003
    val message = "User not found"
  }

  case object InvalidUUID extends ErrorCode {
    val code = 1004
    val message = "Id must be UUID"
  }
}
