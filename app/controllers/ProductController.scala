package controllers

import actions.UserAction
import dtos.request.product.{CreateProductRequestDto, UpdateProductRequestDto}
import dtos.response.ApiResponse
import play.api.libs.Files.TemporaryFile
import play.api.libs.json.Json
import play.api.mvc.{
  AbstractController,
  Action,
  AnyContent,
  ControllerComponents,
  MultipartFormData
}
import services.ProductService
import utils.json.WritesExtras.unitWrites
import validations.ValidationHandler

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import scala.io.Source
import scala.util.Using

class ProductController @Inject() (
  cc: ControllerComponents,
  productService: ProductService,
  userAction: UserAction
)(implicit ec: ExecutionContext)
    extends AbstractController(cc)
    with ValidationHandler {

  def createProduct: Action[MultipartFormData[TemporaryFile]] =
    userAction.async(parse.multipartFormData) {
      request =>
        val userId = request.userId

        // Get file JSON product
        request.body.file("product") match {
          case Some(productFile) =>
            // Read JSON from temporary file
            val jsonContent =
              Using.resource(Source.fromFile(productFile.ref.path.toFile)) {
                source =>
                  source.mkString
              }

            // Parse to JsValue
            val json = Json.parse(jsonContent)

            // Validate JSON and map to CreateProductRequestDto
            handleJsonValidation[CreateProductRequestDto](json) {
              createProductDto =>
                // continue read image file from request
                request.body.file("image") match {
                  case Some(image) =>
                    // Call service to create product
                    productService
                      .createProduct(
                        createProductDto,
                        image.ref.path.toFile,
                        userId
                      )
                      .map {
                        _ =>
                          val response = ApiResponse[Unit](
                            success = true,
                            message = "Product created successfully"
                          )
                          Created(Json.toJson(response))
                      }

                  case None =>
                    Future.successful(
                      BadRequest(
                        Json.obj(
                          "success" -> false,
                          "message" -> "Product image file is required"
                        )
                      )
                    )
                }
            }

          case None =>
            Future.successful(
              BadRequest(
                Json.obj(
                  "success" -> false,
                  "message" -> "Product data is required"
                )
              )
            )
        }
    }

  def getAllProducts(page: Int, size: Int): Action[AnyContent] =
    userAction.async {
      _ =>
        productService.getAllProducts(page, size).map {
          pagedResult =>
            val response = ApiResponse(
              success = true,
              message = "Products retrieved successfully",
              data = Some(Json.toJson(pagedResult))
            )
            Ok(Json.toJson(response))
        }
    }

  def updateProduct(id: String): Action[MultipartFormData[TemporaryFile]] =
    userAction.async(parse.multipartFormData) {
      request =>
        val userId = request.userId

        request.body.file("product") match {
          case Some(productFile) =>
            val jsonContent = Using.resource(
              Source.fromFile(productFile.ref.path.toFile)
            )(_.mkString)
            val json = Json.parse(jsonContent)

            handleJsonValidation[UpdateProductRequestDto](json) {
              updateDto =>
                val imageFileOpt =
                  request.body.file("image").map(_.ref.path.toFile)

                productService
                  .updateProduct(id, updateDto, imageFileOpt, userId)
                  .map {
                    case Right(rows) if rows > 0 =>
                      Ok(
                        Json.obj(
                          "success" -> true,
                          "message" -> "Product updated successfully"
                        )
                      )
                    case Right(_) =>
                      NotFound(
                        Json.obj(
                          "success" -> false,
                          "message" -> "Product not found"
                        )
                      )
                    case Left(errorMessage) =>
                      BadRequest(
                        Json.obj("success" -> false, "message" -> errorMessage)
                      )
                  }
            }

          case None =>
            Future.successful(
              BadRequest(
                Json.obj(
                  "success" -> false,
                  "message" -> "Product data is required"
                )
              )
            )
        }
    }
}
