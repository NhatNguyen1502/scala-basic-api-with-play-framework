package controllers

import dtos.request.auth.{LoginRequestDto, SignUpRequestDto}
import dtos.response.ApiResponse
import dtos.response.auth.LoginResponseDto
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{AbstractController, Action, ControllerComponents}
import services.AuthService
import utils.json.WritesExtras.unitWrites
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

  def signUp: Action[JsValue] = Action.async(parse.json) {
    request =>
      handleJsonValidation[SignUpRequestDto](request.body) {
        signUp =>
          authService.signUp(signUp).map {
            _ =>
              Ok(
                Json.toJson(
                  ApiResponse[Unit](
                    success = true,
                    message = "Sign up successfully"
                  )
                )
              )
          }
      }
  }
}
