package com.rtx.eas.qas.it.test.seleniumlite.MasterProject.selenium;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;

import com.rtx.eas.qas.it.test.seleniumlite.MasterProject.selenium.Pages.SeleniumPageObjectExample;
import com.rtx.eas.qas.it.test.seleniumlite.MasterProject.util.OneClickEncryptor;
import com.rtx.eas.qas.it.test.seleniumlite.MasterProject.util.SSOHandshake;

import io.github.bonigarcia.wdm.WebDriverManager;

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
* @1148194--1-3-21--Added startTestwithAuth function  and launchChromeBrowserThruAuth which will route to auth page as test account 
* @1148194--1-25-21-- Added launchChromeBrowserwithauthingcognito(String, String) and startTestExe(String, String) which will authenticate 
* test users with exe when opened in private mode
* -----------------------------------------------------------------------------
* 
* 06/08/2021 nrp0253353
* Changed this: String downloadFilepath = getProperty("test.downloadFilePath");
* to this: String downloadFilepath = System.getProperty("user.dir") + "\\DownloadFiles";
* for uploadTemplate() and downloadTemplate() methods
* -----------------------------------------------------------------------------
* 
* 12/10/2021 nrp0253353
* Updated launchRemoteBrowser() method so that it integrates the hRTN automation 
* process to follow the same hUTC process. This way, the hRTN flow can access and
* utilize all the available step definitions
* -----------------------------------------------------------------------------
* 
* 02/14/2022 nrp0253353
* Updated getProperty() method to getTestProperty() where apporopriate
* Created method:
* - decryptPassword()
* -----------------------------------------------------------------------------
*/

public class Browsers extends BaseConfiguration {
	
	public Browsers() {
		super();
	}
	
	private static Properties ssoProps;
	private static Properties userProps;
	
	// PLEASE USE THE FOLLOWING METHOD ONLY FOR hRTN AND hRTN-LOCAL MACHINE
	public void startTestwithAuth(String userID, String browser, String test) throws Throwable {
		try {
			test = test.replaceAll("[^a-zA-Z0-9\\-_\\.]+", "_");
			String outputPath = getTestProperty("test.path.output");

			setOutputDirName((outputPath == null ? "" : outputPath + File.separator) + test + File.separator + browser
					+ File.separator + new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new Date()) + File.separator);
			System.out.println("outputDirName:" + getOutputDirName());
			System.setProperty("test.output.path", getOutputDirName());
			System.setProperty("test.output.logfile", getOutputDirName() + "test.log");
			startLogger();
			String execLocation = getTestProperty("test.executionLocation");
			if (execLocation.equalsIgnoreCase("local") || execLocation.equalsIgnoreCase("hrtn")) {
				LOG.info("Test execution is occurring on the " + execLocation + " side.");
				if (browser.equalsIgnoreCase("Chrome")) {
					if (execLocation.equalsIgnoreCase("local")) {
						try {
							launchChromeBrowserThruAuth(userID);
						} catch (Throwable e) {
							e.printStackTrace();
						}
					} else if (execLocation.equalsIgnoreCase("hrtn")) {
						launchRemoteBrowserThruAuth(userID);
					}
				} else if (browser.equalsIgnoreCase("IE")) {
					if (execLocation.equalsIgnoreCase("local")) {
						launchIEBrowserwithauth(userID);
					} else if (execLocation.equalsIgnoreCase("hrtn")) {
						launchRemoteBrowser("internet explorer");
					}
				} else if (browser.equalsIgnoreCase("Edge")) {
					if (execLocation.equalsIgnoreCase("local")) {
						launchEdgeBrowserThruAuth(userID);
					} else if (execLocation.equalsIgnoreCase("hrtn")) {
						launchRemoteBrowser("MicrosoftEdge");
					}
				}
			} else if(execLocation.equalsIgnoreCase("hutc")){
				LOG.warn("When using this method, please make sure location is set to either hRTN or local");
			} else {
				LOG.error("incorrect location value");
				LOG.warn("Make sure that the location value in the test.properties file is set to either of these locations: local, hRTN, or hUTC");
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
			LOG.error(e.getMessage());
		}
	}
	
