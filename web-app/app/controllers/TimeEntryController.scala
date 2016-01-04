package controllers

import javax.inject.Inject

import play.api.i18n.MessagesApi

class TimeEntryController @Inject()(messages: MessagesApi) extends BaseController {

  def list(projectHash: String) = AuthenticatedBaseAction {
    implicit request =>
      Ok("")
  }

  override def messagesApi: MessagesApi = messages
}
