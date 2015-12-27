package controllers

import javax.inject.Inject

import logic.UserLogic
import neo4j.services.Neo4jProvider
import play.api.data._
import play.api.data.Forms._
import play.api.i18n.MessagesApi

class UserController @Inject()(messages: MessagesApi) extends BaseController {

  def login = BaseAction {
    implicit request =>
      Ok("ok")
  }

  def register = BaseAction {
    implicit request =>
      Ok("ok")
  }

  case class Register(registerUrl: String, registerEmail: String, registerPassword: String, registerPassword2: String)

  val registerForm: Form[Register] = Form(
    mapping(
      "registerUrl" -> text,
      "registerEmail" -> email,
      "registerPassword" -> nonEmptyText(minLength = 5),
      "registerPassword2" -> nonEmptyText(minLength = 5)
    )(Register.apply)(Register.unapply)
        .verifying("error.passwordNotMatch", register => register.registerPassword == register.registerPassword2)
        .verifying("error.duplicateEmail", register =>
      Neo4jProvider.get().userRepository.findByEmail(register.registerEmail) == null)
  )

  case class Login(loginUrl: String, loginEmail: String, loginPassword: String)

  val loginForm: Form[Login] = Form(
    mapping(
      "loginUrl" -> text,
      "loginEmail" -> email,
      "loginPassword" -> nonEmptyText
    )(Login.apply)(Login.unapply)
        .verifying("error.signInFailed", login => UserLogic.login(login.loginEmail, login.loginPassword) != null
    )
  )

  override def messagesApi: MessagesApi = messages
}
