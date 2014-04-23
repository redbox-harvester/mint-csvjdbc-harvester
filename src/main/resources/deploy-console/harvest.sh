#!/bin/bash
# Adding 'resources/lib' to the classpath
export CLASSPATH="resources/lib/*:*"
# Remove the next line if you don't want to load demo data
java -cp $CLASSPATH org.hsqldb.cmdline.SqlTool --rcfile=db/local-hsqldb.rc local db/dataset.sql
# Running the harvester client
export PROG_DIR=`cd \`dirname $0\`; pwd`
java -Denvironment=development -Dlog4j.debug=true -Dlog4j.configuration=file:///$PROG_DIR/log4j.xml -cp $CLASSPATH au.com.redboxresearchdata.harvester.json.client.Main harvester-config-console.groovy