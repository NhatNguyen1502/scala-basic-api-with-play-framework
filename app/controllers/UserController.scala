package controllers

import dtos.request.user.{CreateUserRequestDto, UpdateUserRequestDto}
import dtos.response.ApiResponse
import play.api.libs.json._
import play.api.mvc._
import services.UserService
import validations.ValidationHandler

import javax.inject._
import scala.concurrent.ExecutionContext
import utils.json.WritesExtras.unitWrites

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

  def updateUser(id: String): Action[JsValue] = Action.async(parse.json) {
    request =>
      handleJsonValidation[UpdateUserRequestDto](request.body) {
        user => userService.updateUser(id, user).map { _ =>
          Ok(Json.toJson(ApiResponse[Unit](success = true, message = "User updated successfully")))
        }
      }
  }

  def deleteUser(id: String): Action[AnyContent] = Action.async {
    userService.deleteUser(id).map { _ =>
      val response = ApiResponse[Unit](
        success = true,
        message = "User deleted successfully"
      )
      Ok(Json.toJson(response))
    }
  }
}
