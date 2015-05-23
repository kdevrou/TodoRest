package models

import java.util.Date

import org.joda.time.DateTime
import play.Logger


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

  def findOneById(id: Long): Option[User] = DB.withConnection { implicit connection =>
    SQL("SELECT * FROM usertable WHERE id = {id}").on('id -> id).as(parser.singleOpt)
  }

  def findByEmailAndPassword(email: String, password: String): Option[User] = DB.withConnection {
    Logger.debug(s"received email: $email and password: $password")
    implicit connection =>
      SQL("SELECT * FROM usertable WHERE email = {email} AND password = {password}").on(
        'email -> email, 'password -> password
      ).as(parser.singleOpt)
  }
}
