package test;

import java.io.{File,OutputStreamWriter, ByteArrayOutputStream, StringReader}
import java.util

import com.github.jknack.handlebars.Handlebars
import com.github.jknack.handlebars.io.FileTemplateLoader


import org.specs2.mutable._


class HandlebarsSpec extends Specification {
  
  def scopes = {
    val scopes = new java.util.HashMap[String, Any]()
    scopes.put("firstname", "jean-luc")
    scopes
  }
  "mustache" should {
    "be ok" in {

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
    
  }
  
}