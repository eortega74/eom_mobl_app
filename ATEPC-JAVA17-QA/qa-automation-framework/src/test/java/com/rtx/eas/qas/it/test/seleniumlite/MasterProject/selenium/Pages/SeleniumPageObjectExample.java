package com.rtx.eas.qas.it.test.seleniumlite.MasterProject.selenium.Pages;

import java.io.IOException;
import java.util.ArrayList;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.rtx.eas.qas.it.test.seleniumlite.MasterProject.common.GenericMethods;
import com.rtx.eas.qas.it.test.seleniumlite.MasterProject.selenium.NavigationBoundPage;

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
*/

public class SeleniumPageObjectExample extends NavigationBoundPage {
	
	GenericMethods gm = new GenericMethods();
	
	@FindBy(name="q")
	WebElement searchField;
	
	@FindBy(name="btnI")	
	WebElement luckyButton;
	
	@FindBy(name="btnK")	
	WebElement searchButton;
	
	public String getNavigationUrl() throws IOException{
		 return getTestProperty("test.url");
	}

	public void setRequiredFields() {
		// TODO Add the required fields for the page here.
		// TODO Be sure to call this after the page you are testing is initialized in Cucumber.
		requiredFields = new ArrayList<WebElement>();
	}

	public void searchFor(String text) {
		try {
			gm.highLightElement(searchField);
			searchField.sendKeys(text);
			LOG.info("Typing " + text + " into the Search field");
		} catch (Exception e) {
			e.printStackTrace();
			pageError("Unable to search for: " + text);
		}
	}

	public void clickLuckyButton() {
		try {
			gm.highLightElement(luckyButton);
			luckyButton.click();
			LOG.info("Clicked the I'm Feeling Lucky button");
		} catch (Exception e) {
			e.printStackTrace();
			pageError("Unable to click the 'I'm Feeling Lucky' button");
		}
	}
	
	public void clickSearchButton() {
		try {
			gm.highLightElement(searchButton);
			searchButton.click();
			LOG.info("Clicked the Search button");
		} catch (Exception e) {
			e.printStackTrace();
			pageError("Unable to click the 'Search' button");
		}
	}
}



