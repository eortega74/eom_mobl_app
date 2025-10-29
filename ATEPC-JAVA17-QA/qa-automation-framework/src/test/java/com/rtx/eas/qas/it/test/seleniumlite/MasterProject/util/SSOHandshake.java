package com.rtx.eas.qas.it.test.seleniumlite.MasterProject.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.net.ssl.SSLContext;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContexts;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.rtx.eas.qas.it.test.seleniumlite.MasterProject.common.QARuntimeException;

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

/**
 * QA SSOHandshake  
 * Adapted by
 * @author NRP0228739
 * 
 * Originally SsoUtil.java from alfredo (PDM automated test runner)
 * @author 1146146
 *
 * This class handles getting the smsession cookie needed for sso environments such as PROD, DEV and QA.
 * The method will return the labeled cookie with it's path and domain (i.e "SMSESSION=****;path=/;domain=.ray.com")
 */
public class SSOHandshake
{
	
	/**
	 * Logger for the class
	 */
    private static final Logger logger = LogManager.getLogger(SSOHandshake.class);
    
    /**
     * All host Urls to parse through for bypass url
     */
    // ##begin static.initialization preserve=yes
    static
    {
        String ssoPropertiesFile ="sso.properties";
        Properties props;
        Properties sysProps= System.getProperties();
       
    	try {
    	  // Prefer the system properties before defaulting to the sso.properties	
    	  props = new Properties();
	      props.load(java.lang.ClassLoader.getSystemResourceAsStream(ssoPropertiesFile));
	      props.forEach((k,v) -> {
	    	  if (sysProps.contains(k) == false ) {
	    		  sysProps.put(k,v);
	    	  }
	      });
	      
	      
    	} catch (IOException e) {
    		logger.error("Unable to load "+ ssoPropertiesFile +" from classpath.  " + e.getMessage());
    		System.exit(-1);
    	}
    }
    // ##end static.initialization
    /**
     * Location header label. Expected in redirect responses
     */
    private static final String LOCATION_HEADER = "Location";
    
    /**
     * Set-Cookie header label
     */
    private static final String SET_COOKIE_HEADER = "Set-Cookie";
    
    /**
     * SMSESSION cookie will have this label
     */
    private static final String SMSESSION_LABEL = "SMSESSION";
    
    /**
     * FORMCRED cookie will have this label
     */
    private static final String FORMCRED_LABEL = "FORMCRED";
    
    /**
     * SMTRYNO cookie will have this label
     */
    private static final String SMTRYNO_LABEL = "SMTRYNO";
    
    /**
     * Gets the bypass url for the current environment
     * @return empty string if not found
     */

    /**
     * Main helper method that returns the smsession cookie
     *
     * 
     * Does the following:
     * 1. Goes to the alternate login url
     * 2. Digests the response to get the SSO redirect url
     * 3. Submits the userId and password to the SSO redirect url
     * 4. Collects the siteminder cookie from the successful response and returns it as a string.
     * 
     * It does all of this in a browser independent http context.  The cookie is then passed to
     * the test context for use within selenium driven browser (Chrome, IE, Firefox)
     *
     * @param userId
     * @param password
     * @return labeled smsession cookie with path and domain (i.e "SMSESSION=****;path=/;domain=.ray.com")
     * @throws Exception 
     * @throws ClientProtocolException
     * @throws IOException
     * @throws KeyStoreException 
     * @throws NoSuchAlgorithmException 
     * @throws KeyManagementException 
     * @throws UnsupportedEncodingException
     */
    
    public static String getAlternateLoginURL() {
    	return System.getProperty("sso.alternate.login.url");    	
    }
    
