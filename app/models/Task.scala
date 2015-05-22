package models

import org.joda.time.DateTime

/**
 * Created by Kevin on 5/21/2015.
 */
case class Task(id: Long, listId: Long, description: String, completedDate: Option[DateTime], createdDate: DateTime)

object Task {
  import play.api.libs.json._
  import play.api.libs.functional.syntax._

  implicit val reads: Reads[Task] = (
    (__ \ "id").read[Long] and
      (__ \ "listId").read[Long] and
      (__ \ "description").read[String] and
      (__ \ "completedDate").readNullable[DateTime] and
      (__ \ "createdDate").read[DateTime]
    )(Task.apply _)

  implicit val writes: Writes[Task] = (
    (__ \ "id").write[Long] and
      (__ \ "listId").write[Long] and
      (__ \ "description").write[String] and
      (__ \ "completedDate").writeNullable[DateTime] and
      (__ \ "createdDate").write[DateTime]
    )(unlift(Task.unapply))
}