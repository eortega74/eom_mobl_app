package com.rtx.eas.qas.it.test.seleniumlite.MasterProject.common.api;

import java.io.IOException;

import org.junit.Test;

import com.rtx.eas.qas.it.test.seleniumlite.MasterProject.selenium.BaseConfiguration;
import com.rtx.eas.qas.it.test.seleniumlite.MasterProject.selenium.CommonObjects;

import io.restassured.RestAssured;
import io.restassured.response.Response;

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
* @authors nrp0253348
*
* Change Log:
* 
* -----------------------------------------------------------------------------
*/

public class GetRequest extends BaseConfiguration {
	
	public void GetApiDetails() throws IOException {
		LOG.info("Starting of the execution: ");

		// the url is being retrieved from api.properties file
		Response response = RestAssured.get(CommonObjects.getApiProps().getProperty("request.url"));
	
		// get response body
		LOG.info(response.getBody());

		// get response statusCode
		LOG.info(response.getStatusCode());

		// get response statusLine
		LOG.info(response.getStatusLine());

		// get response header
		LOG.info(response.getHeader("content-type"));

		// get response current time
		LOG.info(response.getTime());

		// execution ended successfully
		LOG.info("Ending of the successful execution: ");
	}

}