	public static String getSmSessionCookie(final String userId, final String password) throws QARuntimeException
    {
    	String SMSESSION = null;
    	
     // 1. Goes to the alternate login url
        // Start by making the first request to the alternative login url.
        // We expect the response to be a 302 redirect to the siteminder login page.
    	String altLoginUrl = getAlternateLoginURL();
    	if (altLoginUrl == null || altLoginUrl.isEmpty()) {
    		throw new QARuntimeException("No alternative Login specified at property sso.alternate.login.url.  Exiting.");
    	}
    	URIBuilder uriBuilder;
    	URI getUri;
    	try {    		
			uriBuilder = new URIBuilder(altLoginUrl); 
			getUri = uriBuilder.build();
    	} catch (URISyntaxException e) {
    		throw new QARuntimeException("Unable to build  URI from alternate Login: (" + altLoginUrl + ")", e);
    	}
      CloseableHttpClient httpClient = HttpClientBuilder.create()
          .setSSLSocketFactory(createSelfSignedTrustedSSLSocketFactory())
          .build();
        
      final HttpPost initRequest = new HttpPost(getUri);
      HttpResponse initResponse;
      try {
    	  initResponse = httpClient.execute( initRequest);
      } catch (IOException e) {
    	  throw new QARuntimeException(" Unable to execute request against initial url (" +getUri +")", e);
      }
      final Header[] headers = initResponse.getHeaders(LOCATION_HEADER);
        
      // We expect a 302 here, if not, throw error
      final StatusLine initStatus = initResponse.getStatusLine();
      if (initStatus.getStatusCode() != 302)
      {
          logger.error("SSO Http request failed: " + initStatus.getStatusCode() + ": " + initStatus.getReasonPhrase());
          throw new QARuntimeException("SSO Http request failed: " + initStatus.getStatusCode() + ": " + initStatus.getReasonPhrase());
      }
      else
      {
        if(logger.isDebugEnabled())
        {
          logger.debug("Initial SSOHandshake.java post status code = " + initStatus.getStatusCode());
        }
      }
        
      // 2.  Digests the response to get the SSO redirect url
      // 2.1 Search the response for the necessary redirect url needed for login.
      String redirectUrl = "";
      if(headers!=null)
      {
        for(Header header: headers)
        {
          redirectUrl = header.getValue();
        }
      }  
      else
      {
        throw new QARuntimeException("Error getting redirect url from sso login alternative login.  Exiting.");
      }
      if(redirectUrl == null)
      {
        throw new QARuntimeException("Redirect Url is null, no target for submitting credentials. Exiting.");
      }
        
        // 3. Submits the userId and password to the SSO redirect url
        // 3.1 Build the redirect URI from the redirect URL
        URIBuilder redirectUriBuilder;
        URI redirectUri;
    	try {
            redirectUriBuilder = new URIBuilder(redirectUrl);
            redirectUri = redirectUriBuilder.build();
    	} catch (URISyntaxException e) {
    		throw new QARuntimeException("Unable to build  URI from redirect: (" + redirectUrl + ")", e);
    	}
        final HttpPost request = new HttpPost(redirectUri);
        // 3.2 Set connection to keep-alive and the referer to the initial url
        request.addHeader(new BasicHeader("Referer",altLoginUrl));
        request.addHeader(new BasicHeader("Connection","keep-alive"));
        
        // 3.3 Set the post body as the credentials for login
        final List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("USER", userId));
        params.add(new BasicNameValuePair("PASSWORD", password)); 
        UrlEncodedFormEntity encodedParams;
        try {
      	encodedParams = new UrlEncodedFormEntity(params);	
        } catch( UnsupportedEncodingException e) {
        	throw new QARuntimeException("Unable to encode username and password for (" + userId +")", e);
        }
        request.setEntity(encodedParams);
        HttpResponse response; 
        try {
        	response = httpClient.execute( request);
        } catch( IOException e )	 {
        	throw new QARuntimeException("Unable to execute login with username and password for (" + userId +")", e);        	
        }

        // 4. Collects the siteminder cookie from the successful response and returns it as a string.
        // 4.1 In this response we expect either 
        //     the SMSESSION cookie directly 
        //     or another redirect
        //     with the FORMCRED and SMTRYNO cookies
        final StatusLine status = response.getStatusLine();
        if (status.getStatusCode() != 200 && status.getStatusCode() != 302)
        {
            logger.error("SSO Http request failed: " + status.getStatusCode() + ": " + status.getReasonPhrase());
            throw new QARuntimeException("SSO Http request failed: " + status.getStatusCode() + ": " + status.getReasonPhrase());
        }
        else
        {
        	if(logger.isDebugEnabled())
        	{
        		logger.debug("Redirect with credentials post status code = " + status.getStatusCode());
        	}
        }

