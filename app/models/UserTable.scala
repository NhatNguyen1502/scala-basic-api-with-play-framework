package models

import java.time.LocalDateTime
import java.util.UUID
import slick.jdbc.PostgresProfile.api._

class UserTable(tag: Tag) extends Table[User](tag, "users") {
  def id = column[UUID]("id", O.PrimaryKey)
  def email = column[String]("email")
  def password = column[String]("password")
  def age = column[Option[Int]]("age")
  def isActive = column[Boolean]("is_active")
  def roleId = column[Int]("role_id")
  def createdAt = column[LocalDateTime]("created_at")
  def updatedAt = column[LocalDateTime]("updated_at")

  def roleFk = foreignKey("fk_user_role", roleId, Tables.roles)(
    _.id,
    onDelete = ForeignKeyAction.Restrict
  )

  def * = (
    id,
    email,
    password,
    age,
    isActive,
    roleId,
    createdAt,
    updatedAt
  ) <> ((User.apply _).tupled, User.unapply)
}
