package controllers

import play.api.libs.json.Json
import play.api.mvc.Controller

/**
 * Created by kdevrou on 5/22/2015.
 */
object TaskLists extends Controller with Security {

  def getAll(userId: Long) = HasToken() { _ => currentId => implicit  request =>
    Ok(Json.toJson(List.findAll(userId)))
  }

  def get(userId: Long, id: Long) = TODO

  def create(userId: Long) = TODO

  def update(userId: Long, id: Long) = TODO

  def delete(userId: Long, id: Long) = TODO
}
