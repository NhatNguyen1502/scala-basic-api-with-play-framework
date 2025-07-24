package models

import slick.jdbc.PostgresProfile.api._
import java.time.LocalDateTime

class RoleTable(tag: Tag) extends Table[Role](tag, "roles") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")
  def description = column[Option[String]]("description")
  def createdAt = column[LocalDateTime]("created_at")
  def updatedAt = column[LocalDateTime]("updated_at")

  def * = (id, name, description, createdAt, updatedAt) <> (
    (Role.apply _).tupled,
    Role.unapply
  )
}
