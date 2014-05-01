#!/bin/bash
# Adding 'resources/lib' to the classpath
export CLASSPATH="resources/lib/*:*"
# Running the harvester client
export PROG_DIR=`cd \`dirname $0\`; pwd`
java -Denvironment=development -Dlog4j.debug=true -Xdebug -Xrunjdwp:server=y,transport=dt_socket,address=4005,suspend=n -Dlog4j.configuration=file:///$PROG_DIR/log4j.xml -cp $CLASSPATH au.com.redboxresearchdata.harvester.json.client.Main harvester-config-console.groovy
