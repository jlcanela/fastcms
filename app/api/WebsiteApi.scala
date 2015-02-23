package api


import models._

import java.io.{PrintWriter, File}


import scalaz.{\/}


import play.Play
import play.api.Logger


object WebsiteApi {
  
  lazy val config = Play.application().configuration()
  lazy val configDb = config.getString("jsondb.website")
  lazy val wwwPath = config.getString("nginx.www")
  lazy val adminPort = config.getInt("admin.port")

  lazy val websiteDb = WebsiteDb(configDb, adminPort, new File(wwwPath))

  def create(website: Website): \/[String, Website] = {
    Logger.info(s"adding $website")

    val path = website.www(new File(wwwPath))// + File.separator + website.name

    for {
      fetched <- websiteDb.fetchContent(website.url, path)
      path <- websiteDb.checkContent(path)
      updated = website.copy(path = path)
      _ = websiteDb.add(updated)
      _ = regenerate
      _ = reload
    } yield updated

  }

  def regenerate {
    val nginxConfig = new File(config.getString("nginx.local_etc"))
    regenerate(nginxConfig)
  }

  def regenerate(configFile: File) {
    val wsc = WebserverConfig(
      defaultPort = 9001,
      dataPath = new File(config.getString("nginx.data")),
      wwwPath = new File(config.getString("nginx.www")),
      logPath = new File(config.getString("nginx.log")),
      tempPath = new File(config.getString("nginx.temp")),
      nginxEtcPath = new File(config.getString("nginx.etc")),
      websites = websiteDb.all
    )
    val content = wsc.generate()

    (configFile.getParentFile :: wsc.pathsToCreate) foreach { folder => if (!folder.exists()) {
      Logger.logger.info(s"creating folder for ${folder.getAbsolutePath}")
      folder.mkdirs()
    }}

    val folder = configFile.getParentFile
    if (!folder.exists()) folder.mkdirs()

    val writer = new PrintWriter(configFile)
    writer.write(content)
    writer.close()

  }

  def reload = {
    import scala.sys.process._
    "./bin/reload.sh" !
  }

  def start = {
    regenerate

    import scala.sys.process._
    "./bin/startup.sh" !
  }

  def stop = {
    import scala.sys.process._
    "./bin/shutdown.sh" !
  }

}
