package controllers

import api.WebsiteApi
import models.Website
import models.Source
import play.api.libs.json._
import play.api.mvc._
import play.api.db._
import play.api.Play.current
import anorm._
//import anorm._
import play.api.Logger

object SourceController extends Controller with ControllerHelper {

  implicit val sourceFormat = Json.format[Source]
  
  def list() = Action {
    implicit request =>
      def res = DB.withConnection { implicit conn =>
        // do whatever you need with the connection
        // Create an SQL query
        val selectSources = SQL("Select * from Source")

        // Transform the resulting Stream[Row] to a List[(String,String)]
        selectSources().map(row =>
          row[String]("name") -> row[String]("url")
        ).map { case (name, url) => Source(name, url) }.toList
      }

      Ok(Json.toJson(res)).withHeaders(("Access-Control-Allow-Origin", "*"));
  }



}
