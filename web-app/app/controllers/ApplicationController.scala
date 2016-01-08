package controllers

import javax.inject.Inject

import play.api.i18n.MessagesApi

class ApplicationController @Inject()(messages: MessagesApi) extends BaseController {

  def index = BaseAction {
    implicit request =>
      if (userInfo(request).isDefined) {
        Redirect(routes.ProjectController.listProjects)
      } else {
        Redirect(routes.UserController.index)
      }
  }

  override def messagesApi: MessagesApi = messages
}
