package controllers

import logic.UserLogic

case class CreateUserRequest(email: String, password: String)

case class AuthenticateUserRequest(email: String, password: String)

class UserController extends BaseController {

  def create = BaseAction {
    implicit request =>
      val createRequest = gson.fromJson(request.body.asJson.getOrElse("").toString, classOf[CreateUserRequest])
      if (createRequest != null) {
        val user = UserLogic.createUser(createRequest.email, createRequest.password)
        Ok(user.hash)
      } else {
        BadRequest
      }
  }

}
