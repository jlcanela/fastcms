package controllers

import java.io._
import java.util

import anorm._
import com.github.jknack.handlebars.Handlebars
import com.github.jknack.handlebars.cache.ConcurrentMapTemplateCache
import com.github.jknack.handlebars.io.FileTemplateLoader
import play.Logger
import play.api.Play.current
import play.api.db._
import play.api.libs.json._
import play.api.mvc._

import models.{Source, RoutingRule}
import api.{RenderingApi, ContentApi, RuleApi, WebsiteApi}


object ServeController extends Controller with ControllerHelper {

  implicit val sourceFormat = Json.format[Source]

  //import com.github.mustachejava.DefaultMustacheFactory
  //val mf = new DefaultMustacheFactory();
  //val m1 = mf.compile(new StringReader("""la test {{firstname}} mustache"""), "tsr")

  //Logger.info("tsr template:" + mf.getReader("tsr"))
  
  // mf.compile(new InputStreamReadear())
  
  val rules = DB.withConnection { implicit conn => RuleApi.fetchRoutingRule }

  
  def serve() = Action {
    implicit request =>
      val host = request.queryString("host").headOption getOrElse ""
      val uri = request.queryString("uri").headOption getOrElse ""
      

      val html = (for {
        contentRef <- ContentApi.findEntity(host, uri, rules)
        content <- ContentApi.aggregateEntity(contentRef, List())
        templateRef <- RenderingApi.findTemplate(contentRef, host, List())
      } yield RenderingApi.render(content, templateRef)).getOrElse("ERR")

       Ok(html).as("text/html")

  }



}
