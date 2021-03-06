package controllers

import com.google.gson.Gson
import neo4j.models.project.{ProjectColumnType, Project}
import neo4j.models.user.User
import neo4j.services.Neo4jProvider
import play.api.i18n.{Messages, I18nSupport}
import play.api.mvc.Security.AuthenticatedRequest
import play.api.mvc._
import scala.collection.JavaConversions._

/**
 * Base controller handling user authentication handling and common helpers.
 *
 * @author Johannes Unterstein (unterstein@me.com)
 */
trait BaseController extends Controller with I18nSupport {

  class BaseRequest(request: Request[AnyContent], user: User) extends AuthenticatedRequest[AnyContent, User](user, request)

  val gson = new Gson()

  val USER_HASH = "userHash"

  def BaseAction(f: Request[AnyContent] => Result): Action[AnyContent] = Neo4jTransactionAction {
    implicit request =>
      f(request)
  }

  def AuthenticatedBaseAction(f: => BaseRequest => Result) = Security.Authenticated(userInfo, onUnauthorized) {
    user =>
      BaseAction(request => {
        f(new BaseRequest(request, user))
      })
  }

  private def onUnauthorized(requestHeader: RequestHeader) = Unauthorized

  def userInfo(request: play.api.mvc.RequestHeader): Option[User] = {
    request match {
      case request: BaseRequest =>
        Option(request.user)
      case _ =>
        val userHash: String = request.session.get(USER_HASH).orNull

        if (userHash != null) {
          Option(Neo4jProvider.get().userRepository.findByHash(userHash))
        } else {
          None
        }
    }
  }

  def projectToColumnList(project: Project): List[CaseColumn] = {
    if (project.columns != null) {
      project.getColumns.map {
        column =>
          CaseColumn(column.key, column.name, column.`type`.name()) // TODO column.properties
      }.toList
    } else {
      List()
    }
  }

  def defaultColumns()(implicit lang: play.api.i18n.Lang) = List(CaseColumn("id", Messages("entry.id"), ProjectColumnType.HIDDEN.name()), CaseColumn("start", Messages("entry.start"), ProjectColumnType.TIME.name()), CaseColumn("end", Messages("entry.end"), ProjectColumnType.TIME.name()))

  def trashColumn()(implicit lang: play.api.i18n.Lang) = List(CaseColumn("trash", Messages("delete"), ProjectColumnType.DELETE.name()))
}
