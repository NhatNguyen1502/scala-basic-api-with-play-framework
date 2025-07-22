package controllers

import exceptions.AppException
import dtos.request.user.CreateUserRequestDto
import dtos.response.{ApiResponse, FieldError}
import play.api.libs.json._
import play.api.mvc._
import services.UserService
import utils.json.WritesExtras._

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UserController @Inject() (
  cc: ControllerComponents,
  userService: UserService
)(implicit ec: ExecutionContext)
    extends AbstractController(cc) {

  def createUser: Action[JsValue] = Action.async(parse.json) {
    request =>
      request.body
        .validate[CreateUserRequestDto]
        .fold(
          // Validate request fail
          errors => {
            val fieldErrors = errors.flatMap {
              case (path, validationErrors) =>
                validationErrors.map(
                  e => FieldError(path.toString().substring(1), e.message)
                )
            }.toList
            val response: ApiResponse[Unit] = ApiResponse(
              success = false,
              message = "Validation failed",
              errors = Some(fieldErrors)
            )
            Future.successful(BadRequest(Json.toJson(response)))
            // Note: Json.toJson(response) need import "unitWrites" in WritesExtras to serialize Unit
          },

          // Validate request success
          userDto => {
            userService
              .createUser(userDto)
              .map {
                createdUser =>
                  val response = ApiResponse(
                    success = true,
                    message = "User created successfully",
                    data = Some(Json.toJson(createdUser))
                  )
                  Created(Json.toJson(response))
              }
          }
        )
  }

  def getListUsers: Action[AnyContent] = Action.async {
    userService.getAllUsers.map {
      users =>
        val response = ApiResponse(
          success = true,
          message = "List users fetched",
          data = Some(Json.toJson(users))
        )
        Ok(Json.toJson(response))
    }
  }

  def getUserById(id: String): Action[AnyContent] = Action.async {
    userService
      .findById(id)
      .map {
        user =>
          val response = ApiResponse(
            success = true,
            message = "User fetched",
            data = Some(Json.toJson(user))
          )
          Ok(Json.toJson(response))
      }
  }
}
