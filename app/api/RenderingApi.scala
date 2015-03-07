package api

import org.codehaus.jackson.JsonNode

object RenderingApi {

  import ContentApi.EntityRef

//  trait TemplateIdentifier {}
  trait TemplatingRule {}
  case class TemplateRef()

  
  def findTemplate(ref: EntityRef, rules: List[TemplatingRule]): Option[TemplateRef] = ???

  def render(content: JsonNode, template: TemplateRef) : String = ???

}
