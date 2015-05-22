package models


/**
 * Created by Kevin on 5/21/2015.
 */
case class User(id: Long,       // used to query
                email: String,  // used to autenticate
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
      None // never put the password into json
    ))
}
