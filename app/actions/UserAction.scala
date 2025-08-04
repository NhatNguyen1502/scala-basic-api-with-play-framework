package actions

import play.api.libs.json.Json
import play.api.mvc.{
  ActionBuilder,
  ActionTransformer,
  AnyContent,
  BodyParsers,
  Request
}
import utils.RequestAttrKeys

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

/**
 * This is an action function (an intermediate component between the original
 * request and the controller).
 *
 * Its responsibilities are:
 *
 * Retrieve the JwtClaim data that has been attached to the request by the
 * AuthFilter.
 *
 * Parse the claim as JSON and extract the userId and role.
 *
 * Create a new request (UserRequest) that contains the additional user
 * information.
 *
 * Forward this new request to the controller.
 *
 * example usage:
 * {{{
 * class CategoryController @Inject()(cc: ControllerComponents, userAction: UserAction)
 *   extends AbstractController(cc) {
 *
 *   def createCategory = userAction.async(parse.json) { request =>
 *     val userId = request.userId
 *     val role   = request.role
 *
 *     // Perform logic using userId, role, and request body
 *     Future.successful(Ok(s"Category created by user: $userId with role: $role"))
 *   }
 * }
 * }}}
 */
class UserAction @Inject() (val parser: BodyParsers.Default)(implicit
  val executionContext: ExecutionContext
) extends ActionBuilder[UserRequest, AnyContent]
    with ActionTransformer[Request, UserRequest] {
  def transform[A](request: Request[A]): Future[UserRequest[A]] =
    Future.successful {
      val rawClaim = request.attrs
        .get(RequestAttrKeys.JwtClaim)
        .getOrElse("""{"userId": "", "role": ""}""")
      val json = Json.parse(rawClaim)
      val userId = (json \ "userId").asOpt[String].getOrElse("")
      val role = (json \ "role").asOpt[String].getOrElse("")
      UserRequest(userId, role, request)
    }
}
