package models

/**
 * Created by Kevin on 5/21/2015.
 */
case class List(id: Long,
                ownerId: Long,  //user who owns the list
                name: String
               )

object List {
  import play.api.libs.json._
  import play.api.libs.functional.syntax._

  implicit val reads: Reads[List] = (
    (__ \ "id").read[Long] and
      (__ \ "ownerId").read[Long] and
      (__ \ "name").read[String]
    )(List.apply _)

  implicit val writes: Writes[List] = (
    (__ \ "id").write[Long] and
      (__ \ "ownerId").write[Long] and
      (__ \ "name").write[String]
    )(unlift(List.unapply))
}
