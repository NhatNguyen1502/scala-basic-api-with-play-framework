package controllers

import actions.UserAction
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
  categoryService: CategoryService,
  userAction: UserAction
)(implicit ec: ExecutionContext)
    extends AbstractController(cc)
    with ValidationHandler {

  def createCategory: Action[JsValue] = userAction.async(parse.json) {
    request =>
      val userId = request.userId
      handleJsonValidation[CreateCategoryRequestDto](request.body) {
        request =>
          categoryService.createCategory(request, userId).map {
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

  def updateCategory(id: String): Action[JsValue] =
    userAction.async(parse.json) {
      request =>
        val userId = request.userId
        handleJsonValidation[UpdateCategoryRequestDto](request.body) {
          request =>
            categoryService.updateCategory(id, request, userId).map {
              _ =>
                val response = ApiResponse[Unit](
                  success = true,
                  message = "Category update successfully"
                )
                Created(Json.toJson(response))
            }
        }
    }

  def deleteCategory(id: String): Action[AnyContent] = userAction.async {
    request =>
      val userId = request.userId
      categoryService.deleteCategory(id, userId).map {
        _ => NoContent
      }
  }
}
