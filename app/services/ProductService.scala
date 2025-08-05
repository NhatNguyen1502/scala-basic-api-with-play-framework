package services

import dtos.request.product.CreateProductRequestDto
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

}
