package controllers

import javax.inject.Inject

import com.google.gson.Gson
import controllers.ProjectController.CaseProject
import neo4j.models.project.ProjectColumnType
import neo4j.services.Neo4jProvider
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.{Messages, MessagesApi}

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._

import java.util

class ProjectController @Inject()(messages: MessagesApi) extends BaseController {

  def list = AuthenticatedBaseAction {
    implicit request =>
      Ok(views.html.projectList(Neo4jProvider.get().projectRepository.findByUser(request.user).toList))
  }

  def create = AuthenticatedBaseAction {
    implicit request =>
      Ok(views.html.projectEdit(true, ProjectController.initialProjectForm))
  }

  def edit(hash: String) = AuthenticatedBaseAction {
    implicit request =>

      Ok(views.html.projectEdit(false, ProjectController.projectForm.fill(CaseProject(-1L, "TODO", List()))))
  }

  override def messagesApi: MessagesApi = messages
}

object ProjectController {

  case class CaseColumn(columnId: Long, columnName: String, columnType: String)

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

  def initialProjectForm()(implicit messages: play.api.i18n.Messages): Form[CaseProject] = {
    val columns = List(
      CaseColumn(-1L, Messages("description"), ProjectColumnType.STRING.name()),
      CaseColumn(-1L, Messages("planned"), ProjectColumnType.BOOLEAN.name())
    )
    projectForm.fill(CaseProject(-1L, Messages("project.default"), columns))
  }
}