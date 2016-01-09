package controllers

import javax.inject.Inject

import neo4j.services.Neo4jProvider
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.MessagesApi

import scala.collection.JavaConversions._

class TimeEntryController @Inject()(messages: MessagesApi) extends BaseController {

  def listEntries(projectHash: String) = AuthenticatedBaseAction {
    implicit request =>
      val dbProject = Neo4jProvider.get().projectRepository.findByHashAndUser(projectHash, request.user)
      val dbEntries = Neo4jProvider.get().timeEntryRepository.findByProject(dbProject)

      val entries = dbEntries.map(entry => CaseEntry(entry.startTime, entry.endTime, mapToProps(entry.properties.toMap)))

      Ok(views.html.projectDetails(projectHash, entryForm.fill(CaseEntries(dbProject.name, entries.toList, defaultColumns ++ projectToColumnList(dbProject)))))
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
      )(CaseEntry.apply)(CaseEntry.unapply)),
      "columns" -> list(mapping(
        "columnKey" -> nonEmptyText,
        "columnName" -> nonEmptyText,
        "columnType" -> nonEmptyText
      )(CaseColumn.apply)(CaseColumn.unapply))
    )(CaseEntries.apply)(CaseEntries.unapply)
  )

  def mapToProps(input: Map[String, String]): List[Prop] = input.map { case (key: String, value: String) => Prop(key, value)}.toList
}

case class CaseEntries(name: String, entries: List[CaseEntry], columns: List[CaseColumn])

case class CaseEntry(start: Long, end: Long, props: List[Prop])

case class Prop(key: String, value: String)

