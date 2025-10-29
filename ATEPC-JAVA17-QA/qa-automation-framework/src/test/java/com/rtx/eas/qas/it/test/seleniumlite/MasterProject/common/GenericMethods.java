package com.rtx.eas.qas.it.test.seleniumlite.MasterProject.common;

import java.util.List;
import java.util.function.Function;

import javax.imageio.ImageIO;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.AWTException;
import java.awt.Desktop;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.time.Duration;
import java.util.ArrayList;

import org.apache.commons.net.ntp.TimeStamp;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.rtx.eas.qas.it.test.seleniumlite.MasterProject.selenium.BaseConfiguration;
import com.rtx.eas.qas.it.test.seleniumlite.MasterProject.selenium.BasePage;

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
* @authors nrp0253353/1148194
*
* Change Log:
* 
* 04/26/2021 nrp0253353
* Added new methods:
* - findElementByPolling(By locator, int maxTime, int pollTime)
* - findElementByPolling(WebElement element, int maxTime, int pollTime)
* - highlightElement(By locator)
* - highlightElement(WebElement element)
* - checkCheckBox(By locator)
* - checkCheckBox(WebElement element)
* - uncheckCheckBox(By locator)
* - uncheckCheckBox(WebElement element)
* - pause()
* - pause(int milliseconds)
* -----------------------------------------------------------------------------
* 
* 05/07/2021 nrp0253353
* Added new methods:
* - switchToWindowByURL(String url)
* - closeWindwoByURL(String url)
* - switchToWindowByTitle(String url)
* - closeWindwoByTitle(String url)
* -----------------------------------------------------------------------------
* 
* 06/08/2021 nrp0253353 collab with nrp0253505 on uploadTemplate() method
* Updated code logic for downloadTemplate(String fileName, WebElement download)
* Added new methods:
* - copy()
* - paste()
* - uploadTemplateLocalOnly(WebElement element, String fileName)
* - uploadTemplateLocalOnly(By locator, String fileName)
* - uploadTemplate(WebElement element, String fileName)
* - uploadTemplate(By locator, String fileName)
* - downloadTemplate(String fileName, By download) method-overloaded the original downloadTemplate() method
* -----------------------------------------------------------------------------
* 
* 12/10/2021 nrp0253353
* Updated methods to switch back to the original browser window after closing the secondary window
* Modified methods:
* - closeWindowByTitle(String title)
* - closeWindowByURL(String url)
* -----------------------------------------------------------------------------
* 
* 02/14/2022 nrp0253353
* Added new method:
* - smCookieCleanUp(WebElement element)
* -----------------------------------------------------------------------------
*/

public class GenericMethods extends BaseConfiguration{
	
	Robot robot;
	Actions action;
	StringSelection s;
	
	/**
	 * The following method uses findElement() to locate a single web element
	 * by passing in the element locator and element type.
	 * @param elementLocator
	 * @param elementType
	 * @return driver.findelement(by) or @return null
	 */
	public WebElement locateElement(String elementLocator, String elementType) {
		elementType = elementType.toLowerCase();
		try {
			if(elementType.equals("id")){
				LOG.info("The element you are attempting to locate using id is: " + elementLocator + ".");
				return driver.findElement(By.id(elementLocator));
			}
			else if(elementType.equals("name")){
				LOG.info("The element you are attempting to locate using name is: " + elementLocator + ".");
				return driver.findElement(By.name(elementLocator));
			}
			else if(elementType.equals("classname")){
				LOG.info("The element you are attempting to locate using classname is: " + elementLocator + ".");
				return driver.findElement(By.className(elementLocator));
			}
			else if(elementType.equals("css")){
				LOG.info("The element you are attempting to locate using css is: " + elementLocator + ".");
				return driver.findElement(By.cssSelector(elementLocator));
			}
			else if(elementType.equals("xpath")){
				LOG.info("The element you are attempting to locate using xpath is: " + elementLocator + ".");
				return driver.findElement(By.xpath(elementLocator));
			}
			else if(elementType.equals("tagname")){
				LOG.info("The element you are attempting to locate using tagname is: " + elementLocator + ".");
				return driver.findElement(By.tagName(elementLocator));
			}
			else if(elementType.equals("linktext")){
				LOG.info("The element you are attempting to locate using linktext is: " + elementLocator + ".");
				return driver.findElement(By.linkText(elementLocator));
			}
			else if(elementType.equals("partiallinktext")){
				LOG.info("The element you are attempting to locate using partial link text is: " + elementLocator + ".");
				return driver.findElement(By.partialLinkText(elementLocator));
			}
			else {
				LOG.error("Element locator type is not supported.");
				LOG.warn("Please use a valid element locator type.");
				return null;
			}
		} catch(Exception e) {
			if(elementType.equals("id") || elementType.equals("name") || elementType.equals("classname") || elementType.equals("css") || elementType.equals("xpath") || elementType.equals("tagname") || elementType.equals("linktext") || elementType.equals("partiallinktext")) {
				LOG.error("The element was not located");
				LOG.warn("Please use a valid element locator.");
			}
			return null;
		}
	}
	
	/**
	 * The following method uses findElements() to locate and create a list of 
	 * web elements by passing in the element locator and element type.
	 * @param elementLocator
	 * @param elementType
	 * @return elementList
	 */
	public List<WebElement> locateElements(String elementLocator, String elementType) {
		elementType = elementType.toLowerCase();
		List<WebElement> elementList = new ArrayList<WebElement>();
		try {
			if(elementType.equals("id")){
				elementList = driver.findElements(By.id(elementLocator));
			}
			else if(elementType.equals("name")){
				elementList = driver.findElements(By.name(elementLocator));
			}
			else if(elementType.equals("classname")){
				elementList = driver.findElements(By.className(elementLocator));
			}
			else if(elementType.equals("css")){
				elementList = driver.findElements(By.cssSelector(elementLocator));
			}
			else if(elementType.equals("xpath")){
				elementList = driver.findElements(By.xpath(elementLocator));
			}
			else if(elementType.equals("tagname")){
				elementList = driver.findElements(By.tagName(elementLocator));
			}
			else if(elementType.equals("linktext")){
				elementList = driver.findElements(By.linkText(elementLocator));
			}
			else if(elementType.equals("partiallinktext")){
				elementList = driver.findElements(By.partialLinkText(elementLocator));
			}
			else {
				LOG.error("Element locator type is not supoorted.");
				LOG.warn("Please use a valid element locator type.");
			}
		} catch(Exception e) {
			//THIS CATCH BLOCK IS NEVER REACHED!!
			LOG.error("The element(s) were not located");
			LOG.warn("Please use a valid element locator.");
		}
		if(elementList.isEmpty()) {
			LOG.error("No elements were located using the " + elementType + ": " + elementLocator + ".");
		}
		else {
			LOG.info("The elements were located using the " + elementType + ": " + elementLocator + ".");
		}
		return elementList;
	}
	
	/**
	 * The following method uses locateElements(), which can be found above,
	 * to check and return the size of the element list. elementListSize() is
	 * used to check whether locateElements() contains elements or not.
	 * @param elementLocator
	 * @param elementType
	 * @return listSize
	 */
	public void elementListSize(String elementLocator, String elementType) {
		List<WebElement> elementList = locateElements(elementLocator, elementType);
		
		int listSize = elementList.size();
		
		if(listSize > 0) {
			LOG.info("The size of this WebElement list is: " + listSize + ".");
		}
		else {
			LOG.warn("This WebElement list is empty. The list size is: " + listSize + ".");
		}
	}
	
