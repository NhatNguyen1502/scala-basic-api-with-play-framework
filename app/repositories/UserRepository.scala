package repositories

import dtos.response.user.UserResponseDto
import models.{User, UserTable}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import java.util.UUID
import javax.inject._
import scala.concurrent.{ExecutionContext, Future}

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

  def list(): Future[Seq[UserResponseDto]] = {
    db.run(
      users
        .map(
          u => (u.id, u.email, u.age, u.isActive, u.createdAt, u.updatedAt)
        ) // Projection
        .sortBy(_._5.desc)
        .result
    ).map(_.map(UserResponseDto.tupled))
  }

  def findById(id: UUID): Future[Option[UserResponseDto]] = {
    db.run(
      users
        .filter(_.id === id)
        .map(
          u => (u.id, u.email, u.age, u.isActive, u.createdAt, u.updatedAt)
        )
        .result
        .headOption
    ).map(_.map(UserResponseDto.tupled))
  }
}
