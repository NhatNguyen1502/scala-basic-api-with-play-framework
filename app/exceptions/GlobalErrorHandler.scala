package exceptions

import dtos.response.ApiResponse
import play.api.http._
import play.api.libs.json._
import play.api.mvc._

import javax.inject.Singleton
import scala.concurrent._

@Singleton
class GlobalErrorHandler extends HttpErrorHandler {

  override def onClientError(
    request: RequestHeader,
    statusCode: Int,
    message: String
  ): Future[Result] = {
    val defaultMessage =
      if (message.trim.nonEmpty) message
      else
        statusCode match {
          case Status.NOT_FOUND    => "Not Found"
          case Status.BAD_REQUEST  => "Bad Request"
          case Status.UNAUTHORIZED => "Unauthorized"
          case Status.FORBIDDEN    => "Forbidden"
          case _                   => "Client Error"
        }

    val response = ApiResponse[JsValue](
      success = false,
      message = defaultMessage
    )

    Future.successful(
      Results.Status(statusCode)(Json.toJson(response))
    )
  }

  override def onServerError(
    request: RequestHeader,
    exception: Throwable
  ): Future[Result] = {
    val (status, message) = exception match {
      case ex: AppException => (ex.httpStatus, ex.getMessage)
      case ex2              =>
        (
          Status.INTERNAL_SERVER_ERROR,
          "Internal server error: " + ex2.getMessage
        )
    }

    val response = ApiResponse[JsValue](
      success = false,
      message = message
    )

    Future.successful(Results.Status(status)(Json.toJson(response)))
  }
}
