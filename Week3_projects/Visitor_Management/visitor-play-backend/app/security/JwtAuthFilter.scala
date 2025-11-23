package security

import org.apache.pekko.stream.Materializer
import play.api.mvc._
import play.api.http.HttpFilters

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class JwtAuthFilter @Inject()(
                               implicit val mat: Materializer,
                               ec: ExecutionContext
                             ) extends Filter {

  override def apply(next: RequestHeader => Future[Result])
                    (request: RequestHeader): Future[Result] = {

    val publicExact = Seq("/", "/api/login")

    val isApi = request.path.startsWith("/api")

    // Allow public paths
    if (!isApi || publicExact.contains(request.path)) {
      return next(request)
    }

    // Extract Bearer token
    val tokenOpt = request.headers
      .get("Authorization")
      .map(_.replace("Bearer ", ""))

    JwtUtil.validateToken(tokenOpt.getOrElse("")) match {
      case Some(userId) =>
        next(request) // Token valid
      case None =>
        Future.successful(Results.Unauthorized("Invalid or missing token"))
    }
  }
}
