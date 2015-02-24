package controllers

import anorm._
import api.WebsiteApi
import models.Source
import play.Logger
import play.api.Play.current
import play.api.db._
import play.api.libs.json._
import play.api.mvc._

import java.io.{FileReader, OutputStreamWriter, ByteArrayOutputStream}

case class Rule(id: Int, uri: String, content: String, priority: Int) {
}

object ServeController extends Controller with ControllerHelper {

  implicit val sourceFormat = Json.format[Source]

  import com.github.mustachejava.DefaultMustacheFactory
  val mf = new DefaultMustacheFactory();
  
  val rules : List[Rule] = DB.withConnection { implicit conn =>
    // do whatever you need with the connection
    // Create an SQL query
    val selectRules = SQL("Select * from RULE")

    selectRules().map(row =>
      row[Int]("ID") -> row[String]("URI") -> row[String]("CONTENT") -> row[Int]("PRIORITY")
    ).map { case (((id, uri), content), priority) => Rule(id, uri, content, priority) }.toList
  }


  def serve() = Action {
    implicit request =>
      val host = request.queryString("host").headOption getOrElse ""
      val uri = request.queryString("uri").headOption getOrElse ""

      val bytes = (for {
        content <- rules.filter { _.uri == uri }.sortBy(_.priority).headOption.map(_.content)
        file <-  WebsiteApi.websiteDb.all.filter { _.name == host} .headOption.map { _.path } map { _ + s"$content.html"}
        mustache = mf.compile(new FileReader(new java.io.File(file)), content)
        scopes = new java.util.HashMap[String, Object]()
        out = new ByteArrayOutputStream(1024 * 64)
        writer = new OutputStreamWriter(out)
        _ = mustache.execute(writer, scopes)
        _ = writer.flush()
      } yield out.toByteArray) getOrElse("ERR".getBytes("UTF-8"))

      Logger.info("" + bytes.length)

       Ok(bytes).as("text/html")

  }



}
