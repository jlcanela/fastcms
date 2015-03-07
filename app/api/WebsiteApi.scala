package api


import models._

import java.io.{PrintWriter, File}


import scalaz.{\/}


import play.Play
import play.api.Logger


case class WebsiteApiConfig(configDb: String, wwwPath: File, adminPort: Int, nginxConfig: File, dataPath: File, logPath: File, tempPath: File, nginxEtcPath: File) {
  //def this(configDb: String, wwwPath: String, adminPort: Int) = this(configDb, wwwPath, adminPort)

}
object WebsiteApiConfig {
  
   def apply(config: play.Configuration): WebsiteApiConfig = WebsiteApiConfig(
    config.getString("jsondb.website"),
    new File(config.getString("nginx.www")),
    config.getInt("admin.port"),
    new File(config.getString("nginx.local_etc")),
    new File(config.getString("nginx.data")),
    new File(config.getString("nginx.log")),
    new File(config.getString("nginx.temp")),
    new File(config.getString("nginx.etc"))
  )
  
}

object WebsiteApi {
  
 /* lazy val config = Play.application().configuration()
  lazy val configDb = config.getString("jsondb.website")
  lazy val wwwPath = config.getString("nginx.www")
  lazy val adminPort = config.getInt("admin.port")
*/
  //lazy val websiteDb = WebsiteDb(configDb, adminPort, new File(wwwPath))

  def create(config: WebsiteApiConfig, websiteDb: WebsiteDb)(website: Website): \/[String, Website] = {
    Logger.info(s"adding $website")

    val path = website.www(config.wwwPath)// + File.separator + website.name

    for {
      fetched <- websiteDb.fetchContent(website.url, path)
      path <- websiteDb.checkContent(path)
      updated = website.copy(path = path)
      _ = websiteDb.add(updated)
      _ = regenerate(config, websiteDb)
      _ = reload
    } yield updated

  }


  def regenerate(config: WebsiteApiConfig, websiteDb: WebsiteDb) {
    val configFile = config.nginxConfig
    val wsc = WebserverConfig(
      defaultPort = 9001,
      dataPath = config.dataPath,
      wwwPath = config.wwwPath,
      logPath = config.logPath,
      tempPath = config.tempPath,
      nginxEtcPath = config.nginxEtcPath,
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

  def start(config: WebsiteApiConfig, websiteDb: WebsiteDb) = {
    regenerate(config, websiteDb)

    import scala.sys.process._
    "./bin/startup.sh" !
  }

  def stop = {
    import scala.sys.process._
    "./bin/shutdown.sh" !
  }

}
