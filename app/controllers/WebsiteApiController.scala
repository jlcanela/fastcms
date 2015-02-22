package controllers

import java.io.{FileReader, OutputStreamWriter, ByteArrayOutputStream, StringReader}
import java.util

import api.WebsiteApi
import com.github.mustachejava.DefaultMustacheFactory
import models.{Website, WebsiteDb}

import play.Play
import play.api.libs.json._
import play.api.mvc._

import scalaz.\/

case class Error(err: Seq[(play.api.libs.json.JsPath, Seq[play.api.data.validation.ValidationError])]) {
  def toJsonErr(err: (play.api.libs.json.JsPath, Seq[play.api.data.validation.ValidationError])) = {
    JsObject(Seq(err._1.toJsonString -> JsArray(err._2.map{e=> JsString(e.message)})))
  }
  def toJson : JsValue=  {
    JsObject(Seq("error" -> JsArray(err.map(toJsonErr _))))
    
  }
}

object WebsiteApiController extends Controller {


  implicit val websiteFormat = Json.format[Website]

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
  }

  def list() = Action {
    implicit request =>
      Ok(Json.toJson(WebsiteDb.all)).withHeaders(("Access-Control-Allow-Origin", "*"));
  }

  def toJsResult[T, U](json: Option[JsValue], f: T => U)(implicit ev: Reads[T], ev2: Writes[U]) = (for {
    v <- json
    r = Json.fromJson[T](v)(ev).fold(
      err => Error(err).toJson,
      good => Json.toJson(f(good))
    )
  } yield r) getOrElse JsString(s"Invalid Json Error: $json")

  implicit val unitSer = new Writes[Unit] {
    def writes(u: Unit) = JsObject(Seq("success" -> JsBoolean(true)))
  }
  
  implicit val scalazSerializer = new Writes[\/[String, Website]] {
    def writes(w: \/[String, Website]) = {
      w.fold( 
        err => JsObject(Seq("success" -> JsBoolean(false), "error" -> JsString(err))) ,
        website => Json.toJson(website)
      )
      
    }
    
    
  }
  
  def JsonParserAction[T, U](code: Int, f: T => U)(implicit rds: Reads[T], wrs: Writes[U]): Action[JsValue] = {
    Action(BodyParsers.parse.json) { request =>

      val obj = request.body.validate[T]
      obj.fold(
        errors => {
          BadRequest(Json.obj("status" ->"KO", "message" -> JsError.toFlatJson(errors))).withHeaders(("Access-Control-Allow-Origin", "*"))
        },
        o =>
          Status(code)(Json.toJson(f(o))).withHeaders(("Access-Control-Allow-Origin", "*"))
          //Ok(Json.obj("status" ->"OK", "message" -> Json.toJson(f(o)) ))
      )
    }
  }

  def create() = JsonParserAction(CREATED, WebsiteApi.create)

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
      WebsiteDb.delete(id)
      Ok(JsObject(Seq("success" -> JsBoolean(true))))
  }

  def update(id:Int) = JsonParserAction(ACCEPTED, WebsiteDb.update(id))

}
