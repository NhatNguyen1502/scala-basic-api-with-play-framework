package services

import Exceptions.{AppException, ErrorCode}
import dtos.request.user.CreateUserRequestDto
import dtos.response.user.UserResponseDto
import models.User
import play.api.http.Status
import repositories.UserRepository

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

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
      user = User(
        email = request.email,
        password = request.password,
        age = request.age
      )
      savedUser <- userRepository.create(user)
    } yield UserResponseDto.fromUser(savedUser)
  }

  def getAllUsers: Future[Seq[UserResponseDto]] = userRepository
    .list() //  Future[Seq[User]]
    .map(
      _.map(UserResponseDto.fromUser)
    ) // .map(...) : Future, _.map(UserResponseDto.fromUser): Seq[User]
}
