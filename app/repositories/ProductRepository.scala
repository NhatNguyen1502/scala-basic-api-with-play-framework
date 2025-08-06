package repositories

import dtos.request.product.UpdateProductRequestDto
import jakarta.inject.Inject
import models.Tables.categories
import models.{Category, Product, Tables}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import java.time.LocalDateTime
import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}

class ProductRepository @Inject() (
  protected val dbConfigProvider: DatabaseConfigProvider
)(implicit ec: ExecutionContext)
    extends HasDatabaseConfigProvider[JdbcProfile] {

  private val products = Tables.products
  import profile.api._

  def create(product: Product): Future[Int] = {
    db.run(products += product)
  }

  def findAllWithCategory(
    page: Int,
    size: Int
  ): Future[(Seq[(Product, Category)], Int)] = {
    val baseQuery = products
      .filter(_.isDeleted === false)
      .join(categories.filter(_.isDeleted === false))
      .on(_.categoryId === _.id)

    val sorted = baseQuery.sortBy {
      case (product, _) => product.createdAt.desc
    }

    val paged = sorted.drop(page * size).take(size)

    for {
      total <- db.run(baseQuery.length.result)
      data <- db.run(paged.result)
    } yield (data, total)
  }

  def existsByIdAndIsDeletedFalse(id: UUID): Future[Boolean] = {
    db.run(
      products
        .filter(
          p => p.id === id && !p.isDeleted
        )
        .exists
        .result
    )
  }

  def update(
    id: UUID,
    dto: UpdateProductRequestDto,
    imageUrl: Option[String],
    userId: UUID
  ): Future[Int] = {
    val now = LocalDateTime.now()

    val updateQuery = products.filter(_.id === id)

    db.run(updateQuery.result.headOption).flatMap {
      case Some(existing) =>
        val updated = existing.copy(
          name = dto.name.getOrElse(existing.name),
          description = dto.description.orElse(existing.description),
          price = dto.price.getOrElse(existing.price),
          categoryId = dto.categoryId.getOrElse(existing.categoryId),
          quantity = dto.quantity.getOrElse(existing.quantity),
          isFeatured = dto.isFeatured.getOrElse(existing.isFeatured),
          imageUrl = imageUrl.getOrElse(existing.imageUrl),
          updatedBy = userId,
          updatedAt = now
        )
        db.run(updateQuery.update(updated))
      case None => Future.successful(0)
    }
  }

  def softDelete(id: UUID, deletedBy: UUID): Future[Int] = {
    val now = LocalDateTime.now()
    val query = products
      .filter(_.id === id)
      .map(
        c => (c.isDeleted, c.updatedAt, c.updatedBy)
      )
      .update(true, now, deletedBy)
    db.run(query)
  }

}
