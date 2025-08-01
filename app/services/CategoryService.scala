package services

import dtos.request.category.{
  CreateCategoryRequestDto,
  UpdateCategoryRequestDto
}
import dtos.response.category.CategoryResponseDto
import exceptions.{AppException, ErrorCode}
import models.Category
import play.api.http.Status
import repositories.CategoryRepository
import utils.UuidSupport

import java.time.LocalDateTime
import java.util.UUID
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CategoryService @Inject() (categoryRepository: CategoryRepository)(
  implicit ec: ExecutionContext
) extends UuidSupport {

  def createCategory(request: CreateCategoryRequestDto): Future[Int] = {
    categoryRepository.existsByNameAndIsDeleteFalse(request.name).flatMap {
      exists =>
        if (exists) {
          Future.failed(
            new AppException(
              ErrorCode.CategoryNameAlreadyExits,
              Status.CONFLICT
            )
          )
        } else {
          val now = LocalDateTime.now()
          val category = Category(
            id = UUID.randomUUID(),
            name = request.name,
            createdAt = now,
            updatedAt = now,
            createdBy = UUID.randomUUID(),
            updatedBy = UUID.randomUUID(),
            isDeleted = false
          )
          categoryRepository.create(category)
        }
    }
  }

  def getAllCategories: Future[Seq[CategoryResponseDto]] =
    categoryRepository.findAll()

  def updateCategory(
    id: String,
    request: UpdateCategoryRequestDto
  ): Future[Any] = {
    withUuid(id) {
      uuid =>
        categoryRepository.update(uuid, request.name).flatMap {
          case 0 =>
            Future.failed(
              new AppException(ErrorCode.CategoryNotFound, Status.NOT_FOUND)
            )
          case _ =>
            Future.successful(())
        }

    }
  }

  def deleteCategory(
    id: String
  ): Future[Any] = {
    withUuid(id) {
      uuid =>
        categoryRepository.softDelete(uuid).flatMap {
          case 0 =>
            Future.failed(
              new AppException(ErrorCode.CategoryNotFound, Status.NOT_FOUND)
            )
          case _ =>
            Future.successful(())
        }

    }
  }

}
