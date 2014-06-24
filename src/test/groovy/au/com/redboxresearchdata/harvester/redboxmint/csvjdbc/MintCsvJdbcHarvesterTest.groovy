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
	boolean validStat
	String sampleUrl = "https://raw.githubusercontent.com/redbox-mint/mint-build-distro/master/src/main/config/home/data"
	
	void setUpChannel() {
		File inputDir = new File ("target/input/")
		inputDir.deleteDir()
		
		config = new ConfigSlurper("test").parse(new File("src/main/resources/deploy-manager/harvester-config.groovy").toURI().toURL())
		grailsConfig = ["environment":"test", "clientConfigObj":config, "managerBase":'', "harvesterId":'TestHarvester']
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
		validStat = this."validate${json.data.data[0].rulesConfig}"(json)
		synchronized(siThread) {
			siThread.notifyAll()
		}
		logger.info "------ Message Validated --------"
		return msg
	}
	// ---------------------------------------------------------------------------------------------------
	void testHarvest() {
		logger.info("Setting up test............")
		setUpChannel()
		logger.info("Starting Tests....")
		doServices()
		assertTrue(validStat)
		doParties_People()
		assertTrue(validStat)
		doParties_Groups()
		assertTrue(validStat)
		doFunding_Bodies()
		assertTrue(validStat)
		doLanguages()
		assertTrue(validStat)
		doActivities_NHMRC_2010()
		assertTrue(validStat)
		tearDownChannel()
	}
	// ---------------------------------------------------------------------------------------------------
	void doServices() { 
		logger.info "Testing Services.........."
		String serviceUrl = "${sampleUrl}/Services.csv"
		String csvText = serviceUrl.toURL().text
		new File(config.harvest.directory, "Services.csv").withWriter {
			it.write(csvText)
		}				
		synchronized(siThread) {
			siThread.wait()
		}
	}
	// ---------------------------------------------------------------------------------------------------
	boolean validateServices(json) {
		logger.info "------- Validating Service ---------"
		try {
			json.data.data.each {d->
				assertEquality("Services", d.rulesConfig)
			}
		} catch (e) {
			logger.error(e)
			return false
		}
		return true
	}
	// ---------------------------------------------------------------------------------------------------
	void doParties_People() {
		logger.info "Testing People.........."
		String peopleSampleUrl = "${sampleUrl}/Parties_People.csv"
		String csvText = peopleSampleUrl.toURL().text
		new File(config.harvest.directory, "Parties_People.csv").withWriter {
			it.write(csvText)
		}
		synchronized(siThread) {
			siThread.wait()
		}
	}
	// ---------------------------------------------------------------------------------------------------
	boolean validateParties_People(json) {
		logger.info "------- Validating People ---------"
		try {
			json.data.data.each {d->
				assertEquality("Parties_People", d.rulesConfig)
			}
		} catch (e) {
			logger.error("Assertion test failed on People---------")
			logger.error(e)
			return false
		}
		return true
	}
	// ---------------------------------------------------------------------------------------------------
	void doParties_Groups() {
		logger.info "Testing Groups.........."
		String groupsSampleUrl = "${sampleUrl}/Parties_Groups.csv"
		String csvText = groupsSampleUrl.toURL().text
		new File(config.harvest.directory, "Parties_Groups.csv").withWriter {
			it.write(csvText)
		}
		synchronized(siThread) {
			siThread.wait()
		}
	}
	// ---------------------------------------------------------------------------------------------------
	boolean validateParties_Groups(json) {
		logger.info "------- Validating Groups ---------"
		try {
			json.data.data.each {d->
				assertEquality("Parties_Groups", d.rulesConfig)
			}
		} catch (e) {
			logger.error(e)
			return false
		}
		return true
	}
	// ---------------------------------------------------------------------------------------------------
	void doFunding_Bodies() {
		logger.info "Testing Funding Bodies.........."
		String fundingBodiesSampleUrl = "${sampleUrl}/Funding_Bodies.csv"
		String csvText = fundingBodiesSampleUrl.toURL().text
		new File(config.harvest.directory, "Funding_Bodies.csv").withWriter {
			it.write(csvText)
		}
		synchronized(siThread) {
			siThread.wait()
		}
	}
	// ---------------------------------------------------------------------------------------------------
	boolean validateFunding_Bodies(json) {
		logger.info "------- Validating Funding Bodies ---------"
		try {
			json.data.data.each {d->
				assertEquality("Funding_Bodies", d.rulesConfig)
			}
		} catch (e) {
			logger.error(e)
			return false
		}
		return true
	}
	// ---------------------------------------------------------------------------------------------------
	void doLanguages() {
		logger.info "Testing Languages.........."
		String languagesUrl = "${sampleUrl}/Languages.csv"
		String csvText = languagesUrl.toURL().text
		new File(config.harvest.directory, "Languages.csv").withWriter {
			it.write(csvText)
		}
		synchronized(siThread) {
			siThread.wait()
		}
	}
	// ---------------------------------------------------------------------------------------------------
	boolean validateLanguages(json) {
		logger.info "------- Validating Languages ---------"
		try {
			json.data.data.each {d->
				assertEquality("Languages", d.rulesConfig)
			}
		} catch (e) {
			logger.error(e)
			return false
		}
		return true
	}
	// ---------------------------------------------------------------------------------------------------
	void doActivities_NHMRC_2010() {
		logger.info "Testing Activities_NHMRC_2010.........."
		String languagesUrl = "${sampleUrl}/Activities_NHMRC_2010.csv"
		String csvText = languagesUrl.toURL().text
		new File(config.harvest.directory, "Activities_NHMRC_2010.csv").withWriter {
			it.write(csvText)
		}
		synchronized(siThread) {
			siThread.wait()
		}
	}
	// ---------------------------------------------------------------------------------------------------
	boolean validateActivities_NHMRC_2010(json) {
		logger.info "------- Validating Activities_NHMRC_2010 ---------"
		try {
			json.data.data.each {d->
				assertEquality("Activities_NHMRC_2010", d.rulesConfig)
			}
		} catch (e) {
			logger.error(e)
			return false
		}
		return true
	}
	// ---------------------------------------------------------------------------------------------------
	private void assertEquality(expected, actual) {
		if (expected != actual) {
			throw new Exception("Expected: ${expected} but got: ${actual}")
		}
	}
}
