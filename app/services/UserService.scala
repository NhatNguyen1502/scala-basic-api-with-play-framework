package services

import Exceptions.{AppException, ErrorCode}
import dtos.request.user.CreateUserRequestDto
import dtos.response.user.UserResponseDto
import models.User
import org.mindrot.jbcrypt.BCrypt
import play.api.http.Status
import repositories.UserRepository

import java.util.UUID
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

@Singleton
class UserService @Inject() (userRepository: UserRepository)(implicit
  ec: ExecutionContext
) {

  def createUser(request: CreateUserRequestDto): Future[UserResponseDto] = {
    // Using for-comprehension to solve async easily
    for {
      existingUser <- userRepository.existByEmail(request.email)
      _ <-
        if (existingUser) {
          Future.failed(
            new AppException(ErrorCode.EmailAlreadyExists, Status.CONFLICT)
          )
        } else {
          Future.successful(())
        }

      hashedPassword = BCrypt.hashpw(request.password, BCrypt.gensalt())

      user = User(
        email = request.email,
        password = hashedPassword,
        age = request.age
      )
      savedUser <- userRepository.create(user)
    } yield UserResponseDto.fromUser(savedUser)
  }

  def getAllUsers: Future[Seq[UserResponseDto]] = userRepository.list()

  def findById(id: String): Future[UserResponseDto] = {
    Try(UUID.fromString(id)) match {
      case Success(uuid) =>
        userRepository.findById(uuid).flatMap {
          case Some(userDto) => Future.successful(userDto)
          case None          =>
            Future.failed(
              new AppException(ErrorCode.UserNotFound, Status.NOT_FOUND)
            )
        }

      case Failure(_) =>
        Future.failed(
          new AppException(ErrorCode.InvalidUUID, Status.BAD_REQUEST)
        )
    }
  }
}
