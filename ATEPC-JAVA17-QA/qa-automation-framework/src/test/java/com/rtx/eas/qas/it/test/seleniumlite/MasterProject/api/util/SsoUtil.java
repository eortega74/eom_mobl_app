package com.rtx.eas.qas.it.test.seleniumlite.MasterProject.api.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.CharEncoding;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicHeader;

import com.rtx.eas.qas.it.test.seleniumlite.MasterProject.api.exception.ExternalServiceException;

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

public class SsoUtil {

	public enum SsoType {
		TEST_SSO("https://webauth-test.ext.ray.com/siteminderagent/forms/login.fcc",
				"e3ExuLNlFco5BtI5W8WII0U5vpXfnGuuB8rMk4IeTnPZvkaiJZYqGEvZ0vC9Xb3u",
				"-SM-HTTPS://webauth--testiwa.ext.ray.com/bounce/bounce.asp"),

		PROD_SSO("https://webauth.ext.ray.com/siteminderagent/forms/login.fcc",
				"PZpEL7wM03kO2LyBKIRHd6Yrewdl1lMi8oA9JXp7DZUgw4HAEqwdQKXB1ksdtgOD",
				"https://webauth--iwa.ext.ray.com/bounce/bounce.asp");

		private String ssoHost;
		private String smAgentName;
		private String redirectUrl;

		private SsoType(String ssoHost, String smAgentName, String redirectUrl) {
			this.ssoHost = ssoHost;
			this.smAgentName = smAgentName;
			this.redirectUrl = redirectUrl;
		}

		public String getSsoHost() {
			return ssoHost;
		}

		public String getSmAgentName() {
			return smAgentName;
		}

		public String getRedirectUrl() {
			return redirectUrl;
		}
	}

	public static String ssoEncode(String value) {
		String result = "";
		result = value.replace("-", "----");
		result = result.replace(":", "-:");
		result = result.replace("/", "-/");
		result = result.replace(".", "-.");
		return result;
	}

	/**
	 * Gets the SMS cookie that can be used to send further requests to endpoints
	 * that are projected by SSO.
	 * 
	 * The method returns the SMS cookie or null if login fails.
	 * 
	 * @param ssoType
	 * @param userId
	 * @param password
	 * @return
	 * @throws ExternalServiceException
	 */
	// public static String getSmSessionCookie(SsoType ssoType, String userId,
	// String password) throws ExternalServiceException {
	public static String getSmSessionCookie(SsoType ssoType) throws ExternalServiceException {

		String SSO_HOST = ssoType.getSsoHost();
		String SMAGENTNAME = ssoType.getSmAgentName();
		String REDIRECT = ssoType.getRedirectUrl();

		StringBuilder url = new StringBuilder();

		url.append(SSO_HOST);
		url.append("?SMAGENTNAME=" + SMAGENTNAME);
		url.append("&TARGET=" + REDIRECT);

		CloseableHttpClient httpClient = null;
		try {
			httpClient = HttpClientUtil.getConfiguredHttpClient();
		} catch (IOException e) {
			throw new ExternalServiceException(e);
		}
		HttpResponse response = null;
		/*
		 * HttpPost post = new HttpPost(url.toString()); post.addHeader("Content-Type",
		 * "application/x-www-form-urlencoded");
		 * 
		 * HttpEntity entity = new SsoLoginHttpEntity(userId, password);
		 * 
		 * post.setEntity(entity);
		 * 
		 * 
		 * try { response = httpClient.execute(post); } catch (IOException e) { throw
		 * new ExternalServiceException(e); }
		 */

		Header[] responseHeaders = response.getHeaders("Set-Cookie");
		if (responseHeaders != null) {
			for (Header header : responseHeaders) {
				String[] subCookies = header.getValue().split(";");

				for (String subCookie : subCookies) {
					for (String possibleKey : new String[] { "SMSESSION" }) {
						if (subCookie.toUpperCase().contains(possibleKey)) {
							String[] smSessionParts = subCookie.split("=");
							if (smSessionParts.length == 2) {
								return smSessionParts[1];
							}
						}
					}
				}
			}
		}

		HttpClientUtils.closeQuietly(httpClient);

		return null;
	}

	public static String executeGet(String targetURL, String smSessionCookie)
			throws ClientProtocolException, IOException {
		String responseString = null;

		CloseableHttpClient httpClient = null;
		try {
			httpClient = HttpClientUtil.getConfiguredHttpClient();

			HttpGet httpGetRequest = new HttpGet(targetURL);
			httpGetRequest.addHeader("Cookie", "SMSESSION=" + smSessionCookie + "; path=/; domain=.ray.com");

			HttpResponse response = httpClient.execute(httpGetRequest);
			responseString = IOUtils.toString(response.getEntity().getContent(), CharEncoding.UTF_8);
		} finally {
			HttpClientUtils.closeQuietly(httpClient);
		}

		return responseString;
	}

	/*
	 * private static class SsoLoginHttpEntity implements HttpEntity {
	 * 
	 * private String postBody; public SsoLoginHttpEntity(String userId, String
	 * password) throws ExternalServiceException { String encodedPassword = "";
	 * String encodedUserId = ""; try { encodedPassword =
	 * URLEncoder.encode(password, CharEncoding.UTF_8); encodedUserId =
	 * URLEncoder.encode(userId, CharEncoding.UTF_8); } catch
	 * (UnsupportedEncodingException e) { throw new
	 * ExternalServiceException("Unable to encode user and password for SSO login",
	 * e); } this.postBody = "USER=" + encodedUserId + "&PASSWORD=" +
	 * encodedPassword; }
	 * 
	 * @Override public void consumeContent() {}
	 * 
	 * @Override public InputStream getContent() throws IllegalStateException {
	 * return null; //not needed when sending request }
	 * 
	 * @Override public Header getContentEncoding() { return null; //new
	 * BasicHeader("Content-Encoding", ""); }
	 * 
	 * @Override public long getContentLength() { return postBody.length(); }
	 * 
	 * @Override public Header getContentType() { return new
	 * BasicHeader("Content-Type", "application/x-www-form-urlencoded"); }
	 * 
	 * @Override public boolean isChunked() { return false; }
	 * 
	 * @Override public boolean isRepeatable() { return true; }
	 * 
	 * @Override public boolean isStreaming() { return false; }
	 * 
	 * @Override public void writeTo(OutputStream os) throws IOException {
	 * os.write(postBody.getBytes(CharEncoding.UTF_8)); } }
	 */
}