	public void startTest(String browser, String test) {
		try {
			test = test.replaceAll("[^a-zA-Z0-9\\-_\\.]+", "_");
			String outputPath = getTestProperty("test.path.output");
			setOutputDirName((outputPath == null ? "": outputPath + File.separator)  + test + File.separator + browser + File.separator +  new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new Date()) + File.separator);
			System.out.println("outputDirName:"+getOutputDirName());
			System.setProperty("test.output.path", getOutputDirName());
			System.setProperty("test.output.logfile", getOutputDirName() + "test.log");
			startLogger();
			String execLocation = getTestProperty("test.executionLocation");
			if (execLocation.equalsIgnoreCase("local") || execLocation.equalsIgnoreCase("hrtn") || execLocation.equalsIgnoreCase("hutc")) {
				LOG.info("Test execution is occurring on the " + execLocation + " side.");
				if (browser.equalsIgnoreCase("Chrome")) {
					if (execLocation.equalsIgnoreCase("local")) {
						launchChromeBrowser();
					} else if (execLocation.equalsIgnoreCase("hutc") || execLocation.equalsIgnoreCase("hrtn")) {
						launchRemoteBrowser("chrome");
					}
				} else if (browser.equalsIgnoreCase("IE")) {
					if (execLocation.equalsIgnoreCase("local")) {
						launchIEBrowser();
					} else if (execLocation.equalsIgnoreCase("hutc") || execLocation.equalsIgnoreCase("hrtn")) {
						launchRemoteBrowser("internet explorer");
					}
				} else if (browser.equalsIgnoreCase("Edge")) {
					if (execLocation.equalsIgnoreCase("local")) {
						launchEdgeBrowser();
					} else if (execLocation.equalsIgnoreCase("hutc") || execLocation.equalsIgnoreCase("hrtn")) {
						launchRemoteBrowser("MicrosoftEdge");
					}
				}
			} else {
				LOG.error("incorrect location value");
				LOG.warn("Make sure that the location value in the test.properties file is set to either of these locations: local, hRTN, or hUTC");
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
			LOG.error(e.getMessage());
		}
	}
	
	public void startTestExe(String browser, String test,String ExeFileName) throws Throwable {
		try {
			test = test.replaceAll("[^a-zA-Z0-9\\-_\\.]+", "_");
			String outputPath = getTestProperty("test.path.output");
			setOutputDirName((outputPath == null ? "": outputPath + File.separator)  + test + File.separator + browser + File.separator +  new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new Date()) + File.separator);
			System.out.println("outputDirName:"+getOutputDirName());
			System.setProperty("path", getOutputDirName());
			System.setProperty("test.output.logfile", getOutputDirName() + "test.log");
			startLogger();
			String execLocation = getTestProperty("test.executionLocation");
			if (execLocation.equalsIgnoreCase("local") || execLocation.equalsIgnoreCase("hrtn") || execLocation.equalsIgnoreCase("hutc")) {
				LOG.info("Test execution is occurring on the " + execLocation + " side.");
				if (browser.equalsIgnoreCase("Chrome")) {
					if (execLocation.equalsIgnoreCase("local")) {
						launchChromeBrowserwithauthingcognito(browser,test,ExeFileName);
					} else if (execLocation.equalsIgnoreCase("hutc") || execLocation.equalsIgnoreCase("hrtn")) {
						launchRemoteBrowser("chrome");
					}
				} else if (browser.equalsIgnoreCase("IE")) {
					if (execLocation.equalsIgnoreCase("local")) {
						//DOES NOT WORK FOR IE
//						launchIEBrowser();
					} else if (execLocation.equalsIgnoreCase("hutc") || execLocation.equalsIgnoreCase("hrtn")) {
						launchRemoteBrowser("internet explorer");
					}
				} else if (browser.equalsIgnoreCase("Edge")) {
					if (execLocation.equalsIgnoreCase("local")) {
						launchEdgeBrowserwithauthingcognito(browser,test,ExeFileName);
					} else if (execLocation.equalsIgnoreCase("hutc") || execLocation.equalsIgnoreCase("hrtn")) {
						launchRemoteBrowser("MicrosoftEdge");
					}
				}
			} else {
				LOG.error("incorrect location value");
				LOG.warn("Make sure that the location value in the test.properties file is set to either of these locations: local, hRTN, or hUTC");
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
			LOG.error(e.getMessage());
		}
	}

