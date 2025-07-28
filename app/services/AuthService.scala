package services

import dtos.request.authen.LoginRequestDto
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
  def authenticate(loginRequestDto: LoginRequestDto): Future[String] = {
    userRepository.findPasswordByEmail(loginRequestDto.email).map {
      case Some(storedHash)
          if BCrypt.checkpw(loginRequestDto.password, storedHash) =>
        jwtService.createToken(loginRequestDto.email)

      case Some(_) =>
        // Wrong Password
        throw new AppException(ErrorCode.WrongPassword, Status.UNAUTHORIZED)

      case None =>
        // Email not found
        throw new AppException(ErrorCode.EmailNotFound, Status.UNAUTHORIZED)
    }

  }
}
