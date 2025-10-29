package com.rtx.eas.qas.it.test.seleniumlite.MasterProject.cucumber.step.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.collections.bag.SynchronizedSortedBag;
import org.openqa.selenium.By;

import com.rtx.eas.qas.it.test.seleniumlite.MasterProject.api.implementation.CookieMethods;
import com.rtx.eas.qas.it.test.seleniumlite.MasterProject.selenium.BaseConfiguration;
import com.rtx.eas.qas.it.test.seleniumlite.MasterProject.selenium.Browsers;
import com.rtx.eas.qas.it.test.seleniumlite.MasterProject.selenium.CommonObjects;
import com.rtx.eas.qas.it.test.seleniumlite.MasterProject.selenium.Pages.SeleniumPageObjectExample;
import com.rtx.eas.qas.it.test.seleniumlite.MasterProject.user.DriverUser;

import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.AfterStep;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

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
* 05/05/2021 1148194
* added @Then LDAP Login for UTC
* -----------------------------------------------------------------------------
* 
* 02/14/2022 nrp0253353
* Added @Given for API test execution
* Updated method names to apply new getTestProperty() and getDBPropery() methods in BaseConfiguration
* -----------------------------------------------------------------------------
* 
* 05/11/2022 nrp0253353
* Removed all Extent Report-related code
* Removed redundant Given method 
* Removed constructor and it's contents
* -----------------------------------------------------------------------------
*/

public class CommonSteps extends BaseConfiguration {

	Browsers brwsr = new Browsers();
	CommonObjects cObj;
	ResultSet results;
	BaseConfiguration baseConf;
	SeleniumPageObjectExample seleniumPageObjectExample;
	CookieMethods cookieMethods;

	@After
	public void endSession(Scenario scenario) throws InterruptedException, IOException {
		brwsr.endTest(scenario);
	}

	@Given("I launch a {string} browser")
	public void i_launch_a_browser(String browser) throws IOException {
		brwsr.startTest(browser, "Anonymous Test");
	}

	@Given("I launch a {string} browser for {string}")
	public void i_launch_a_browser_for(String browser, String test) throws IOException {
		brwsr.startTest(browser, test);
	}

	@Given("{string}:{string} launches a {string} browser for {string}")
	public void test_user_launches_browser_for(String user, String password, String browser, String test) throws IOException {
		brwsr.startTest(user, password, browser, test);
	}

	@Given("{string} launches a {string} browser for auth {string}")
	public void test_user_launches_browser_for_auth(String UserID, String browser, String test) throws Throwable {
		brwsr.startTestwithAuth(UserID, browser, test);
	}

	// Use this Given for auth page which is opened after URL is opened - second time auth
	@Given("Verify Authpage is opened for {string}")
	public void Verify_Authpage_is_opened_for(String UserID) throws Throwable {
		cObj.VerifiedIfAuthPage(UserID);
	}

	// Use this Given for Chrome with launches in privatemode and enters credentials to auth pop up
	@Given("{string} launches and authenticated in Privatemode {string} for {string}")	
	public void Verify_Page_is_opened_in_Privatemode(String Browser, String Test, String ExeFileName) throws Throwable {
		brwsr.startTestExe(Browser, Test, ExeFileName);
	}
	
	//Given API step definition
	@Given("{string} launches {string} browser to obtain SMSESSION cookie from {string} for endpoint {string}")
	public void api_obtain_SMS_cookie(String user, String browser, String cookieSite, String endpoint) throws Throwable {
		baseConf = new BaseConfiguration();
		if(baseConf.getExecutionLocation().equalsIgnoreCase("hrtn")) {
			brwsr.startTestwithAuth(CommonObjects.getApiProps().getProperty(user), browser, "API test");
			String cookieUrl = CommonObjects.getApiProps().getProperty(cookieSite);
			driver.navigate().to(cookieUrl);

			driver.findElement(By.name("USER")).sendKeys(CommonObjects.getApiProps().getProperty(user));
			brwsr.decryptPassword();
			
			cookieMethods = new CookieMethods();
			cookieMethods.fetchCookie();
			cookieMethods.fetchEndPoint();
		} else if(baseConf.getExecutionLocation().equalsIgnoreCase("hutc")) {
			//need an example on testing API for an hUTC app to automate the flow
			//is there an auth page when attempting to access the .cgi url for an hUTC app?
		} else {
			LOG.error("Unable to run the API script");
			LOG.warn("Please execute this API script on either the hRTN/hUTC grid");
		}
	}

