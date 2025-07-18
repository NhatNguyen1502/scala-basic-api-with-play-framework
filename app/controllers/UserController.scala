package controllers

import dtos.request.user.CreateUserRequestDto

import javax.inject._
import play.api.mvc._
import play.api.libs.json._
import services.UserService

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UserController @Inject()(
                                cc: ControllerComponents,
                                userService: UserService
                              )(implicit ec: ExecutionContext) extends AbstractController(cc) {

  /**
   This action handles the creation of a new user.
   It expects a JSON request body and attempts to parse it into CreateUserRequestDto.
   - If the JSON is invalid (fails validation): return 400 BadRequest with error details.
   - If the JSON is valid:
    - Call userService.createUser with the parsed request data.
    - When the service returns the created user, respond with 201 Created and the user data as JSON.
  */
  def createUser: Action[JsValue] = Action.async(parse.json) { request =>
    request.body.validate[CreateUserRequestDto].fold(
      // Handle validation errors
      errors => Future.successful(BadRequest(Json.obj("error" -> JsError.toJson(errors)))),

      // Validation passed → call userService
      user =>
        userService.createUser(user).map { createdUser =>
          // Return 201 Created with the response DTO as JSON
          Created(Json.toJson(createdUser))
        }
    )
  }

  def getListUsers: Action[AnyContent] = Action.async {
    userService.getAllUsers.map(users => Ok(Json.toJson(users)))
  }
}