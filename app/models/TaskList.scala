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


  import anorm._
  import anorm.~
  import anorm.SqlParser._
  import play.api.db.DB
  import play.api.Play.current

  val parser = {
    get[Long]("id") ~
      get[Long]("ownerId") ~
      get[String]("name")
  } map {
    case id ~ ownerId ~ name =>
      List(id, ownerId, name)
  }

  val multiParser: ResultSetParser[Seq[List]] = parser *

  def findAll(ownerId: Long): Seq[List] = DB.withConnection( implicit connection =>
    SQL("SELECT * FROM list WHERE ownerId = {ownerId}").on(
      'ownerId -> ownerId
    ).as(multiParser)
  )


}
