package controllers

import javax.inject.Inject

import neo4j.services.Neo4jProvider
import play.api.i18n.MessagesApi
import scala.collection.JavaConversions._

class ProjectController @Inject()(messages: MessagesApi) extends BaseController {

  def list = AuthenticatedBaseAction {
    implicit request =>
      Ok(views.html.projectsList(Neo4jProvider.get().projectRepository.findByUser(request.user).toList))
  }

  override def messagesApi: MessagesApi = messages
}
