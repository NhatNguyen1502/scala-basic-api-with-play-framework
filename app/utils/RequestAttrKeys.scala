package utils

import play.api.libs.typedmap.TypedKey

/**
 * Keys for attributes stored in the request.
 * These keys are used to store and retrieve data from the request attributes.
 * They are typically used to pass data between filters and actions.
 */
object RequestAttrKeys {
  val JwtClaim: TypedKey[String] = TypedKey[String]("jwtClaim")
}
