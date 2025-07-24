package utils

import exceptions.{AppException, ErrorCode}
import play.api.http.Status

import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

/**
 * Trait that provides utility for parsing and validating UUID strings.
 *
 * Instead of repeating the conversion logic and error handling for invalid
 * UUIDs across multiple services, this trait centralizes that logic into a
 * reusable `withUuid` method.
 *
 * Example usage:
 * {{{
 * class UserService @Inject()(repo: UserRepository)(implicit ec: ExecutionContext)
 *   extends UuidSupport {
 *
 *   def deleteUser(id: String): Future[Unit] =
 *     withUuid(id) { uuid =>
 *       repo.delete(uuid).map {
 *         case 0 => throw new AppException(ErrorCode.UserNotFound, Status.NOT_FOUND)
 *         case _ => ()
 *       }
 *     }
 * }
 * }}}
 */
trait UuidSupport {

  /**
   * Safely parses a UUID string and runs the provided block if valid. If the
   * UUID string is invalid, it returns a failed Future with an AppException.
   *
   * @param id
   *   The UUID string (e.g., "550e8400-e29b-41d4-a716-446655440000")
   * @param block
   *   The block of code to execute with the parsed UUID
   * @param ec
   *   ExecutionContext for asynchronous execution
   * @tparam T
   *   The result type of the block
   * @return
   *   A Future of type T or a failed Future if the UUID is invalid
   */
  def withUuid[T](
    id: String
  )(block: UUID => Future[T])(implicit ec: ExecutionContext): Future[T] = {
    Try(UUID.fromString(id)) match {
      case Success(uuid) => block(uuid)
      case Failure(_)    =>
        Future.failed(
          new AppException(ErrorCode.InvalidUUID, Status.BAD_REQUEST)
        )
    }
  }
}
