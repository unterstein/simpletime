package controllers

import javax.inject.Inject

import logic.HashHelper
import neo4j.models.project.{Project, ProjectColumn, ProjectColumnType}
import neo4j.services.Neo4jProvider
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.{Messages, MessagesApi}

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._

class ProjectController @Inject()(messages: MessagesApi) extends BaseController {

  def list = AuthenticatedBaseAction {
    implicit request =>
      Ok(views.html.projectList(Neo4jProvider.get().projectRepository.findByUserAndActive(request.user, true).toList))
  }

  def post(hash: String) = AuthenticatedBaseAction {
    implicit request =>
      ProjectController.projectForm.bindFromRequest.fold(
        formWithErrors => Ok(views.html.projectEdit(hash, formWithErrors, initialColumn, columnTypes)),
        value => {
          // TODO remove "new" hack
          if ("new".equals(hash)) {
            val project = new Project
            project.name = value.name
            project.user = request.user
            project.hash = HashHelper.uuid()
            project.setColumns(createColumns(value))
            Neo4jProvider.get().projectRepository.save(project)
            Redirect(routes.ProjectController.edit(project.hash))
          } else {
            val dbProject = Neo4jProvider.get().projectRepository.findByHashAndUser(hash, request.user)
            dbProject.name = value.name
            dbProject.setColumns(createColumns(value))
            Redirect(routes.ProjectController.edit(hash))
          }
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
      Redirect(routes.ProjectController.list())
  }

  def edit(hash: String) = AuthenticatedBaseAction {
    implicit request =>
      val project = Neo4jProvider.get().projectRepository.findByHashAndUser(hash, request.user)
      val columns = if (project.columns != null) {
        project.getColumns.map {
          column =>
            CaseColumn(column.key, column.name, column.`type`.name()) // TODO column.properties
        }.toList
      } else {
        List()
      }
      val caseProject = CaseProject(project.name, columns)
      Ok(views.html.projectEdit(hash, ProjectController.projectForm.fill(caseProject), initialColumn, columnTypes))
  }

  override def messagesApi: MessagesApi = messages

  def initialColumn()(implicit messages: play.api.i18n.Messages) = CaseColumn("", "", ProjectColumnType.STRING.name())

  def initialProjectForm(): Form[CaseProject] = {
    val columns = List(
      CaseColumn("description", Messages("Description"), ProjectColumnType.STRING.name()),
      CaseColumn("planned", Messages("Planned"), ProjectColumnType.BOOLEAN.name())
    )
    ProjectController.projectForm.fill(CaseProject(Messages("project.default"), columns))
  }

  def columnTypes: String = "[\"" + ProjectColumnType.values().map { e => e.name}.mkString("\",\"") + "\"]"
}

// TODO move this to class
object ProjectController {
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
