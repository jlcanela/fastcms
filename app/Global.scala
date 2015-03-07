import api.{WebsiteApiConfig, WebsiteApi}
import models.WebsiteDb
import play.Play
import play.api._


object Global extends GlobalSettings {


  override def onStart(app: Application): Unit = {
    val config = new play.Configuration(app.configuration)
    val websiteApiConfig : WebsiteApiConfig = WebsiteApiConfig(config)
    
    if (app.configuration.getBoolean("nginx.autostartstop").getOrElse( false)) {
      if (app.mode == Mode.Test) {
        Logger.info("starting in test mode")
      } else {
        val websiteDb = new WebsiteDb(websiteApiConfig.configDb, websiteApiConfig.adminPort, websiteApiConfig.wwwPath)
        WebsiteApi.start(websiteApiConfig, websiteDb)  
      }
      Logger.info(s"nginx started on port ${websiteApiConfig.adminPort}")
    } else {
      Logger.info("not starting nginx as nginx.autostartstop is deactivated")
    }
  }
  
  override def onStop(app: Application): Unit = {
    if (app.configuration.getBoolean("nginx.autostartstop").getOrElse( false)) {
      if (app.mode == Mode.Test) {
        Logger.info("stopping in test mode")
      } else {
        WebsiteApi.stop
      }
    } else {
      Logger.info("not stopping nginx as nginx.autostartstop is deactivated")
    }
  }

}