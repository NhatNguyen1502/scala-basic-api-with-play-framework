package services

import dtos.request.product.{CreateProductRequestDto, UpdateProductRequestDto}
import dtos.response.PagedResponse
import dtos.response.product.{CategoryShortDto, ProductWithCategoryResponseDto}
import exceptions.{AppException, ErrorCode}
import models.Product
import play.api.http.Status
import repositories.{CategoryRepository, ProductRepository}
import utils.UUIDUtils.parseUUID
import utils.UuidSupport

import java.io.File
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ProductService @Inject() (
  productRepository: ProductRepository,
  categoryRepository: CategoryRepository,
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

  def updateProduct(
    id: String,
    dto: UpdateProductRequestDto,
    imageFile: Option[File],
    userId: String
  ): Future[Either[String, Int]] = {
    val productUUID = parseUUID(id)
    val userUUID = parseUUID(userId)
    for {
      // 1. Check product tồn tại và chưa bị xóa
      productExists <- productRepository.existsByIdAndIsDeletedFalse(
        productUUID
      )
      _ <-
        if (!productExists)
          Future.failed(
            new AppException(ErrorCode.ProductNotFound, Status.BAD_REQUEST)
          )
        else Future.unit

      // 2. Check category nếu có
      _ <- dto.categoryId match {
        case Some(catId) =>
          categoryRepository.existsByIdAndIsDeleteFalse(catId).flatMap {
            case false =>
              Future.failed(
                new AppException(
                  ErrorCode.CategoryNotFound,
                  Status.BAD_REQUEST
                )
              )
            case true => Future.unit
          }
        case None => Future.unit
      }

      // 3. Upload ảnh mới nếu có
      imageUrl <- imageFile match {
        case Some(file) => cloudinaryService.uploadImage(file)
        case None       => Future.successful("")
      }

      // 4. Update product
      updated <- productRepository.update(
        productUUID,
        dto,
        if (imageUrl.nonEmpty) Some(imageUrl) else None,
        userUUID
      )
    } yield Right(updated)
  }.recover {
    case ex: Exception => Left(ex.getMessage)
  }

}
