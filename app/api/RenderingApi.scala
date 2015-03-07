package api

import com.github.jknack.handlebars.Handlebars
import com.github.jknack.handlebars.cache.ConcurrentMapTemplateCache
import com.github.jknack.handlebars.io.FileTemplateLoader
import com.github.jknack.handlebars.Template
import org.codehaus.jackson.JsonNode

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
  
  def findTemplate(ref: EntityRef, host: String, rules: List[TemplatingRule]): Option[TemplateRef] =
    for {
      file <- WebsiteApi.websiteDb.all.filter { _.name == host} .headOption.map { _.path }// map { _ + s"$content.html"}
      loader = new FileTemplateLoader(file, ".html")
      handlebars = (new Handlebars(loader)).`with`(cache)
      template = handlebars.compile(ref)
    } yield template


  def render(content: JsonNode, template: TemplateRef) : String = template.apply(context(content))

}
