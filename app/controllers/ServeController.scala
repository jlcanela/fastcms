package controllers


import scala.concurrent._

import play.{Play, Logger}
import play.api.Play.current
import play.api.db._
import play.api.libs.json._
import play.api.mvc._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.cache.Cache

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

      def timer = (new java.util.Date).getTime
      val startTime = timer

      def error(msg: String) = Ok(s"ERR $msg")
      
      ContentApi.findEntity(host, uri, routingRules) map { contentRef =>
        
        Option(Cache.get(s"$host:$contentRef")) map { html => Logger.info(s"total time: $uri : ${timer - startTime}");Future.successful(Ok(html.toString).as("text/html"))} getOrElse {
         
          def refs = for {
            templateRef <-  RenderingApi.findTemplate(contentRef, host, List())(websiteDb)
          } yield (templateRef)

          def html = refs map { templateRef =>
            for {
              content <- ContentApi.aggregateEntityHttp(contentRef, sources, aggregationRules)
              html = RenderingApi.render(content, templateRef)
              _ = Cache.set(s"$host:$contentRef", html)
              _ = Logger.info(s"total time: $uri : ${timer - startTime}")
            } yield Ok(html).as("text/html")
          } getOrElse { Future.successful(error("content not found")) }
          
          html

        }
        
      } getOrElse Future.successful(error("entity not identified"))

  }
  
}