	/**
	 * The following method uses the Explicit Wait functionality to 
	 * check if the title of a webpage contains a specified text.
	 * @param title
	 * @param timeout
	 */
	public void waitUntilTitleContains(String title, int timeout) {
		String obtainedTitle = null;
		try {
			LOG.info("Waiting for a maximum of " + timeout + " seconds to check if the page's title contains: " + title + ".");
			
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
			wait.until(ExpectedConditions.titleContains(title));
		
			obtainedTitle = driver.getTitle();
			LOG.info("The actual title found on the page: " + obtainedTitle + ".");
			
			LOG.info("The text provided was found within the title: " + obtainedTitle + ".");
		} catch(Exception e) {
			LOG.error("The text provided was not found within the title of the webpage.");
			
			obtainedTitle = driver.getTitle();
			LOG.info("The actual title found on the page: " + obtainedTitle + ".");
			LOG.warn("Confirm that the title text you provided is correct.");
			
			String errorMessage = e.getMessage();
			LOG.error(errorMessage);
		}
	}
	
	/**
	 * The following method uses the Explicit Wait functionality
	 * to validate the title of the webpage.
	 * @param title
	 * @param timeout
	 */
	public void waitUntilTitleIs(String title, int timeout) {
		String obtainedTitle = null;
		try {
			LOG.info("Waiting for a maximum of " + timeout + " seconds to confirm that the title of this page is: " + title + ".");
			
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
			wait.until(ExpectedConditions.titleIs(title));
			
			obtainedTitle = driver.getTitle();
			LOG.info("The actual title found on the page: " + obtainedTitle + ".");
			
			LOG.info("The title was found on the webpage.");
		} catch(Exception e) {
			LOG.error("The title was not found on the webpage.");
			
			obtainedTitle = driver.getTitle();
			LOG.info("The actual title found on the page: " + obtainedTitle + ".");
			LOG.warn("Confirm that the title you provided is correct.");
			
			String errorMessage = e.getMessage();
			LOG.error(errorMessage);
		}
	}
	
	/**
	 * The following method uses the Explicit Wait functionality to 
	 * check if the URL of a webpage contains a specified text.
	 * @param urlText
	 * @param timeout
	 */
	public void waitUntilURLContains(String urlText, int timeout) {
		String obtainedURL = null;
		try {
			LOG.info("Waiting for a maximum of " + timeout + " seconds to check if the page's URL contains: " + urlText + ".");
			
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
			wait.until(ExpectedConditions.urlContains(urlText));
			
			obtainedURL = driver.getCurrentUrl();
			LOG.info("The actual URL found on the page: " + obtainedURL + ".");
			
			LOG.info("The text provided was found within the URL: " + obtainedURL + ".");
		} catch(Exception e) {
			LOG.error("The text provided was not found within the URL of the webpage.");
			
			obtainedURL = driver.getCurrentUrl();
			LOG.info("The actual URL found on the page: " + obtainedURL + ".");
			LOG.warn("Confirm that the URL text you provided is correct.");
			
			String errorMessage = e.getMessage();
			LOG.error(errorMessage);
		}
	}
	
	/**
	 * The following method uses the Explicit Wait functionality
	 * to wait for the URL of a page to load on the webpage.
	 * @param url
	 * @param timeout
	 */
	public void waitUntilURLToBe(String url, int timeout) {
		String obtainedURL = null;
		try {
			LOG.info("Waiting for a maximum of " + timeout + " seconds for the URL, " + url + ", to load on the page.");
			
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
			wait.until(ExpectedConditions.urlToBe(url));
			
			obtainedURL = driver.getCurrentUrl();
			LOG.info("The actual URL found on the page: " + obtainedURL + ".");
			
			LOG.info("The URL was found on the webpage.");
		} catch(Exception e) {
			LOG.error("The expected URL was not found on the webpage.");
			
			obtainedURL = driver.getCurrentUrl();
			LOG.info("The actual URL found on the page: " + obtainedURL + ".");
			LOG.warn("Confirm that the URL you provided is correct.");
			
			String errorMessage = e.getMessage();
			LOG.error(errorMessage);
		}
	}
	
	/**
	 * The following method uses the Explicit Wait functionality
	 * to confirm that an element is present on the webpage.
	 * @param elementLocator
	 * @param timeout
	 * @return element
	 */
	public WebElement waitUntilPresent(By elementLocator, int timeout) {
		WebElement element = null;
		try {
			LOG.info("Waiting for a maximum of " + timeout + " seconds to check if the element " + elementLocator + " is present on the webpage.");
			
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
			element = wait.until(ExpectedConditions.presenceOfElementLocated(elementLocator));
			
			LOG.info("The element was found on the webpage.");
		} catch(Exception e) {
			LOG.error("The element was not found on the webpage.");
			LOG.warn("Confirm that the element locator you provided is correct.");
			
			String errorMessage = e.getMessage();
			LOG.error(errorMessage);
		}
		return element;
	}
	
	/**
	 * The following method uses the Explicit Wait functionality
	 * to wait for an element to become visible on the webpage.
	 * @param elementLocator
	 * @param timeout
	 * @return element
	 */
	public WebElement waitUntilVisible(By elementLocator, int timeout) {
		WebElement element = null;
		try {
			LOG.info("Waiting for a maximum of " + timeout + " seconds for this element to become visible on the webpage.");
			
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
			element = wait.until(ExpectedConditions.visibilityOfElementLocated(elementLocator));
			
			LOG.info("The element was located on the webpage.");
		} catch(Exception e) {
			LOG.error("The element was not located on the webpage.");
			LOG.warn("Confirm that the element locator you provided is correct.");
			
			String errorMessage = e.getMessage();
			LOG.error(errorMessage);
		}
		return element;
	}
	
	public WebElement waitUntilVisible(WebElement elementLocator, int timeout) {
		try {
			LOG.info("Waiting for a maximum of " + timeout + " seconds for this element to become visible on the webpage.");
			
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
			wait.until(ExpectedConditions.visibilityOf(elementLocator));
			
			LOG.info("The element was located on the webpage.");
		} catch(Exception e) {
			LOG.error("The element was not located on the webpage.");
			LOG.warn("Confirm that the element locator you provided is correct.");
			
			String errorMessage = e.getMessage();
			LOG.error(errorMessage);
		}
		return elementLocator;
	}
	
	/**
	 * The following method uses the Explicit Wait functionality to wait
	 * to check if an element is present but invisible on the webpage.
	 * @param elementLocator
	 * @param timeout
	 * @return element
	 */
	public WebElement waitUntilInvisible(By elementLocator, int timeout) {
		WebElement element = null;
		try {
			LOG.info("Waiting for a maximum of " + timeout + " seconds for this element is present but invisible on the webpage.");
			
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
			wait.until(ExpectedConditions.invisibilityOfElementLocated(elementLocator));
			
			element = driver.findElement(elementLocator);
			
			LOG.info("The element was located on the webpage.");
		} catch(Exception e) {
			LOG.error("The element was not located on the webpage.");
			LOG.warn("Confirm that the element locator you provided is correct.");
			
			String errorMessage = e.getMessage();
			LOG.error(errorMessage);
		}
		return element;
	}
	
	public WebElement waitUntilInvisible(WebElement elementLocator, int timeout) {
		try {
			LOG.info("Waiting for a maximum of " + timeout + " seconds for this element is present but invisible on the webpage.");
			
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
			wait.until(ExpectedConditions.invisibilityOf(elementLocator));
			
			LOG.info("The element was located on the webpage.");
		} catch(Exception e) {
			LOG.error("The element was not located on the webpage.");
			LOG.warn("Confirm that the element locator you provided is correct.");
			
			String errorMessage = e.getMessage();
			LOG.error(errorMessage);
		}
		return elementLocator;
	}
	
	/**
	 * The following method uses the Explicit Wait functionality
	 * to wait for an element to become clickable on the webpage.
	 * @param elementLocator
	 * @param timeout
	 * @return element
	 */
	public WebElement waitUntilClickable(By elementLocator, int timeout) {
		WebElement element = null;
		try {
			LOG.info("Waiting for a maximum of " + timeout + " seconds for this element to become clickable.");
			
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
			element = wait.until(ExpectedConditions.elementToBeClickable(elementLocator));
			
			LOG.info("The element was located on the webpage.");
		} catch(Exception e) {
			LOG.error("The element was not located on the webpage.");
			LOG.warn("Confirm that the element locator you provided is correct.");
			
			String errorMessage = e.getMessage();
			LOG.error(errorMessage);
		}
		return element;
	}
	
