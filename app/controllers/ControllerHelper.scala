package controllers

import models.Website

import play.api.libs.json._
import play.api.mvc._

import scalaz.\/

trait ControllerHelper { self : Controller =>

  implicit val websiteFormat = Json.format[Website]
  
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
        errors =>
          BadRequest(Json.obj("status" ->"KO", "message" -> JsError.toFlatJson(errors)))
            .withHeaders(("Access-Control-Allow-Origin", "*")),
        o =>
          Status(code)(Json.toJson(f(o))).withHeaders(("Access-Control-Allow-Origin", "*"))
      )
    }
  }

}
