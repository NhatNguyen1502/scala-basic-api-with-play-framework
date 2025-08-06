package services

import dtos.request.product.CreateProductRequestDto
import dtos.response.PagedResponse
import dtos.response.product.{CategoryShortDto, ProductWithCategoryResponseDto}
import models.Product
import repositories.ProductRepository
import utils.UuidSupport

import java.io.File
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ProductService @Inject() (
  productRepository: ProductRepository,
  cloudinaryService: CloudinaryService
)(implicit ex: ExecutionContext)
    extends UuidSupport {

  def createProduct(
    request: CreateProductRequestDto,
    productImage: File,
    creatorId: String
  ): Future[Int] = {
    withUuid(creatorId) {
      creatorId =>
        cloudinaryService.uploadImage(productImage).flatMap {
          imageUrl =>
            val product = Product(
              id = UUID.randomUUID(),
              name = request.name,
              description = request.description,
              price = request.price,
              categoryId = request.categoryId,
              quantity = request.quantity,
              isFeatured = request.isFeatured,
              imageUrl = imageUrl,
              createdBy = creatorId,
              updatedBy = creatorId,
              createdAt = LocalDateTime.now()
            )
            productRepository.create(product)
        }

    }
  }

  def getAllProducts(
    page: Int,
    size: Int
  ): Future[PagedResponse[ProductWithCategoryResponseDto]] = {
    productRepository.findAllWithCategory(page, size).map {
      case (rows, total) =>
        val dtoList = rows.map {
          case (p, c) =>
            ProductWithCategoryResponseDto(
              id = p.id,
              name = p.name,
              description = p.description,
              imageUrl = p.imageUrl,
              price = p.price,
              category = CategoryShortDto(c.id, c.name),
              quantity = p.quantity,
              isFeatured = p.isFeatured,
              createdAt = p.createdAt,
              updatedAt = p.updatedAt,
              createdBy = p.createdBy,
              updatedBy = p.updatedBy
            )
        }
        PagedResponse(
          dtoList,
          page,
          size,
          total,
          total / size + (if (total % size > 0) 1 else 0)
        )
    }
  }

}
