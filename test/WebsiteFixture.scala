package test

import models.Website
import play.api.libs.json.Json

trait WebsiteFixture {

  implicit val format = Json.format[Website]
  
  val website0 = Website(id = 0, name = "name", url = "url")
  val website1 = Website(id = 1, name = "name1", url = "url1")
  val website_update = website0.copy(name = "name2", url = "url2")
  val DB_WEBSITES = List(website0)
  val websites2 = List(website0, website1)
  
  val WEBSITES_JSON = Json.toJson(DB_WEBSITES)
  val CREATED_WEBSITES_JSON = Json.toJson(websites2)
  
  val WEBSITE_JSON = Json.toJson(website1)
  val SUCCESS_JSON = Json.parse("""{"success":true}""")
  val WEBSITE_UPDATE_JSON = Json.toJson(website_update)

}
