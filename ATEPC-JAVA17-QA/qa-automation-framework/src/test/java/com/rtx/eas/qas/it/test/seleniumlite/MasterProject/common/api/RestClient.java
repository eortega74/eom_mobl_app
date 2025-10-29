package com.rtx.eas.qas.it.test.seleniumlite.MasterProject.common.api;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

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
* @authors nrp0253348
*
* Change Log:
* 
* -----------------------------------------------------------------------------
*/

public class RestClient extends BaseConfiguration{

	/* GENERIC GET METHOD WITHOUT HEADERS. */
	public CloseableHttpResponse get(String url) throws ClientProtocolException, IOException {
			CloseableHttpClient httpClient = HttpClients.createDefault();
		
			/* Create get connection with this url. */
			HttpGet httpGet = new HttpGet(url);

			url = CommonObjects.getApiProps().getProperty("endpoint");
			
		/* Send the request to hit the GET url. */
		CloseableHttpResponse closeableHttpResponse = httpClient.execute(httpGet);
		return closeableHttpResponse;
	}

	/* GENERIC GET METHOD WITH HEADERS. */
	public CloseableHttpResponse get(String url, HashMap<String, String> headerMap)
			throws ClientProtocolException, IOException {

		CloseableHttpClient httpClient = HttpClients.createDefault();

		/* Create get connection with this url. */
		HttpGet httpGet = new HttpGet(url);
		for (Map.Entry<String, String> entry : headerMap.entrySet()) {
			httpGet.addHeader(entry.getKey(), entry.getValue());
		}

		/* Send the request to hit the GET url. */
		CloseableHttpResponse closeableHttpResponse = httpClient.execute(httpGet);
		return closeableHttpResponse;
	}

	/* GENERIC POST METHOD WITHOUT HEADERS. */
	public CloseableHttpResponse post(String url, String entityString, HashMap<String, String> headerMap)
			throws ClientProtocolException, IOException {

		CloseableHttpClient httpClient = HttpClients.createDefault();

		/* Create get connection with this url. */
		HttpPost httpPost = new HttpPost(url);

		/* Define the payload. */
		httpPost.setEntity(new StringEntity(entityString));

		/* Define headers. */
		for (Map.Entry<String, String> entry : headerMap.entrySet()) {
			httpPost.addHeader(entry.getKey(), entry.getValue());
		}
		/* Send the request to hit the GET url. */
		CloseableHttpResponse closeableHttpResponse = httpClient.execute(httpPost);
		return closeableHttpResponse;
	}

}