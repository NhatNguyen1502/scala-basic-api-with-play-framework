package models

import slick.jdbc.PostgresProfile.api._

object Tables {
  lazy val roles = TableQuery[RoleTable]
  lazy val users = TableQuery[UserTable]
  lazy val categories = TableQuery[CategoryTable]
  lazy val products = TableQuery[ProductTable]
}
