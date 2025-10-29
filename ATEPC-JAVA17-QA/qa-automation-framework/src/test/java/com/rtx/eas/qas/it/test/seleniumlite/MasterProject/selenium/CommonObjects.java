package com.rtx.eas.qas.it.test.seleniumlite.MasterProject.selenium;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.hamcrest.core.Is;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

import com.rtx.eas.qas.it.test.seleniumlite.MasterProject.selenium.NavigationBoundPage;
import com.rtx.eas.qas.it.test.seleniumlite.MasterProject.util.OneClickEncryptor;

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
*Change Log:
*
* 05/05/2021 1148194
* added LDAP Login for UTC -
*-----------------------------------------------------------------------------
*
* 02/14/2022 nrp0253353
* removed getNonProdProps() & getProdProps() methods, using getAPIProps() instead
* -----------------------------------------------------------------------------
*/

public class CommonObjects extends NavigationBoundPage {
	
	@FindBy(css="table:nth-child(2) tr tr:nth-child(1) > td")
	WebElement AuthPageIndicator;
	
	@FindBy(xpath="//input[@value='Login']")	
	WebElement AuthPageLoginButton;
	
	@FindBy(name="USER")	
	WebElement AuthPageUserID;
	
	@FindBy(name="PASSWORD")	
	WebElement AuthPagePassword;
	
	private static Properties ssoProps;
	private static Properties userProps;
	private static Properties nonProdProps;
	private static Properties prodProps;
	private static Properties nonProdCerts;
	private static Properties apiProps;

	public boolean UserTextDisplayed() {
		if (AuthPageIndicator.isDisplayed()) {
			return true;
		} else
			return false;
	}
	
	public void VerifiedIfAuthPage(String UserID) throws Throwable {
		String AuthText = "Raytheon Login";
		if (AuthText != null)
			driver.findElement(By.name("USER")).sendKeys(UserID);
		try {
			final OneClickEncryptor encryptor = new OneClickEncryptor(getSsoProps().getProperty("aes.encryption.key"));
			String Password = encryptor.decryptValue(getUserProps().getProperty("test.user.password.",
					getUserProps().getProperty("test.user.password.default")));
			driver.findElement(By.name("PASSWORD")).sendKeys(Password);
		}

		catch (final Exception e) {
			LOG.error("This method can only be used for locations set to local and hRTN");
			throw new InstantiationException("Could not retrieve password from encrypted file");
		}
		driver.findElement(By.cssSelector("input:nth-child(5)")).submit();
	}
	
	public boolean UTCLDAPLogin(String LDAPUserID, String LDAPPassword) throws Exception {
		if(driver.getCurrentUrl().contains("https://smauthin.utc.com/forms/UTC_UserLogin"))
		{
		System.out.println(driver.getCurrentUrl());
		driver.findElement(By.id("username")).click(); 
		driver.findElement(By.id("username")).sendKeys(LDAPUserID); 
		LOG.info("User entered LDAP UserName");
		driver.findElement(By.id("password")).click(); 
		driver.findElement(By.id("password")).sendKeys(LDAPPassword); 
		LOG.info("User entered LDAP password");
		driver.findElement(By.cssSelector("button:nth-child(4)")).click(); 
		LOG.info("User clicked on login");
		Thread.sleep(1000);
	//	LOG.info(driver.getCurrentUrl()); 
	//	LOG.info("Verify user Logged in url is correct or wrong with above url");
		} else {
			LOG.info("User DID NOT log In Unkown issue");
			return false;
		} 
		return true;
	}

	public static Properties getSsoProps() throws IOException {
		if (ssoProps != null) {
			return ssoProps;
		}
		ssoProps = new Properties();
		ssoProps.load(java.lang.ClassLoader.getSystemResourceAsStream("sso.properties"));
		return ssoProps;
	}

	public static Properties getUserProps() throws IOException {
		if (userProps != null) {
			return userProps;
		}
		userProps = new Properties();
		userProps.load(java.lang.ClassLoader.getSystemResourceAsStream("users.properties"));
		return userProps;
	}

	public static Properties getNonProdCerts() throws IOException {
		if (nonProdCerts != null) {
			return nonProdCerts;
		}
		nonProdCerts = new Properties();
		nonProdCerts.load(java.lang.ClassLoader.getSystemResourceAsStream("api.properties"));
		return nonProdCerts;
	}
	
	public static Properties getApiProps() throws IOException {
		if (apiProps != null) {
			return apiProps;
		}
		apiProps = new Properties();
		apiProps.load(java.lang.ClassLoader.getSystemResourceAsStream("api.properties"));
		return apiProps;
	}

	@Override
	public String getNavigationUrl() throws IOException {
		return null;
	}

	@Override
	public void setRequiredFields() {

	}
}
