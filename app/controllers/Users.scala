package controllers

import models.User
import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc._

/**
 * Created by Kevin on 5/21/2015.
 */
object Users extends Controller with Security {

  def getAll = HasToken() { _ => currentId => implicit  request =>
    Ok(Json.toJson(User.findAll))
  }

  def get(id: Long) = HasToken() { _ => currentId => implicit  request =>
    User.findOneById (currentId) map { user =>
      Ok(Json.toJson(user))
    } getOrElse NotFound (Json.obj("err" -> "User Not Found"))
  }

  def create = HasToken() { _ => currentId => implicit request =>
    Ok(Json.toJson(""))
  }

  def update(id: Long) = HasToken() { _ => currentId => implicit request =>
    Ok(Json.toJson(""))
  }

  def delete(id: Long) = HasToken() { _ => currentId => implicit request =>
    User.delete(currentId)
    Ok(Json.toJson(""))
  }
}
