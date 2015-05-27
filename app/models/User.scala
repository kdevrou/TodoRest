package models


/**
 * Created by Kevin on 5/21/2015.
 */
case class User(id: Long,       // used to query
                email: String,  // used to authenticate
                name: String,
                password: String)

object User {

  import play.api.libs.json._
  import play.api.libs.functional.syntax._

  implicit val reads: Reads[User] = (
    (__ \ "id").read[Long] and
      (__ \ "name").read[String] and
      (__ \ "email").read[String] and
      (__ \ "password").read[String]
    )(User.apply _)

  implicit val writes: Writes[User] = (
    (__ \ "id").write[Long] and
      (__ \ "name").write[String] and
      (__ \ "email").write[String] and
      (__ \ "password").writeNullable[String]
    )((user: User) => (
      user.id,
      user.name,
      user.email,
      None // never put the password into json
    ))

  import anorm._
  import anorm.~
  import anorm.SqlParser._
  import play.api.db.DB
  import play.api.Play.current

  val parser = {
    get[Long]("id") ~
      get[String]("name") ~
      get[String]("email") ~
      get[String]("password")
  } map {
    case id ~ name ~ email ~ password =>
      User(id, name, email, password)
  }

  val multiParser: ResultSetParser[Seq[User]] = parser *

  /*authenticate user*/
  def findByEmailAndPassword(email: String, password: String): Option[User] = DB.withConnection {
    implicit connection =>
      SQL("SELECT * FROM usertable WHERE email = {email} AND password = {password}").on(
        'email -> email, 'password -> password
      ).as(parser singleOpt)
  }

  /*select all users*/
  def findAll: Seq[User] = DB.withConnection { implicit connection =>
    SQL("SELECT * FROM usertable where inactive = 0").as(multiParser)
  }

  /*find user by id*/
  def findOneById(id: Long): Option[User] = DB.withConnection { implicit connection =>
    SQL("SELECT * FROM usertable WHERE id = {id} and inactive = 0").on(
      'id -> id
    ).as(parser singleOpt)
  }

  def create(name: String, email: String, password: String): Boolean = DB.withConnection { implicit connection =>
    SQL(
      """INSERT INTO usertable
        | (name, email, password)
        |VALUES ({name}, {email}, {password})
      """.stripMargin)
      .on('name -> name, 'email -> email, 'password -> password)
      .executeUpdate() == 1
  }

  def update(id: Long, name: String, email: String, password: String): Boolean = DB.withConnection { implicit connection =>
    SQL(
      """
        |UPDATE usertable
        |SET name = {name}
        |   ,email = {email}
        |   ,password = {password}
        |WHERE id = {id}
      """.stripMargin)
      .on('id -> id, 'name -> name, 'email -> email, 'password -> password)
      .executeUpdate() == 1
  }

  def delete(id: Long): Boolean = DB.withConnection { implicit connection =>
    SQL("UPDATE usertable SET inactive = 1 where id = {id}")
      .on('id -> id)
      .executeUpdate() == 1
  }
}
