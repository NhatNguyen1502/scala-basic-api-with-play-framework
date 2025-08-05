package models

import slick.jdbc.PostgresProfile.api._

import java.time.LocalDateTime
import java.util.UUID

class ProductTable(tag: Tag) extends Table[Product](tag, "products") {
  def id = column[UUID]("id", O.PrimaryKey)
  def name = column[String]("name")
  def description = column[Option[String]]("description")
  def imageUrl = column[String]("image_url")
  def price = column[BigDecimal]("price")
  def categoryId = column[UUID]("category_id")
  def quantity = column[Int]("quantity")
  def isFeatured = column[Boolean]("is_featured")
  def createdAt = column[LocalDateTime]("created_at")
  def updatedAt = column[LocalDateTime]("updated_at")
  def createdBy = column[UUID]("created_by")
  def updatedBy = column[UUID]("updated_by")
  def isDeleted = column[Boolean]("is_deleted")

  def * = (
    id,
    name,
    description,
    imageUrl,
    price,
    categoryId,
    quantity,
    isFeatured,
    createdAt,
    updatedAt,
    createdBy,
    updatedBy,
    isDeleted
  ) <> (
    (Product.apply _).tupled,
    Product.unapply
  )
}