	public WebElement waitUntilClickable(WebElement elementLocator, int timeout) {
		try {
			LOG.info("Waiting for a maximum of " + timeout + " seconds for this element to become clickable.");
			
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
			wait.until(ExpectedConditions.elementToBeClickable(elementLocator));
			
			LOG.info("The element was located on the webpage.");
		} catch(Exception e) {
			LOG.error("The element was not located on the webpage.");
			LOG.warn("Confirm that the element locator you provided is correct.");
			
			String errorMessage = e.getMessage();
			LOG.error(errorMessage);
		}
		return elementLocator;
	}
	
	/**
	 * The following method uses the Explicit Wait functionality
	 * to wait for an element to become clickable and clicks it.
	 * @param elementLocator
	 * @param timeout
	 * @return element
	 */
	public void clickWhenReady(By elementLocator, int timeout) {
		WebElement element = null;
		try {
			LOG.info("Waiting for a maximum of " + timeout + " seconds for this element to become clickable.");
			
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
			element = wait.until(ExpectedConditions.elementToBeClickable(elementLocator));
			
			LOG.info("The element was located on the webpage.");
			element.click();
		} catch(Exception e) {
			LOG.error("The element was not located on the webpage.");
			LOG.warn("Confirm that the element locator you provided is correct.");
			
			String errorMessage = e.getMessage();
			LOG.error(errorMessage);
		}
	}
	
	public void clickWhenReady(WebElement elementLocator, int timeout) {
		WebElement element = null;
		try {
			LOG.info("Waiting for a maximum of " + timeout + " seconds for this element to become clickable.");
			
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
			element = wait.until(ExpectedConditions.elementToBeClickable(elementLocator));
			
			LOG.info("The element was located on the webpage.");
			element.click();
		} catch(Exception e) {
			LOG.error("The element was not located on the webpage.");
			LOG.warn("Confirm that the element locator you provided is correct.");
			
			String errorMessage = e.getMessage();
			LOG.error(errorMessage);
		}
	}
	
	/**
	 * The following method uses the Explicit Wait functionality to
	 * wait for a popup alert box to appear on the webpage.
	 * @param timeout
	 */
	public void waitUntilAlertPresent(int timeout) {
		try {
			LOG.info("Waiting for a maximum of " + timeout + " seconds for this alert box to become present on the webpage.");
			
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
			wait.until(ExpectedConditions.alertIsPresent());
			
			LOG.info("The alert box was found on the webpage.");
			Alert alert = driver.switchTo().alert();
			LOG.info("The text within the alert box: " + alert.getText());
		} catch(NoAlertPresentException noAlert) {
			LOG.error("The alert box was not present on the webpage.");
			
			String errorMessage = noAlert.getMessage();
			LOG.error(errorMessage);
		}
	}
	
	/**
	 * The following method uses the Explicit Wait functionality to wait
	 * for a popup alert box to appear on the webpage and handles it.
	 * @param timeout
	 */
	public void acceptAlert(int timeout) {
		try {
			LOG.info("Waiting for a maximum of " + timeout + " seconds for this alert box to become present on the webpage." + 
			" Then handling the alert by accepting it.");
			
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
			wait.until(ExpectedConditions.alertIsPresent());
			
			LOG.info("The alert box was found on the webpage.");
			Alert alert = driver.switchTo().alert();
			
			alert.accept();
			LOG.info("The alert was accepted.");
		} catch(NoAlertPresentException noAlert) {
			LOG.error("The alert box was not present on the webpage.");
			
			String errorMessage = noAlert.getMessage();
			LOG.error(errorMessage);
		}
	}
	
	/**
	 * The following method uses the Explicit Wait functionality to wait for the
	 * text of a specified element to become present on the webpage.
	 * @param elementLocator
	 * @param expectedText
	 * @param timeout
	 * @return element
	 */
	public WebElement waitUntilTextPresent(By elementLocator, String expectedText, int timeout) {
		WebElement element = null;
		try {
			LOG.info("Waiting for a maximum of " + timeout + " seconds until " + expectedText + " to become available on the webpage.");
			
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
			wait.until(ExpectedConditions.textToBePresentInElementLocated(elementLocator, expectedText));
			
			element = driver.findElement(elementLocator);
			
			LOG.info(expectedText + " was located on the webpage.");
		} catch(Exception e) {
			LOG.error(expectedText + " was not located on the webpage.");
			LOG.warn("Check the case-sensitivity of the text you provided and/or confirm that the text you provided is valid");
			LOG.warn("Also, confirm that the element locator you provided is correct.");
			
			String errorMessage = e.getMessage();
			LOG.error(errorMessage);
		}
		return element;
	}
	
	public WebElement waitUntilTextPresent(WebElement elementLocator, String expectedText, int timeout) {
		try {
			LOG.info("Waiting for a maximum of " + timeout + " seconds until " + expectedText + " to become available on the webpage.");
			
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
			wait.until(ExpectedConditions.textToBePresentInElement(elementLocator, expectedText));
			
			LOG.info(expectedText + " was located on the webpage.");
		} catch(Exception e) {
			LOG.error(expectedText + " was not located on the webpage.");
			LOG.warn("Check the case-sensitivity of the text you provided and/or confirm that the text you provided is valid");
			LOG.warn("Also, confirm that the element locator you provided is correct.");
			
			String errorMessage = e.getMessage();
			LOG.error(errorMessage);
		}
		return elementLocator;
	}
	
	/**
	 * The following method uses the Explicit Wait functionality to wait
	 * for a specified element to be selected on the webpage.
	 * @param elementLocator
	 * @param timeout
	 * @return element
	 */
	public WebElement waitUntilIsSelected(By elementLocator, int timeout) {
		WebElement element = null;
		try {
			LOG.info("Waiting for a maximum of " + timeout + " seconds until the specified element is selected on the webpage.");
			
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
			wait.until(ExpectedConditions.elementToBeSelected(elementLocator));
			
			element = driver.findElement(elementLocator);
			
			LOG.info("The element was located on the webpage.");
		} catch(Exception e) {
			LOG.error("The element was not located on the webpage.");
			LOG.warn("Confirm that the element locator you provided is correct.");
			
			String errorMessage = e.getMessage();
			LOG.error(errorMessage);
		}
		return element;
	}
	
	public WebElement waitUntilIsSelected(WebElement elementLocator, int timeout) {
		try {
			LOG.info("Waiting for a maximum of " + timeout + " seconds until the specified element is selected on the webpage.");
			
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
			wait.until(ExpectedConditions.elementToBeSelected(elementLocator));
			
			LOG.info("The element was located on the webpage.");
		} catch(Exception e) {
			LOG.error("The element was not located on the webpage.");
			LOG.warn("Confirm that the element locator you provided is correct.");
			
			String errorMessage = e.getMessage();
			LOG.error(errorMessage);
		}
		return elementLocator;
	}
	
