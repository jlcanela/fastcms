package api

import models.RoutingRule
import org.codehaus.jackson.JsonNode

object ContentApi {


  type EntityRef = String

  trait AggregationRule {}


  def findEntity(host:String, uri: String, rules: List[RoutingRule]) : Option[EntityRef] =  {
    rules.sortBy(_.priority).flatMap(_.findContent(host, uri)).headOption
  }
  

  def aggregateEntity(ref: EntityRef, rules: List[AggregationRule]) : Option[JsonNode] = ???


}
