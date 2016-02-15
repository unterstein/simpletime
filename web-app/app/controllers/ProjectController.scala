package controllers

import javax.inject.Inject

import logic.HashHelper
import neo4j.models.project.{Project, ProjectColumn, ProjectColumnType}
import neo4j.services.Neo4jProvider
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.{Messages, MessagesApi}

import scala.collection.JavaConversions._

/**
 * Controller to edit project settings, like project name, columns, ...
 *
 * @author Johannes Unterstein (unterstein@me.com)
 */
class ProjectController @Inject()(messages: MessagesApi) extends BaseController {

  def listProjects = AuthenticatedBaseAction {
    implicit request =>
      Ok(views.html.projectList(Neo4jProvider.get().projectRepository.findByUserAndActive(request.user, true).toList))
  }

  def post(hash: String) = AuthenticatedBaseAction {
    implicit request =>
      projectForm.bindFromRequest.fold(
        formWithErrors => Ok(views.html.projectEdit(hash, formWithErrors, initialColumn, columnTypes)),
        value => {
          // TODO remove "new" hack
          val project = if ("new".equals(hash)) {
            val newProject = new Project
            newProject.name = value.name
            newProject.user = request.user
            newProject.hash = HashHelper.uuid()
            newProject.setColumns(createColumns(value))
            newProject
          } else {
            val dbProject = Neo4jProvider.get().projectRepository.findByHashAndUser(hash, request.user)
            dbProject.name = value.name
            dbProject.setColumns(createColumns(value))
            dbProject
          }
          Neo4jProvider.get().projectRepository.save(project)
          Redirect(routes.ProjectController.edit(project.hash))
        }
      )
  }

  def createColumns(value: CaseProject): List[ProjectColumn] = {
    if (value.columns != null) {
      value.columns.map {
        column =>
          val projectColumn = new ProjectColumn
          projectColumn.name = column.columnName
          projectColumn.`type` = ProjectColumnType.valueOf(column.columnType)
          projectColumn.key = column.columnKey
          // TODO column.properties
          projectColumn
      }.toList
    } else {
      List()
    }
  }

  def create = AuthenticatedBaseAction {
    implicit request =>
      Ok(views.html.projectEdit("new", initialProjectForm, initialColumn, columnTypes))
  }

  def delete(hash: String) = AuthenticatedBaseAction {
    implicit request =>
      val project = Neo4jProvider.get().projectRepository.findByHashAndUser(hash, request.user)
      project.active = false
      Neo4jProvider.get().projectRepository.save(project)
      Redirect(routes.ProjectController.listProjects())
  }

  def edit(hash: String) = AuthenticatedBaseAction {
    implicit request =>
      val project = Neo4jProvider.get().projectRepository.findByHashAndUser(hash, request.user)
      val columns = projectToColumnList(project)
      val caseProject = CaseProject(project.name, columns)
      Ok(views.html.projectEdit(hash, projectForm.fill(caseProject), initialColumn, columnTypes))
  }

  override def messagesApi: MessagesApi = messages

  def initialColumn()(implicit messages: play.api.i18n.Messages) = CaseColumn("", "", ProjectColumnType.STRING.name())

  def initialProjectForm()(implicit lang: play.api.i18n.Lang): Form[CaseProject] = {
    val columns = List(
      CaseColumn("description", Messages("Description"), ProjectColumnType.STRING.name()),
      CaseColumn("planned", Messages("Planned"), ProjectColumnType.BOOLEAN.name())
    )
    projectForm.fill(CaseProject(Messages("project.default"), columns))
  }

  val columnTypes: String = {
    "[\"" + ProjectColumnType.values()
      .filter(v => v != ProjectColumnType.DELETE && v != ProjectColumnType.HIDDEN)
      .map { e => e.name}.mkString("\",\"") + "\"]"
  }

  val projectForm: Form[CaseProject] = Form(
    mapping(
      "name" -> nonEmptyText,
      "columns" -> list(mapping(
        "columnKey" -> nonEmptyText,
        "columnName" -> nonEmptyText,
        "columnType" -> nonEmptyText
      )(CaseColumn.apply)(CaseColumn.unapply))
    )(CaseProject.apply)(CaseProject.unapply)
  )
}

case class CaseColumn(columnKey: String, columnName: String, columnType: String)

case class CaseProject(name: String, columns: List[CaseColumn])
