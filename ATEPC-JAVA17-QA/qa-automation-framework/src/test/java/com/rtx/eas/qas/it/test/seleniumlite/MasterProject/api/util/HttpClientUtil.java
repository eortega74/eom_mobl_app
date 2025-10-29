package com.rtx.eas.qas.it.test.seleniumlite.MasterProject.api.util;

import java.io.IOException;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.net.ssl.SSLContext;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.apache.commons.lang.CharEncoding;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.ssl.SSLContexts;

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

public class HttpClientUtil {

	public static final String SSL = "SSL";
	public static final String GZIP = "gzip";
	public static final String HEADER_ACCEPT_ENCODING = "Accept-Encoding";

	public static CloseableHttpClient getConfiguredHttpClient() throws IOException {

		// create ssl context
		SSLContext sslContext = null;
		try {
			sslContext = createSslContext();
		} catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
			throw new IOException(e);
		}

		// create ssl socket factory
		SSLConnectionSocketFactory connectionSocketFactory = new SSLConnectionSocketFactory(sslContext,
				NoopHostnameVerifier.INSTANCE);

		// set up connection factory registry
		Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
				.register("https", connectionSocketFactory).register("http", new PlainConnectionSocketFactory())
				.build();
		// set up connection manager
		HttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);

		// customize connection
		ConnectionConfig connectionConfig = ConnectionConfig.custom().setCharset(Charset.forName(CharEncoding.UTF_8))
				.build();
		// customize requests
		RequestConfig requestConfig = RequestConfig.custom().setRedirectsEnabled(true).setExpectContinueEnabled(true)
				.build();
		// create client
		CloseableHttpClient httpClient = HttpClients.custom()
				// .setUserAgent(USER_AGENT_STRING)
				.setConnectionManager(connectionManager).setDefaultConnectionConfig(connectionConfig)
				.setDefaultRequestConfig(requestConfig).setRedirectStrategy(new LaxRedirectStrategy())
				.addInterceptorLast((final HttpRequest request, final HttpContext context) -> {
					if (!request.containsHeader(HEADER_ACCEPT_ENCODING)) {
						request.addHeader(HEADER_ACCEPT_ENCODING, GZIP);
					}
				}).addInterceptorLast((final HttpResponse response, final HttpContext context) -> {
					HttpEntity entity = response.getEntity();
					if (entity != null) {
						Header ceheader = entity.getContentEncoding();
						if (ceheader != null) {
							HeaderElement[] codecs = ceheader.getElements();
							for (int i = 0; i < codecs.length; i++) {
								if (codecs[i].getName().equalsIgnoreCase(GZIP)) {
									response.setEntity(new GzipDecompressingEntity(response.getEntity()));
									return;
								}
							}
						}
					}
				}).build();
		return httpClient;
	}

	/**
	 * Creates an {@link SSLContext}
	 * 
	 * @return sslContext
	 * @throws KeyManagementException
	 * @throws NoSuchAlgorithmException
	 * @throws KeyStoreException
	 */
	private static SSLContext createSslContext()
			throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
		SSLContext sslContext = SSLContexts.custom()
				// .setProtocol(SSL)
				// .loadTrustMaterial(TrustAllStrategy.INSTANCE)
				.setSecureRandom(new SecureRandom()).build();
		return sslContext;
	}
}
