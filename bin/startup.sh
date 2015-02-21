#!/bin/bash
DIR=$(cd `dirname $0` && cd .. && pwd)
nginx -c $DIR/etc/nginx.conf
