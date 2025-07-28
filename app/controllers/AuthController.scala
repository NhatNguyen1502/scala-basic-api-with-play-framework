package controllers

import dtos.request.authen.LoginRequestDto
import dtos.response.ApiResponse
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{AbstractController, Action, ControllerComponents}
import services.{AuthService, JwtService}
import validations.ValidationHandler

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

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
            token =>
              val response = ApiResponse(
                success = true,
                message = "Login successfully",
                data = Some(Json.toJson(token))
              )
              Ok(Json.toJson(response))
          }
      }
  }
}
