package test;

import java.io.File
import java.util


import org.specs2.mutable._


class HandlebarsSpec extends Specification {
  
  def scopes = {
    val scopes = new java.util.HashMap[String, Any]()
    scopes.put("firstname", "jean-luc")
    scopes
  }
  "mustache" should {
    "be ok" in {
      import com.github.jknack.handlebars.Handlebars
      import com.github.jknack.handlebars.io.FileTemplateLoader


      val loader = new FileTemplateLoader(new File("test/resources/handlebars"), ".hbs");
      val handlebars = new Handlebars(loader);
      val template = handlebars.compile("home");
      val home = handlebars.compile("home");

      val hashMap = new util.Hashtable[String, String]()
      hashMap.put("firstname", "jean-luc")
      hashMap.put("job", "developer")
      home.apply(hashMap) must be_==(
        """<h1>Title-jean-luc</h1>
          |<p>Home page - developer</p>
          |<span>Powered by Handlebars.java</span>""".stripMargin)
    }
    
    "parse json" in {
      import com.github.jknack.handlebars.Handlebars
      import com.github.jknack.handlebars.Context
      import com.github.jknack.handlebars.JsonNodeValueResolver
      import com.github.jknack.handlebars.cache.ConcurrentMapTemplateCache

      import org.codehaus.jackson.map.ObjectMapper
      import org.codehaus.jackson.JsonNode

      val mapper = new ObjectMapper()
      val json = mapper.readValue("""{"firstname":"john"}""", classOf[JsonNode])
      val context = Context
        .newBuilder(json)
        .resolver(JsonNodeValueResolver.INSTANCE)
        .build();

      val cache = new ConcurrentMapTemplateCache()
      val handlebars = new Handlebars()
      val cachedhandlebars = handlebars.`with`(cache)
      val template = handlebars.compileInline("A{{firstname}}B")
      template.apply(context) must be_==("AjohnB")


    }
    
  }
  
}