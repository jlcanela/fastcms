package controllers

import api._
import models.{Source, WebsiteDb}
import play.Play
import play.api.Play.current
import play.api.db._
import play.api.libs.json._
import play.api.libs.ws.WS
import play.api.mvc._
import scala.concurrent._

import play.api.libs.concurrent.Execution.Implicits.defaultContext


object ContentController extends Controller with ControllerHelper {

  lazy val sources = DB.withConnection { implicit conn => DbApi.fetchSource }
  
  def content(name: String) = Action.async {

    sources.filter(_.name == name).headOption map { source =>
      WS.url(source.url).get.map {
        response => Ok(JsObject(Seq(source.name -> response.json)))
      }
    } getOrElse future { Ok(JsObject(Seq(name -> JsNull)))}

  }

}
