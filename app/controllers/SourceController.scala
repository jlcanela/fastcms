package controllers


import api.RuleApi
import models.{RoutingRule, Source}
import play.api.libs.json._
import play.api.mvc._
import play.api.db._
import play.api.Play.current
import play.api.Logger


object SourceController extends Controller with ControllerHelper {

  implicit val sourceFormat = Json.format[Source]
  implicit val ruleFormat = Json.format[RoutingRule]

  def list() = Action {
    implicit request =>
      def res = DB.withConnection { implicit conn => Source.fetch }

      Ok(Json.toJson(res)).withHeaders(("Access-Control-Allow-Origin", "*"));
  }


  def rules = Action {
    implicit request =>
      def res = DB.withConnection { implicit conn => RuleApi.fetchRoutingRule }

      Ok(Json.toJson(res)).withHeaders(("Access-Control-Allow-Origin", "*"));
  }


}
