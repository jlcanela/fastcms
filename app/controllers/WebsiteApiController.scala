package controllers

import api.{WebsiteApiConfig, WebsiteApi}
import models.{WebsiteDb, Website}
import play.api.Play

import play.api.libs.json._
import play.api.mvc._

import scalaz._
import Scalaz._


object WebsiteApiController extends Controller with ControllerHelper {

  val websiteApiConfig : WebsiteApiConfig = WebsiteApiConfig(new play.Configuration(Play.current.configuration))

  val websiteDb = new WebsiteDb(websiteApiConfig.configDb, websiteApiConfig.adminPort, websiteApiConfig.wwwPath)


  def list() = Action {
    implicit request =>
      Ok(Json.toJson(websiteDb.all)).withHeaders(("Access-Control-Allow-Origin", "*"));
  }

  case class Entry(name: String, id: String, target: Option[String]) {
    def toJson = JsObject(Seq("roleName" -> JsString(name), "roleId" -> JsString(id)) ++ target.map(t => ("target" -> JsString(t))).toSeq)
  }

  def generateMenu(f: Website => Tree[Entry]) = {
    
    val addWebsite = Entry("Add website", "add-website", None)
    val websites = Entry("Websites", "websites", None)
    val sources = Entry("Sources", "sources", None)
    def allMenu = websiteDb.all.filter(_.name != "admin").map(f)
    def treeToJson(t: Tree[Entry]): JsObject = t.rootLabel.toJson + ("children" -> JsArray(t.subForest.toSeq.map(treeToJson _)))
    
    JsArray((addWebsite.toJson :: websites.toJson :: sources.toJson :: allMenu.map(treeToJson _)).toSeq)
  }

  def menu() = Action {
    implicit request =>
      Ok(generateMenu(ws =>
        Entry(ws.name, "website", None).node(
          Entry("Routing rule", "routing-rule", None).leaf,
          Entry("Aggregation rule", "aggregation-rule", None).leaf,
          Entry("Preview", "preview", s"http://localhost:${ws.port}/".some).leaf,
          Entry("Logs", "logs", None).leaf)
      )).withHeaders(("Access-Control-Allow-Origin", "*"));
  }


  def create() = JsonParserAction(CREATED, WebsiteApi.create(websiteApiConfig, websiteDb))

  def options = Action {
    Ok("").withHeaders(
      "Access-Control-Allow-Origin" -> "*",
      "Access-Control-Allow-Methods" -> "GET, POST, PUT, DELETE, OPTIONS",
      "Access-Control-Allow-Headers" -> "Accept, Origin, Content-type, X-Json, X-Prototype-Version, X-Requested-With",
      "Access-Control-Allow-Credentials" -> "true",
      "Access-Control-Max-Age" -> (60 * 60 * 24).toString
    )
  }

  def delete(id:Int) = Action {
    implicit request =>
      websiteDb.delete(id)
      Ok(JsObject(Seq("success" -> JsBoolean(true))))
  }

  def update(id:Int) = JsonParserAction(ACCEPTED, websiteDb.update(id))

}
