#!/bin/bash

source /etc/profile

nohup /data/jdk8/bin/java -classpath "../lib/*" jndc_server.start.ServerStart >>/data/app/jndc-server/bin/nohup.out 2>&1 &
