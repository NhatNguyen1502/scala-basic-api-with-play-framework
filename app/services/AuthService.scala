package services

import dtos.request.auth.LoginRequestDto
import dtos.response.auth.LoginResponseDto
import dtos.response.user.UserResponseDto
import exceptions.{AppException, ErrorCode}
import org.mindrot.jbcrypt.BCrypt
import play.api.http.Status
import repositories.UserRepository

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AuthService @Inject() (
  userRepository: UserRepository,
  jwtService: JwtService
)(implicit
  ec: ExecutionContext
) {
  def authenticate(
    loginRequestDto: LoginRequestDto
  ): Future[LoginResponseDto] = {
    userRepository.findByEmail(loginRequestDto.email).map {
      case Some(user)
          if BCrypt.checkpw(loginRequestDto.password, user.password) =>
        val accessToken = jwtService.createToken(loginRequestDto.email)
        val refreshToken = accessToken // temporary refreshToken
        LoginResponseDto(
          accessToken,
          refreshToken,
          UserResponseDto.fromUser(user)
        )

      case Some(_) =>
        // Wrong Password
        throw new AppException(ErrorCode.WrongPassword, Status.UNAUTHORIZED)

      case None =>
        // Email not found
        throw new AppException(ErrorCode.EmailNotFound, Status.UNAUTHORIZED)
    }

  }
}
