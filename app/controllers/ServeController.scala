package controllers

import java.text.MessageFormat

import scala.concurrent._

import play.{Play, Logger}
import play.api.Play.current
import play.api.db._
import play.api.libs.json._
import play.api.mvc._
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import models.{WebsiteDb, Source}
import api._


object ServeController extends Controller with ControllerHelper {

  implicit val sourceFormat = Json.format[Source]
  lazy val config = Play.application().configuration()
  lazy val aggregateServiceUrl = config.getString("content.aggregateUrl")
  lazy val websiteApiConfig = WebsiteApiConfig(config)
  lazy val websiteDb = WebsiteDb(config.getString("configDb"),websiteApiConfig.adminPort, websiteApiConfig.wwwPath)

  val sources = Map(DB.withConnection { implicit conn => DbApi.fetchSource }.map { s => (s.name, s) }:_*)
  val routingRules = DB.withConnection { implicit conn => DbApi.fetchRoutingRule }
  val aggregationRules = DB.withConnection { implicit conn => DbApi.fetchAggregationRule }

  def serve() = Action.async {
    implicit request =>
      val host = request.queryString("host").headOption getOrElse ""
      val uri = request.queryString("uri").headOption getOrElse ""

      def error = Ok("ERR")
      Logger.info(host)
      def refs = for {
        contentRef <- ContentApi.findEntity(host, uri, routingRules)
        templateRef <-  RenderingApi.findTemplate(contentRef, host, List())(websiteDb)
      } yield (contentRef, templateRef)
      
      refs map { case (contentRef, templateRef) =>
        for {
          content <- ContentApi.aggregateEntityHttp(contentRef, sources, aggregationRules)
          html = RenderingApi.render(content, templateRef)
        } yield Ok(html).as("text/html")
      } getOrElse { Future.successful(error) }

  }
  
}
