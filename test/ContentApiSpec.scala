package test


import api.ContentApi
import org.specs2.mutable._

class ContentApiSpec extends Specification with RuleFixture with TestApplicationHelper {
  "ServeApiSpec" should {
    "provide content ref feature" in {
      ContentApi.findEntity("localhost", "/index.html", defaultRules) must be_==(Some("index"))
    }
    "provide basic featuree" in {
      (ContentApi.findEntity("localhost", "/home.html", defaultRules) must be_==(Some("home"))) and
        (ContentApi.findEntity("localhost", "/portfolio_one.html", defaultRules) must be_==(Some("portfolio_one")))

    }
    "provide advanced featuree" in {
      ContentApi.findEntity("localhost", "/testing/blog.html", defaultRules) must be_==(Some("testingblog"))
    }

  }
}

    