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

  def create() = Action {
    implicit request =>
      val res = (for {
        v <- request.body.asJson
        r = Json.fromJson[Website](v)
        result : JsValue = r.fold (
          err => Error(err).toJson,
          good => Json.toJson(WebsiteDb.add(good))
        )
      } yield result) getOrElse JsString("Invalid Json Error")
      Ok(res)
  
  }

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
