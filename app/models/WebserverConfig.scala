package models

import java.io.File

// nginxEtcPath => /usr/local/etc/nginx
// dataPath = data
// logPath = log
// tempPath = data/temp
case class WebserverConfig(defaultPort: Int, dataPath: File, wwwPath: File, logPath: File, tempPath: File, nginxEtcPath: File, websites: List[Website]) {

  def pathsToCreate = dataPath :: wwwPath :: logPath :: tempPath :: Nil ::: logPaths ::: tempPaths
  
  def generateServer(website: Website) = {
    println("@" + wwwPath)
    println(website.name + " - " + website.www(wwwPath).getCanonicalFile.getAbsolutePath)
 s"""    server {
        listen ${website.port};
        error_log ${website.log(logPath).getAbsolutePath}/error_log;
        root ${website.www(wwwPath).getCanonicalFile.getAbsolutePath}/;
        location / {
        }
    }
""".stripMargin
  }

  def generate() =
  s"""error_log ${logPath.getAbsolutePath}/error_log;

events {
    worker_connections  1024;
}

http {
    index index.php index.htm index.html;

    include ${nginxEtcPath.getAbsolutePath}/mime.types;

    client_body_temp_path ${tempPath.getAbsolutePath}/client_body_temp;
    proxy_temp_path ${tempPath.getAbsolutePath}/proxy_temp;
    fastcgi_temp_path ${tempPath.getAbsolutePath}/fastcgi_temp 1 2;
    uwsgi_temp_path ${tempPath.getAbsolutePath}/uwsgi_temp;
    scgi_temp_path ${tempPath.getAbsolutePath}/scgi_temp;
    
${websites.map {generateServer} mkString "\n" }
}"""
  def logPaths = websites.map { _.log(logPath).getAbsolutePath} map { x => new File(x) }

  def tempPaths = ("client_body_temp" :: "proxy_temp" :: "fastcgi_temp" :: "uwsgi_temp" :: "scgi_temp" :: Nil) map { new File(tempPath, _)}
}
