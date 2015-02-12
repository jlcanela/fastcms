package api

import com.typesafe.config.ConfigFactory
import models._

import java.io.File
import java.net.URL

import org.zeroturnaround.zip.ZipUtil

import scala.collection.mutable.ListBuffer

import play.api.{Configuration, Logger}


object WebsiteApi {
  
  def create(website: Website): Website = {
    Logger.info(s"adding $website")

    val config = Configuration(ConfigFactory.load)

    val path = new File(config.getString("fastcms.path") getOrElse("./data/www") + File.separator + website.name )
    Logger.info(s"info: $path")

    ZipUtil.unpack(new URL(website.url).openStream(), path )

    WebsiteDb.add(website)
    website
  }
  
  
}
