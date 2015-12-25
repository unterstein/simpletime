package controllers

import javax.inject.Inject

import play.api.i18n.MessagesApi

class ApplicationController @Inject() (messages: MessagesApi) extends BaseController {

  def index = BaseAction {
    implicit request =>
      Ok(views.html.login())
  }

  override def messagesApi: MessagesApi = messages
}
