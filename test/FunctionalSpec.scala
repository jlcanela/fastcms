package test

import com.wordnik.swagger.core._
import com.wordnik.swagger.model._

import org.json4s._
import org.json4s.JsonDSL._
import org.json4s.jackson.JsonMethods._
import org.json4s.jackson.Serialization.{read, write}

import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._
import play.api.Logger

import scala.io._
import scala.collection.JavaConverters._

class FunctionalSpec extends Specification {
  implicit val formats = SwaggerSerializers.formats

  "Application" should {
    "have the proper resource metadata" in {
      running(TestServer(3333)) {
        val json = Source.fromURL("http://localhost:3333/websites").mkString
        val doc = parse(json).extract[ResourceListing]
        doc.swaggerVersion must_== ("1.2")
      }
    }
  }
}