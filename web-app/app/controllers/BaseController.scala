package controllers

import com.google.gson.Gson
import neo4j.models.user.User
import neo4j.services.Neo4jProvider
import play.api.i18n.I18nSupport
import play.api.mvc._

/**
 * @author Johannes Unterstein (unterstein@me.com)
 */
trait BaseController extends Controller with I18nSupport {

  case class BaseRequest(request: Request[AnyContent], user: User)

  val gson = new Gson()

  val USER_HASH = "userHash"

  def BaseAction(f: Request[AnyContent] => Result): Action[AnyContent] = Action {
    implicit request =>
      f(request)
  }

  def AuthenticatedBaseAction()(f: => BaseRequest => Result) = Security.Authenticated(userInfo, onUnauthorized) {
    user =>
      BaseAction(request => {
        f(BaseRequest(request, null))
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
}
