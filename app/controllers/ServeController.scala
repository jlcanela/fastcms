package controllers

import java.io.{FileReader, OutputStreamWriter, ByteArrayOutputStream}

import anorm._
import play.Logger
import play.api.Play.current
import play.api.db._
import play.api.libs.json._
import play.api.mvc._

import models.{Source, Rule}
import api.{ServeApi, WebsiteApi}


object ServeController extends Controller with ControllerHelper {

  implicit val sourceFormat = Json.format[Source]

  import com.github.mustachejava.DefaultMustacheFactory
  val mf = new DefaultMustacheFactory();
  
  val rules = DB.withConnection { implicit conn => Rule.fetch }
  

  def serve() = Action {
    implicit request =>
      val host = request.queryString("host").headOption getOrElse ""
      val uri = request.queryString("uri").headOption getOrElse ""

      
      val bytes = (for {
        content <- ServeApi(rules).contentRef(host, uri)
        file <-  WebsiteApi.websiteDb.all.filter { _.name == host} .headOption.map { _.path } map { _ + s"$content.html"}
        mustache = mf.compile(new FileReader(new java.io.File(file)), content)
        scopes = new java.util.HashMap[String, Object]()
        out = new ByteArrayOutputStream(1024 * 64)
        writer = new OutputStreamWriter(out)
        _ = mustache.execute(writer, scopes)
        _ = writer.flush()
      } yield out.toByteArray).getOrElse("ERR".getBytes("UTF-8"))

      Logger.info("" + bytes.length)

       Ok(bytes).as("text/html")

  }



}
