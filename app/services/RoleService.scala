package services

import models.Role
import repositories.RoleRepository

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class RoleService @Inject() (roleRepository: RoleRepository)(implicit
  ec: ExecutionContext
) {
  def findByName(name: String): Future[Role] = {
    roleRepository.findByName(name)
  }
}