        final Header[] responseHeaders = response.getHeaders(SET_COOKIE_HEADER);        
        String FORMCRED = null;
        String SMTRYNO = null;

        if (responseHeaders != null)
        {
            for(final Header header : responseHeaders)
            {
            	String cookie = header.getValue();
				      logger.debug("Parsing Cookie: " + cookie);
				      if (cookie.toUpperCase().startsWith(SMSESSION_LABEL)) 
				      {
					      SMSESSION = cookie;
				      }
              else if (cookie.toUpperCase().startsWith(FORMCRED_LABEL)) 
              {
                FORMCRED = cookie;
              }
              else if (cookie.toUpperCase().startsWith(SMTRYNO_LABEL)) 
              {
                SMTRYNO = cookie;
              }
            }
        }
        
        // If the FORMCRED and SMTRYNO cookies are found, make another request
        if(FORMCRED !=null && SMTRYNO != null)
        {
        	// Expect another redirect url provided from the second request if
        	// FORMCRED is found
        	final Header[] location = response.getHeaders(LOCATION_HEADER);
        	String redirect = "";
        	if(location == null)
        	{
        		throw new QARuntimeException("Could not obtain redirect url on second pass using formcred cookie");
        	}
        	
          for(Header header: location)
          {
            redirect = header.getValue();
          }
            
        	// Start building the final post, add FORMCRED, SMTRYNO and the login creds to the post
            final HttpPost finalPost = new HttpPost(redirect);
            finalPost.addHeader(new BasicHeader("Cookie", FORMCRED + ";" + SMTRYNO + ";"));
            finalPost.setEntity(encodedParams);
            
            // Need to create a new client since the last post connection is kept alive to allow for
            // us to successfully grab the smsession
        	httpClient = HttpClientBuilder.create()
        		.setSSLSocketFactory(createSelfSignedTrustedSSLSocketFactory())
        		.build();
            
            // Search the final post for the SMSESSION now
            HttpResponse lastResponse;

            try {
            	lastResponse = httpClient.execute( finalPost);
            } catch (IOException e) {
            	throw new QARuntimeException("Unable to access final redirect.  Exiting" , e);
            }
            final Header[] lastResponseHeaders = lastResponse.getHeaders(SET_COOKIE_HEADER);
            if (lastResponseHeaders != null)
            {
                for(final Header header : lastResponseHeaders)
                {
                	String cookie = header.getValue();
    				      logger.debug("Parsing Cookie: " + cookie);
                  if (cookie.toUpperCase().startsWith(SMSESSION_LABEL)) 
                  {
                    SMSESSION = cookie;
                  }
                }
            }
        }
        
        try 
        {
          httpClient.close();
        } 
        catch (IOException e) 
        {
          logger.error("Error closing http client", e);
        }
        
        // If SMSESSION still not found, throw an error.
        if(SMSESSION == null)
        {
        	throw new QARuntimeException("Could not obtain SMSESSION cookie, validate user exists on this environment.");
        }
        
        logger.debug("SSO Cookie Obtained: " + SMSESSION);
        return SMSESSION;
    }

	
	  public static SSLConnectionSocketFactory createSelfSignedTrustedSSLSocketFactory() throws QARuntimeException
	    {
	      SSLContext sslContext;
	      SSLConnectionSocketFactory connectionFactory;
	      try 
	      {
	        // Fix for SSL handshake error
	        sslContext = SSLContexts.custom().useProtocol("TLS").loadTrustMaterial(new TrustSelfSignedStrategy()).build();
	        SSLContext.setDefault(sslContext);
	        connectionFactory = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
	      }   
	      catch(Exception e)
	      {   
	        throw new QARuntimeException("Error initializing self signed cert context", e); 
	      }   
	        return connectionFactory;
	    }   
}
