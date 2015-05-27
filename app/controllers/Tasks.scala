package controllers

import models.Task
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json
import play.api.mvc.Controller

/**
 * Created by Lily on 5/26/2015.
 */
object Tasks extends Controller with Security {
  def getAll(listId: Long) = HasToken() { _ => currentId => implicit request =>
    Ok(Json.toJson(Task.findAll(listId)))
  }

  def get(listId: Long, id: Long) = HasToken() { _ => currentId => implicit request =>
    Task.findOneById(listId, id) map { task =>
      Ok(Json.toJson(task))
    } getOrElse NotFound (Json.obj("err" -> "Task not found"))
  }

  case class Data(description: String)

  val dataForm = Form(
    mapping(
      "description" -> nonEmptyText
    )(Data.apply)(Data.unapply)
  )

  def create(listId: Long) = HasToken(parse.json) { _ => currentId => implicit request =>
    dataForm.bind(request.body).fold(
      formErrors => BadRequest(Json.obj("err" -> formErrors.errorsAsJson)),
      createData => {
        if(Task.create(createData.description, listId)) Ok(Json.obj("status" -> "success"))
        else InternalServerError (Json.obj("err" -> "Task not created"))
      }
    )
  }

  def update(listId: Long, id: Long) = HasToken(parse.json) { _ => currentId => implicit request =>
    dataForm.bind(request.body).fold(
      formErrors => BadRequest(Json.obj("err" -> formErrors.errorsAsJson)),
      updateData => {
        if(Task.update(listId, id, updateData.description)) Ok(Json.obj("status" -> "success"))
        else InternalServerError (Json.obj("err" -> "Task not updated"))
      }
    )
  }

  def delete(listId: Long, id: Long) = HasToken() { _ => currentId => implicit request =>
    if(Task.delete(listId, id)) Ok(Json.obj("status" -> "success"))
    else InternalServerError (Json.obj("err" -> "Task not removed"))
  }
}
