package com.rtx.eas.qas.it.test.seleniumlite.MasterProject.selenium;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.UnexpectedTagNameException;
import org.openqa.selenium.support.ui.WebDriverWait;

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
* 
*/

public abstract class  BasePage extends Browsers {
	
	private static WebDriverWait wait;
	
	static @FindBy(id="requireLoadingDiv")
	WebElement requireLoadingDiv;
	
	static @FindBy(id="loadingDiv")
	WebElement loadingDiv;
	
	public List<WebElement> requiredFields;
	
	public abstract void setRequiredFields(); 

	public BasePage() {
		super();
	}

	public void initElements() {
		AjaxElementLocatorFactory factory = new AjaxElementLocatorFactory(driver, 10);
		PageFactory.initElements(factory, this);
		setRequiredFields();
		LOG.info("Initialized the page elements for " + driver.getTitle());
	}

	public void areRequiredFieldsColoredCorrectly() {
		for (WebElement x :  requiredFields) {
			try {
				Select y = new Select(x);
				WebElement z = y.getFirstSelectedOption();
				if ("Select...".equals(z.getText()) || z.getText() == null || z.getText().isEmpty()) {
					assertTrue ("FAIL - The background color for a missing required select is not rgba(223, 186, 186, 1) : " + x.getCssValue("background-color") + " : " + x.getAttribute("data-bind") ,x.getCssValue("background-color").equalsIgnoreCase("rgba(223, 186, 186, 1)"));
					assertTrue ("FAIL - The border color for a missing required select is not 1px solid rgb(205, 10, 10):  " + x.getCssValue("border") + " : " + x.getAttribute("data-bind"),x.getCssValue("border").equalsIgnoreCase("1px solid rgb(205, 10, 10)"));					
				} else {
					assertFalse ("FAIL - The background color for a populated required select is rgba(223, 186, 186, 1)" + " : " + x.getAttribute("data-bind"),x.getCssValue("background-color").equalsIgnoreCase("rgba(223, 186, 186, 1)"));
					assertFalse ("FAIL - The border color for a populated required select is 1px solid rgb(205, 10, 10)" + " : " + x.getAttribute("data-bind"),x.getCssValue("border").equalsIgnoreCase("1px solid rgb(205, 10, 10)"));				
				}
				
			} catch (UnexpectedTagNameException e) {
				if (x.isEnabled()  ) {
					if (! (
							( x.getText() != null && !x.getText().isEmpty()) 
							|| (	x.getAttribute("value") != null  && ! x.getAttribute("value").isEmpty()))) {						
						assertTrue ("FAIL - The background color for a missing required field is not rgba(223, 186, 186, 1) : " + x.getCssValue("background-color") + " : " + x.getAttribute("data-bind") ,x.getCssValue("background-color").equalsIgnoreCase("rgba(223, 186, 186, 1)"));
						assertTrue ("FAIL - The border color for a missing required field is not 1px solid rgb(205, 10, 10):  " + x.getCssValue("border") + " : " + x.getAttribute("data-bind"),x.getCssValue("border").equalsIgnoreCase("1px solid rgb(205, 10, 10)"));	
					}
					else {
						assertFalse ("FAIL - The background color for a populated required field is rgba(223, 186, 186, 1)" + " : " + x.getAttribute("data-bind"),x.getCssValue("background-color").equalsIgnoreCase("rgba(223, 186, 186, 1)"));
						assertFalse ("FAIL - The border color for a populated required field is 1px solid rgb(205, 10, 10)" + " : " + x.getAttribute("data-bind"),x.getCssValue("border").equalsIgnoreCase("1px solid rgb(205, 10, 10)"));				
					}
				}
			}
		}
	}

	public String getPageTitle() {
		String title = driver.getTitle();
		LOG.info("Page Title: " + title);
		return driver.getTitle();
	}
	
	public static void waitForPageLoad(WebDriver driver) {
		ExpectedCondition<Boolean> pageLoadCondition = new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver driver) {
				return ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete");
			}
		};
		wait = new WebDriverWait(driver, Duration.ofSeconds(30));
		wait.until(pageLoadCondition);
	}
	
	public void clickButton(WebElement button) {
		waitForPageLoad(driver);
		wait = new WebDriverWait(driver, Duration.ofSeconds(15));
		if (loadingDiv.isDisplayed()) {
			wait.until(ExpectedConditions.invisibilityOf(loadingDiv));
		}
		if (requireLoadingDiv.isDisplayed()) {
			wait.until(ExpectedConditions.invisibilityOf(requireLoadingDiv));
		}
		wait.until(ExpectedConditions.visibilityOf(button));
		wait.until(ExpectedConditions.elementToBeClickable(button));
		try {
			button.click();
		} catch (Exception e) {
			pageError("BUTTON WAS NOT CLICKABLE");
			e.printStackTrace();
		}
	}
	
	public void addTextToEditor(WebElement editor, WebElement body, String text) {
		try {
			driver.switchTo().frame(editor);
			body.click();
			body.sendKeys(text);
			driver.switchTo().parentFrame();
		} catch (Exception e) {
			pageError("UNABLE TO SET TEXT IN RICH TEXT EDITOR");
		}
	}
	
	public void switchToNextTab(int tab) {
		ArrayList<String> tabs = new ArrayList<String> (driver.getWindowHandles());
		driver.switchTo().window(tabs.get(tab));
	}
	
	public static WebElement getRequireLoadingDiv() {
		return requireLoadingDiv;
	}

	public static WebElement getLoadingDiv() {
		return loadingDiv;
	}
}
