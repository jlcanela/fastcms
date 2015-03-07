package models

import java.text.MessageFormat

case class RoutingRule(id: Int, host: String, uri: String, content: String, priority: Int) {
  
  def findContent(host: String, uri: String) = {
    this.uri.r.unapplySeq(uri) match {
      case Some(x) => Some(MessageFormat.format(content, x.map(_.toString) :_*))
      case _ => None
    }
  }
}

