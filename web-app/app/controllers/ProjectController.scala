package controllers

import javax.inject.Inject

import controllers.ProjectController.{CaseColumn, CaseProject}
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
      Ok(views.html.projectList(Neo4jProvider.get().projectRepository.findByUser(request.user).toList))
  }

  def post(hash: String) = AuthenticatedBaseAction {
    implicit request =>
      // TODO do authorization check
      ProjectController.projectForm.bindFromRequest.fold(
        formWithErrors => Ok(views.html.projectEdit(hash, formWithErrors, ProjectController.initialColumn)),
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
            val dbProject = Neo4jProvider.get().projectRepository.findByHash(hash)
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
      Ok(views.html.projectEdit("new", ProjectController.initialProjectForm, ProjectController.initialColumn))
  }

  def edit(hash: String) = AuthenticatedBaseAction {
    // TODO do authorization check
    implicit request =>
      val project = Neo4jProvider.get().projectRepository.findByHash(hash)
      val columns = if (project.columns != null) {
        project.getColumns.map {
          column =>
            CaseColumn(column.key, column.name, column.`type`.name()) // TODO column.properties
        }.toList
      } else {
        List()
      }
      val caseProject = CaseProject(project.name, columns)
      Ok(views.html.projectEdit(hash, ProjectController.projectForm.fill(caseProject), ProjectController.initialColumn))
  }

  override def messagesApi: MessagesApi = messages
}

object ProjectController {

  case class CaseColumn(columnKey: String, columnName: String, columnType: String) {
    val columnTypes = ProjectColumnType.values().map { e => e.name}.toList.asJava
  }

  case class CaseProject(name: String, columns: List[CaseColumn])

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

  def initialColumn()(implicit messages: play.api.i18n.Messages) = CaseColumn("", "", ProjectColumnType.STRING.name())

  def initialProjectForm()(implicit messages: play.api.i18n.Messages): Form[CaseProject] = {
    val columns = List(
      CaseColumn("description", Messages("Description"), ProjectColumnType.STRING.name()),
      CaseColumn("planned", Messages("Planned"), ProjectColumnType.BOOLEAN.name())
    )
    projectForm.fill(CaseProject(Messages("project.default"), columns))
  }
}