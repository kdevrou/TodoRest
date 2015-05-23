package controllers

import models.User
import play.api.libs.json.Json
import play.api.mvc._

/**
 * Created by Kevin on 5/21/2015.
 */
object Users extends Controller with Security {

  def getAll = HasToken() { _ => currentId => implicit  request =>
    User.findOneById (currentId) map { user =>
      Ok(Json.toJson(user))
    } getOrElse NotFound (Json.obj("err" -> "User Not Found"))
  }

  def get(id: Long) = TODO

  def create = TODO

  def update(id: Long) = TODO

  def delete(id: Long) = TODO
}
