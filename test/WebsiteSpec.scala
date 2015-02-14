package test

import java.io.File

import models.Website
import org.specs2.mutable.Specification
import org.zeroturnaround.zip.ZipUtil

import scalaz.syntax.validation._
import scalaz.Validation

class WebsiteSpec extends Specification {

  "Website" should {
    "provide fetchContent feature" in {
      /*"fetch http zip file" in {
        Website(0, "name", "http://www.opendesigns.org/design/monsoon/download/").fetchContent("tmp") aka "success" must be_==(Unit.success[String])
      }*/
      "fetch successfully local zip file" in {
        if (!new File("tmp/sample.zip").exists()) {
          ZipUtil.pack(new File("test/resources/sample"), new File("test/resources/sample.zip"))
        }
 
        Website(0, "name", "file:test/resources/sample.zip").fetchContent("tmp/sample") aka "success" must be_==(().success[String])
      }
      "issue error when local zip file does not exist" in {
        Website(0, "name", "file:test/resources/sample2.zip").fetchContent("tmp/sample") aka "fileNotFound" must be_==("test/resources/sample2.zip (No such file or directory)".failure[Unit])
      }
      "issue error when local zip file is not a valid zip file" in {
        Website(0, "name", "file:test/resources/invalid.zip").fetchContent("tmp/sample2") aka "invalidZip" must be_==("file:test/resources/invalid.zip (invalid zip file - folder 'tmp/sample2' not created".failure[Unit])
      }
    }
    "provide checkContent feature" in {
      "succeed when content have index.html file" in {
        Website(0, "name", "dummyfile").checkContent("test/resources/sample") aka "success" must be_==(().success[String])
      }
      "fails when content does not have index.html file" in {
        Website(0, "name", "dummyfile").checkContent("test/resources/sample2") aka "success" must be_==("index.html file not found (test/resources/sample2/index.html)".failure[Unit])
      }
    }
  }

}
