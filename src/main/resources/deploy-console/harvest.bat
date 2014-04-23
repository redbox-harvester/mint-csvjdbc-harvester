REM The next line seeds the DB with demo data. Remove it you don't want to load that data. 
java -cp "resources/lib/*;*" org.hsqldb.cmdline.SqlTool --rcfile=db/local-hsqldb.rc local db/dataset.sql
REM Running the harvester client... 
java -Denvironment=development -Dlog4j.debug=true -Dlog4j.configuration=file:///%CD%\log4j.xml -cp "resources/lib/*;*" au.com.redboxresearchdata.harvester.json.client.Main harvester-config-console.groovy