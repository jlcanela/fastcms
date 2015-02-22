package test

import java.io.File

import models.{WebsiteDb, Website}
import org.specs2.mutable.Specification
import org.zeroturnaround.zip.ZipUtil

import scalaz.\/


class WebsiteSpec extends Specification {

  val websiteDb = WebsiteDb("[]", 10000, new File("tmp/data/www"))
  
  "Website" should {
    "provide fetchContent feature" in {
      /*"fetch http zip file" in {
        Website(0, "name", "http://www.opendesigns.org/design/monsoon/download/").fetchContent("tmp") aka "success" must be_==(Unit.success[String])
      }*/
      "fetch successfully local zip file" in {
        if (!new File("tmp/sample.zip").exists()) {
          ZipUtil.pack(new File("test/resources/sample"), new File("test/resources/sample.zip"))
        }

        websiteDb.fetchContent("file:test/resources/sample.zip", new File("tmp/sample")) aka "success" must be_==(\/.right())
      }
      "issue error when local zip file does not exist" in {
        websiteDb.fetchContent("file:test/resources/sample2.zip", new File("tmp/sample")) aka "fileNotFound" must be_==(\/.left("test/resources/sample2.zip (No such file or directory)"))
      }
      "issue error when local zip file is not a valid zip file" in {
        websiteDb.fetchContent("file:test/resources/invalid.zip", new File("tmp/sample2")) aka "invalidZip" must be_==(\/.left("file:test/resources/invalid.zip (invalid zip file - folder 'tmp/sample2' not created"))
      }
    }
    "provide checkContent feature" in {
      "succeed when content have index.html file" in {
        websiteDb.checkContent(new File("test/resources/sample")) aka "success" must be_==(\/.right(""))
      }
      "fails when content does not have index.html file" in {
        websiteDb.checkContent(new File("test/resources/sample2")) aka "failure" must be_==(\/.left("index.html file not found (test/resources/sample2)"))
      }
    }
  }

}