	// STEP DEFS FOR OJDBC CONNECTION
	@Given("I connect to the database")
	public void i_connect_to_the_database() {
		try {
			String outputPath = getTestProperty("test.path.output");
			setOutputDirName((outputPath == null ? "" : outputPath + File.separator) + "DatabaseTest" + File.separator
					+ File.separator + new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new Date()) + File.separator);
			System.out.println("outputDirName:" + getOutputDirName());
			System.setProperty("test.output.path", getOutputDirName());
			File dirs = new File(getOutputDirName());
			dirs.mkdirs();
			System.setProperty("test.output.logfile", getOutputDirName() + "test.log");
			startLogger();
			assertTrue("FAILED - UNABLE TO CONNECT TO THE DATABASE", connectToDatabase());
		} catch (IOException e) {
			LOG.error(e.getMessage());
		}
	}

	@Given("{user} launches a {string} browser for {string}")
	public void test_user_launches_browser_for(DriverUser user, String browser, String test) throws IOException {
		brwsr.startTest(user.getUsername(), user.getPassword(), browser, test);
	}

	// STEP DEFS FOR SQL JDBC CONNECTION
	@Given("I connect to {string} database")
	public void i_connect_to_database(String databaseName) {
		try {
			String outputPath = getTestProperty("test.path.output");
			setOutputDirName((outputPath == null ? "" : outputPath + File.separator) + "DatabaseTest" + File.separator
					+ File.separator + new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new Date()) + File.separator);
			System.out.println("outputDirName:" + getOutputDirName());
			System.setProperty("test.output.path", getOutputDirName());
			File dirs = new File(getOutputDirName());
			dirs.mkdirs();
			System.setProperty("test.output.logfile", getOutputDirName() + "test.log");
			startLogger();
			if (databaseName.equalsIgnoreCase("Oracle")) {
				assertTrue("FAILED - UNABLE TO CONNECT TO THE DATABASE", connectToDatabase());
			} else if (databaseName.equalsIgnoreCase("sql")) {
				assertTrue("FAILED - UNABLE TO CONNECT TO THE DATABASE", connectTo_SQL_Database());
			}
		} catch (IOException e) {
			LOG.error(e.getMessage());
		}
	}
	
	@Then("Enter UTCLDAP {string} and {string}")
	public void enter_UTCLDAP_and(String LDAPUserID, String LDAPPassword)  throws Throwable {
		 cObj.UTCLDAPLogin(LDAPUserID,LDAPPassword);
	}

	@When("I run a query")
	public void i_run_a_query() {
		String query = ""; // "select LAST_UPDATED_BY from EXEMPTION_SET where UUID =
						   // 'c3ddf26a-f1c5-480c-b07d-da49feb6a800'";
		results = runQuery(query);
	}

	@Given("I navigate to the application url")
	public void i_navigate_to_the_application_url() {
		seleniumPageObjectExample.openPage();
	}

	@Then("the results are correct")
	public void the_results_are_correct() throws SQLException {
		while (results.next()) {
			String example = results.getString(""/* "LAST_UPDATED_BY" */);
			LOG.info(""/* "result was: " + example + ", expected: 1119218" */);
			assertEquals(""/* "1119218" */, example);
		}
		conn.close();
	}

	@Then("the connection is closed")
	public void the_connection_is_closed() throws SQLException {
		conn.close();
	}
}
