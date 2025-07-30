package services

import dtos.request.user.{CreateUserRequestDto, UpdateUserRequestDto}
import dtos.response.user.UserResponseDto
import exceptions.{AppException, ErrorCode}
import models.User
import org.mindrot.jbcrypt.BCrypt
import play.api.http.Status
import repositories.UserRepository
import utils.UuidSupport

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UserService @Inject() (
  userRepository: UserRepository,
  roleService: RoleService
)(implicit
  ec: ExecutionContext
) extends UuidSupport {

  def createUser(request: CreateUserRequestDto): Future[Unit] = {
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
      defaultRole <- roleService.findByName(request.role)

      hashedPassword = BCrypt.hashpw(request.password, BCrypt.gensalt())

      user = User(
        email = request.email,
        password = hashedPassword,
        age = request.age,
        roleId = defaultRole.id,
        firstName = request.firstName,
        lastName = request.lastName,
        address = request.address,
        phoneNumber = request.phoneNumber
      )
      _ <- userRepository.create(user)
    } yield None
  }

  def getAllUsers: Future[Seq[UserResponseDto]] = userRepository.list()

  def findById(id: String): Future[UserResponseDto] = {
    withUuid(id) {
      uuid =>
        userRepository.findById(uuid).flatMap {
          case Some(userDto) => Future.successful(userDto)
          case None          =>
            print(uuid)
            Future.failed(
              new AppException(ErrorCode.UserNotFound, Status.NOT_FOUND)
            )
        }
    }

  }

  def updateUser(id: String, user: UpdateUserRequestDto): Future[Any] = {
    withUuid(id) {
      uuid =>
        userRepository.update(uuid, user).flatMap {
          case 0 =>
            Future.failed(
              new AppException(ErrorCode.UserNotFound, Status.NOT_FOUND)
            )
          case -1 =>
            Future.failed(
              new AppException(ErrorCode.NoFieldToUpdate, Status.BAD_REQUEST)
            )
          case _ =>
            Future.successful(())
        }
    }
  }

  def deleteUser(id: String): Future[Unit] = {
    withUuid(id) {
      uuid =>
        userRepository.delete(uuid).flatMap {
          case 0 =>
            Future.failed(
              new AppException(ErrorCode.UserNotFound, Status.NOT_FOUND)
            )
          case _ =>
            Future.successful(())
        }
    }
  }
}
