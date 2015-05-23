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
  val AuthTokenHeader = "X-XSRF-TOKEN"
  val AuthTokenCookieKey = "XSRF-TOKEN"
  val AuthTokenUrlKey = "auth"

  /** Checks that a token is either in the header or in the query string */
  def HasToken[A](p: BodyParser[A] = parse.anyContent)(f: String => Long => Request[A] => Result): Action[A] =
    Action(p) { implicit request =>
      val maybeToken = request.headers.get(AuthTokenHeader).orElse(request.getQueryString(AuthTokenUrlKey))
      maybeToken flatMap { token =>
        Cache.getAs[Long](token) map { userid =>
          f(token)(userid)(request)
        }
      } getOrElse Unauthorized(Json.obj("err" -> "No Token"))
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
          Logger.debug(s"assiging token: $token to user ${user.id}")
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
    request.headers.get(AuthTokenHeader) map { token =>
      Redirect("/").discardingToken(token)
    } getOrElse BadRequest(Json.obj("err" -> "No Token"))
  }

}