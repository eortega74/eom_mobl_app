package com.rtx.eas.qas.it.test.seleniumlite.MasterProject.api.implementation;

import static io.restassured.RestAssured.given;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.rtx.eas.qas.it.test.seleniumlite.MasterProject.api.constants.AppConstants;
import com.rtx.eas.qas.it.test.seleniumlite.MasterProject.api.util.JWTUtil;
import com.rtx.eas.qas.it.test.seleniumlite.MasterProject.common.GenericMethods;
import com.rtx.eas.qas.it.test.seleniumlite.MasterProject.selenium.BaseConfiguration;
import com.rtx.eas.qas.it.test.seleniumlite.MasterProject.selenium.CommonObjects;

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
* @authors nrp0253353/nrp0253348
*
* Change Log:
* 
* 02/14/2022 nrp0253353
* Combined multiple classes into this one class.
* Improved and generalized the entire flow for API test execution.
* Corrected issues with code logic
* Added logging and try/catch blocks
* -----------------------------------------------------------------------------
*/

public class CookieMethods extends BaseConfiguration{
	
	GenericMethods gm = new GenericMethods();
	
	public String fetchCookie() throws IOException {
		try {
			WebElement smSession = driver.findElement(By.xpath("/html/body/table[3]/tbody/tr/td/table/tbody/tr[2]/td[2]"));
			String smSessionText = gm.smCookieCleanUp(smSession);
			
			String cookie = CommonObjects.getApiProps().getProperty("SMScookie.piece") + " SMSESSION=" + smSessionText;
			
			LOG.info("SMSESSION cookie passed: " + cookie);
			LOG.info("Test is executing on the " + CommonObjects.getApiProps().getProperty("environment") + " environment");
			return cookie;
		} catch(Exception e) {
			LOG.error("Unable to obtain the cookie");
			LOG.error(e.getMessage());
			e.printStackTrace();
			return null;
		}
	}
	
	public String fetchEndPoint() throws IOException {
		try {
			String endPoint = CommonObjects.getApiProps().getProperty("endPoint");
			LOG.info("Testing endpoint: " + endPoint);
			
			return endPoint;
		} catch(Exception e) {
			LOG.error("Unable to obtain the endpoint");
			LOG.error(e.getMessage());
			e.printStackTrace();
			return null;
		}
	}
	
	public void passCookieInHeader() throws Throwable {
		try {
			String url = fetchEndPoint();
			String cookie = fetchCookie();
			
			Map<String, String> headers = new HashMap<String, String>() {
				{
					put("Accept", "application/json");
					put("cookie", cookie);
				}
			};
			given().headers(headers).when().get(url).then().assertThat().statusCode(200).log().all();
			LOG.info("Successfully passed cookie in header");
		} catch(Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void assertHeader() throws IOException {
		try {
			String url = fetchEndPoint();
			String cookie = fetchCookie();
			
			given().header("cookie", cookie).get(url).then().assertThat().statusCode(200).log().all();
			LOG.info("Assert header complete");
		} catch(Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
	}
	
	//The following method is used for nonProd SOA APIgee, Prod, and ProdAPIgee test scenarios
	public void passCookieInHeader2() throws IOException, URISyntaxException {
		try {
			String url = fetchEndPoint();
			String cookie = fetchCookie();
			
			Map<String, String> headers = new HashMap<String, String>();
			headers.put("Accept", "application/json");
			headers.put("cookie", cookie);

			given().headers(headers).when().get(new URI(url)).then().assertThat().statusCode(200).log().all();
		} catch(Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
	}
	
	//Same code as passCookieInHeader and this method was ONLY used for nonProd SOA APIgee test scenario
	public void validateResCode() throws IOException {
		try {
			String url = fetchEndPoint();
			String cookie = fetchCookie();

			Map<String, String> headers = new HashMap<String, String>();
			headers.put("Accept", "application/json");
			headers.put("cookie", cookie);

			given().header("cookie", cookie).get(url).then().assertThat().statusCode(200).log().all();
		} catch(Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void getJwtCookie() throws IOException {
		try {
			String token = JWTUtil.createJwtToken("synergy-testapp.nonprod", "Audiance");
			LOG.info("JwtToken Endpoint NonProd: " + CommonObjects.getApiProps().getProperty("nonProd.echo-jwt.endpoint"));
			LOG.info("JWT TOKEN CERT NonProd: " + AppConstants.PKI_CERT_RESOURCE);
			LOG.info("JWT TOKEN VALUE NONPROD: " + token);

			String tokenProd = JWTUtil.createJwtToken_Prod("synergy-testapp.prod", "Audiance");

			LOG.info("JwtToken Endpoint Prod: " + CommonObjects.getApiProps().getProperty("prod.echo-jwt.endpoint"));
			LOG.info("JWT TOKEN CERT Prod: " + AppConstants.PKI_CERT_RESOURCE_PROD);
			LOG.info("JWT TOKEN VALUE PROD: " + tokenProd);
		} catch(Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		
	}
}
