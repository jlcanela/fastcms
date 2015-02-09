package api

import com.typesafe.config.ConfigFactory
import models._

import java.util.ArrayList
import java.io.File
import java.net.URL

import org.zeroturnaround.zip.ZipUtil

import scala.collection.mutable.ListBuffer
import scala.collection.JavaConversions._

import play.api.{Configuration, Logger}

import scala.tools.nsc.Global

/**
 * Created by jlcanela on 08/02/15.
 */
class WebsiteApi {
  val websites: ListBuffer[Website] = new ListBuffer[Website]()
  websites += Website("my site", "file:///site.zip")


/*  def getWebsitebyId(WebsiteId: Long): Option[Website] = {
    Websites.filter(Website => Website.id == WebsiteId) match {
      case Websites if(Websites.size) > 0 => Some(Websites.head)
      case _ => None
    }
  }
*/
 /* def findWebsiteByStatus(status: String): java.util.List[Website] = {
    var statues = status.split(",")
    var result = new java.util.ArrayList[Website]()
    for (Website <- Websites) {
      if (statues.contains(Website.status)) {
        result.add(Website)
      }
    }
    result
  }
*/
 /* def findWebsiteByTags(tags: String): java.util.List[Website] = {
    var tagList = tags.split(",")
    var result = new java.util.ArrayList[Website]()
    for (Website <- Websites) {
      if (null != Website.tags) {
        for (tag <- Website.tags) {
          if (tagList.contains(tag.name)) {
            result.add(Website)
          }
        }
      }
    }
    result
  }*/

  def createWebsite(website: Website): Unit = {
    Logger.info(s"adding $website")

    val config = Configuration(ConfigFactory.load)
    
    val path = new File(config.getString("fastcms.path") getOrElse("./data/www") + File.separator + website.logicalName )
    Logger.info(s"info: $path")
    

    ZipUtil.unpack(new URL(website.url).openStream(), path )
    // , new File(s"$path" + File.separator + website.logicalName ));

    
    //websites --= websites.filter(existing => existing.logicalName == website.logicalName)
    websites += website
    Logger.info(s"added websites=$websites")
  }
}
