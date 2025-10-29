package com.rtx.eas.qas.it.test.seleniumlite.MasterProject.api.constants;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
* @authors nrp0253348
*
* Change Log:
* 
* -----------------------------------------------------------------------------
*/

public class AppConstants {

	private static final Logger logger = LoggerFactory.getLogger(AppConstants.class);

	private static boolean inJunitTest = false;

	public static boolean isInJunitTest() {
		return inJunitTest;
	}

	public static void setInJunitTest(boolean inJunitTest) {
		AppConstants.inJunitTest = inJunitTest;
	}
	
	//Add the nonprod cert path within the quotes for the following PKI_CERT_PATH_NONPROD parameter
	public static final String PKI_CERT_PATH_NONPROD = System.getProperty("user.dir") + "";

	public static final int TIME_SKEW_FUDGE_SECONDS = 10;
	public static final int TIMEOUT_SECONDS = 60;

	//Add the cert path within the quotes for the following PKI_CERT_PATH parameter
	public static final String PKI_CERT_PATH = System.getProperty("user.dir") + "";

	/*Add the cert resource path within the quotes for the following parameters
	 * Ex1: PKI_CERT_RESOURCE = PKI_CERT_PATH + "synergy-testapp.nonprod.jks";
	 * Ex2: PKI_CERT_RESOURCE_PROD = PKI_CERT_PATH + "synergy-testapp.prod.jks"
	 * */
	public static final String PKI_CERT_RESOURCE = PKI_CERT_PATH + "";
	public static final String PKI_CERT_RESOURCE_PROD = PKI_CERT_PATH + "";

	public static final String CERT_TYPE = "JKS";

	public static final String ENVIRONMENT_DEV = "DEV";
	public static final String ENVIRONMENT_QA2 = "QA2";
	public static final String ENVIRONMENT_QA = "QA";
	public static final String ENVIRONMENT_UAT = "UAT";
	
	public static Properties prop;
	public static final int RESPONSE_STATUS_CODE_200 = 200;
	public static final int RESPONSE_STATUS_CODE_500 = 500;
	public static final int RESPONSE_STATUS_CODE_400 = 400;
	public static final int RESPONSE_STATUS_CODE_401 = 401;
	public static final int RESPONSE_STATUS_CODE_201 = 201;
	
	//Add users.json path within the quotes for the following USER_PATH parameter
	public static String USER_PATH = System.getProperty("user.dir") + "";
	public static String CONFIG_PROPERTIES_PATH = System.getProperty("user.dir") + "/src/test/resources/api.properties";


}

