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

import models.{Source, Rule}
import api.{ServeApi, WebsiteApi}


object ServeController extends Controller with ControllerHelper {

  implicit val sourceFormat = Json.format[Source]

  //import com.github.mustachejava.DefaultMustacheFactory
  //val mf = new DefaultMustacheFactory();
  //val m1 = mf.compile(new StringReader("""la test {{firstname}} mustache"""), "tsr")

  //Logger.info("tsr template:" + mf.getReader("tsr"))
  
  // mf.compile(new InputStreamReadear())
  
  val rules = DB.withConnection { implicit conn => Rule.fetch }
  
  val cache = new ConcurrentMapTemplateCache()

  def context = {
    import com.github.jknack.handlebars.Context
    import com.github.jknack.handlebars.JsonNodeValueResolver
    import org.codehaus.jackson.map.ObjectMapper
    import org.codehaus.jackson.JsonNode

    val mapper = new ObjectMapper()
    val json = mapper.readValue("""{"firstname":"john"}""", classOf[JsonNode])
    Context
      .newBuilder(json)
      .resolver(JsonNodeValueResolver.INSTANCE)
      .build()
  }
  
  def serve() = Action {
    implicit request =>
      val host = request.queryString("host").headOption getOrElse ""
      val uri = request.queryString("uri").headOption getOrElse ""
      

      val html = (for {
        content <- ServeApi(rules).contentRef(host, uri)
        file <-  WebsiteApi.websiteDb.all.filter { _.name == host} .headOption.map { _.path }// map { _ + s"$content.html"}
        loader = new FileTemplateLoader(file, ".html")
        handlebars = (new Handlebars(loader)).`with`(cache)
        template = handlebars.compile(content)
      } yield template.apply(context)).getOrElse("ERR")

      Logger.info("" + html.length)

       Ok(html).as("text/html")

  }



}
