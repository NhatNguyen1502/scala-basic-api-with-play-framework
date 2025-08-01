package models

import slick.jdbc.PostgresProfile.api._

import java.time.LocalDateTime
import java.util.UUID

class CategoryTable(tag: Tag) extends Table[Category](tag, "categories") {
  def id = column[UUID]("id", O.PrimaryKey)

  def name = column[String]("name")

  def createdAt = column[LocalDateTime]("created_at")

  def updatedAt = column[LocalDateTime]("updated_at")

  def createdBy = column[UUID]("create_by")

  def updatedBy = column[UUID]("update_by")

  def isDeleted = column[Boolean]("is_delete")

  def * = (id, name, createdAt, updatedAt, createdBy, updatedBy, isDeleted) <> (
    (Category.apply _).tupled,
    Category.unapply
  )
}
