package repositories

import jakarta.inject.Inject
import models.Tables.categories
import models.{Category, Product, Tables}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

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

    val paged = baseQuery.drop(page * size).take(size)

    for {
      total <- db.run(baseQuery.length.result)
      data <- db.run(paged.result)
    } yield (data, total)
  }

}
