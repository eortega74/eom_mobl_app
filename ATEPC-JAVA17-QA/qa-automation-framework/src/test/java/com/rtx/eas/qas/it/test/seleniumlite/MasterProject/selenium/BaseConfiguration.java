package com.rtx.eas.qas.it.test.seleniumlite.MasterProject.selenium;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

import cucumber.api.Scenario;

/**
* =============================================================================
*                           RAYTHEON PROPRIETARY
*
* Company Name: Raytheon Company
* Company Address: 870 Winter Street Waltham, MA 02451-1449
*
* Unpublished work Copyright 2016 Raytheon Company.
*
* Contact Methods: EAS-QAS
*
* This document contains proprietary data or information pertaining to items,
* or components, or processes, or other matter developed or acquired at the
* private expense of the Raytheon Company and is restricted to use only by
* persons authorized by Raytheon in writing to use it. Disclosure to
* unauthorized persons would likely cause substantial competitive harm to
* Raytheon's business position. Neither said document nor said technical data or
* information shall be furnished or disclosed to or copied or used by persons
* outside Raytheon without the express written approval of Raytheon.
*
* This Proprietary Notice Is Not Applicable If Delivered To The US Government
*
* =============================================================================
* @author nrp0236009/40003339
*
* Change Log:
* 
* 02/14/2022 nrp0253353
* Added getDBProperty() method for database properties file
* Updated getProperty() method name to getTestProeprty()
* -----------------------------------------------------------------------------
* 
* 05/11/2022 nrp0253353
* Removed all Extent Report-related code
* -----------------------------------------------------------------------------
*/

public class BaseConfiguration {

	public Properties props;
	public  String outputDirName;
	protected static Connection conn;
	public static Logger LOG;	
	public static WebDriver driver;	
	public static String location;

	public Properties getProps() {
		return props;
	}

	public void setProps(Properties props) {
		this.props = props;
	}

	public String getOutputDirName() {
		return outputDirName;
	}

	public void setOutputDirName(String outputDirName) {
		this.outputDirName = outputDirName;
	}

	public static Connection getConn() {
		return conn;
	}

	public static void setConn(Connection conn) {
		BaseConfiguration.conn = conn;
	}

	public BaseConfiguration() {
	}
	
	public String getExecutionLocation() throws IOException {
		location = getTestProperty("test.executionLocation");
		return location;
	}

