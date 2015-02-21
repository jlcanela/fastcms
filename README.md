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

You can run the Fast CMS app as such:

````
sbt run
````


The application will listen on port 9000 and respond to `http://localhost:9000/websites`. 
To trigger the automatic startup of nginx you can `curl http://localhost:9000/websites` from another console.

