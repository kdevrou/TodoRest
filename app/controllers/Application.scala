package controllers

import models.User
import play.api._
import play.api.cache._
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json
import play.api.mvc._
import scala.concurrent.duration._
import play.api.Play.current

trait Security { self: Controller =>
  val AuthTokenCookieKey = "XSRF-TOKEN"

  /** Checks that a token is either in the header or in the query string */
  def HasToken[A](p: BodyParser[A] = parse.anyContent)(f: String => Long => Request[A] => Result): Action[A] =
    Action(p) { implicit request =>
      // get a reference to the potential token in the cookie
      val maybeToken = request.cookies.get(AuthTokenCookieKey)
      maybeToken flatMap { token =>
        // found token
        Cache.getAs[Long](token.value) map { userid =>
          // add userid from cache to request
          f(token.value)(userid)(request)
        }
      } getOrElse Unauthorized(Json.obj("err" -> "No Token")) // send error if token is not present in cookie or cache
    }

}

object Application extends Controller with Security {

  /** establish the amount of time that a session will last */
  lazy val CacheExpiration = Duration(Play.configuration.getInt("cache.expiration").getOrElse(120), SECONDS)

  /** Serve the login page */
  def index = Action {
    Ok(views.html.index())
  }

  /** Use an object other than User to hold the form input */
  case class Login(email: String, password: String)

  /** Setup validation rules for binding data to Login object */
  val loginForm = Form(
    mapping(
      "email" -> email,
      "password" -> nonEmptyText
    )(Login.apply)(Login.unapply)
  )

  implicit class ResultWithToken(result: Result) {
    def withToken(token: (String, Long)): Result = {
      Cache.set(token._1, token._2, CacheExpiration)
      result.withCookies(Cookie(AuthTokenCookieKey, token._1, None, httpOnly = false))
    }

    def discardingToken(token: String): Result = {
      Cache.remove(token)
      result.discardingCookies(DiscardingCookie(name = AuthTokenCookieKey))
    }
  }

  /** Check credentials, generate token and serve it back as auth token in a Cookie */
  def login = Action(parse.json) { implicit request =>
    loginForm.bind(request.body).fold( // Bind JSON body to form values
      formErrors => BadRequest(Json.obj("err" -> formErrors.errorsAsJson)),
      loginData => {
        User.findByEmailAndPassword(loginData.email, loginData.password) map { user =>
          val token = java.util.UUID.randomUUID().toString
          Ok(Json.obj(
            "authToken" -> token,
            "userId" -> user.id
          )).withToken(token -> user.id)
        } getOrElse NotFound(Json.obj("err" -> "User Not Found or Password Invalid"))
      }
    )
  }

  /** Invalidate the token in the Cache and discard the cookie */
  def logout = Action { implicit request =>
    request.cookies.get(AuthTokenCookieKey) map { token =>
      Redirect("/").discardingToken(token.value)
    } getOrElse BadRequest(Json.obj("err" -> "No Token"))
  }
}
