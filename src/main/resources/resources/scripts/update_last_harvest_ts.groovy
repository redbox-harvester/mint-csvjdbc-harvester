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
import groovy.util.ConfigObject
import au.com.redboxresearchdata.util.config.Config

import java.util.Map
import groovy.json.*
import org.apache.log4j.Logger
import org.apache.commons.io.FilenameUtils

def systemConfig = config
if (!configPath) {
	configPath = "${FilenameUtils.removeExtension(scriptPath)}-config.groovy"
}
log.debug("Try to use script-specific: ${configPath}")
def configFile = new File(configPath)
if (configFile.exists()) {
	log.debug("Attempting to load script config '${configPath}' using environment:${config.environment}")
	def scriptConfig= new ConfigSlurper(config.environment).parse(configFile.toURI().toURL())
	config = scriptConfig
} else {
	log.debug("Ending up with using the system config.")
}

systemConfig.harvest.jdbc[type].sqlParam.last_harvest_ts = new Date().format("yyyy-MM-dd HH:mm:ss")