	public void startTest(String username, String password, String browser, String test) {
		try {
			test = test.replaceAll("[^a-zA-Z0-9\\-_\\.]+", "_");
			String outputPath = getTestProperty("test.path.output");
			setOutputDirName((outputPath == null ? "": outputPath + File.separator)  + test + File.separator + browser + File.separator +  new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new Date()) + File.separator);
			System.out.println("outputDirName:"+getOutputDirName());
			System.setProperty("test.output.path", getOutputDirName());
			System.setProperty("test.output.logfile", getOutputDirName() + "test.log");
			startLogger();
			String execLocation = getTestProperty("test.executionLocation");
			if (execLocation.equalsIgnoreCase("local") || execLocation.equalsIgnoreCase("hutc")) {	
				LOG.info("Test execution is occurring on the " + execLocation + " side.");
				if (browser.equalsIgnoreCase("Chrome")) {
					if (execLocation.equalsIgnoreCase("local")) {
						launchChromeBrowser(username,password);
					} else if (execLocation.equalsIgnoreCase("hrtn")) {
						launchRemoteBrowser("chrome");
					}
				} else if (browser.equalsIgnoreCase("IE")) {
					if (execLocation.equalsIgnoreCase("local")) {
						launchIEBrowser();
					} else if (execLocation.equalsIgnoreCase("hrtn")) {
						launchRemoteBrowser("internet explorer");
					}
				} else if (browser.equalsIgnoreCase("Edge")) {
					if (execLocation.equalsIgnoreCase("local")) {
						launchEdgeBrowser();
					} else if (execLocation.equalsIgnoreCase("hrtn")) {
						launchRemoteBrowser("MicrosoftEdge");
					}
				}
			} else if(execLocation.equalsIgnoreCase("hutc")){
				LOG.warn("When using this method, please make sure location is set to either hRTN or local");
			} else {
				LOG.error("incorrect location value");
				LOG.warn("Make sure that the location value in the test.properties file is set to either of these locations: local, hRTN, or hUTC");
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
			LOG.error(e.getMessage());
		}
	}
	
	private void launchRemoteBrowser(String browser) {
		try {
			String execLocation = getTestProperty("test.executionLocation");
			DesiredCapabilities capability = new DesiredCapabilities();
			capability.setBrowserName(browser);// "chrome" "internet explorer" "MicrosoftEdge"
			if (execLocation.equalsIgnoreCase("hutc")) {
				LOG.info("Connecting to the hUTC hub: " + getTestProperty("utcHub.IP") + " for browser: Chrome");
				driver = new RemoteWebDriver(new URL(getTestProperty("utcHub.IP")), capability);
			} else if (execLocation.equalsIgnoreCase("hrtn")) {
				LOG.info("Connecting to the hRTN hub: " + getTestProperty("rtnHub.IP") + " for browser: Chrome");
				driver = new RemoteWebDriver(new URL(getTestProperty("rtnHub.IP")), capability);
			}
			driver.manage().window().maximize();
		} catch (Exception e) {
			LOG.error("UNABLE TO launch Remote Browser");
			e.printStackTrace();
		}
	}

	private void launchIEBrowser() {
		try {
		System.setProperty("webdriver.ie.driver", getTestProperty("test.path.selenium.ie.driverserver"));
		driver = new InternetExplorerDriver();
		setDriverProperties(driver);
		} catch (IOException e) {
			LOG.error(e.getMessage());
		}
	}
	
