package utils

import exceptions.{AppException, ErrorCode}

import java.util.UUID
import scala.util.{Failure, Success, Try}
import play.api.http.Status

object UUIDUtils {
  def parseUUID(id: String): UUID = {
    Try(UUID.fromString(id)) match {
      case Success(uuid) => uuid
      case Failure(_)    =>
        throw new AppException(ErrorCode.InvalidUUID, Status.BAD_REQUEST)
    }
  }
}
