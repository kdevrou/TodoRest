package models

/**
 * Created by Kevin on 5/21/2015.
 */
case class TaskList(id: Long,
                ownerId: Long,  //user who owns the list
                name: String,
                complete: Boolean)

object TaskList {
  import play.api.libs.json._
  import play.api.libs.functional.syntax._

  implicit val reads: Reads[TaskList] = (
    (__ \ "id").read[Long] and
      (__ \ "ownerId").read[Long] and
      (__ \ "name").read[String] and
      (__ \ "complete").read[Boolean]
    )(TaskList.apply _)

  implicit val writes: Writes[TaskList] = (
    (__ \ "id").write[Long] and
      (__ \ "ownerId").write[Long] and
      (__ \ "name").write[String] and
      (__ \ "complete").write[Boolean]
    )(unlift(TaskList.unapply))


  import anorm._
  import anorm.~
  import anorm.SqlParser._
  import play.api.db.DB
  import play.api.Play.current

  val parser = {
    get[Long]("id") ~
    get[Long]("ownerId") ~
    get[String]("name") ~
    get[Byte]("complete")
  } map {
    case id ~ ownerId ~ name ~ complete =>
      TaskList(id, ownerId, name, complete == 1)
  }

  val multiParser: ResultSetParser[Seq[TaskList]] = parser *

  def findAll(ownerId: Long): Seq[TaskList] = DB.withConnection( implicit connection =>
    SQL("SELECT * FROM list WHERE ownerId = {ownerId}").on(
      'ownerId -> ownerId
    ).as(multiParser)
  )

  def findOneById(ownerId: Long, id: Long): Option[TaskList] = DB.withConnection( implicit connection =>
    SQL("SELECT * from list WHERE ownerId = {ownerId} and id = {id}").on(
      'ownerId -> ownerId, 'id -> id
    ).as(parser singleOpt)
  )

  def create(name: String, ownerId: Long): Boolean = DB.withConnection( implicit connection =>
    SQL("INSERT INTO list (name, ownerId) VALUES ({name}, {ownerId})").on(
      'name -> name, 'ownerId -> ownerId
    ).executeUpdate() == 1
  )

  def update(id: Long, ownerId: Long, name: String): Boolean = DB.withConnection( implicit connection =>
    SQL(
      """
        |UPDATE list
        |SET name = {name}
        |   ,ownerId = {ownerId}
        |WHERE id = {id}
      """.stripMargin)
      .on('id -> id, 'ownerId -> ownerId, 'name -> name)
      .executeUpdate() == 1
  )

  def delete(id: Long, ownerId: Long): Boolean = DB.withConnection( implicit connection =>
    SQL("UPDATE list SET complete = 1 where ownerId = {ownerId} and id = {id}").on(
      'id -> id, 'ownerId -> ownerId
    ).executeUpdate() == 1
  )
}
