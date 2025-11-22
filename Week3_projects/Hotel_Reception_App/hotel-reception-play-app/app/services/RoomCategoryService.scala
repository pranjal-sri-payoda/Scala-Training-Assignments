package services

import javax.inject._
import models.{RoomCategory, RoomCategoryDAO}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class RoomCategoryService @Inject()(
                                     dao: RoomCategoryDAO
                                   )(implicit ec: ExecutionContext) {

  def list(): Future[Seq[RoomCategory]] = dao.list()
}
