package controllers

import models.User
import play.api.data.Form
import play.api.data.Forms._
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
    User.findOneById (id) map { user =>
      Ok(Json.toJson(user))
    } getOrElse NotFound (Json.obj("err" -> "User not found"))
  }

  case class Data(name: String, email: String, password: String)

  val dataForm = Form(
    mapping(
      "name" -> nonEmptyText,
      "email" -> email,
      "password" -> nonEmptyText
    )(Data.apply)(Data.unapply)
  )

  def create = HasToken(parse.json) { _ => currentId => implicit request =>
    dataForm.bind(request.body).fold(
      formErrors => BadRequest(Json.obj("err" -> formErrors.errorsAsJson)),
      createData => {
        if(User.create(createData.name, createData.email, createData.password)) Ok(Json.obj("status" -> "Success"))
        else InternalServerError (Json.obj("err" -> "User not created"))
      }
    )
  }

  def update(id: Long) = HasToken(parse.json) { _ => currentId => implicit request =>
    dataForm.bind(request.body).fold(
      formErrors => BadRequest(Json.obj("err" -> formErrors.errorsAsJson)),
      updateData => {
        if(User.update(id, updateData.name, updateData.email, updateData.password)) Ok(Json.obj("status" -> "success"))
        else InternalServerError (Json.obj("err" -> "User not updated"))
      }
    )
  }

  def delete(id: Long) = HasToken() { _ => currentId => implicit request =>
    if(User.delete(id)) Ok(Json.obj("status" -> "success"))
    else InternalServerError (Json.obj("err" -> "User not removed"))
  }
}
