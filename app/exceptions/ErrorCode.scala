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

  case object NoFieldToUpdate extends ErrorCode {
    val code = 1005
    val message = "No field to update"
  }

  case object WrongPassword extends ErrorCode {
    val code = 1006
    val message = "Wrong password"
  }

  case object EmailNotFound extends ErrorCode {
    val code = 1007
    val message = "Email not found"
  }

  case object CategoryNotFound extends ErrorCode {
    val code = 1008
    val message = "Category not found"
  }

  case object CategoryNameAlreadyExits extends ErrorCode {
    val code = 1009
    val message = "Category name already exits"
  }

  case object ProductNotFound extends ErrorCode {
    val code = 1010
    val message = "Product not found"
  }

}