	public void decryptPassword() throws Throwable {
		try {
			final OneClickEncryptor encryptor = new OneClickEncryptor(getSsoProps().getProperty("aes.encryption.key"));
			String Password = encryptor.decryptValue(getUserProps().getProperty("test.user.password.", getUserProps().getProperty("test.user.password.default")));
			driver.findElement(By.name("PASSWORD")).sendKeys(Password);
			driver.findElement(By.cssSelector("input:nth-child(5)")).submit();
			if (driver.getTitle() == "Raytheon Simple Auth Login: Internal") {
				// LOG.error("FAIL - Check that the PASSWORD FOR TEST ACCOUNT ");
				// driver.quit();
			}
		} catch (Exception e) {
			driver.quit();
			throw new InstantiationException("Could not retrieve password from encrypted file");
		}
	}

	public void launchIEBrowserwithauth(String userID) throws Throwable  {		
		System.setProperty("webdriver.ie.driver", getTestProperty("test.path.selenium.ie.driverserver"));
		InternetExplorerOptions Options = new InternetExplorerOptions();
//      Options.setCapability(InternetExplorerDriver.FORCE_CREATE_PROCESS, true);
//      Options.useCreateProcessApiToLaunchIe();
        Options.setCapability(InternetExplorerDriver.IE_SWITCHES,"--private");
        Options.introduceFlakinessByIgnoringSecurityDomains();
        Options.setCapability("ignoreProtectedModeSettings", true);
        Options.setCapability("nativeEvents", false);
        driver = new InternetExplorerDriver(Options);

		setDriverProperties(driver);
		driver.manage().deleteAllCookies();		
		String AuthURL=getTestProperty("auth.url");
		driver.get(AuthURL);
		driver.findElement(By.name("USER")).sendKeys(userID);
		decryptPassword();
	}
	
