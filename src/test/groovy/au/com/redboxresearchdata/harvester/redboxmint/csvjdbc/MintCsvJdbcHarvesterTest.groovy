/*******************************************************************************
 *Copyright (C) 2014 Queensland Cyber Infrastructure Foundation (http://www.qcif.edu.au/)
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
package au.com.redboxresearchdata.harvester.redboxmint.csvjdbc

import groovy.ui.*
import groovy.json.*
import org.apache.log4j.Logger
import org.springframework.beans.factory.support.DefaultListableBeanFactory
import org.springframework.context.support.FileSystemXmlApplicationContext
import org.springframework.context.support.GenericApplicationContext
import org.springframework.core.io.FileSystemResource
import org.springframework.integration.MessageChannel
import org.springframework.integration.endpoint.AbstractEndpoint
import org.springframework.integration.support.MessageBuilder
import org.springframework.integration.Message
import org.springframework.jmx.support.MBeanServerFactoryBean
import org.springframework.beans.factory.xml.*

/**
 * Test the CSVJDBC Harvester for Mint.
 * 
 * @author <a href="https://github.com/shilob">Shilo Banihit</a>
 *
 */
class MintCsvJdbcHarvesterTest extends GroovyTestCase {
	private static final Logger logger = Logger.getLogger(MintCsvJdbcHarvesterTest.class)
	
	def grailsApplication = [:]
	def appContext
	def config
	def grailsConfig
	MessageChannel csvjdbcHarvestMainChannel
	def receiverAppContext
	def siThread
	
	void setUpChannel() {
		File inputDir = new File ("target/input/")
		inputDir.deleteDir()
		
		config = new ConfigSlurper("test").parse(new File("src/main/resources/deploy-manager/harvester-config.groovy").toURI().toURL())
		grailsConfig = ["environment":"test", "clientConfigObj":config, "managerBase":'', "harvesterId":'']
		grailsApplication.config = grailsConfig
		
		MBeanServerFactoryBean mbeanServer = new MBeanServerFactoryBean()
		DefaultListableBeanFactory parentBeanFactory = new DefaultListableBeanFactory()
		parentBeanFactory.registerSingleton("grailsApplication", grailsApplication)
		parentBeanFactory.registerSingleton("mbeanServer", mbeanServer)
		GenericApplicationContext parentContext = new GenericApplicationContext(parentBeanFactory)
		parentContext.refresh()
		
		appContext = new GenericApplicationContext(parentContext)
		XmlBeanDefinitionReader xmlReader = new XmlBeanDefinitionReader(appContext)
		xmlReader.loadBeanDefinitions(new FileSystemResource(config.client.siPath))
		siThread = Thread.start {
			logger.info "------- Starting Main SI Thread -------"
			appContext.refresh()
			while (appContext.isRunning()) {
				Thread.sleep(1000);
			}
			logger.info "-------- Stopped SI Thread ---------"
		}
		logger.info "------ Starting JMS Receiver Context ----------"
		
		DefaultListableBeanFactory receiverBeanFactory = new DefaultListableBeanFactory()
		receiverBeanFactory.registerSingleton("testInstance", this)
		receiverBeanFactory.registerSingleton("mbeanServer", new MBeanServerFactoryBean())
		GenericApplicationContext receiverParentContext = new GenericApplicationContext(receiverBeanFactory)
		receiverParentContext.refresh()
		String[] locs = ["file:src/test/resources/appContext-jmsTesting.xml"]
		receiverAppContext = new FileSystemXmlApplicationContext(locs, true, receiverParentContext)
		logger.info "------ Started JMS Receiver Context ----------"
		sleep(5000) // enough time to get the main SI thread running
	}
	// ---------------------------------------------------------------------------------------------------
	void tearDownChannel() {
		logger.info("Test completed! Shutting down SI...")
		def mbeanExporter = appContext.getBean(config.client.mbeanExporter)
		mbeanExporter.stopActiveComponents(false, 5000)
		logger.info "Waiting for Main SI Thread to stop."
		sleep(5000)
		appContext.close()
		mbeanExporter = receiverAppContext.getBean("mbeanExporterMintCsvJdbcTest")
		mbeanExporter.stopActiveComponents(false, 5000)
		receiverAppContext.close()
		logger.info("Shutdown command success.")		
	}
	// ---------------------------------------------------------------------------------------------------
	public Object handleMessage(Message msg) {
		logger.info "------ Got Message --------"
		def json = new JsonSlurper().parseText(msg.getPayload())
		assertEquals("MintJson", json.type)
		assertTrue(json.data["data"].size() > 0)
		String rulesConfig = json.data.data[0].rulesConfig
		assertTrue(rulesConfig != null)
		logger.info "RulesConfig is: ${rulesConfig}"
		this."validate${json.data.data[0].rulesConfig}"(json)
		logger.info "------ Message Validated --------"
		return msg
	}
	// ---------------------------------------------------------------------------------------------------
	void testHarvest() {
		logger.info("Setting up test............")
		setUpChannel()
		logger.info("Starting Tests....")
		doServices()
		tearDownChannel()
	}
	// ---------------------------------------------------------------------------------------------------
	void doServices() { 
		logger.info "Testing Services.........."
		String serviceUrl = "https://raw.githubusercontent.com/redbox-mint/mint/master/config/src/main/config/home/data/Services.csv"
		String csvText = serviceUrl.toURL().text
		new File(config.harvest.directory, "Services.csv").withWriter {
			it.write(csvText)
		}				
		sleep(10000) // enough time to handle the services message.
	}
	// ---------------------------------------------------------------------------------------------------
	void validateServices(json) {
		logger.info "------- Validating Service ---------"
		
	}
}
