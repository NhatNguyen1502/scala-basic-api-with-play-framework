package controllers

import Exceptions.ErrorCode
import dtos.request.user.CreateUserRequestDto
import dtos.response.user.UserResponseDto
import models.User
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import play.api.Play.materializer
import play.api.http.Status.{BAD_REQUEST, CONFLICT, CREATED, NOT_FOUND, OK}
import play.api.libs.json.{JsValue, Json}
import play.api.test.Helpers.{GET, POST, call, contentAsJson, defaultAwaitTimeout, status, stubControllerComponents}
import play.api.test.{FakeRequest, Injecting}
import repositories.UserRepository
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

  val mockUserRepository: UserRepository = mock[UserRepository]
  val userService = new UserService(mockUserRepository)
  val controller =
    new UserController(stubControllerComponents(), userService)

  "UserController#createUser" should {

    "return 201 Created when user is created successfully" in {
      val requestDto =
        CreateUserRequestDto("test@email.com", "123456", Some(25))

      val newUser = User(
        UUID.randomUUID(),
        "test@email.com",
        "12345",
        Some(25),
        isActive = true,
        LocalDateTime.now(),
        LocalDateTime.now()
      )

      when(mockUserRepository.existByEmail(any()))
        .thenReturn(Future.successful(false))
      when(mockUserRepository.create(any()))
        .thenReturn(Future.successful(newUser))

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
      val requestDto =
        CreateUserRequestDto("duplicate@email.com", "123456", Some(25))

      when(mockUserRepository.existByEmail(any()))
        .thenReturn(Future.successful(true))

      val requestBody = Json.toJson(requestDto)
      val request = FakeRequest(POST, "/api/users")
        .withBody(requestBody)
        .withHeaders("Content-Type" -> "application/json")

      val result = call(controller.createUser, request)

      status(result) mustBe CONFLICT
      (contentAsJson(result) \ "success").as[Boolean] mustBe false
      (contentAsJson(result) \ "message")
        .as[String] mustBe ErrorCode.EmailAlreadyExists.message
    }
  }

  "UserController#getListUsers" should {

    "return 200 OK with user list in JSON" in {
      val users = Seq(
        UserResponseDto(
          id = UUID.randomUUID(),
          email = "test@example.com",
          age = Some(25),
          isActive = true,
          createdAt = LocalDateTime.now(),
          updatedAt = LocalDateTime.now()
        )
      )

      when(mockUserRepository.list()).thenReturn(Future.successful(users))

      val result = controller.getListUsers.apply(FakeRequest(GET, "/api/users"))

      status(result) mustBe OK

      val json = contentAsJson(result)
      (json \ "success").as[Boolean] mustBe true
      (json \ "message").as[String] must include("List users fetched")
      (json \ "data").as[Seq[UserResponseDto]] mustBe users
    }
  }

  "UserController#getUserById" should {

    "return 200 OK with user in JSON" in {
      val userId = UUID.randomUUID();

      val user =
        UserResponseDto(
          id = userId,
          email = "test@example.com",
          age = Some(25),
          isActive = true,
          createdAt = LocalDateTime.now(),
          updatedAt = LocalDateTime.now()
        )

      when(mockUserRepository.findById(userId)).thenReturn(Future.successful(Some(user)))

      val result = controller.getUserById(userId.toString).apply(FakeRequest(GET, s"/api/users/${userId}"))

      status(result) mustBe OK

      val json = contentAsJson(result)
      (json \ "success").as[Boolean] mustBe true
      (json \ "message").as[String] mustBe ("User fetched")
      (json \ "data").as[UserResponseDto] mustBe user
    }

    "return 404 NotFound with user not found" in {
      val userId = UUID.randomUUID();

      when(mockUserRepository.findById(userId)).thenReturn(Future.successful(None))

      val result = controller.getUserById(userId.toString).apply(FakeRequest(GET, s"/api/users/${userId}"))

      status(result) mustBe NOT_FOUND

      val json = contentAsJson(result)
      (json \ "success").as[Boolean] mustBe false
      (json \ "message").as[String] mustBe ("User not found")
    }

    "return 400 BadRequest with user not found" in {
      val userId = "NotUUID";

      val result = controller.getUserById(userId.toString).apply(FakeRequest(GET, s"/api/users/${userId}"))

      status(result) mustBe BAD_REQUEST

      val json = contentAsJson(result)
      (json \ "success").as[Boolean] mustBe false
      (json \ "message").as[String] mustBe ("Id must be UUID")
    }
  }
}
