package controllers

import api.WebsiteApi
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
  
  def list() = Action {
    implicit request =>
      Ok(Json.toJson(WebsiteDb.all))
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
          BadRequest(Json.obj("status" ->"KO", "message" -> JsError.toFlatJson(errors)))
        },
        o =>
          Status(code)(Json.toJson(f(o)))
          //Ok(Json.obj("status" ->"OK", "message" -> Json.toJson(f(o)) ))
      )
    }
  }

  def create() = JsonParserAction(CREATED, WebsiteApi.create)

  def delete(id:Int) = Action {
    implicit request =>
      WebsiteDb.delete(id)
      Ok(JsObject(Seq("success" -> JsBoolean(true))))
  }

  def update(id:Int) = JsonParserAction(ACCEPTED, WebsiteDb.update(id))

}
