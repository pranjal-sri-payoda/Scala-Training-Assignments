package controllers

import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.libs.Files.TemporaryFile
import play.api.mvc.*

import javax.inject.*
import java.nio.file.{Files, Paths}

@Singleton
class HomeController @Inject()(
                                val controllerComponents: ControllerComponents
                              ) extends BaseController
  with Logging
  with I18nSupport {   // <-- IMPORTANT FIX

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
            s"/Users/admin/IdeaProjects/file-upload-testing/conf/uploaded-files/$fileName"
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
}
