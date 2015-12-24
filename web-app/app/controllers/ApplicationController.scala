package controllers

class ApplicationController extends BaseController {

  def index = BaseAction {
    implicit request =>
      Ok("ok")
  }

}
