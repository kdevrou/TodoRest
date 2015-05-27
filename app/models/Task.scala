package models

import java.util.Date

/**
 * Created by Kevin on 5/21/2015.
 */
case class Task(id: Long,
                listId: Long,
                description: String,
                createdDate: Date,
                completed: Boolean)

object Task {
  import play.api.libs.json._
  import play.api.libs.functional.syntax._

  implicit val reads: Reads[Task] = (
    (__ \ "id").read[Long] and
      (__ \ "listId").read[Long] and
      (__ \ "description").read[String] and
      (__ \ "createdDate").read[Date] and
      (__ \ "completed").read[Boolean]
    )(Task.apply _)

  implicit val writes: Writes[Task] = (
    (__ \ "id").write[Long] and
      (__ \ "listId").write[Long] and
      (__ \ "description").write[String] and
      (__ \ "createdDate").write[Date] and
      (__ \ "completed").write[Boolean]
    )(unlift(Task.unapply))


  import anorm._
  import anorm.~
  import anorm.SqlParser._
  import play.api.db.DB
  import play.api.Play.current

  val parser = {
    get[Long]("id") ~
      get[Long]("listId") ~
      get[String]("description") ~
      get[Date]("createdDate") ~
      get[Byte]("complete")
  } map {
    case id ~ ownerId ~ description ~ created ~ complete =>
      Task(id, ownerId, description, created, complete == 1)
  }

  val multiParser: ResultSetParser[Seq[Task]] = parser *

  def findAll(listId: Long): Seq[Task] = DB.withConnection( implicit connection =>
    SQL("SELECT * FROM task WHERE listId = {listId}").on(
      'listId -> listId
    ).as(multiParser)
  )

  def findOneById(listId: Long, id: Long): Option[Task] = DB.withConnection( implicit connection =>
    SQL("SELECT * FROM task WHERE listId = {listId} and id = {id}").on(
      'listId -> listId, 'id -> id
    ).as(parser singleOpt)
  )

  def create(description: String, listId: Long): Boolean = DB.withConnection( implicit connection =>
    SQL(
      """
        |INSERT INTO task
        | (listId, description)
        |VALUES ({listId}, {description})
      """.stripMargin)
      .on('listId -> listId, 'description -> description)
      .executeUpdate() == 1
  )

  def update(listId: Long, id: Long, description: String): Boolean = DB.withConnection( implicit connection =>
    SQL(
      """
        |UPDATE task
        |SET description = {description}
        |   ,listId = {listId}
        |WHERE id = {id}
      """.stripMargin)
      .on('description -> description, 'listId -> listId, 'id -> id)
      .executeUpdate() == 1
  )

  def delete(listId: Long, id: Long): Boolean = DB.withConnection( implicit connection =>
    SQL(
      """
        |UPDATE task
        |SET complete = 1
        |WHERE id = {id}
        |  AND listId = {listId}
      """.stripMargin)
      .on('listId -> listId, 'id -> id)
      .executeUpdate() == 1
  )

}