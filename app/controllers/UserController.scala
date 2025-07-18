package controllers

import dtos.request.user.CreateUserRequestDto
import dtos.response.{ErrorResponse, SuccessResponse}

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

  def createUser: Action[JsValue] = Action.async(parse.json) { request =>
    request.body.validate[CreateUserRequestDto].fold(
      // Handle validation errors
      errors => {
        val errorJson = JsError.toJson(errors)
        val errorResponse = ErrorResponse(
          message = "Validation failed",
          errors = Some(errorJson)
        )
        Future.successful(BadRequest(Json.toJson(errorResponse)))
      },

      // Validation passed → call userService
      user =>
        userService.createUser(user).map { createdUser =>
          val successResponse = SuccessResponse(
            message = "User created successfully",
            data = Some(createdUser)
          )
          Created(Json.toJson(successResponse))
        }
    )
  }

  def getListUsers: Action[AnyContent] = Action.async {
    userService.getAllUsers.map { users =>
      val successResponse = SuccessResponse(
        message = "Fetched all users",
        data = Some(users)
      )
      Ok(Json.toJson(successResponse))
      }
  }
}