	private void launchChromeBrowser() {
		try {

		//	System.setProperty("webdriver.chrome.driver", getTestProperty("test.path.selenium.chrome.driverserver"));
//			WebDriverManager chromeDriver = WebDriverManager.chromedriver();
//			chromeDriver.proxy(getTestProperty("chrome.network.proxy")).setup();
			
			WebDriverManager.chromedriver().proxy(getTestProperty("chrome.network.proxy")).setup();
//			System.setProperty("webdriver.chrome.driver", getTestProperty("test.path.selenium.chrome.driverserver"));
//			WebDriverManager chromeDriver = WebDriverManager.chromedriver();
//			chromeDriver.proxy(getTestProperty("chrome.network.proxy")).setup();
			ChromeOptions options = new ChromeOptions();
			options.addArguments("--remote-allow-origins=*");
			options.addArguments("--start-maximized");
//    		options.addArguments("--incognito");
			//********** need to be set while it needs to run on different os
			//	options.setCapability("platformName", "Windows 10");
			options.setExperimentalOption("useAutomationExtension", true);
			options.addArguments("--ignore-certificate-errors");
		    options.setAcceptInsecureCerts(true);
		    options.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS, true);
		    
		    // ADD: Configuración para pruebas sin Chrome instalado
		    options.addArguments("--headless"); // Modo sin interfaz gráfica
		    options.addArguments("--no-sandbox");
		    options.addArguments("--disable-dev-shm-usage");
		    options.addArguments("--disable-gpu");
		    options.addArguments("--window-size=1920,1080");
			driver = new ChromeDriver(options);
			driver.manage().deleteAllCookies();
		//	setDriverProperties(driver);
		} catch (IOException e) {
			LOG.error(e.getMessage());
		}
	}
	
	public void launchChromeBrowserwithauthingcognito(String browser, String test, String ExeFileName) throws Throwable {
		try {

			WebDriverManager.chromedriver().proxy(getTestProperty("chrome.network.proxy")).setup();
			ChromeOptions options = new ChromeOptions();
//		//	System.setProperty("webdriver.chrome.driver", getTestProperty("test.path.selenium.chrome.driverserver")); 			
			//	setDriverProperties(driver);
		 	//*********Below line of 475 code can used when there is specific test onlt to test with particular version of browser
	        //options.setBinary("C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe");		
			//WebDriverManager chromeDriver = WebDriverManager.chromedriver();
			//chromeDriver.proxy(getTestProperty("chrome.network.proxy")).setup();
	//		ChromeOptions options = new ChromeOptions();
		//	System.setProperty("webdriver.chrome.driver", getTestProperty("test.path.selenium.chrome.driverserver")); 
			driver = new ChromeDriver();
			setDriverProperties(driver);
		    options.addArguments("--incognito");
	    	//options.addArguments("--remote-allow-origins=*");
            //options.setExperimentalOption("useAutomationExtension", false);
            //Handle accepting insecure certs
            //options.addArguments("--ignore-certificate-errors");
           // options.setAcceptInsecureCerts(true);
           //options.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS, true);
          //options.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
		    String downloadFilepath = System.getProperty("user.dir") + "\\DownloadFiles";
	        HashMap<String, Object> chromePrefs = new HashMap<String, Object>();
	        chromePrefs.put("profile.default_content_settings.popups", 0);
	        chromePrefs.put("download.default_directory", downloadFilepath);
	        options.setExperimentalOption("prefs", chromePrefs);   
			driver = new ChromeDriver(options);
			setDriverProperties(driver);
			String TestURL=getTestProperty("test.url");			
			driver.get(TestURL);			
			runtestexe(ExeFileName);	
			Thread.sleep(5000);
			}
			catch (IOException e) {
			LOG.error(e.getMessage());
				driver.quit();
		}
	} 
	public void launchEdgeBrowserwithauthingcognito(String browser, String test, String ExeFileName) throws Throwable {
		try {
			WebDriverManager.edgedriver().proxy(getTestProperty("edge.network.proxy")).setup();
			EdgeOptions options = new EdgeOptions();
			options.addArguments("inprivate");
		//	System.setProperty("webdriver.chrome.driver", getTestProperty("test.path.selenium.chrome.driverserver")); 			
			//	setDriverProperties(driver);
	//*********Below line of 475 code can used when there is specific test onlt to test with particular version of browser
	        //options.setBinary("C:\\Program Files\\Google\\Chrome\\Application\\edge.exe");		
	//********** need to be set while it needs to run on different os
		    //options.setCapability("platformName", "Windows 10");
	    	//options.addArguments("--remote-allow-origins=*");
            //options.setExperimentalOption("useAutomationExtension", false);
            //Handle accepting insecure certs
            //options.addArguments("--ignore-certificate-errors");
           // options.setAcceptInsecureCerts(true);
           //options.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS, true);
          //options.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);		 
			driver = new EdgeDriver(options);
			setDriverProperties(driver);
			String TestURL=getTestProperty("test.url");			
			driver.get(TestURL);			
			runtestexe(ExeFileName);	
			Thread.sleep(5000);
			}
			catch (IOException e) {
			LOG.error(e.getMessage());
				driver.quit();
		}
	} 

	private void launchChromeBrowser(String username, String password) {
		try {
		//	WebDriverManager chromeDriver = WebDriverManager.chromedriver();
			//chromeDriver.proxy(getTestProperty("chrome.network.proxy")).setup();
			WebDriverManager.chromedriver().proxy(getTestProperty("chrome.network.proxy")).setup();
			ChromeOptions options = new ChromeOptions();
		//	System.setProperty("webdriver.chrome.driver", getTestProperty("test.path.selenium.chrome.driverserver")); 
		//	ChromeOptions options = new ChromeOptions();
		//	System.setProperty("webdriver.chrome.driver", getTestProperty("test.path.selenium.chrome.driverserver")); 
			driver = new ChromeDriver();
			setDriverProperties(driver);
		    options.addArguments("--incognito");
			options.setExperimentalOption("useAutomationExtension", false);
	        //Handle accepting insecure certs
	        options.addArguments("--ignore-certificate-errors");
	        options.setAcceptInsecureCerts(true);
	        options.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS, true);	     		
	       // options.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS, true);
	      //  options.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);			
			driver = new ChromeDriver(options);
			driver.manage().deleteAllCookies();
			String smcookie = SSOHandshake.getSmSessionCookie(username, password);
			LOG.debug("Browsers:  smcookie:  "+ smcookie);
			String[] smcookieString =smcookie.split(";");
			LOG.debug("smcookie split into " + smcookieString.length);
			LOG.debug("SMSession cookie = " + smcookieString[0] +" \n "+ smcookieString[1] + " ; " + smcookieString[2]);
			String[] value = smcookieString[0].split("=");
			String[] path = smcookieString[1].split("=");
			String[] domain = smcookieString[2].split("=");
			Cookie smck = new Cookie(value[0],value[1],domain[1],path[1], null);
			LOG.debug("Adding Cookie " + smck);
			driver.get(SSOHandshake.getAlternateLoginURL());
			driver.manage().addCookie(smck);			
			setDriverProperties(driver);
		} catch (IOException e) {
			LOG.error(e.getMessage());
		}
	}
	
	public void launchEdgeBrowser() {
		try {
			WebDriverManager.edgedriver().proxy(getTestProperty("edge.network.proxy")).setup();
			driver = new EdgeDriver();    
		//	EdgeOptions options = new EdgeOptions(); 
		//	options.getBrowserVersion();
		//	System.setProperty("webdriver.edge.driver", getTestProperty("test.path.selenium.edge.driverserver")); 
			
		//	setDriverProperties(driver);
		} catch (IOException e) {
			LOG.error(e.getMessage());
		}
	}
	
	
	public void launchEdgeBrowserThruAuth(String userID)  throws Throwable {
		try {
		//System.setProperty("webdriver.edge.driver", getTestProperty("test.path.selenium.edge.driverserver")); 		
		WebDriverManager.edgedriver().proxy(getTestProperty("edge.network.proxy")).setup();
			driver = new EdgeDriver();    
			EdgeOptions options = new EdgeOptions(); 
			options.getBrowserVersion();
		//	setDriverProperties(driver);options.addArguments("--incognito");         			 
				driver.manage().deleteAllCookies();
				String AuthURL=getTestProperty("auth.url");
				driver.get(AuthURL);
				driver.findElement(By.name("USER")).sendKeys(userID);
				try {
					final OneClickEncryptor encryptor = new OneClickEncryptor(getSsoProps().getProperty("aes.encryption.key"));
					String Password = encryptor.decryptValue(getUserProps().getProperty("test.user.password.",getUserProps().getProperty("test.user.password.default")));
					driver.findElement(By.name("PASSWORD")).sendKeys(Password);
					driver.findElement(By.cssSelector("input:nth-child(5)")).click();
				} catch (final Exception e) {
					LOG.error("FAIL - Check that the PASSWORD FOR TEST ACCOUNT ");
					driver.quit();
					throw new InstantiationException("Could not retrieve password from encrypted file");
				}
			} catch (final IOException e) {
				LOG.error(e.getMessage());
				e.printStackTrace();
			}
		}
	
		
	

	
    public void launchChromeBrowserThruAuth(String userID) throws Throwable {
		try {

			WebDriverManager.chromedriver().proxy(getTestProperty("chrome.network.proxy")).setup();
			ChromeOptions options = new ChromeOptions();
//		//	System.setProperty("webdriver.chrome.driver", getTestProperty("test.path.selenium.chrome.driverserver")); 			
			//	setDriverProperties(driver);
		 	//		Below line of 475 code can used when there is specific test onlt to test with particular version of browser
	//		options.setBinary("C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe");
			options.setExperimentalOption("useAutomationExtension", true);
        	//Handle accepting insecure certs
	   //     options.addArguments("--ignore-certificate-errors");
	   //     options.setAcceptInsecureCerts(true);
	   //     options.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS, true);
	   //     options.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
	   //     options.addArguments("--disable-extensions"); 
	    //    options.addArguments("--test-type");

//		 	options.addArguments("--incognito");
		//	options.setBinary("C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe");
	//		options.setExperimentalOption("useAutomationExtension", false);
        	//Handle accepting insecure certs
	        options.addArguments("--ignore-certificate-errors");
	        options.setAcceptInsecureCerts(true);
	        options.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS, true);
	   //     options.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
	        options.addArguments("--disable-extensions"); 
	        options.addArguments("--test-type");
	        options.addArguments("--start-maximized");
	        String downloadFilepath = System.getProperty("user.dir") + "\\DownloadFiles";
	        HashMap<String, Object> chromePrefs = new HashMap<String, Object>();
	        chromePrefs.put("profile.default_content_settings.popups", 0);
	        chromePrefs.put("download.default_directory", downloadFilepath);
	        options.setExperimentalOption("prefs", chromePrefs);   
	        driver = new ChromeDriver(options);
			driver.manage().deleteAllCookies();
			String AuthURL=getTestProperty("auth.url");
			driver.get(AuthURL);
			driver.findElement(By.name("USER")).sendKeys(userID);
			try {
				final OneClickEncryptor encryptor = new OneClickEncryptor(getSsoProps().getProperty("aes.encryption.key"));
				String Password = encryptor.decryptValue(getUserProps().getProperty("test.user.password.",getUserProps().getProperty("test.user.password.default")));
				driver.findElement(By.name("PASSWORD")).sendKeys(Password);
				driver.findElement(By.cssSelector("input:nth-child(5)")).click();
			} catch (final Exception e) {
				LOG.error("FAIL - Check that the PASSWORD FOR TEST ACCOUNT ");
				driver.quit();
				throw new InstantiationException("Could not retrieve password from encrypted file");
			}
		} catch (final IOException e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
	}
	
    public void launchRemoteBrowserThruAuth(String userID) throws Throwable {
		try {
			LOG.info("Connecting to hub: " + getTestProperty("rtnHub.IP") + " for browser: Chrome");
			WebDriverManager chromeDriver = WebDriverManager.chromedriver();
			chromeDriver.proxy(getTestProperty("chrome.network.proxy")).setup();
			ChromeOptions options = new ChromeOptions();
//		 	options.addArguments("--incognito");
			options.setExperimentalOption("useAutomationExtension", false);
        	//Handle accepting insecure certs
	        options.addArguments("--ignore-certificate-errors");
	        options.setAcceptInsecureCerts(true);
	        options.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS, true);
	    //    options.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
    		options.addArguments("--no-sandbox");
			 options.addArguments("--headless");
			options.addArguments("--disable-dev-shm-usage");
	        options.addArguments("--disable-extensions"); 
	        options.addArguments("--test-type");
	        options.addArguments("--start-maximized");
			LOG.info("Finished ChromeOptions.");
	        String downloadFilepath = System.getProperty("user.dir") + "\\DownloadFiles";
	        HashMap<String, Object> chromePrefs = new HashMap<String, Object>();
	        chromePrefs.put("profile.default_content_settings.popups", 0);
	        chromePrefs.put("download.default_directory", downloadFilepath);
	        options.setExperimentalOption("prefs", chromePrefs);
			LOG.info("Finished ChromePrefs.");
			DesiredCapabilities capability = new DesiredCapabilities();
			capability.setCapability(ChromeOptions.CAPABILITY, options);
			options.merge(capability);
			LOG.info("Finished Desired Capabilities and merged with ChromeOptions.");
			driver = new RemoteWebDriver(new URL(getTestProperty("rtnHub.IP")), options);
			LOG.info("Chrome openned and maximized.");
			driver.manage().deleteAllCookies();
			String AuthURL=getTestProperty("auth.url");
			driver.get(AuthURL);
			LOG.info("Alternate url opened.");
			driver.findElement(By.name("USER")).sendKeys(userID);
			LOG.info("UserID sent to Auth popup text box.");
			try {
				final OneClickEncryptor encryptor = new OneClickEncryptor(getSsoProps().getProperty("aes.encryption.key"));
				String Password = encryptor.decryptValue(getUserProps().getProperty("test.user.password.",getUserProps().getProperty("test.user.password.default")));
				driver.findElement(By.name("PASSWORD")).sendKeys(Password);
				LOG.info("Password sent to Auth popup text box. ");
				driver.findElement(By.cssSelector("input:nth-child(5)")).click();
				LOG.info("Clicked the Submit button of Auth popup text box ");
				LOG.info("Successfully authenticated thru Alternate URL. ");
			} catch (final Exception e) {
				LOG.error("FAIL - Check that the PASSWORD FOR TEST ACCOUNT is correct.");
				driver.quit();
				throw new InstantiationException("Could not retrieve password from encrypted file");
			}
		} catch (final IOException e) {
			LOG.error(e.getMessage());
		}
	}
	
	private void setDriverProperties(WebDriver driver) throws IOException {
		String timeoutStr = getTestProperty("test.timeout.seconds");
		long timeout = timeoutStr!=null? Long.valueOf(timeoutStr): 5;
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(timeout));
		driver.manage().window().maximize();
	}

	public void pageError(String error) {
		LOG.error(error);
		error = error.replaceAll("[^a-zA-Z0-9 ]+","");
		String timestamp = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new Date());
		TakesScreenshot scrShot = ((TakesScreenshot) driver);
		File SrcFile = scrShot.getScreenshotAs(OutputType.FILE);
		File DestFile = new File(System.getProperty("CCS.test.output.path")+ timestamp + "_" + error + ".jpeg");
		try {
			FileUtils.copyFile(SrcFile, DestFile);
			LOG.error("Screenshot taken - " + DestFile.toString());
		} catch (Exception e) {
			LOG.error("Take Screenshot Failed");
		}
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
	
	public void startDefaultURL(String browser, String test) {
		try {
			String outputPath = getTestProperty("test.path.output");
			setOutputDirName((outputPath == null ? "" : outputPath + File.separator) + test + File.separator + browser
					+ File.separator + new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new Date()) + File.separator);
			System.out.println("outputDirName:" + getOutputDirName());
			System.setProperty("test.output.path", getOutputDirName());
			File dirs = new File(getOutputDirName());
			dirs.mkdirs();
			System.setProperty("test.output.logfile", getOutputDirName() + "test.log");
			startLogger();
			String execLocation = getTestProperty("test.executionLocation");
			if (execLocation.equalsIgnoreCase("local") || execLocation.equalsIgnoreCase("hrtn") || execLocation.equalsIgnoreCase("hutc")) {
				LOG.info("Test execution is occurring on the " + execLocation + " side.");
				if (browser.equalsIgnoreCase("Chrome")) {
					if (execLocation.equalsIgnoreCase("local")){
						launchChromeBrowser();
					} else if (execLocation.equalsIgnoreCase("hutc") || execLocation.equalsIgnoreCase("hrtn")) {
						launchRemoteBrowser("chrome");
					}
				} else if (browser.equalsIgnoreCase("IE")) {
					if (execLocation.equalsIgnoreCase("local")){
						launchIEBrowser();
					} else if (execLocation.equalsIgnoreCase("hutc") || execLocation.equalsIgnoreCase("hrtn")) {
						launchRemoteBrowser("internet explorer");
					}
				} else if (browser.equalsIgnoreCase("Edge")) {
					if (execLocation.equalsIgnoreCase("local")){
						launchEdgeBrowser();
					}else if (execLocation.equalsIgnoreCase("hutc") || execLocation.equalsIgnoreCase("hrtn")) {
						launchRemoteBrowser("MicrosoftEdge");
					}
				}
			} else {
				LOG.error("incorrect location value");
				LOG.warn("Make sure that the location value in the test.properties file is set to either of these locations: local, hRTN, or hUTC");
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
			LOG.error(e.getMessage());
		}
		SeleniumPageObjectExample pageExample = new SeleniumPageObjectExample();
		pageExample.openPage();
	}
	
}