	public void startLogger() {
		LOG = LogManager.getLogger(BaseConfiguration.class.getName());
		String path = System.getProperty(("user.dir"));
		LoggerContext context = (LoggerContext)LogManager.getContext(false);
		File file = new File(path, "log4j2.properties");
        context.setConfigLocation(file.toURI());
        String timestamp = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new Date());
        LOG.info("--------------------------------------------------");
		LOG.info("--------------- START SCENARIO -------------------");
		LOG.info("--------------" + timestamp + "-----------------");
		LOG.info("--------------------------------------------------");	
	}
	
	public void endTest(Scenario scenario) {
		String result = " - PASSED ";
		if (scenario.isFailed()) {
			result = " - FAILED ";
		}
		
		if ((driver != null) && ((RemoteWebDriver) driver).getSessionId() != null) {
			if(location.equalsIgnoreCase("local")) {
				if (scenario.isFailed()) {
					scenario.embed(((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES), "image/png");
				}
			} 
			
			driver.close();
			driver.quit();
			
			try {
			    Kill_Process_ID("Chromedriver");
				Kill_Process_ID("IEDriverServer");
			} catch (IOException e) {
				e.printStackTrace();
			} 
		}
				
		LOG.info("--------------------------------------------------");
		LOG.info("-------- SCENARIO COMPLETE" + result + "--------------");
		LOG.info("--------------------------------------------------");
	}
	
	public String takeScreenshot(String screenshotName) {
		String timestamp = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new Date());
		TakesScreenshot scrShot = ((TakesScreenshot) driver);
		File SrcFile = scrShot.getScreenshotAs(OutputType.FILE);
		File dir = new File(System.getProperty("user.dir") +"/Screenshots/");
	    File[] files = dir.listFiles();
	    File lastModified = Arrays.stream(files).filter(File::isDirectory).max(Comparator.comparing(File::lastModified)).orElse(null);
	    System.out.println(lastModified);
		File DestFile = new File(lastModified+ "/"+screenshotName+"_"+timestamp+ ".png");

		try {
			FileUtils.copyFile(SrcFile, DestFile);
		} catch (Exception e) {
			LOG.error("Take Screenshot Failed");
		}
		return DestFile.getAbsolutePath();
	}

	public boolean connectToDatabase() {
		String url = new String();
		conn = null;
		try {
			url = getDBProperty("test.db.driver") + "@//" + getDBProperty("test.db.host") + ":" + getDBProperty("test.db.port") + "/" + getDBProperty("test.db.sid");
		    Properties connectionProps = new Properties();
		    connectionProps.put("user", getDBProperty("test.db.username"));
		    connectionProps.put("password", getDBProperty("test.db.password"));
		    conn = DriverManager.getConnection(url, connectionProps);
		    
		    LOG.info("Database connection created: " + url);
		    return true;
		} catch (SQLException e) {
			LOG.error("UNABLE TO CONNECT TO THE DATABASE USING THE FOLLOWING:");
			LOG.error("     URL: " + url);
			LOG.warn("      !!!! Check connection to AAG");
			e.printStackTrace();
			return false;
		} catch (IOException x) {
			LOG.error("Unable to retrieve properties: " + x.getMessage());
			return false;
		}
	}
	
	//Connect to SQL JDBC
	public boolean connectTo_SQL_Database() {
		String url = new String();;
		conn = null;
		try {
			//url=jdbc:sqlserver://TTUC-SQL16QA01V;databaseName=wis
			url = getDBProperty("test.db.driver") + "//" + getDBProperty("test.db.datasource") + ";" + getDBProperty("test.db.initialCatalogKey") + "=" + getDBProperty("test.db.initialCatalogValue");	
		    Properties connectionProps = new Properties();
		    connectionProps.put("user", getDBProperty("test.db.username"));
		    connectionProps.put("password", getDBProperty("test.db.password"));
		    conn = DriverManager.getConnection(url, connectionProps);
		    LOG.info("Database connection created: " + url);
		    return true;
		} catch (SQLException e) {
			LOG.error("UNABLE TO CONNECT TO THE DATABASE USING THE FOLLOWING:");
			LOG.error("     URL: " + url);
			LOG.warn("      !!!! Check connection to AAG");
			e.printStackTrace();
			return false;
		} catch (IOException x) {
			LOG.error("Unable to retrieve properties: " + x.getMessage());
			return false;
		}		
	}	
	
	public ResultSet runQuery(String query) {
		ResultSet rs = null;
	    try {
			Statement stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			conn.close();
		} 
	    catch (SQLException e) {
	    	LOG.error("UNABLE TO RUN THE QUERY: " + query);
			e.printStackTrace();
		}
	    return rs;
	}
	
	public String getTestProperty(String name) throws IOException {
		if (props == null) {
			props = new Properties();
			String testPropfile = "/test.properties";
			InputStream is = this.getClass().getResourceAsStream(testPropfile);
			props.load(is);
		}		
		return props.getProperty(name);
	}
	
	public String getDBProperty(String name) throws IOException {
		if (props == null) {
			props = new Properties();
			String dbPropfile = "/database.properties";
			InputStream is = this.getClass().getResourceAsStream(dbPropfile);
			props.load(is);
		}		
		return props.getProperty(name);
	}
	
	public  void Kill_Process_ID(String processname) throws IOException {		
		String FullTaskKill="taskkill"+" /F"+" /IM "+processname+".exe";
    	Runtime.getRuntime().exec(FullTaskKill);    
        System.out.println(processname+" -process is killed ");
    }
	
	public  void runtestexe(String ExeFileName) throws Throwable {
		String ExeLocation=System.getProperty("user.dir") + "\\src\\test\\resources\\selenium\\driverServer\\";
		String FullPath= ExeLocation+ExeFileName;	
		System.out.println(FullPath);
		Runtime.getRuntime().exec(FullPath);	
	}
}
