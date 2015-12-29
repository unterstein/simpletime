package controllers

import javax.inject.Inject

import controllers.ProjectController.CaseProject
import neo4j.services.Neo4jProvider
import play.api.i18n.MessagesApi

import scala.collection.JavaConversions._

class ProjectController @Inject()(messages: MessagesApi) extends BaseController {

  def list = AuthenticatedBaseAction {
    implicit request =>
      Ok(views.html.projectList(Neo4jProvider.get().projectRepository.findByUser(request.user).toList))
  }

  def create = AuthenticatedBaseAction {
    implicit request =>
      Ok(views.html.projectEdit(ProjectController.projectForm.fill(CaseProject("TODO", List()))))
  }

  def edit(hash: String) = AuthenticatedBaseAction {
    implicit request =>

      Ok(views.html.projectEdit(ProjectController.projectForm.fill(CaseProject("TODO", List()))))
  }

  override def messagesApi: MessagesApi = messages
}

object ProjectController {

  case class CaseColumn(name: String, columnType: String)

  case class CaseProject(name: String, columns: List[CaseColumn])

  val projectForm: Form[CaseProject] = Form(
    mapping(
      "name" -> nonEmptyText,
      "columns" -> list(mapping(
        "name" -> nonEmptyText,
        "columnType" -> nonEmptyText
      )(CaseColumn.apply)(CaseColumn.unapply))
    )(CaseProject.apply)(CaseProject.unapply)
  )
}