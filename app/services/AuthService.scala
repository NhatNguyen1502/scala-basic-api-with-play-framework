package services

import dtos.request.auth.{LoginRequestDto, SignUpRequestDto}
import dtos.request.user.CreateUserRequestDto
import dtos.response.auth.LoginResponseDto
import dtos.response.user.UserWithRoleResponseDto
import exceptions.{AppException, ErrorCode}
import org.mindrot.jbcrypt.BCrypt
import play.api.http.Status
import repositories.UserRepository

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AuthService @Inject() (
  userRepository: UserRepository,
  userService: UserService,
  jwtService: JwtService,
  mailerService: MailerService
)(implicit
  ec: ExecutionContext
) {

  def authenticate(
    loginRequestDto: LoginRequestDto
  ): Future[LoginResponseDto] = {
    userRepository.findUserWithRoleByEmail(loginRequestDto.email).map {
      case Some((user, roleName)) =>
        if (!user.isVerified) {
          throw new AppException(
            ErrorCode.EmailNotVerified,
            Status.UNAUTHORIZED
          )
        } else if (!user.isActive) {
          throw new AppException(ErrorCode.UserInactive, Status.UNAUTHORIZED)
        } else if (!BCrypt.checkpw(loginRequestDto.password, user.password)) {
          throw new AppException(ErrorCode.WrongPassword, Status.UNAUTHORIZED)
        } else {
          val accessToken =
            jwtService.createToken(user.email, user.id, roleName)
          val refreshToken = accessToken // temporary refreshToken
          val userInfo = UserWithRoleResponseDto(user.id, user.email, roleName)
          LoginResponseDto(accessToken, refreshToken, userInfo)
        }

      case None =>
        throw new AppException(ErrorCode.EmailNotFound, Status.UNAUTHORIZED)
    }
  }

  def signUp(request: SignUpRequestDto): Future[Unit] = {
    for {
      hasAnyData <- userRepository.hasAnyData
      role = if (hasAnyData) "USER" else "ADMIN"
      createUserDto = CreateUserRequestDto(
        email = request.email,
        password = request.password,
        firstName = request.firstName,
        lastName = request.lastName,
        address = request.address,
        phoneNumber = request.phoneNumber,
        role = role,
        age = None
      )
      _ <- userService.createUser(createUserDto)
      maybeUser <- userRepository.findByEmail(request.email)
      user <- maybeUser match {
        case Some(u) => Future.successful(u)
        case None    =>
          Future.failed(
            new AppException(ErrorCode.UserNotFound, Status.NOT_FOUND)
          )
      }
    } yield {
      val token = jwtService.createToken(user.email, user.id, role)

      // send verification email asynchronously
      Future {
        mailerService.sendVerificationEmail(user.email, token)
      }(ExecutionContext.global)

      // response immediately without waiting for email to be sent
      ()
    }
  }

  def verifyNewAccount(token: String): Future[Unit] = {
    if (jwtService.verifyToken(token)) {
      val userId = jwtService.getUserIdFromToken(token).get
      userService.verifyUser(userId)
    } else {
      Future.failed(
        new AppException(ErrorCode.InvalidToken, Status.UNAUTHORIZED)
      )
    }
  }
}
