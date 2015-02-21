# Fast CMS



## Overview

This is a scala API to deploy in minutes your web applicatins using an nginx server.

More details in doc folder.


## Nginx

This application is serving websites thanks to nginx which must already be deployed.
nginx is started automatically through script bin/startup.sh

Logs are configured with etc/nginx.conf file automatically generated at startup. 
The solution is tested on Mac OSX. Please consult your nginx default log file location if folder logs is empty.

To manually shutdown nginx you can use `bin/shutdown.sh`.

## Version compatibility

This version is compatible with Play 2.4.0-M2


### To run

Let's run the application

* clone git repo: `git clone git@github.com:jlcanela/fastcms.git`

* change folder: `cd fastcms`

* run application: `sbt run`

* trigger nginx startup: `curl http://localhost:9000/websites`.

* go to admin page at `http://localhost:10000/`

### Configuration

If default ports 10000 is not available, please change admin.port in conf/application.conf.