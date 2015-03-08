package controllers


import api.DbApi
import models.{AggregationRule, Article, RoutingRule, Source}
import play.api.libs.json._
import play.api.mvc._
import play.api.db._
import play.api.Play.current
import play.api.Logger


object SourceController extends Controller with ControllerHelper {

  implicit val sourceFormat = Json.format[Source]
  implicit val ruleFormat = Json.format[RoutingRule]
  implicit val aggregationRuleFormat = Json.format[AggregationRule]
  implicit val articleFormat = Json.format[Article]

  private def fetch[T](f: java.sql.Connection => List[T])(implicit format: Format[T]) = Action {
    implicit request =>
      def res = DB.withConnection { implicit conn => f(conn) }
      Ok(Json.toJson(res)).withHeaders(("Access-Control-Allow-Origin", "*"));
  }
  
  def list() = Action {
    implicit request =>
      def res = DB.withConnection { implicit conn => DbApi.fetchSource }

      Ok(Json.toJson(res)).withHeaders(("Access-Control-Allow-Origin", "*"));
  }


  def rules = Action {
    implicit request =>
      def res = DB.withConnection { implicit conn => DbApi.fetchRoutingRule }

      Ok(Json.toJson(res)).withHeaders(("Access-Control-Allow-Origin", "*"));
  }

  def aggregationRules = fetch[AggregationRule] { conn:  java.sql.Connection => DbApi.fetchAggregationRule(conn) }
  
  def articles = fetch[Article] { conn:  java.sql.Connection => DbApi.fetchArticle(conn) }


}
