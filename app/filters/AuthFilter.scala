package filters

import pdi.jwt.{Jwt, JwtAlgorithm}
import play.api.Configuration
import play.api.libs.json.Json
import play.api.libs.streams.Accumulator
import play.api.mvc._

import javax.inject._

@Singleton
class AuthFilter @Inject() (
  config: Configuration
) extends EssentialFilter {

  // List of endpoints that do not require authentication
  private val publicPaths: Set[String] = Set(
    "/api/login"
  )

  private val secretKey = config.get[String]("jwt.secret.key")

  /**
   * The `apply` method of EssentialFilter:
   *   - Takes an EssentialAction (the controller action)
   *   - Returns a new EssentialAction with filter logic
   */
  override def apply(next: EssentialAction): EssentialAction = EssentialAction {
    request =>
      // Allow public endpoints to bypass authentication
      if (isPublic(request)) {
        next(request)
      } else {
        // Extract token from Authorization header
        extractToken(request) match {
          case Some(token)
              if Jwt.isValid(token, secretKey, Seq(JwtAlgorithm.HS256)) =>
            // Token is valid -> continue to the controller
            next(request)

          case _ =>
            // Invalid or missing token -> return 401 Unauthorized immediately
            // Accumulator.done() stops request processing early (no request body parsing)
            Accumulator.done(
              Results.Unauthorized(
                Json.obj("error" -> "Invalid or missing token")
              )
            )
        }
      }
  }

  /** Check if request path is in the list of public endpoints */
  private def isPublic(request: RequestHeader): Boolean = {
    val path = request.path
    (path == "/api/users" && request.method == "POST") || // temporary use to create an account
    publicPaths.contains(path) ||
    path.startsWith("/docs/swagger") ||
    path.startsWith("/assets") ||
    path.startsWith("/auth")
  }

  /** Extract JWT token from Authorization header */
  private def extractToken(request: RequestHeader): Option[String] =
    request.headers.get("Authorization").flatMap {
      header =>
        if (header.startsWith("Bearer ")) Some(header.substring(7)) else None
    }
}
