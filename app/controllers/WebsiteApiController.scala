package controllers

import api.{WebsiteApiConfig, WebsiteApi}
import models.{WebsiteDb, Website}
import play.api.Play

import play.api.libs.json._
import play.api.mvc._


object WebsiteApiController extends Controller with ControllerHelper {


  /*
  import java.io.{FileReader, OutputStreamWriter, ByteArrayOutputStream, StringReader}

  import com.github.mustachejava.DefaultMustacheFactory

  val mf = new DefaultMustacheFactory();
  val mustache = mf.compile(new FileReader(new java.io.File("node/views/home.handlebars")), "example");
  def html() = Action {
    implicit request =>
        val scopes = new util.HashMap[String, Object]();
        scopes.put("name", "Mustache")
        val out = new ByteArrayOutputStream(1024 * 64)
        //scopes.put("feature", new Feature("Perfect!"));
        val  writer = new OutputStreamWriter(out);
        mustache.execute(writer, scopes);
        writer.flush();

      Ok(out.toByteArray).as("text/html")
  }*/

  //val config = new play.Configuration(app.configuration)
  
  val websiteApiConfig : WebsiteApiConfig = WebsiteApiConfig(new play.Configuration(Play.current.configuration))

  val websiteDb = new WebsiteDb(websiteApiConfig.configDb, websiteApiConfig.adminPort, websiteApiConfig.wwwPath)


  def list() = Action {
    implicit request =>
      Ok(Json.toJson(websiteDb.all)).withHeaders(("Access-Control-Allow-Origin", "*"));
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
