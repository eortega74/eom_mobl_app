package com.rtx.eas.qas.it.test.seleniumlite.MasterProject.cucumber.step.common;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;

import com.rtx.eas.qas.it.test.seleniumlite.MasterProject.common.api.GetRequest;
import com.rtx.eas.qas.it.test.seleniumlite.MasterProject.api.implementation.CookieMethods;
import com.rtx.eas.qas.it.test.seleniumlite.MasterProject.selenium.BaseConfiguration;
import com.rtx.eas.qas.it.test.seleniumlite.MasterProject.selenium.Browsers;

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
* @authors nrp0253353/nrp0253348
*
* Change Log:
* 
* 02/14/2022 nrp0253353
* Combined multiple classes into this one class.
* Improved and generalized the entire flow for API test execution.
* Corrected issues with code logic
* -----------------------------------------------------------------------------
*/

public class APISteps extends BaseConfiguration {

	Browsers brw = new Browsers();
	CookieMethods cookieMethods;
	GetRequest getRequest;

	@When("cookie is passed in the header")
	public void i_Set_request_HEADER_with_ssoNonProd() throws Throwable {
		cookieMethods = new CookieMethods();
		cookieMethods.passCookieInHeader();
	}

	@Then("user can validate the HTTP response code")
	public void i_validate_ssoNonProd_HTTP_Response_code() throws IOException {
		cookieMethods.assertHeader();
		
		getRequest = new GetRequest();
		//All common api are here.
		getRequest.GetApiDetails();
	}
	
	//Given for JWT test
	@Given("I Set GET SOA service api {string}")
	public void i_Set_GET_SOA_service_api(String token) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, UnrecoverableEntryException, IOException {
		cookieMethods = new CookieMethods();
		cookieMethods.getJwtCookie();
	}

}
