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

import java.util.Map
import groovy.json.*
import org.apache.log4j.Logger
import org.apache.commons.io.FilenameUtils

if (!configPath) {
	configPath = "${FilenameUtils.removeExtension(scriptPath)}-config.groovy"
}
log.debug("Using ${configPath}")
def configFile = new File(configPath)
if (configFile.exists()) {
	log.debug("Attempting to load script config '${configPath}' using environment:${config.environment}")
	def scriptConfig= new ConfigSlurper(config.environment).parse(configFile.toURI().toURL())
	scriptConfig.harvest.scripts.scriptBase = config.harvest.scripts.scriptBase
	config = scriptConfig		
}

// reading the Template ...
log.debug(config)
log.debug(type)
def templateJsonFile = config[type].templateJsonFile
if (!templateJsonFile) {
	data = null
	message = "This script requires a 'templateJsonFile' entry"
	log.error(message)
	return
}
templateJsonFile = (config.harvest.scripts.scriptBase ? config.harvest.scripts.scriptBase : "") + config[type].templateJsonFile

def jsonSlurper = new JsonSlurper()
def templateJson = jsonSlurper.parse(new FileReader(new File(templateJsonFile)))

def mergeUtil = new MergeUtil(config:config, data:data, type:type, log:log)
mergeUtil.doMerge((Map)templateJson)
data = mergeUtil.data
message = mergeUtil.message

// --------------------------------------------------------------------------------------
/**
 * Class for merging maps, using a configurable template. The template map is loaded from a JSON file.
 * 
 * @author Shilo Banihit
 *
 */
class MergeUtil {
	ConfigObject config
	Map data
	String type
	String message
	Logger log
	
	def doMerge(Map templateJson) {
		mergeField(data, templateJson)		
	}
	
	/**
	 * This method just compares each key in the template map against the source map. If the key is not an empty string or not null, the source map's entry is not altered.
	 * Otherwise, the template map's value is copied over. If the template field data is a map, the map's fields are inspected recursively. 
	 * The parent key is also passed on so flattened keys present on the source data can be mapped to its equivalent template keys. E.g. "tfpackage.title" == "tfpackage { title "
	 */
	def mergeField(Map srcData, Map tempData, parentKey=null) {
		for (key in tempData?.keySet()) {			 
			if (tempData[key] instanceof Map) {
				if (srcData[key] == null) {
					srcData[key] = [:]
				}                								
				mergeField(srcData[key], tempData[key], (parentKey ? parentKey + "." + key : key) )	
			} else {
				if (parentKey) {
					// try to check if srcData has 'parentKey.key' entry
                    String fullKey = parentKey + "." + key  
					def srcDataVal = data[fullKey]                    
					if (srcDataVal) {
						srcData[key] = srcDataVal
					}
				} 
				if (srcData[key] == null || "".equals(srcData[key])) {
					srcData[key] = tempData[key]
				} 				
			}
			
		}
	}
}