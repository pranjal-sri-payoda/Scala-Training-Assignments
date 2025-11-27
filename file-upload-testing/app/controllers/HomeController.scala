package controllers

import play.api.libs.Files.TemporaryFile
import play.api.mvc.*

import javax.inject.*
import java.nio.file.{Files, Paths}

@Singleton
class HomeController @Inject()(
                                val controllerComponents: ControllerComponents
                              ) extends BaseController {

  def index: Action[AnyContent] = Action { implicit request =>
    println("Index page called")
    Ok(views.html.index())
  }

  def upload: Action[MultipartFormData[TemporaryFile]] =
    Action(parse.multipartFormData) { implicit request =>
      println("Upload request received")

      request.body.file("file") match {
        case Some(filePart) =>
          val tempFile = filePart.ref
          val fileName = filePart.filename

          val target = Paths.get(
            s"HOME_PATH/uploaded-files/$fileName"
          )

          Files.createDirectories(target.getParent)
          tempFile.moveTo(target, replace = true)

          println(s"File saved to: $target")
          Ok(s"File uploaded to: $target")

        case None =>
          println("Upload attempt without a file")
          BadRequest("No file uploaded")
      }
    }

  def indexFiles: Action[AnyContent] = Action { implicit request =>
    println("Files page called")
    Ok(views.html.files())
  }

  def uploadMultiple: Action[MultipartFormData[TemporaryFile]] =
    Action(parse.multipartFormData) { implicit request =>
      println("Multiple file upload triggered")

      val uploadedFiles = request.body.files

      if (uploadedFiles.isEmpty) {
        println("No files uploaded")
        BadRequest("No files uploaded")
      } else {
        uploadedFiles.foreach { filePart =>
          val tempFile = filePart.ref
          val fileName = filePart.filename

          val target = Paths.get(
            s"HOME_PATH/multiple-uploaded-files/$fileName"
          )

          Files.createDirectories(target.getParent)
          tempFile.moveTo(target, replace = true)

          println(s"Saved: $fileName -> $target")
        }

        Ok(s"${uploadedFiles.length} files uploaded successfully!")
      }
    }
}
