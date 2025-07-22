package controllers

import dtos.request.user.CreateUserRequestDto
import dtos.response.ApiResponse
import play.api.libs.json._
import play.api.mvc._
import services.UserService
import validations.ValidationHandler

import javax.inject._
import scala.concurrent.ExecutionContext

@Singleton
class UserController @Inject() (
  cc: ControllerComponents,
  userService: UserService
)(implicit ec: ExecutionContext)
    extends AbstractController(cc)
    with ValidationHandler {

  def createUser: Action[JsValue] = Action.async(parse.json) {
    request =>
      handleJsonValidation[CreateUserRequestDto](request.body) {
        userDto =>
          userService.createUser(userDto).map {
            createdUser =>
              val response = ApiResponse(
                success = true,
                message = "User created successfully",
                data = Some(Json.toJson(createdUser))
              )
              Created(Json.toJson(response))
          }
      }
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
