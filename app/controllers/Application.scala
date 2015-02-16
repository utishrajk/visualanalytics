package controllers

import java.io.File

import play.Play
import play.api.mvc.Action
import play.api.mvc.Controller
import securesocial.core.SecureSocial


object Application extends Controller with SecureSocial {

  def index(any: String) = SecuredAction {
    implicit request =>
    Ok(views.html.index(request.user))
  }

  def loadCharts1A(any: String) = SecuredAction {
    implicit  request =>
    Ok(views.html.charts1A(request.user))
  }

  def loadCharts1B(any: String) = SecuredAction {
    implicit  request =>
      Ok(views.html.Charts1B())
  }

  def getURI(any: String): String = any match {
    case "main" => "/public/html/main.html"
    case "detail" => "/public/html/detail.html"
    case "visual" => "/public/html/visual.html"
    case _ => "error"
  }

  def loadPublicHTML(any: String) = Action {
    println("inside loadPublicHTML")
    val projectRoot = Play.application().path()
    var file = new File(projectRoot + getURI(any))
    if (file.exists())
      Ok(scala.io.Source.fromFile(file.getCanonicalPath()).mkString).as("text/html");
    else
      NotFound
  }

}