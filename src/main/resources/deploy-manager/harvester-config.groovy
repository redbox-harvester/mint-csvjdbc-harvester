/*******************************************************************************
 *Copyright (C) 2013 Queensland Cyber Infrastructure Foundation (http://www.qcif.edu.au/)
 *
 *This program is free software: you can redistribute it and/or modify
 *it under the terms of the GNU General Public License as published by
 *the Free Software Foundation; either version 2 of the License, or
 *(at your option) any later version.
 *
 *This program is distributed in the hope that it will be useful,
 *but WITHOUT ANY WARRANTY; without even the implied warranty of
 *MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *GNU General Public License for more details.
 *
 *You should have received a copy of the GNU General Public License along
 *with this program; if not, write to the Free Software Foundation, Inc.,
 *51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 ******************************************************************************/
/**
 * Sample Dataset JDBC harvester configuration compatible with Harvester Manager.
 * 
 * @author Shilo Banihit
 *
 */
// Environment specific config below...
environments {
	development {
		client {
			harvesterId = harvesterId // the unique harvester name, can be dynamic or static. Console only clients likely won't need this to be dynamic.
			description = "Sample ReDBox Dataset JDBC Harvester"
			base = "${managerBase}${harvesterId}/".toString() // optional base directory. 
			autoStart = true // whether the Harvester Manager will start this harvester upon start up otherwise, it will be manually started by an administrator 
			siFile = "applicationContext-SI-harvester.xml" // the app context definition for SI
			siPath = base+siFile // the path used when starting this harvester
			classPathEntries = ["resources/lib/hsqldb-2.3.1.jar"] // entries that will be added to the class path
			mbeanExporter = "mbeanExporterRedboxJdbcHarvester" // the exporter is necessary for orderly shutdown
			orderlyShutdownTimeout = 10000 // in ms
		}
		file {
			runtimePath = client.base+"runtime/" + configPath
			customPath = client.base+"custom/" + configPath
			ignoreEntriesOnSave = ["runtime"]
		}
		harvest {			
			jdbc {
				user = "SA"
				pw = ""
				driver = "org.hsqldb.jdbcDriver"
				url = "jdbc:hsqldb:file:"+client.base+"db/data/local"
				Dataset {
					query = "SELECT * FROM \"dataset\" WHERE \"last_updated\" >= TIMESTAMP(:last_harvest_ts)"
					sqlParam {
						last_harvest_ts = "2013-10-10 00:00:00"
					}
				}
			}
			pollRate = "120000" // poll every x milliseconds
			pollTimeout = "60000" // each poll should complete within these milliseconds 
			scripts {
				scriptBase = client.base + "resources/scripts/"
				//             "script path" : "configuration path" - pass in an emtpy string config path if you do not want to override the script's default config lookup behavior.
				preBuild = [] // executed after a successful, but prior to building the JSON String, no data is passed
				preAssemble = [["merge.groovy":""]] // executed prior to building the JSON string, each resultset (map) of the JDBC poll is passed as 'data'
				postBuild = [["update_last_harvest_ts.groovy":""],["saveconfig.groovy":""]] // executed after the data is processed, but prior to ending the poll
			}
		}
		activemq {
			url = "tcp://localhost:9101"
		}
	}
	test {
		client {
			harvesterId = harvesterId // the unique harvester name, can be dynamic or static. Console only clients likely won't need this to be dynamic.
			description = "Sample ReDBox Dataset JDBC Harvester"
			base = "${managerBase}${harvesterId}/".toString() // optional base directory.
			autoStart = true // whether the Harvester Manager will start this harvester upon start up otherwise, it will be manually started by an administrator
			siFile = "applicationContext-SI-harvester.xml" // the app context definition for SI
			siPath = base+siFile // the path used when starting this harvester
			classPathEntries = ["resources/lib/hsqldb-2.3.1.jar"] // entries that will be added to the class path
			mbeanExporter = "mbeanExporterRedboxJdbcHarvester" // the exporter is necessary for orderly shutdown
			orderlyShutdownTimeout = 10000 // in ms
		}
		file {
			runtimePath = client.base+"runtime/" + configPath
			customPath = client.base+"custom/" + configPath
			ignoreEntriesOnSave = ["runtime"]
		}
		harvest {
			jdbc {
				user = "SA"
				pw = ""
				driver = "org.hsqldb.jdbcDriver"
				url = "jdbc:hsqldb:file:"+client.base+"db/data/local"
				Dataset {
					query = "SELECT * FROM \"dataset\" WHERE \"last_updated\" >= TIMESTAMP(:last_harvest_ts)"
					sqlParam {
						last_harvest_ts = "2013-10-10 00:00:00"
					}
				}
			}
			pollRate = "120000" // poll every x milliseconds
			pollTimeout = "60000" // each poll should complete within these milliseconds
			scripts {
				scriptBase = client.base + "resources/scripts/"
				//             "script path" : "configuration path" - pass in an emtpy string config path if you do not want to override the script's default config lookup behavior.
				preBuild = [] // executed after a successful, but prior to building the JSON String, no data is passed
				preAssemble = [["merge.groovy":""]] // executed prior to building the JSON string, each resultset (map) of the JDBC poll is passed as 'data'
				postBuild = [["update_last_harvest_ts.groovy":""],["saveconfig.groovy":""]] // executed after the data is processed, but prior to ending the poll
			}
		}
		activemq {
			url = "tcp://localhost:9101"
		}
	}
	production {
		client {
			harvesterId = harvesterId // the unique harvester name, can be dynamic or static. Console only clients likely won't need this to be dynamic.
			description = "Sample ReDBox Dataset JDBC Harvester"
			base = "${managerBase}${harvesterId}/".toString() // optional base directory.
			autoStart = true // whether the Harvester Manager will start this harvester upon start up otherwise, it will be manually started by an administrator
			siFile = "applicationContext-SI-harvester.xml" // the app context definition for SI
			siPath = base+siFile // the path used when starting this harvester
			classPathEntries = ["resources/lib/hsqldb-2.3.1.jar"] // entries that will be added to the class path
			mbeanExporter = "mbeanExporterRedboxJdbcHarvester" // the exporter is necessary for orderly shutdown
			orderlyShutdownTimeout = 10000 // in ms
		}
		file {
			runtimePath = client.base+"runtime/" + configPath
			customPath = client.base+"custom/" + configPath
			ignoreEntriesOnSave = ["runtime"]
		}
		harvest {
			jdbc {
				user = "SA"
				pw = ""
				driver = "org.hsqldb.jdbcDriver"
				url = "jdbc:hsqldb:file:"+client.base+"db/data/local"
				Dataset {
					query = "SELECT * FROM \"dataset\" WHERE \"last_updated\" >= TIMESTAMP(:last_harvest_ts)"
					sqlParam {
						last_harvest_ts = "2013-10-10 00:00:00"
					}
				}
			}
			pollRate = "120000" // poll every x milliseconds
			pollTimeout = "60000" // each poll should complete within these milliseconds
			scripts {
				scriptBase = client.base + "resources/scripts/"
				//             "script path" : "configuration path" - pass in an emtpy string config path if you do not want to override the script's default config lookup behavior.
				preBuild = [] // executed after a successful, but prior to building the JSON String, no data is passed
				preAssemble = [["merge.groovy":""]] // executed prior to building the JSON string, each resultset (map) of the JDBC poll is passed as 'data'
				postBuild = [["update_last_harvest_ts.groovy":""],["saveconfig.groovy":""]] // executed after the data is processed, but prior to ending the poll
			}
		}
		activemq {
			url = "tcp://localhost:9101"
		}
	}
}