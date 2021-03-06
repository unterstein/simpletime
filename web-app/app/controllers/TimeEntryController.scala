package controllers

import javax.inject.Inject

import neo4j.models.time.TimeEntry
import neo4j.services.Neo4jProvider
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.MessagesApi

import scala.collection.JavaConversions._

/**
 * Controller to edit time entries related to a project.
 *
 * @author Johannes Unterstein (unterstein@me.com)
 */
class TimeEntryController @Inject()(messages: MessagesApi) extends BaseController {

  def listEntries(projectHash: String) = AuthenticatedBaseAction {
    implicit request =>
      val dbProject = Neo4jProvider.get().projectRepository.findByHashAndUser(projectHash, request.user)
      val dbEntries = Neo4jProvider.get().timeEntryRepository.findByProject(dbProject)

      val entries = dbEntries.map(entry => CaseEntry(entry.id, entry.startTime, entry.endTime, mapToProps(if (entry.properties == null) Map() else entry.properties.toMap)))

      val columns = projectToColumnList(dbProject)
      Ok(views.html.projectDetails(projectHash, dbProject.name, entryForm.fill(CaseEntries(entries.toList, defaultColumns ++ columns ++ trashColumn)), exampleEntry(columns)))
  }

  def exampleEntry(columns: List[CaseColumn]) = {
    CaseEntry(-1L, System.currentTimeMillis(), System.currentTimeMillis(), columns.map(column => Prop(column.columnKey, "")).toList)
  }

  def post(projectHash: String) = AuthenticatedBaseAction {
    implicit request =>
      val dbProject = Neo4jProvider.get().projectRepository.findByHashAndUser(projectHash, request.user)
      val columns = projectToColumnList(dbProject)

      entryForm.bindFromRequest.fold(
        formWithErrors => Ok(views.html.projectDetails(projectHash, dbProject.name, formWithErrors, exampleEntry(columns))),
        value => {
          val entries = value.entries.map {
            entry =>
              val filteredElements = dbProject.timeEntries.filter(p => p.id == entry.id)
              val updateEntry: TimeEntry = if (entry.id > 0 && filteredElements.size == 1) {
                // update existing
                filteredElements.toList.get(0)
              } else {
                // create new, add and modify
                val newEntry = new TimeEntry
                newEntry
              }
              updateEntry.project = dbProject
              updateEntry.startTime = entry.start
              updateEntry.endTime = entry.end
              updateEntry.properties.clear()
              entry.props.foreach {
                prop =>
                  updateEntry.properties.put(prop.key, prop.value)
              }
              updateEntry
          }.toList
          Neo4jProvider.get().timeEntryRepository.save(entries)
          Redirect(routes.TimeEntryController.listEntries(projectHash))
        }
      )
  }

  override def messagesApi: MessagesApi = messages

  val entryForm: Form[CaseEntries] = Form(
    mapping(
      "entries" -> list(mapping(
        "id" -> longNumber,
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

case class CaseEntry(id: Long, start: Long, end: Long, props: List[Prop])

case class Prop(key: String, value: String)

