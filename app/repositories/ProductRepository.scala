package repositories

import jakarta.inject.Inject
import models.{Tables, Product}
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

}
