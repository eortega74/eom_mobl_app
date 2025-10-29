package com.rtx.eas.qas.it.test.seleniumlite.MasterProject.util;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.rtx.eas.qas.it.test.seleniumlite.MasterProject.selenium.BaseConfiguration;
import com.rtx.eas.qas.it.test.seleniumlite.MasterProject.selenium.Browsers;

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
* 
* @author nrp0253353/nrp0253503
*
* Change Log:
* 
*/
public class AcknowledgementMinimumAccessEmail extends BaseConfiguration{

	//Make sure you do not run this method for the same application name more than once!!
	public static void main(String[] args) throws InterruptedException {
		//All you need to do is call acknwldgmntMinAccessEmail() and pass your email, application name, and your full name as the parameters
		//EXAMPLE: acknwldgmntMinAccessEmail("nrp0251145@us.ray.com", "ODW", "John Smith");

	}
	
	private static void acknwldgmntMinAccessEmail(String email, String appName, String fullName) throws InterruptedException {
		try {
			Browsers brow = new Browsers();
			brow.startTest("chrome", "some test");
			driver.get("https://outlook.office365.us/mail/inbox");
			LOG.info("Navigating to Microsoft Office");
			
			WebElement emailBox = driver.findElement(By.xpath("//input[@type='email']"));
			WebElement nextBtn = driver.findElement(By.xpath("//input[@type='submit']"));
			emailBox.sendKeys(email);
			nextBtn.click();
			LOG.info("Entering your email: " + email);
			
			WebElement newEmail = driver.findElement(By.xpath("//span[contains(text(),'New message')]"));
			newEmail.click();
			
			WebElement toBox = driver.findElement(By.xpath("//input[@aria-label='To']"));
			WebElement subjectBox = driver.findElement(By.xpath("//input[@placeholder='Add a subject']"));
			WebElement bodyBox = driver.findElement(By.xpath("//div[@aria-label='Message body']"));
			toBox.sendKeys("rmail.synergy@raytheon.com");
			subjectBox.sendKeys("Acknowledgement of minimum access for " + appName);
			bodyBox.sendKeys("To Whom it May Concern,\n\nI, " + fullName 
				+ ", acknowledge that in accessing the application " + appName 
				+ ", I will only access and view the minimum data required to do my job in creating an automated test for the application. " 
				+ "I also acknowledge that my work for this application may be audited at any time. \n\nSincerely, \n" + fullName);
			
			LOG.info("Starting a new email for the app: " + appName + " which is sent by: " + fullName);
			Thread.sleep(2000);
			WebElement sendBtn = driver.findElement(By.xpath("//span[contains(text(),'Send')]"));
			sendBtn.click();
			LOG.info("The email was sent.");
			
			Thread.sleep(4000);
			driver.close();
		} catch(Exception e) {
			LOG.error("Unable to start a new email for the app:" + appName + " for the user: " + email);
			
			e.printStackTrace();
			String error = e.getMessage();
			LOG.error(error);
		}
	}

}
