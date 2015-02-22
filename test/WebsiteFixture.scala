package test

import models.Website
import play.api.libs.json.Json

trait WebsiteFixture {

  implicit val format = Json.format[Website]
  
  val adminWebsite = Website(id = 0, name = "admin", url = "", port = 10000, path = "admin")
  val website0 = Website(id = 0, name = "name", url = "file:test/resources/sample.zip", port = 10001, path = "")
  val website1 = Website(id = 1, name = "name1", url = "file:test/resources/sample.zip", port = 10002, path = "")
  val website_update = website0.copy(name = "name2", url = "file:test/resources/sample.zip", port = 10003, path = "")
  val DB_WEBSITES = List(website0, adminWebsite)
  val websites2 = List(website0, adminWebsite)
  
  val WEBSITES_JSON = Json.toJson(DB_WEBSITES)
  val CREATED_WEBSITES_JSON = Json.toJson(websites2)
  
  val WEBSITE_JSON = Json.toJson(website1)
  val SUCCESS_JSON = Json.parse("""{"success":true}""")
  val WEBSITE_UPDATE_JSON = Json.toJson(website_update)

}
