package controllers

import play.api.libs.json._

case class Error(err: Seq[(play.api.libs.json.JsPath, Seq[play.api.data.validation.ValidationError])]) {

  def toJsonErr(err: (play.api.libs.json.JsPath, Seq[play.api.data.validation.ValidationError])) = {
    JsObject(Seq(err._1.toJsonString -> JsArray(err._2.map{e=> JsString(e.message)})))
  }

  def toJson : JsValue=  {
    JsObject(Seq("error" -> JsArray(err.map(toJsonErr _))))
  }
}
