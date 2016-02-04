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

      val columns = projectToColumnList(dbProject)
      Ok(views.html.projectDetails(projectHash, dbProject.name, entryForm.fill(CaseEntries(entries.toList, defaultColumns ++ columns)), exampleEntry(columns)))
  }

  def exampleEntry(columns: List[CaseColumn]) = {
    CaseEntry(0L, 0L, columns.map(column => Prop(column.columnKey, "")).toList)
  }

  def post(projectHash: String) = AuthenticatedBaseAction {
    implicit request =>
      val dbProject = Neo4jProvider.get().projectRepository.findByHashAndUser(projectHash, request.user)
      val columns = projectToColumnList(dbProject)

      entryForm.bindFromRequest.fold(
        formWithErrors => Ok(views.html.projectDetails(projectHash, dbProject.name, formWithErrors, exampleEntry(columns))),
        value => {
          Ok("")
        }
      )
  }

  override def messagesApi: MessagesApi = messages

  val entryForm: Form[CaseEntries] = Form(
    mapping(
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

case class CaseEntries(entries: List[CaseEntry], columns: List[CaseColumn])

case class CaseEntry(start: Long, end: Long, props: List[Prop])

case class Prop(key: String, value: String)

