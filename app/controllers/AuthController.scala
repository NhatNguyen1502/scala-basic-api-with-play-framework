package controllers

import dtos.request.auth.LoginRequestDto
import dtos.response.ApiResponse
import dtos.response.auth.LoginResponseDto
//import dtos.response.auth.LoginResponseDto._

import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{AbstractController, Action, ControllerComponents}
import services.AuthService
import validations.ValidationHandler

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class AuthController @Inject() (
  cc: ControllerComponents,
  authService: AuthService
)(implicit ec: ExecutionContext)
    extends AbstractController(cc)
    with ValidationHandler {

  def login: Action[JsValue] = Action.async(parse.json) {
    request =>
      handleJsonValidation[LoginRequestDto](request.body) {
        loginRequestDto =>
          authService.authenticate(loginRequestDto).map {
            loginResponseDto: LoginResponseDto =>
              val response = ApiResponse(
                success = true,
                message = "Login successfully",
                data = Some(Json.toJson(loginResponseDto))
              )
              Ok(Json.toJson(response))
          }
      }
  }
}
