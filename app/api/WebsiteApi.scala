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
      path <- website.checkContent(path)
      updated = website.copy(path = path)
      _ = WebsiteDb.add(updated)
      _ = regenerate(nginxConfig)
    } yield updated
    
  }
  
  def regenerate(configFile: File) = {
   val wsc = WebserverConfig(
      defaultPort = 9001, 
      dataPath = new File(config.getString("nginx.data")), 
      logPath = new File(config.getString("nginx.log")),
      tempPath = new File(config.getString("nginx.temp")),
      nginxEtcPath = new File(config.getString("nginx.etc")),
      websites = WebsiteDb.all
    )
    val content = wsc.generate()
    wsc.prepareLogs

    val writer = new PrintWriter(configFile)
    writer.write(content)
    writer.close()

  }
  
  
}
