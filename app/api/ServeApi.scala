package api

import models.Rule

case class ServeApi(val rules: List[Rule]) {

  def contentRef(host:String, uri:String) : Option[String] = {
    rules.sortBy(_.priority).flatMap(_.findContent(host, uri)).headOption
  }

}

