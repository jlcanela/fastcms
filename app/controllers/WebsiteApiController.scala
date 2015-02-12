package controllers

import api._
import models.{Website, WebsiteDb}
import play.api.libs.json._

import play.api.mvc._
import play.api.mvc.Result

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
  } yield r) getOrElse JsString("Invalid Json Error")

  def JsonParserAction[T, U](f: T => U): Action[AnyContent] = {
    Action { implicit request =>
        Ok(toJsResult(request.body.asJson, WebsiteDb.add _))
    }
  }
  

  def create() = JsonParserAction(WebsiteDb.add)

  def delete(id:Int) = Action {
    implicit request =>
      WebsiteDb.delete(id)
      Ok
  }

  def update(id:Int) = Action {
    implicit request =>
      WebsiteDb.update(null)
      Ok
  }

}
