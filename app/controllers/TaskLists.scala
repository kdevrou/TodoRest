package controllers

import models.TaskList
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json
import play.api.mvc.Controller

/**
 * Created by kdevrou on 5/22/2015.
 */
object TaskLists extends Controller with Security {

  def getAll(userId: Long) = HasToken() { _ => currentId => implicit  request =>
    Ok(Json.toJson(TaskList.findAll(userId)))
  }

  def get(userId: Long, id: Long) = HasToken() { _ => currentId => implicit request =>
    TaskList.findOneById(userId, id) map { list =>
      Ok(Json.toJson(list))
    } getOrElse NotFound (Json.obj("err" -> "List not found"))
  }

  case class Data(name: String)

  val dataForm = Form(
    mapping(
      "name" -> nonEmptyText
    )(Data.apply)(Data.unapply)
  )

  def create(userId: Long) = HasToken(parse.json) { _ => currentId => implicit request =>
    dataForm.bind(request.body).fold(
      formErrors => BadRequest(Json.obj("err" -> formErrors.errorsAsJson)),
      createData => {
        if(TaskList.create(createData.name, userId)) Ok(Json.obj("status" -> "success"))
        else InternalServerError (Json.obj("err" -> "List not created"))
      }
    )
  }

  def update(userId: Long, id: Long) = HasToken(parse.json) { _ => currentId => implicit request =>
    dataForm.bind(request.body).fold(
      formErrors => BadRequest(Json.obj("err" -> formErrors.errorsAsJson)),
      updateData => {
        if(TaskList.update(id, userId, updateData.name)) Ok(Json.obj("status" -> "success"))
        else InternalServerError (Json.obj("err" -> "List not updated"))
      }
    )
  }

  def delete(userId: Long, id: Long) = HasToken() { _ => currentId => implicit request =>
    if(TaskList.delete(id, userId)) Ok(Json.obj("status" -> "success"))
    else InternalServerError (Json.obj("err" -> "List not removed"))
  }
}
