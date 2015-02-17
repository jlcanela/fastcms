package api

import com.typesafe.config.ConfigFactory
import models._

import java.io.{PrintWriter, File}


import scalaz.{\/}


import play.Play
import play.api.{Configuration, Logger}


object WebsiteApi {
  
  lazy val config = Play.application().configuration()
  
  def create(website: Website): \/[String, Website] = {
    Logger.info(s"adding $website")

    val path = website.www(new File(config.getString("nginx.data")))// + File.separator + website.name
    val nginxConfig = new File(config.getString("nginx.local_etc"))

    for {
      fetched <- website.fetchContent(path)
      checked <- website.checkContent(path)
      _ = WebsiteDb.add(website)
      _ = regenerate(nginxConfig)
    } yield website
    
  }
  
  def regenerate(configFile: File) = {
    val content = WebserverConfig(
      defaultPort = 9001, 
      dataPath = new File(config.getString("nginx.data")), 
      logPath = new File(config.getString("nginx.log")),
      tempPath = new File(config.getString("nginx.temp")),
      nginxEtcPath = new File(config.getString("nginx.etc")),
      websites = WebsiteDb.all
    ).generate()

    val writer = new PrintWriter(configFile)
    writer.write(content)
    writer.close()

  }
  
  
}
