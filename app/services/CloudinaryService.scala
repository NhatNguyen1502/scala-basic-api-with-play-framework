package services

import com.cloudinary._
import com.cloudinary.utils.ObjectUtils

import javax.inject.{Inject, Singleton}
import java.io.File
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

@Singleton
class CloudinaryService @Inject() (config: play.api.Configuration)(implicit
  ec: ExecutionContext
) {

  private val cloudName = config.get[String]("cloudinary.cloud-name")
  private val apiKey = config.get[String]("cloudinary.api-key")
  private val apiSecret = config.get[String]("cloudinary.api-secret")

  private val cloudinary = new Cloudinary(
    ObjectUtils.asMap(
      "cloud_name",
      cloudName,
      "api_key",
      apiKey,
      "api_secret",
      apiSecret
    )
  )

  /**
   * Upload an image on Cloudinary.
   *
   * @param file
   *   File need to upload
   * @return
   *   URL of image on Cloudinary
   */

  def uploadImage(file: File): Future[String] = Future {
    Try {
      val uploadResult =
        cloudinary.uploader().upload(file, ObjectUtils.emptyMap())
      uploadResult.get("url").toString
    } match {
      case Success(url) => url
      case Failure(ex)  =>
        // Log lỗi, có thể trả về URL mặc định hoặc throw tiếp
        throw new RuntimeException(s"Upload failed: ${ex.getMessage}", ex)
    }
  }
}
