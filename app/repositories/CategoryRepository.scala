package repositories

import dtos.response.category.CategoryResponseDto
import models.{Category, Tables}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import java.time.LocalDateTime
import java.util.UUID
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CategoryRepository @Inject() (
  protected val dbConfigProvider: DatabaseConfigProvider
)(implicit ec: ExecutionContext)
    extends HasDatabaseConfigProvider[JdbcProfile] {
  private val categories = Tables.categories

  import profile.api._

  def findAll(): Future[Seq[CategoryResponseDto]] =
    db.run(
      categories
        .filter(_.isDeleted === false)
        .map(
          c =>
            (c.id, c.name, c.createdAt, c.updatedAt, c.createdBy, c.updatedBy)
        )
        .sortBy(_._3.desc)
        .result
    ).map(_.map(CategoryResponseDto.tupled))

  def findById(id: UUID): Future[Option[Category]] =
    db.run(
      categories
        .filter(
          c => c.id === id && c.isDeleted === false
        )
        .result
        .headOption
    )

  def create(category: Category): Future[Int] =
    db.run(categories += category)

  def existsByNameAndIsDeleteFalse(name: String): Future[Boolean] = {
    val query = categories
      .filter(
        c => c.name === name && c.isDeleted === false
      )
      .exists

    db.run(query.result)
  }

  def existsByIdAndIsDeleteFalse(id: UUID): Future[Boolean] = {
    val query = categories
      .filter(
        c => c.id === id && c.isDeleted === false
      )
      .exists

    db.run(query.result)
  }

  def update(id: UUID, newName: String, updatedBy: UUID): Future[Int] = {
    val now = LocalDateTime.now()
    db.run(
      categories
        .filter(_.id === id)
        .map(
          c => (c.name, c.updatedAt, c.updatedBy)
        )
        .update((newName, now, updatedBy))
    )
  }

  def softDelete(id: UUID, deletedBy: UUID): Future[Int] = {
    val now = LocalDateTime.now()
    val query = categories
      .filter(_.id === id)
      .map(
        c => (c.isDeleted, c.updatedAt, c.updatedBy)
      )
      .update(true, now, deletedBy)
    db.run(query)
  }
}