	/**
	 * The following method uses the Explicit Wait functionality to wait
	 * for a specified frame to be available on the webpage and switches to it.
	 * @param elementLocator
	 * @param timeout
	 */
	public void waitUntilFrameAvailableAndSwitch(By elementLocator, int timeout) {
		try {
			LOG.info("Waiting for a maximum of " + timeout + " seconds until the specified frame to become available on the webpage.");
			
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
			wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(elementLocator));
			
			LOG.info("The frame was located and switched to.");
		} catch(Exception e) {
			LOG.info("The frame was not located on the webpage.");
			LOG.warn("Confirm that the frame locator you provided is correct.");
			
			String errorMessage = e.getMessage();
			LOG.error(errorMessage);
		}
	}
	
	public void waitUntilFrameAvailableAndSwitch(int elementLocator, int timeout) {
		try {
			LOG.info("Waiting for a maximum of " + timeout + " seconds until the specified frame to become available on the webpage.");
			
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
			wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(elementLocator));
			
			LOG.info("The frame was located and switched to.");
		} catch(Exception e) {
			LOG.info("The frame was not located on the webpage.");
			LOG.warn("Confirm that the frame locator you provided is correct.");
			
			String errorMessage = e.getMessage();
			LOG.error(errorMessage);
		}
	}
	
	public void waitUntilFrameAvailableAndSwitch(String elementLocator, int timeout) {
		try {
			LOG.info("Waiting for a maximum of " + timeout + " seconds until the specified frame to become available on the webpage.");
			
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
			wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(elementLocator));
			
			LOG.info("The frame was located and switched to.");
		} catch(Exception e) {
			LOG.info("The frame was not located on the webpage.");
			LOG.warn("Confirm that the frame locator you provided is correct.");
			
			String errorMessage = e.getMessage();
			LOG.error(errorMessage);
		}
	}
	
	public void waitUntilFrameAvailableAndSwitch(WebElement elementLocator, int timeout) {
		try {
			LOG.info("Waiting for a maximum of " + timeout + " seconds until the specified frame to become available on the webpage.");
			
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
			wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(elementLocator));
			
			LOG.info("The frame was located and switched to.");
		} catch(Exception e) {
			LOG.info("The frame was not located on the webpage.");
			LOG.warn("Confirm that the frame locator you provided is correct.");
			
			String errorMessage = e.getMessage();
			LOG.error(errorMessage);
		}
	}
	
	//NEEDS TO BE REVISITED AS THERE SEEMS TO BE AN ISSUE WITH THE visibilityOfAllElements() METHOD, USE waitUntilPresenceOfAll() INSTEAD
	/**
	 * The following method uses the Explicit Wait functionality to wait
	 * for a list of elements to become visible on the webpage.
	 * @param elementLocator
	 * @param elementType
	 * @param timeout
	 * @return elementList
	 */
	public List<WebElement> waitUntilVisibilityOfAll(String elementLocator, String elementType, int timeout) {
		List<WebElement> elementList = null;
		try {
			LOG.info("Waiting for a maximum of " + timeout + " seconds for the list of elements to become visible on the webpage.");
			
			elementList = locateElements(elementLocator, elementType);
			
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
			wait.until(ExpectedConditions.visibilityOfAllElements(elementList));
			
			LOG.info("The elements were located on the webpage.");
		} catch(Exception e) {
			LOG.error("The elements were not located on the webpage.");
			LOG.warn("Confirm that the element locator you provided is correct.");
			
			String errorMessage = e.getMessage();
			LOG.error(errorMessage);
		}
		return elementList;
	}
	
	public List<WebElement> waitUntilVisibilityOfAll(List<WebElement> elementList, int timeout) {
		try {
			LOG.info("Waiting for a maximum of " + timeout + " seconds for the list of elements to become visible on the webpage.");
			
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
			wait.until(ExpectedConditions.visibilityOfAllElements(elementList));
			
			LOG.info("The elements were located on the webpage.");
		} catch(Exception e) {
			LOG.error("The elements were not located on the webpage.");
			LOG.warn("Confirm that the element locator you provided is correct.");
			
			String errorMessage = e.getMessage();
			LOG.error(errorMessage);
		}
		return elementList;
	}
	
	/**
	 * The following method uses the Explicit Wait functionality to wait
	 * for a specified text to become present in an element's value attribute.
	 * @param elementLocator
	 * @param text
	 * @param timeout
	 * @return element
	 */
	public WebElement waitUntilTextPresentInElementValue(By elementLocator, String text, int timeout) {
		WebElement element = null;
		try {
			LOG.info("Waiting for a maximum of " + timeout + " seconds for a specified element's value attribute to contain the text: " + text + ".");
			
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
			wait.until(ExpectedConditions.textToBePresentInElementValue(elementLocator, text));
			
			element = driver.findElement(elementLocator);
			
			LOG.info("The text was found within the element value.");
		} catch(Exception e) {
			LOG.error("The text was not found within the element value");
			LOG.warn("Confirm that the element locator you provided is correct.");
			LOG.warn("Confirm that the the text you provided is contained within the element.");
			LOG.warn("Also, check the case-sensitivity of the text you provided.");
			
			String errorMessage = e.getMessage();
			LOG.error(errorMessage);
		}
		return element;
	}
	
	public WebElement waitUntilTextPresentInElementValue(WebElement elementLocator, String text, int timeout) {
		try {
			LOG.info("Waiting for a maximum of " + timeout + " seconds for a specified element's value attribute to contain the text: " + text + ".");
			
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
			wait.until(ExpectedConditions.textToBePresentInElementValue(elementLocator, text));
			
			LOG.info("The text was found within the element value.");
		} catch(Exception e) {
			LOG.error("The text was not found within the element value");
			LOG.warn("Confirm that the element locator you provided is correct.");
			LOG.warn("Confirm that the the text you provided is contained within the element.");
			LOG.warn("Also, check the case-sensitivity of the text you provided.");
			
			String errorMessage = e.getMessage();
			LOG.error(errorMessage);
		}
		return elementLocator;
	}
	
	/**
	 * The following method uses the Explicit Wait functionality to wait
	 * for a list of elements to become present on the webpage.
	 * @param elementLocator
	 * @param timeout
	 * @return elementList
	 */
	public List<WebElement> waitUntilPresenceOfAll(By elementLocator, int timeout) {
		List<WebElement> elementList = null;
		try {
			LOG.info("Waiting for a maximum of " + timeout + " seconds for the list of elements to become present on the webpage.");
			
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
			wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(elementLocator));
			
			elementList = driver.findElements(elementLocator);
			
			LOG.info("The elements were located on the webpage.");
		} catch(Exception e) {
			LOG.error("The elements were not located on the webpage.");
			LOG.warn("Confirm that the element locator you provided is correct.");
			
			String errorMessage = e.getMessage();
			LOG.error(errorMessage);
		}
		return elementList;
	}
	
	/**
	 * The following method uses the Explicit Wait functionality to wait
	 * for a specified attribute of an element to contain a specified value.
	 * @param elementLocator
	 * @param attribute
	 * @param value
	 * @param timeout
	 * @return element
	 */
	public WebElement waitUntilAttributeContains(By elementLocator, String attribute, String value, int timeout) {
		WebElement element = null;
		try {
			LOG.info("Waiting for a maximum of " + timeout + " seconds for a specified element's value attribute to contain the value: " + value + ".");
			
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
			wait.until(ExpectedConditions.attributeContains(elementLocator, attribute, value));
			
			element = driver.findElement(elementLocator);
			
			LOG.info("The " + attribute + " of the element contains the value: " + value + ".");
		} catch(Exception e) {
			LOG.error("The " + attribute + " of the element does not contain the value: " + value + ".");
			LOG.warn("Confirm that the element locator you provided is correct.");
			LOG.warn("Confirm that the the attribute and/or attribute-value you provided are correct.");
			
			String errorMessage = e.getMessage();
			LOG.error(errorMessage);
		}
		return element;
	}
	
	public WebElement waitUntilAttributeContains(WebElement elementLocator, String attribute, String value, int timeout) {
		try {
			LOG.info("Waiting for a maximum of " + timeout + " seconds for a specified element's value attribute to contain the value: " + value + ".");
			
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
			wait.until(ExpectedConditions.attributeContains(elementLocator, attribute, value));
			
			LOG.info("The " + attribute + " of the element contains the value: " + value + ".");
		} catch(Exception e) {
			LOG.error("The " + attribute + " of the element does not contain the value: " + value + ".");
			LOG.warn("Confirm that the element locator you provided is correct.");
			LOG.warn("Confirm that the the attribute and/or attribute-value you provided are correct.");
			
			String errorMessage = e.getMessage();
			LOG.error(errorMessage);
		}
		return elementLocator;
	}
	
	/**
	 * The following method uses the Explicit Wait functionality to wait
	 * for a specified attribute of an element to be a specified value.
	 * @param elementLocator
	 * @param attribute
	 * @param valaue
	 * @param timeout
	 * @return element
	 */
	public WebElement waitUntilAttributeToBe(By elementLocator, String attribute, String value, int timeout) {
		WebElement element = null;
		try {
			LOG.info("Waiting for a maximum of " + timeout + " seconds for a specified element's value attribute to be: " + value + ".");
			
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
			wait.until(ExpectedConditions.attributeToBe(elementLocator, attribute, value));
			
			element = driver.findElement(elementLocator);
			
			LOG.info("The " + attribute + " of the element has a value of: " + value + ".");
		} catch(Exception e) {
			LOG.error("The " + attribute + " of the element does not have a value of: " + value + ".");
			LOG.warn("Confirm that the element locator you provided is correct.");
			LOG.warn("Confirm that the the attribute and/or attribute-value you provided are correct.");
			
			String errorMessage = e.getMessage();
			LOG.error(errorMessage);
		}
		return element;
	}
	
	public WebElement waitUntilAttributeToBe(WebElement elementLocator, String attribute, String value, int timeout) {
		try {
			LOG.info("Waiting for a maximum of " + timeout + " seconds for a specified element's value attribute to be: " + value + ".");
			
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
			wait.until(ExpectedConditions.attributeToBe(elementLocator, attribute, value));
			
			LOG.info("The " + attribute + " of the element has a value of: " + value + ".");
		} catch(Exception e) {
			LOG.error("The " + attribute + " of the element does not have a value of: " + value + ".");
			LOG.warn("Confirm that the element locator you provided is correct.");
			LOG.warn("Confirm that the the attribute and/or attribute-value you provided are correct.");
			
			String errorMessage = e.getMessage();
			LOG.error(errorMessage);
		}
		return elementLocator;
	}
	
	/**
	 * The following method uses the Explicit Wait functionality to wait
	 * for a list of elements that are invisible on the webpage.
	 * @param elementLocator
	 * @param elementType
	 * @param timeout
	 * @return elementList
	 */
	public List<WebElement> waitUntilInvisibilityOfAll(String elementLocator, String elementType, int timeout) {
		List<WebElement> elementList = null;
		try {
			LOG.info("Waiting for a maximum of " + timeout + " seconds for the list of invisible elements on the webpage.");
			
			elementList = locateElements(elementLocator, elementType);
			
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
			wait.until(ExpectedConditions.invisibilityOfAllElements(elementList));
			
			LOG.info("The elements were located on the webpage.");
		} catch(Exception e) {
			LOG.error("The elements were not located on the webpage.");
			LOG.warn("Confirm that the element locator you provided is correct.");
			
			String errorMessage = e.getMessage();
			LOG.error(errorMessage);
		}
		return elementList;
	}
	
	public List<WebElement> waitUntilInvisibilityOfAll(List<WebElement> elementList, int timeout) {
		try {
			LOG.info("Waiting for a maximum of " + timeout + " seconds for the list of invisible elements on the webpage.");
			
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
			wait.until(ExpectedConditions.invisibilityOfAllElements(elementList));
			
			LOG.info("The elements were located on the webpage.");
		} catch(Exception e) {
			LOG.error("The elements were not located on the webpage.");
			LOG.warn("Confirm that the element locator you provided is correct.");
			
			String errorMessage = e.getMessage();
			LOG.error(errorMessage);
		}
		return elementList;
	}
	
	/**
	 * The following method uses the Robot class to perform 'TAB'.
	 * It takes a parameter of how many tabs you want to perform
	 * @param numOfTabs
	 */
	public void hitTab(int numOfTabs) throws AWTException, InterruptedException {
		try {
			robot = new Robot();
			for(int i = 0; i < numOfTabs; i++) {
				robot.keyPress(KeyEvent.VK_TAB);
				robot.keyRelease(KeyEvent.VK_TAB);
				pause(1000);
			}
			LOG.info("Tabbed " + numOfTabs + " times using the Robot class.");
		} catch(Exception e) {
			LOG.error("Unable to tab. Check that a number was provided for the number of tabs.");
			String error = e.getMessage();
			LOG.error(error);
		}
	}
	
	/**
	 * The following method uses the Robot class to perform 'ENTER'
	 */
	public void hitEnter() throws AWTException, InterruptedException {
		try {
			robot = new Robot();
			robot.keyPress(KeyEvent.VK_ENTER);
			robot.keyRelease(KeyEvent.VK_ENTER);
			pause(1000);
			LOG.info("Hitting ENTER using the Robot class.");
		} catch(Exception e) {
			LOG.error("Unable to hit ENTER.");
			String error = e.getMessage();
			LOG.error(error);
		}
	}
	
	/**
	 * The following method performs double click on a specified element 
	 * @param element
	 */
	public void doubleClick(WebElement element) {
		try {
			action = new Actions(driver);
			action.doubleClick(element).perform();
			LOG.info("Double clicking on the element located: " + element);
		} catch(Exception e) {
			LOG.error("Unable to double click on the element.");
			LOG.warn("Confirm that the locator you provided is correct.");
			String error = e.getMessage();
			LOG.error(error);
		}
	}
	
	public void doubleClick(By locator) {
		try {
			action = new Actions(driver);
			action.doubleClick(driver.findElement(locator)).perform();
			LOG.info("Double clicking on the element located: " + locator);
		} catch(Exception e) {
			LOG.error("Unable to double click on the element.");
			LOG.warn("Confirm that the locator you provided is correct.");
			String error = e.getMessage();
			LOG.error(error);
		}
	}
	
	/**
	 * The following method selects the desired menu option from 
	 * the dropdown based on the selection text provided.
	 * @param dropdown
	 * @param selection
	 */
	public void selectFromDropdownByText(WebElement dropdown, String selection) {
		try {
			LOG.info("Finding the dropdown menu using the element: " + dropdown + ".");
			Select menu = new Select(dropdown);
			LOG.info("The dropwdown element " + dropdown + " was found.");
			
			LOG.info("Finding dropwdown selection " + selection + ".");
			menu.selectByVisibleText(selection);
			LOG.info("Selected " + selection + " from the dropdown.");
		} catch(Exception e) {
			LOG.error("Unable to find the dropdown nor select the required text.");
			LOG.warn("Confirm that the element you provided is correct.");
			LOG.warn("And/Or confirm that the text selection provided was correct.");
			
			String error = e.getMessage();
			LOG.error(error);
		}
	}
	
	public void selectFromDropdownByText(By dropdown, String selection) {
		try {
			LOG.info("Finding the dropdown menu using the element: " + dropdown + ".");
			Select menu = new Select(driver.findElement(dropdown));
			LOG.info("The dropwdown element " + dropdown + " was found.");
			
			LOG.info("Finding dropwdown selection " + selection + ".");
			menu.selectByVisibleText(selection);
			LOG.info("Selected " + selection + " from the dropdown.");
		} catch(Exception e) {
			LOG.error("Unable to find the dropdown nor select the required text.");
			LOG.warn("Confirm that the element you provided is correct.");
			LOG.warn("And/Or confirm that the text selection provided was correct.");
			
			String error = e.getMessage();
			LOG.error(error);
		}
	}
	
	/**
	 * The following method selects the desired menu option from 
	 * the dropdown based on the index provided.
	 * @param dropdown
	 * @param index
	 */
	public void selectFromDropdownByIndex(WebElement dropdown, int index) {
		try {
			LOG.info("Finding the dropdown menu using the element: " + dropdown + ".");
			Select menu = new Select(dropdown);
			LOG.info("The dropwdown element " + dropdown + " was found.");
			
			LOG.info("Finding dropwdown selection index number " + index + ".");
			menu.selectByIndex(index);
			LOG.info("Selected index number " + index + " from the dropdown.");
		} catch(Exception e) {
			LOG.error("Unable to find the dropdown nor select by desired index.");
			LOG.warn("Confirm that the element you provided is correct.");
			LOG.warn("And/Or confirm that the index selection provided is correct.");
			
			String error = e.getMessage();
			LOG.error(error);
		}
	}
	
	public void selectFromDropdownByIndex(By dropdown, int index) {
		try {
			LOG.info("Finding the dropdown menu using the element: " + dropdown + ".");
			Select menu = new Select(driver.findElement(dropdown));
			LOG.info("The dropwdown element " + dropdown + " was found.");
			
			LOG.info("Finding dropwdown selection index number " + index + ".");
			menu.selectByIndex(index);
			LOG.info("Selected index number " + index + " from the dropdown.");
		} catch(Exception e) {
			LOG.error("Unable to find the dropdown nor select by desired index.");
			LOG.warn("Confirm that the element you provided is correct.");
			LOG.warn("And/Or confirm that the index selection provided was correct.");
			
			String error = e.getMessage();
			LOG.error(error);
		}
	}
	
	/**
	 * The following method switches to a window 
	 * with the title that is specified.
	 * @param title
	 */
	public boolean switchToWindowByTitle(String title) throws InterruptedException {
		try {
			boolean foundWindow = false;
			int numOfWindows = driver.getWindowHandles().size();

			LOG.info("Switching to the window with the title: " + title);
			LOG.info("The total number of windows currently open: " + numOfWindows);

			for (String handle : driver.getWindowHandles()) {
				driver.switchTo().window(handle);
				LOG.info("Comparing titles: currently focused window title " + driver.getTitle());

				if (driver.getTitle().equals(title)) {
					LOG.info("Found the window title match: " + driver.getTitle());
					BasePage.waitForPageLoad(driver);
					pause(3000);
					foundWindow = true;
					break;
				}
			}
			if (!foundWindow) {
				LOG.error("Failed to switch to the window with the title: " + title);
				return false;
			}
			LOG.info("Finished switching to the window with the title: " + title);
			return true;
		} catch (Exception e) {
			String error = e.getMessage();
			LOG.error(error);
			return false;
		}
	}
	
	/**
	 * The following method uses the switchToWindowByTitle() defined in this class
	 * method to switch to a window with a specified title and closes it.
	 * @param title
	 */
	public void closeWindowByTitle(String title) throws InterruptedException {
		try {
			String mainWindowHandle = driver.getWindowHandles().iterator().next();
			if (switchToWindowByTitle(title)) {
				LOG.info("Closing the window with the title: " + title);
				driver.close();
				driver.switchTo().window(mainWindowHandle);
				LOG.info("Finished closing the window with the title: " + title
						+ " and switched back to the main window");
			}
		} catch (Exception e) {
			String error = e.getMessage();
			LOG.error(error);
		}
	}
	
	/**
	 * The following method switches to a window 
	 * with the URL that is specified.
	 * @param url
	 */
	public boolean switchToWindowByURL(String url) throws InterruptedException {
		try {
			boolean foundWindow = false;
			int numOfWindows = driver.getWindowHandles().size();

			LOG.info("Switching to the window with the URL: " + url);
			LOG.info("The total number of windows currently open: " + numOfWindows);
			
			for (String handle : driver.getWindowHandles()) {
				driver.switchTo().window(handle);
				LOG.info("Comparing titles: currently focused window URL " + driver.getCurrentUrl());

				if (driver.getCurrentUrl().contains(url)) {
					BasePage.waitForPageLoad(driver);
					LOG.info("Found the window URL match: " + driver.getCurrentUrl());
					Thread.sleep(3000);
					foundWindow = true;
					break;
				}
			}
			if (!foundWindow) {
				LOG.error("Failed to switch to the window with the URL: " + url);
				return false;
			}
			LOG.info("Finished switching to the window with the URL: " + url);
			return true;
		} catch (Exception e) {
			String error = e.getMessage();
			LOG.error(error);
			return false;
		}
	}
	
	/**
	 * The following method uses the switchToWindowByURL() defined in this class
	 * method to switch to a window with a specified URL and closes it.
	 * @param url
	 */
	public void closeWindowByURL(String url) throws InterruptedException {
		try {
			String mainWindowHandle = driver.getWindowHandles().iterator().next();
			if (switchToWindowByURL(url)) {
				LOG.info("Closing the window with the URL: " + url);
				driver.close();
				driver.switchTo().window(mainWindowHandle);
				LOG.info("Finished closing the window with the URL: " + url
						+ " and switched back to the main window");
			}
		} catch (Exception e) {
			String error = e.getMessage();
			LOG.error(error);
		}
	}
	
	/**
	 * The following method uses the Actions class to hover over a specified element
	 * @param element
	 */
	public void hover(WebElement element) {
		try {
			action = new Actions(driver);
			action.moveToElement(element).perform();
			LOG.info("Hovering over the WebElement " + element + ".");
		} catch(Exception e) {
			LOG.error("Unable to hover over the WebElement " + element + ".");
			LOG.warn("Confirm that the WebElement provided is correct.");
		}
	}
	
	public void hover(By locator) {
		try {
			action = new Actions(driver);
			action.moveToElement(driver.findElement(locator)).perform();
			LOG.info("Hovering over the locator " + locator + ".");
		} catch(Exception e) {
			LOG.error("Unable to hover over the locator " + locator + ".");
			LOG.warn("Confirm that the locator provided is correct.");
		}
	}
	
	/**
	 * The following method clears text within a specified textbox
	 * and sends the provided text to it
	 * @param element
	 * @param text
	 */
	public void clearAndSendKeys(WebElement element, String text) {
		try {
			LOG.info("Finding element: " + element);
			
			String selectAll = Keys.chord(Keys.CONTROL, "a");
			element.sendKeys(selectAll);
			element.sendKeys(Keys.DELETE);
			
			LOG.info("Cleared textbox with element locator: " + element);
			element.sendKeys(text);
			
			LOG.info("Sending the specified text: " + text + " to the textbox.");
		} catch(Exception UnableToClearElementException) {
			try {
				action = new Actions(driver);
				action.moveToElement(element);
				action.click();
				
				String selectAll = Keys.chord(Keys.CONTROL, "a");
				action.sendKeys(selectAll);
				action.sendKeys(Keys.DELETE);
				action.sendKeys(text);
				action.build().perform();
				
				LOG.info("Clearing the specified element and sending the provided text using the Actions class.");
			} catch(Exception e) {
				LOG.error("Exception occurred while attempting to send text: " + text + "to the textbox element: " + element + ".");
				LOG.warn("Confirm that the element locator provided is correct.");
				
				String error = e.getMessage();
				LOG.error(error);
			}
		}
	}
	
	public void clearAndSendKeys(By locator, String text) {
		try {
			String selectAll = Keys.chord(Keys.CONTROL, "a");
			WebElement element = driver.findElement(locator);
			
			LOG.info("Finding element: " + element);
			
			element.sendKeys(selectAll);
			element.sendKeys(Keys.DELETE);
			
			LOG.info("Cleared textbox with element locator: " + element);
			element.sendKeys(text);
			
			LOG.info("Sending the specified text: " + text + " to the textbox.");
		} catch(Exception UnableToClearElementException) {
			try {
				action = new Actions(driver);
				action.moveToElement(driver.findElement(locator));
				action.click();
				
				String selectAll = Keys.chord(Keys.CONTROL, "a");
				action.sendKeys(selectAll);
				action.sendKeys(Keys.DELETE);
				action.sendKeys(text);
				action.build().perform();
				
				LOG.info("Clearing the specified element and sending the provided text using the Actions class.");
			} catch(Exception e) {
				LOG.error("Exception occurred while attempting to send text: " + text + "to the textbox locator: " + locator + ".");
				LOG.warn("Confirm that the element locator provided is correct.");
				
				String error = e.getMessage();
				LOG.error(error);
			}
		}
	}
	
	/**
	 * The following method downloads a file and validates that the file was downloaded
	 * @param userID
	 * @param projectName
	 * @param fileName
	 * @param download
	 */
	public void downloadTemplate(String fileName, WebElement download)
			throws InterruptedException, Exception {
		try {
			// location of the file
			File file = new File(System.getProperty("user.dir") + "\\DownloadFiles\\" + fileName);
			if (file.delete()) {
				// deleting if there is a file in the folder already
				LOG.info("Deleting the file if it already exists in the folder --Success: " + file.getName() + " is deleted!");
			} else {
				// delete unsuccessful
				LOG.info("Failed: Delete operation has failed. File does not exist or File name was incorrect");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		download.click();
		LOG.info("User Clicked the download template link to download the file: " + fileName);
		
		try {
			// checking if the file was downloaded in the DownloadFiles folder
			new File(System.getProperty("user.dir") + "\\DownloadFiles\\" + fileName).exists();
			LOG.info("File " + fileName + " was Found!--Test Passed");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void downloadTemplate(String fileName, By locator)
			throws InterruptedException, Exception {
		try {
			// location of the file
			File file = new File(System.getProperty("user.dir") + "\\DownloadFiles\\" + fileName);
			if (file.delete()) {
				// deleting if there is a file in the folder already
				LOG.info("Deleting the file if it already exists in the folder --Success: " + file.getName() + " is deleted!");
			} else {
				// delete unsuccessful
				LOG.info("Failed: Delete operation has failed. File does not exist or File name was incorrect");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		WebElement download = driver.findElement(locator);
		download.click();
		LOG.info("User Clicked the download template link to download the file: " + fileName);
		
		try {
			// checking if the file was downloaded in the DownloadFiles folder
			new File(System.getProperty("user.dir") + "\\DownloadFiles\\" + fileName).exists();
			LOG.info("File " + fileName + " was Found!--Test Passed");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * The following method can be used to open a file from the DownloadFiles folder
	 * @param userID
	 * @param projectName
	 * @param fileName
	 */
	public void openFileFromDownloadFiles(String userID, String projectName, String fileName) throws Exception {
		try {
			Desktop.getDesktop().open(new File(System.getProperty("user.dir") + "\\DownloadFiles\\" + fileName));
			LOG.info("File is opened");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * The following method can take screencapture, full screen along with the browser that's launched.
	 * @param userID
	 * @param projectName
	 * @param ScreenshotName
	 */
	public void fullScreenCapture(String userID, String projectName,String ScreenshotName) throws Exception{
		try {
            robot = new Robot();    	 
            Rectangle rectangle = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
            BufferedImage bufferedImage = robot.createScreenCapture(rectangle);
            File file = new File(System.getProperty("user.dir") + "\\DownloadFiles\\" + ScreenshotName);
            boolean status = ImageIO.write(bufferedImage, "png", file);
            System.out.println("Screen Captured ? " + status + " File Path:- " + file.getAbsolutePath());
            LOG.info("Screen Captured and saved in the DownloadFiles directory.");
 
        } catch (AWTException | IOException ex) {
            System.err.println(ex);
        }   
    }
	
	/**
	 * The following method can be used to copy a file from the DownloadFiles directory
	 * @param userID
	 * @param projectName
	 * @param inputfileName
	 * @param outputfileName
	 */
	public void copyFiles(String userID, String projectName, String inputFileName, String outputFileName)
			throws Exception {
		File inputFileName1 = new File(System.getProperty("user.dir") + "\\DownloadFiles\\" + inputFileName);
		File outputFileName1 = new File(System.getProperty("user.dir") + "\\DownloadFiles\\" + outputFileName);

		FileReader in = new FileReader(inputFileName1);
		FileWriter out = new FileWriter(outputFileName1);
		int c;

		while ((c = in.read()) != -1) {
			out.write(c);
			in.close();
			out.close();
		}
	}
	
	/**
	 * The following method can be used to remove whitespace within a file's name
	 * @param userID
	 * @param projectName
	 */
	public void removeWhiteSpace(String userID, String projectName) {
		File Folder = new File(System.getProperty("user.dir") + "\\DownloadFiles\\");
		String oldName;
		String newName;
		for (File old : Folder.listFiles()) {
			oldName = old.getName();
			if (!oldName.contains(" "))
				continue;
			newName = oldName.replaceAll("\\s", "");
			old.renameTo(new File(Folder + "/" + newName));
		}
	}
	
	/**
	 * The following method can be used to read data from a file and print it to the console. It can
	 * also be used to check the contents of the file (data) and use it for other tests
	 * 
	 * eg: String filepath = "C:\\Users\\1148194\\Desktop\\test.csv";
	 * StringBuffer st = gen.fileReader(filePath);
	 * System.out.print(st);
	 * Assert.assertTrue(st.toString().contains("data to be verified"));
	 * 
	 * @param File path 
	 * @Return StringBuffer
	 */
	public StringBuffer fileReader(String filePath) throws IOException {
		StringBuffer sb = new StringBuffer();
		File file = new File(filePath);
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader((Reader) fr);
		String line = "";
		String ReadLine;
		while ((ReadLine = br.readLine()) != null) {
			line = ReadLine;
			sb.append(line);
		}
		fr.close();
		return sb;
	}
	
	/**
	 * The following method can be used to fluently wait for an element to be found
	 * @param locator
	 * @param maxTime
	 * @param pollTime
	 */
	public WebElement findElementByPolling(By locator, int maxTime, int pollTime) throws Exception {
		WebElement elem;
		try {
			LOG.info("Fluently waiting for a maximum of " + maxTime + ", polling every " + pollTime + " seconds for the locator " + locator + ".");
			Wait<WebDriver> wait = new FluentWait<WebDriver>(driver)
					.withTimeout(Duration.ofSeconds(maxTime))
					.pollingEvery(Duration.ofSeconds(pollTime))
					.ignoring(NoSuchElementException.class)
					.ignoring(ElementNotInteractableException.class);
			
			elem = wait.until(new Function<WebDriver, WebElement>() {
				int i = 1;
				public WebElement apply(WebDriver driver) {
					TimeStamp timestamp = new TimeStamp(System.currentTimeMillis());
					LOG.info("Polling attempt: " + i + ", Current time: " + timestamp + " seconds.");
					i++;
				return driver.findElement(locator);
				}
			});
		} catch(Exception e) {
			LOG.error("Exception from fluently waiting.");
			
			String error = e.getMessage();
			LOG.error(error);
			
			throw new Exception("Text/Element not found: " + error);
		}
		return elem;
	}
	
	public WebElement findElementByPolling(WebElement element, int maxTime, int pollTime) throws Exception {
		WebElement elem;
		try {
			LOG.info("Fluently waiting for a maximum of " + maxTime + ", polling every " + pollTime + " seconds for the element " + element + ".");
			Wait<WebDriver> wait = new FluentWait<WebDriver>(driver)
					.withTimeout(Duration.ofSeconds(maxTime))
					.pollingEvery(Duration.ofSeconds(pollTime))
					.ignoring(NoSuchElementException.class)
					.ignoring(ElementNotInteractableException.class);
			
			elem = wait.until(new Function<WebDriver, WebElement>() {
				int i = 1;
				public WebElement apply(WebDriver driver) {
					TimeStamp timestamp = new TimeStamp(System.currentTimeMillis());
					LOG.info("Polling attempt: " + i + ", Current time: " + timestamp + " seconds.");
					i++;
				return element;
				}
			});
		} catch(Exception e) {
			LOG.error("Exception from fluently waiting.");
			
			String error = e.getMessage();
			LOG.error(error);
			
			throw new Exception("Text/Element not found: " + error);
		}
		return elem;
	}
	
	/**
	 * The following method can be used to highlight elements during script execution. This
	 * can be used for demonstration purposes or even for tracking the flow of your script
	 * @param element
	 */
	public void highLightElement(WebElement element) {
		try {
			String highlightSpecs = getTestProperty("test.highlightSpecs");
			if(driver instanceof JavascriptExecutor) {
				((JavascriptExecutor) driver).executeScript("arguments[0].style.border=" + highlightSpecs, element);
				pause(500);
				((JavascriptExecutor) driver).executeScript("arguments[0].style.border='none'", element);
				pause(500);
				((JavascriptExecutor) driver).executeScript("arguments[0].style.border=" + highlightSpecs, element);
				pause(500);
				((JavascriptExecutor) driver).executeScript("arguments[0].style.border='none'", element);
				pause(500);
				((JavascriptExecutor) driver).executeScript("arguments[0].style.border=" + highlightSpecs, element);
			}
		} catch(Exception e) {
			LOG.error("Unable to highlight element");
			LOG.warn("Please make sure the element provided is correct");
			
			String error = e.getMessage();
			LOG.error(error);
		}
	}
	
	public void highLightElement(By locator) {
		try {
			String highlightSpecs = getTestProperty("test.highlightSpecs");
			if(driver instanceof JavascriptExecutor) {
				((JavascriptExecutor) driver).executeScript("arguments[0].style.border=" + highlightSpecs, driver.findElement(locator));
				pause(500);
				((JavascriptExecutor) driver).executeScript("arguments[0].style.border='none'", driver.findElement(locator));
				pause(500);
				((JavascriptExecutor) driver).executeScript("arguments[0].style.border=" + highlightSpecs, driver.findElement(locator));
				pause(500);
				((JavascriptExecutor) driver).executeScript("arguments[0].style.border='none'", driver.findElement(locator));
				pause(500);
				((JavascriptExecutor) driver).executeScript("arguments[0].style.border=" + highlightSpecs, driver.findElement(locator));
			}
		} catch(Exception e) {
			LOG.error("Unable to highlight element");
			LOG.warn("Please make sure the locator provided is correct");
			
			String error = e.getMessage();
			LOG.error(error);
		}
	}
	
	/**
	 * The following method can be used to check a checkbox
	 * @param locator
	 */
	public void checkCheckBox(By locator) {
		try {
			WebElement checkBox = driver.findElement(locator);
			if(!checkBox.isSelected()) {
				checkBox.click();
			}
			LOG.info("Finished checking the checkbox.");
		} catch(Exception e) {
			LOG.error("Unable to Uncheck the checkbox.");
			LOG.error(e.getMessage());
		}
	}
	
	public void checkCheckBox(WebElement element) {
		try {
			if(!element.isSelected()) {
				element.click();
			}
			LOG.info("Finished checking the checkbox.");
		} catch(Exception e) {
			LOG.error("Unable to Uncheck the checkbox.");
			LOG.error(e.getMessage());
		}
	}
	
	/**
	 * The following method can be used to uncheck a checkbox
	 * @param locator
	 */
	public void uncheckCheckBox(By locator) {
		try {
			WebElement checkBox = driver.findElement(locator);
			if(checkBox.isSelected()) {
				checkBox.click();
			}
			LOG.info("Finished unchecking the checkbox.");
		} catch(Exception e) {
			LOG.error("Unable to Uncheck the checkbox.");
			LOG.error(e.getMessage());
		}
	}
	
	public void uncheckCheckBox(WebElement element) {
		try {
			if(element.isSelected()) {
				element.click();
			}
			LOG.info("Finished unchecking the checkbox.");
		} catch(Exception e) {
			LOG.error("Unable to Uncheck the checkbox.");
			LOG.error(e.getMessage());
		}
	}
	
	/**
	 * The following method uses Thread.sleep() to wait/pause the script for a specified number
	 * of seconds provided for the test.pause.seconds parameter in test.properties
	 */
	public void pause() {
		try {
			int pauseTime = Integer.parseInt(getTestProperty("test.pause.seconds"));
			LOG.info("Pausing for " + pauseTime + " seconds.");
			Thread.sleep(pauseTime*1000);
			LOG.info("Done pausing for " + pauseTime + " seconds.");
		} catch(Exception e) {
			String error = e.getMessage();
			LOG.error(error);
		}
	}
	
	/**
	 * The following method uses Thread.sleep() to wait/pause the script
	 * for a specified number of milliseconds
	 * @param milliseconds
	 */
	public void pause(int milliseconds) throws InterruptedException {
		try {
			LOG.info("Pausing for " + milliseconds + " milliseconds.");
			Thread.sleep(milliseconds);
			LOG.info("Done pausing for " + milliseconds + " milliseconds.");
		} catch(Exception e) {
			String error = e.getMessage();
			LOG.error(error);
		}
	}
	
	/**
	 * The following method uses the robot class to perform copy -> ctrl + c
	 */
	public void copy() throws AWTException, InterruptedException {
		try {
			robot = new Robot();
			robot.keyPress(KeyEvent.VK_CONTROL);
			robot.keyPress(KeyEvent.VK_C);
			pause(1000);
			robot.keyRelease(KeyEvent.VK_C);
			robot.keyRelease(KeyEvent.VK_CONTROL);
			pause(1000);
			LOG.info("Performed copy (ctrl + c).");
		} catch(Exception e) {
			LOG.error("Unable to copy.");
			LOG.error(e.getMessage());
		}
	}
	
	/**
	 * The following method uses the robot class to perform paste -> ctrl + v
	 */
	public void paste() throws AWTException, InterruptedException {
		try {
			robot = new Robot();
			robot.keyPress(KeyEvent.VK_CONTROL);
			robot.keyPress(KeyEvent.VK_V);
			pause(1000);
			robot.keyRelease(KeyEvent.VK_V);
			robot.keyRelease(KeyEvent.VK_CONTROL);
			pause(1000);
			LOG.info("Performed paste (ctrl + v).");
		} catch(Exception e) {
			LOG.error("Unable to paste.");
			LOG.error(e.getMessage());
		}
	}
	
	/**
	 * The following method uploads a file using the robot class
	 * @param element
	 * @param fileName
	 */
	public void uploadTemplate(WebElement element, String fileName) throws IOException, InterruptedException, AWTException{
		String filePath = System.getProperty("user.dir") + "\\UploadFiles\\" + fileName;
		try {
			waitUntilVisible(element, 3);
			element.sendKeys(filePath);
			LOG.info("Uploaded the file " + fileName + ".");
		} catch(Exception e) {
			LOG.error("Unable to upload the file " + fileName + ".");
			LOG.error(e.getMessage());
		}
	}
	
	public void uploadTemplate(By locator, String fileName) throws IOException, InterruptedException, AWTException{
		String filePath = System.getProperty("user.dir") + "\\UploadFiles\\" + fileName;
		try {
			WebElement element = driver.findElement(locator);
			waitUntilVisible(element, 3);
			element.sendKeys(filePath);
			LOG.info("Uploaded the file " + fileName + ".");
		} catch(Exception e) {
			LOG.error("Unable to upload the file " + fileName + ".");
			LOG.error(e.getMessage());
		}
	}
	
	/**
	 * The following method uploads a file using the robot class
	 * THIS METHOD CANNOT BE USED FOR REMOTE EXECUTION!!
	 * @param fileName
	 * @param element
	 */
	public void uploadTemplateLocalOnly(WebElement element, String fileName) throws IOException, InterruptedException, AWTException{
		String file = System.getProperty("user.dir") + "\\UploadFiles\\" + fileName;
		try {
			waitUntilVisible(element, 3);
			action = new Actions(driver);
			action.moveToElement(element);
			action.click();
			action.build().perform();
			pause(1000);
			s = new StringSelection(file);
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(s, null);
			paste();
			hitEnter();
			LOG.info("Uploaded the file " + fileName + ".");
		} catch(Exception e) {
			LOG.error("Unable to upload the file " + fileName + ".");
			LOG.error(e.getMessage());
		}
	}
	
	public void uploadTemplateLocalOnly(By locator, String fileName) throws IOException, InterruptedException, AWTException{
		String file = System.getProperty("user.dir") + "\\UploadFiles\\" + fileName;
		try {
			WebElement element = driver.findElement(locator);
			waitUntilVisible(element, 3);
			action = new Actions(driver);
			action.moveToElement(element);
			action.click();
			action.build().perform();
			pause(1000);
			s = new StringSelection(file);
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(s, null);
			paste();
			hitEnter();
			LOG.info("Uploaded the file " + fileName + ".");
		} catch(Exception e) {
			LOG.error("Unable to upload the file " + fileName + ".");
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * The following method cleans up the SMSession cookie
	 * by removing all spaces and new lines
	 * @param cookie
	 */
	public String smCookieCleanUp(WebElement cookie) {
		try {
			String smSessionText = cookie.getText();
			smSessionText = smSessionText.replaceAll("\\s", "");
			smSessionText = smSessionText.replaceAll("\n", "");
			return smSessionText;
		} catch(Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
			return null;
		}
	}
	
}
