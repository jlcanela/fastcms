package test


import models.Website
import org.specs2.mutable._
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.{WSResponse, WS}

import play.api.test._
import play.api.test.Helpers._

import scala.concurrent.Future

trait WebsiteHelper {
  implicit val app: FakeApplication
  def websites_GET = WS.url(s"http://localhost:3333/websites").get
  def websites_POST(body: String) = WS.url(s"http://localhost:3333/websites").withHeaders("Content-Type" -> "application/json").post(body)
  def websites_PUT(id: Int, body: String) = WS.url(s"http://localhost:3333/websites/$id").withHeaders("Content-Type" -> "application/json").put(body)
  def websites_DELETE(id: Int) = WS.url(s"http://localhost:3333/websites/$id").delete()
  
}

trait TestServerHelper { self: Specification =>
  def haveResult(msg: String, code: Int, json: JsValue) = (res : Future[WSResponse]) => res map { r =>
    (r.status aka s"status when $msg" must equalTo(code)) and (r.json must equalTo(json))
  } await()

  def beBadRequest(msg: String, code: Int) = (res : Future[WSResponse]) => res map { r =>
    r.status aka s"status when $msg" must equalTo(code)
  } await()

  def app(websites: List[Website]) = {
    implicit val format = Json.format[Website]
    FakeApplication(additionalConfiguration = Map("db.website" -> Json.toJson(websites).toString))
  }

  abstract class CustomServer(websites: List[Website]) extends WithServer(port = 3333, app = app(websites)) with WebsiteHelper
}

class FunctionalSpec extends Specification with WebsiteFixture with TestServerHelper {

  "API" should {
    "provide websites_GET API" in {
      "simple get succeeds" in new CustomServer(DB_WEBSITES) {
        websites_GET must haveResult(msg = "simple get", code = OK, json = WEBSITES_JSON)
      }
    }

    "provite a websites_POST API" in {
       "create_valid succeeds" in new CustomServer(DB_WEBSITES) {
          websites_POST(WEBSITE_JSON.toString) must haveResult(msg = "create_valid", code = CREATED, json = WEBSITE_JSON)
      }
      "create_empty fails" in new CustomServer(DB_WEBSITES) {
        websites_POST("") must beBadRequest(msg = "create_empty", code = BAD_REQUEST)
      }
      "create_invalid_json fails" in new CustomServer(DB_WEBSITES) {
        websites_POST("invalid_json") must beBadRequest(msg = "create_invalid_json", code = BAD_REQUEST)
      }
      "create_uncompliant_json fails" in new CustomServer(DB_WEBSITES) {
        websites_POST("[]") must haveResult(msg = "create_valid", code = BAD_REQUEST, json = Json.parse("""{"status":"KO","message":{"obj.id":[{"msg":"error.path.missing","args":[]}],"obj.name":[{"msg":"error.path.missing","args":[]}],"obj.url":[{"msg":"error.path.missing","args":[]}]}}"""))
      }

    }

    "provite a websites_PUT API" in {
      "update_valid succeeds" in new CustomServer(DB_WEBSITES) {
        websites_PUT(0, WEBSITE_UPDATE_JSON.toString) must haveResult(msg = "update_valid", code = ACCEPTED, json = WEBSITE_UPDATE_JSON)
      }
      "create_empty fails" in new CustomServer(DB_WEBSITES) {
        websites_PUT(0, "") must beBadRequest(msg = "update_empty", code = BAD_REQUEST)
      }
      "create_invalid_json fails" in new CustomServer(DB_WEBSITES) {
        websites_PUT(0, "invalid_json") must beBadRequest(msg = "update_invalid_json", code = BAD_REQUEST)
      }
      "create_uncompliant_json fails" in new CustomServer(DB_WEBSITES) {
        websites_PUT(0, "[]") must haveResult(msg = "update_valid", code = BAD_REQUEST, json = Json.parse("""{"status":"KO","message":{"obj.id":[{"msg":"error.path.missing","args":[]}],"obj.name":[{"msg":"error.path.missing","args":[]}],"obj.url":[{"msg":"error.path.missing","args":[]}]}}"""))
      }
    }

    "provite a websites_DELETE API" in {
      "delete_valid succeeds" in new CustomServer(DB_WEBSITES) {
        websites_DELETE(0) must haveResult(msg = "delete_valid", code = OK, json = SUCCESS_JSON)
      }
    }

  }
}
