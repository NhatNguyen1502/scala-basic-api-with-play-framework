package controllers

import Exceptions.{AppException, ErrorCode}
import dtos.request.user.CreateUserRequestDto
import dtos.response.user.UserResponseDto
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import play.api.Play.materializer
import play.api.http.Status.{BAD_REQUEST, CONFLICT, CREATED}
import play.api.libs.json.{JsValue, Json}
import play.api.test.Helpers.{
  POST,
  call,
  contentAsJson,
  defaultAwaitTimeout,
  status,
  stubControllerComponents
}
import play.api.test.{FakeRequest, Injecting}
import services.UserService

import java.time.LocalDateTime
import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}

class UserControllerSpec
  extends PlaySpec
  with GuiceOneAppPerTest
  with Injecting
  with MockitoSugar {

  implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.global

  "UserController#createUser" should {

    "return 201 Created when user is created successfully" in {
      val mockUserService = mock[UserService]
      val controller =
        new UserController(stubControllerComponents(), mockUserService)

      val requestDto =
        CreateUserRequestDto("test@email.com", "123456", Some(25))
      val responseDto = UserResponseDto(
        UUID.randomUUID(),
        "test@email.com",
        Some(25),
        isActive = true,
        LocalDateTime.now(),
        LocalDateTime.now()
      )

      when(mockUserService.createUser(any()))
        .thenReturn(Future.successful(responseDto))

      val requestBody: JsValue = Json.toJson(requestDto)
      val request = FakeRequest(POST, "/api/users")
        .withBody(requestBody)
        .withHeaders("Content-Type" -> "application/json")

      val result = call(controller.createUser, request)

      status(result) mustBe CREATED
      (contentAsJson(result) \ "success").as[Boolean] mustBe true
      (contentAsJson(result) \ "data" \ "email")
        .as[String] mustBe "test@email.com"
    }

    "return 400 BadRequest when validation fails" in {
      val mockUserService = mock[UserService]
      val controller =
        new UserController(stubControllerComponents(), mockUserService)

      val invalidJson =
        Json.parse("""{ "email": "", "password": "123", "age": -1 }""")

      val request = FakeRequest(POST, "/api/users")
        .withBody(invalidJson)
        .withHeaders("Content-Type" -> "application/json")

      val result = call(controller.createUser, request)

      status(result) mustBe BAD_REQUEST
      (contentAsJson(result) \ "success").as[Boolean] mustBe false
      (contentAsJson(result) \ "message").as[String] must include(
        "Validation failed"
      )
    }

    "return conflict 409 when email already exists" in {
      val mockUserService = mock[UserService]
      val controller =
        new UserController(stubControllerComponents(), mockUserService)

      val requestDto =
        CreateUserRequestDto("duplicate@email.com", "123456", Some(25))

      when(mockUserService.createUser(any()))
        .thenReturn(
          Future.failed(
            new AppException(ErrorCode.EmailAlreadyExists, CONFLICT)
          )
        )

      val requestBody = Json.toJson(requestDto)
      val request = FakeRequest(POST, "/api/users")
        .withBody(requestBody)
        .withHeaders("Content-Type" -> "application/json")

      val result = call(controller.createUser, request)

      status(result) mustBe CONFLICT
      (contentAsJson(result) \ "success").as[Boolean] mustBe false
      (contentAsJson(result) \ "message").as[String] mustBe ErrorCode.EmailAlreadyExists.message
    }
  }
}
