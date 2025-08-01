package controllers

import dtos.request.category.{
  CreateCategoryRequestDto,
  UpdateCategoryRequestDto
}
import dtos.response.ApiResponse
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{
  AbstractController,
  Action,
  AnyContent,
  ControllerComponents
}
import services.CategoryService
import utils.json.WritesExtras.unitWrites
import validations.ValidationHandler

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class CategoryController @Inject() (
  cc: ControllerComponents,
  categoryService: CategoryService
)(implicit ec: ExecutionContext)
    extends AbstractController(cc)
    with ValidationHandler {

  def createCategory: Action[JsValue] = Action.async(parse.json) {
    request =>
      handleJsonValidation[CreateCategoryRequestDto](request.body) {
        request =>
          categoryService.createCategory(request).map {
            _ =>
              val response = ApiResponse[Unit](
                success = true,
                message = "Category created successfully"
              )
              Created(Json.toJson(response))
          }
      }
  }

  def getAllCategory: Action[AnyContent] = Action.async {
    categoryService.getAllCategories.map {
      categories =>
        val response = ApiResponse(
          success = true,
          message = "Categories retrieved successfully",
          data = Some(Json.toJson(categories))
        )
        Ok(Json.toJson(response))
    }
  }

  def updateCategory(id: String): Action[JsValue] = Action.async(parse.json) {
    request =>
      handleJsonValidation[UpdateCategoryRequestDto](request.body) {
        request =>
          categoryService.updateCategory(id, request).map {
            _ =>
              val response = ApiResponse[Unit](
                success = true,
                message = "Category update successfully"
              )
              Created(Json.toJson(response))
          }
      }
  }

  def deleteCategory(id: String): Action[AnyContent] = Action.async {
    categoryService.deleteCategory(id).map {
      _ => NoContent
    }
  }
}
