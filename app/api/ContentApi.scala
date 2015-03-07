package api

import java.text.MessageFormat

import scala.concurrent.Future

import org.codehaus.jackson.JsonNode

import play.api.libs.json.{JsObject, JsValue}
import play.api.libs.ws.WS
import play.api.Play.current
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import models.{Source, AggregationRule, RoutingRule}

object ContentApi {

  type EntityRef = String

  def findEntity(host:String, uri: String, rules: List[RoutingRule]) : Option[EntityRef] =  {
    rules.sortBy(_.priority).flatMap(_.findContent(host, uri)).headOption
  }

  def aggregateEntityHttp(entityRef: EntityRef, sources: Map[String, Source], rules: List[AggregationRule]) : Future[JsValue] = {
    val fragments = rules.filter(_.content == entityRef).map { ar =>
      val url = sources(ar.source).url
      val target = ar.target
      WS.url(url).get.map { response => JsObject(Seq(target -> response.json)) }
    }
    val o = JsObject(Seq())
    val b = JsObject(Seq())

    Future.sequence[JsObject, List](fragments) .map { _.foldLeft(JsObject(Seq()))(_ ++ _) }
  }
  
  def aggregateEntity(ref: EntityRef, rules: List[AggregationRule]) : Option[JsonNode] = {
    import org.codehaus.jackson.map.ObjectMapper
    import org.codehaus.jackson.JsonNode

    val mapper = new ObjectMapper()

      Some(mapper.readValue("""{"firstname":"john"}""", classOf[JsonNode]))
  }


}
