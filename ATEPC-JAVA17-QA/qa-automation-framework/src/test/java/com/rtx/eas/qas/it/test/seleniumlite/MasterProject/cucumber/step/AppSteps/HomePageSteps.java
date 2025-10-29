package com.rtx.eas.qas.it.test.seleniumlite.MasterProject.cucumber.step.AppSteps;

import static org.junit.Assert.assertTrue;
import org.junit.Assert;

import com.rtx.eas.qas.it.test.seleniumlite.MasterProject.selenium.Browsers;
import com.rtx.eas.qas.it.test.seleniumlite.MasterProject.selenium.Pages.SeleniumPageObjectExample;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.When;
import cucumber.api.java.en.Then;

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
* 05/11/2022 nrp0253353
* Removed all Extent Report-related code
* -----------------------------------------------------------------------------
*/

public class HomePageSteps extends  Browsers {
	
	SeleniumPageObjectExample pageExample;
	
	@Given("Google is opened")
	public void google_is_opened() {
		pageExample = new SeleniumPageObjectExample();	
		assertTrue("FAIL - Unable to open the Google Home page", pageExample.openPage());
	}
	
	@When("I search for {string}")
	public void i_search_for(String text) {
	    pageExample.searchFor(text);
	}
	
	@When("click the {string} button")
	public void click_the_button(String button) {
	    if (button.contains("Lucky")) {
	    	pageExample.clickLuckyButton();
	    } else {
	    	pageExample.clickSearchButton();
	    }
	}
	
	@When("the application is opened")
	public void the_application_is_opened() {
		pageExample = new SeleniumPageObjectExample();	
		assertTrue("FAIL - Unable to open the Application page", pageExample.openPage());
	}

	@Then("the {string} is correct")
	public void the_is_correct(String pageTitle) {
		String title = pageExample.getPageTitle();
	    assertTrue("Page Title is not correct, expected: " + pageTitle + " but got: " + title, title.contains(pageTitle));
	}
	
	@Then("the application {string} is correct")
	public void the_application_pageTitle_is_correct(String pageTitle) {
		try {
		driver.navigate().refresh();
		Thread.sleep(5000);
		String title = pageExample.getPageTitle();
	    assertTrue("Page Title is not correct, expected: " + pageTitle + " but got: " + title, title.contains(pageTitle));
	    LOG.info("Page title is correct: " + title);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Then("the {string} is displayed")
	public void the_is_displayed(String text) {
		pageExample = new SeleniumPageObjectExample();
		if (text.contains(pageExample.getPageTitle())) {
	    } else {
	    	Assert.fail();
	    }
	}
}