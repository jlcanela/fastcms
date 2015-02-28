package test


import api.ServeApi
import models.Rule
import org.specs2.mutable._
import play.Play
import play.api.db.DB
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.{WSResponse, WS}

import play.api.test._
import play.api.test.Helpers._

import scala.concurrent.Future

class ServeApiSpec extends Specification with RuleFixture with TestApplicationHelper {
  "ServeApiSpec" should {
    "provide content ref feature" in {
      ServeApi(defaultRules).contentRef("localhost", "/index.html") must be_==(Some("index"))
    }
    "provide basic featuree" in {
      (ServeApi(defaultRules).contentRef("localhost", "/home.html") must be_==(Some("home"))) and
        (ServeApi(defaultRules).contentRef("localhost", "/portfolio_one.html") must be_==(Some("portfolio_one")))

    }
    "provide advanced featuree" in {
      ServeApi(defaultRules).contentRef("localhost", "/testing/blog.html") must be_==(Some("testingblog"))
    }

  }
}

    