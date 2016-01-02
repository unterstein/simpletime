package controllers

import javax.inject.Inject

import controllers.ProjectController.CaseProject
import neo4j.models.project.{ProjectColumn, Project, ProjectColumnType}
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
      ProjectController.projectForm.bindFromRequest.fold(
        formWithErrors => Ok(views.html.projectEdit(hash, formWithErrors, ProjectController.initialColumn)),
        value => {
          // TODO remove "new" hack
          if ("new".equals("hash")) {
            val project = new Project
            project.name = value.name
            project.id = value.id
            project.user = request.user
            project.columns = value.columns.map {
              column =>
                val projectColumn = new ProjectColumn
                projectColumn.name = column.columnName
                projectColumn.`type` = ProjectColumnType.valueOf(column.columnType)
                projectColumn
            }.toList
            Neo4jProvider.get().projectRepository.save(project)
          } else {
            // edit
          }
          Ok("")
        }
      )
  }

  def create = AuthenticatedBaseAction {
    implicit request =>
      Ok(views.html.projectEdit("new", ProjectController.initialProjectForm, ProjectController.initialColumn))
  }

  def edit(hash: String) = AuthenticatedBaseAction {
    implicit request =>
      // TODO load from database
      Ok(views.html.projectEdit(hash, ProjectController.projectForm.fill(CaseProject(-1L, "TODO", List())), ProjectController.initialColumn))
  }

  override def messagesApi: MessagesApi = messages
}

object ProjectController {

  case class CaseColumn(columnId: Long, columnName: String, columnType: String) {
    val columnTypes = ProjectColumnType.values().map { e => e.name}.toList.asJava
  }

  case class CaseProject(id: Long, name: String, columns: List[CaseColumn])

  val projectForm: Form[CaseProject] = Form(
    mapping(
      "id" -> longNumber,
      "name" -> nonEmptyText,
      "columns" -> list(mapping(
        "columnId" -> longNumber,
        "columnName" -> nonEmptyText,
        "columnType" -> nonEmptyText
      )(CaseColumn.apply)(CaseColumn.unapply))
    )(CaseProject.apply)(CaseProject.unapply)
  )

  def initialColumn()(implicit messages: play.api.i18n.Messages) = CaseColumn(-1L, "", ProjectColumnType.STRING.name())

  def initialProjectForm()(implicit messages: play.api.i18n.Messages): Form[CaseProject] = {
    val columns = List(
      CaseColumn(-1L, Messages("description"), ProjectColumnType.STRING.name()),
      CaseColumn(-1L, Messages("planned"), ProjectColumnType.BOOLEAN.name())
    )
    projectForm.fill(CaseProject(-1L, Messages("project.default"), columns))
  }
}