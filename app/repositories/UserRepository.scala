package repositories

import slick.jdbc.JdbcProfile
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}
import models.{User, UserTable}

@Singleton
class UserRepository @Inject() (
  protected val dbConfigProvider: DatabaseConfigProvider
)(implicit ec: ExecutionContext)
    extends HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  private val users = TableQuery[UserTable]

  def create(user: User): Future[User] = {
    // db is provided by HasDatabaseConfigProvider
    db.run(users += user) // Future[Int] . success = 1, fail = 0
      .map(
        _ => user
      ) // convert to Future[User]
  }
  def existByEmail(email: String): Future[Boolean] = {
    val query = users.filter(_.email === email).exists.result
    db.run(query)
  }

  def list(): Future[Seq[User]] = db.run(users.result)
}
