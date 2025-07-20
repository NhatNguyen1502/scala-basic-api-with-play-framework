package services

import dtos.request.user.CreateUserRequestDto
import dtos.response.user.UserResponseDto
import models.User
import repositories.UserRepository

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UserService @Inject()(userRepository: UserRepository)(implicit ec: ExecutionContext) {

  def createUser(request: CreateUserRequestDto): Future[UserResponseDto] = {
    val user = User(
      email = request.email,
      password = request.password,
      age = request.age
    )
    userRepository.create(user).map(UserResponseDto.fromUser)
  }

  def getAllUsers: Future[Seq[UserResponseDto]] = userRepository.list() //  Future[Seq[User]]
    .map(_.map(UserResponseDto.fromUser)) // .map(...) : Future, _.map(UserResponseDto.fromUser): Seq[User]
}