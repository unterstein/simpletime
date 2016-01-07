package controllers

import javax.inject.Inject

import logic.UserLogic
import neo4j.services.Neo4jProvider
import play.api.data.Forms._
import play.api.data._
import play.api.i18n.MessagesApi

class UserController @Inject()(messages: MessagesApi) extends BaseController {

  def login = BaseAction {
    implicit request =>
      loginForm.bindFromRequest.fold(
        formWithErrors => Ok(views.html.login(formWithErrors, registerForm)),
        value => {
          val user = Neo4jProvider.get().userRepository.findByEmail(value.loginEmail)
          Redirect(routes.ApplicationController.index).withSession(USER_HASH -> user.hash)
        }
      )
  }

  def logout = BaseAction {
    implicit request =>
      Redirect(routes.ApplicationController.index).withNewSession
  }

  def register = BaseAction {
    implicit request =>
      registerForm.bindFromRequest.fold(
        formWithErrors => Ok(views.html.login(loginForm, formWithErrors)),
        value => {
          val user = UserLogic.createUser(value.registerEmail, value.registerPassword)
          Redirect(routes.ApplicationController.index).withSession(USER_HASH -> user.hash)
        }
      )
  }

  def index = BaseAction {
    implicit request =>
      Ok(views.html.login(loginForm, registerForm))
  }

  override def messagesApi: MessagesApi = messages

  val registerForm: Form[Register] = Form(
    mapping(
      "registerEmail" -> email,
      "registerPassword" -> nonEmptyText(minLength = 5),
      "registerPassword2" -> nonEmptyText(minLength = 5)
    )(Register.apply)(Register.unapply)
        .verifying("error.passwordNotMatch", register => register.registerPassword == register.registerPassword2)
        .verifying("error.duplicateEmail", register =>
      Neo4jProvider.get().userRepository.findByEmail(register.registerEmail) == null)
  )

  val loginForm: Form[Login] = Form(
    mapping(
      "loginEmail" -> email,
      "loginPassword" -> nonEmptyText
    )(Login.apply)(Login.unapply)
        .verifying("error.signInFailed", login => UserLogic.login(login.loginEmail, login.loginPassword) != null
        )
  )
}

case class Login(loginEmail: String, loginPassword: String)

case class Register(registerEmail: String, registerPassword: String, registerPassword2: String)
