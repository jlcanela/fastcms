package controllers


import java.sql.Connection

import api.WebsiteApi
import models.Website
import models.{Rule, Source}
import play.api.libs.json._
import play.api.mvc._
import play.api.db._
import play.api.Play.current
import anorm._

import scala.reflect.ClassTag

//import anorm._
import play.api.Logger


object SourceController extends Controller with ControllerHelper {

  implicit val sourceFormat = Json.format[Source]
  implicit val ruleFormat = Json.format[Rule]
  
  import anorm.SqlParser._



  def list() = Action {
    implicit request =>
      def res = DB.withConnection { implicit conn => Source.fetch }

      Ok(Json.toJson(res)).withHeaders(("Access-Control-Allow-Origin", "*"));
  }


  def rules = Action {
    implicit request =>
      def res = DB.withConnection { implicit conn => Rule.fetch }

      Ok(Json.toJson(res)).withHeaders(("Access-Control-Allow-Origin", "*"));
  }


}
