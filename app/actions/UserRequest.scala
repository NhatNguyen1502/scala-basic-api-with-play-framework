package actions

import play.api.mvc.{Request, WrappedRequest}

/**
 * Represents a request that contains user information. This class extends
 * WrappedRequest to include userId and role.
 *
 * @param userId
 *   The ID of the user making the request.
 * @param role
 *   The role of the user (e.g., "admin", "user").
 * @param request
 *   The original request object.
 * @tparam A
 *   The type of the body of the request.
 */
case class UserRequest[A](userId: String, role: String, request: Request[A])
    extends WrappedRequest[A](request)
