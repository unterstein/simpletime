package controllers

import javax.inject.Inject

import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.MessagesApi

class TimeEntryController @Inject()(messages: MessagesApi) extends BaseController {

  def list(projectHash: String) = AuthenticatedBaseAction {
    implicit request =>
      Ok("")
  }

  def create(projectHash: String) = AuthenticatedBaseAction {
    implicit request =>
      Ok("")
  }

  def edit(projectHash: String, entityHash: String) = AuthenticatedBaseAction {
    implicit request =>
      Ok("")
  }

  override def messagesApi: MessagesApi = messages
}

object TimeEntryController {

  case class CaseEntries(name: String, entries: List[CaseEntry])

  case class CaseEntry(start: Long, end: Long, props: List[Prop])

  case class Prop(key: String, value: String)

  val entryForm: Form[CaseEntries] = Form(
    mapping(
      "name" -> nonEmptyText,
      "entries" -> list(mapping(
        "start" -> longNumber,
        "end" -> longNumber,
        "props" -> list(mapping(
          "key" -> nonEmptyText,
          "value" -> nonEmptyText
        )(Prop.apply)(Prop.unapply))
      )(CaseEntry.apply)(CaseEntry.unapply))
    )(CaseEntries.apply)(CaseEntries.unapply)
  )
}
