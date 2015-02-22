package test

import java.io.File

import api.WebsiteApi
import models.{WebsiteDb, WebserverConfig, Website}
import org.specs2.mutable.Specification
import org.zeroturnaround.zip.ZipUtil
import play.api.libs.json.{Json, JsValue}
import play.api.libs.ws.WSResponse
import play.api.test.{WithApplication, WithServer, FakeApplication}

import scala.concurrent.Future
import scalaz.\/

trait TestApplicationHelper { self: Specification =>
/*  def haveResult(msg: String, code: Int, json: JsValue) = (res : Future[WSResponse]) => res map { r =>
    (r.status aka s"status when $msg" must equalTo(code)) and (r.json must equalTo(json))
  } await()

  def beBadRequest(msg: String, code: Int) = (res : Future[WSResponse]) => res map { r =>
    r.status aka s"status when $msg" must equalTo(code)
  } await()
*/
  def app(websites: List[Website]) = {
    implicit val format = Json.format[Website]
    FakeApplication(additionalConfiguration = Map("db.website" -> Json.toJson(websites).toString))
  }

  abstract class CustomApplication(websites: List[Website]) extends WithApplication(app = app(websites))
}
class WebsiteApiSpec extends Specification with WebsiteFixture with TestApplicationHelper {
  "WebsiteApi" should {
    "provide create feature" in new CustomApplication(Nil) {
      (WebsiteApi.create(website0) must be_==(\/.right(website0))) and 
        (WebsiteApi.websiteDb.all must be_==(websites2))
    }
    
    "provide an nginx config file generation" in {
      // nginxEtcPath => /usr/local/etc/nginx
      // dataPath = data
      // logPath = log
      // tempPath = data/temp
      val generatedConfig = WebserverConfig(9001, new File("data"), new File("data/www"), new File("log"), new File("data/temp"), new File("/usr/local/etc/nginx"),
        Website(1, "name1", "file1.zip", 10001, "data") ::
        Website(2, "name2", "file2.zip", 10002, "data") :: Nil
      ).generate()

      (generatedConfig must contain("include /usr/local/etc/nginx/mime.types")) and
        (generatedConfig must contain("log/name1/error_log;"))
      }
  }
}
