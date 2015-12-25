package controllers

import javax.inject.Inject

import play.api.i18n.MessagesApi

class UserController @Inject()(messages: MessagesApi) extends BaseController {

  def login = BaseAction {
    implicit request =>
      Ok("ok")
  }

  override def messagesApi: MessagesApi = messages
}
