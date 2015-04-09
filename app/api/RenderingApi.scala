package api

import com.github.jknack.handlebars.Handlebars
import com.github.jknack.handlebars.cache.ConcurrentMapTemplateCache
import com.github.jknack.handlebars.io.FileTemplateLoader
import com.github.jknack.handlebars.Template
import models.WebsiteDb
import org.codehaus.jackson.JsonNode
import play.api.libs.json.JsValue
import util.JsValueResolver
import play.api.Logger

object RenderingApi {

  import ContentApi.EntityRef

//  trait TemplateIdentifier {}
  trait TemplatingRule {}
  type TemplateRef = Template

  val cache = new ConcurrentMapTemplateCache()

  private def context(json: JsonNode) = {
    import com.github.jknack.handlebars.Context
    import com.github.jknack.handlebars.JsonNodeValueResolver
    Context
      .newBuilder(json)
      .resolver(JsonNodeValueResolver.INSTANCE)
      .build()
  }

  private def context(json: JsValue) = {
    import com.github.jknack.handlebars.Context
    Context
      .newBuilder(json)
      .resolver(JsValueResolver.INSTANCE)
      .build()
  }

  def findTemplate(ref: EntityRef, host: String, rules: List[TemplatingRule])(websiteDb: WebsiteDb): Option[TemplateRef] = try { 
    for {
      website <- websiteDb.all.filter { _.name == host} .headOption
      file = website.path // map { _ + s"$content.html"}
      loader = new FileTemplateLoader(file, ".html")
      handlebars = (new Handlebars(loader)).`with`(cache)
      template = handlebars.compile(ref)
    } yield template
  } catch { 
    case e: Exception => 
      Logger.error(s"ERR: $host / ref: $ref [${e.getClass}: ${e.getMessage}]")
      None 
  }



  def render(content: JsonNode, template: TemplateRef) : String = template.apply(context(content))
  def render(content: JsValue, template: TemplateRef) : String = template.apply(context(content))

}
