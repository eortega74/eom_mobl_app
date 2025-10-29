package com.rtx.eas.qas.it.test.seleniumlite.MasterProject.user;

import java.io.IOException;
import java.util.Properties;

import com.rtx.eas.qas.it.test.seleniumlite.MasterProject.util.OneClickEncryptor;

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
*Change Log:
*
*/

/**
 * Modified from PDM's alfredo project  
 * Simple implementation of DriverUser, retrieves encrypted password from store.
 *
 * PDM Alfredo author:
 * @author nrp0243799
 *
 * RGEMS adopter:
 * @author nrp0228739
 */

public class SimpleDriverUser implements DriverUser {


    /** 
     * Username of the driver user
     */
    private final String username;
       
    /** 
     * Role of user
     */
    private final String role;
        
    /** 
     * The full name of the user 
     */
    private final String fullName;

    /** 
     * Regex that will be matched to the user
     */
    private final String matchingRegex;

    /** 
     * Password for the driver user
     */
    private String password = ""; 
    
    private static Properties ssoProps;
	private static Properties userProps;

    public SimpleDriverUser(final String username, final String role ,  final String fullName, final String matchingRegex) throws InstantiationException
    {   
        this.username = username;
        this.role = role;
        this.matchingRegex = matchingRegex;
        this.fullName = fullName;
          try 
          {   
              final OneClickEncryptor encryptor = new OneClickEncryptor(getSsoProps().getProperty("aes.encryption.key"));
              password = encryptor.decryptValue(getUserProps().getProperty("test.user.password."+username, getUserProps().getProperty("test.user.password.default" )));
          }   
          catch(final Exception e)
          {   
              throw new InstantiationException("Could not retrieve password from encrypted file");
          }   
    }   

    /*  
     * 
     * @see alfredo.user.DriverUser#getMatchingRegex()
     */
    @Override
    public String getMatchingRegex()
    {
        return matchingRegex;
    }

    /*
     * 
     * @see alfredo.user.DriverUser#getUsername()
     */
    @Override
    public String getUsername()
    {
        return username;
    }

    @Override
    public String getFullName()
    {
      return fullName;
    }

    @Override
    public String getRole()
    {
      return role;
    }

    /*
     * 
     * @see alfredo.user.DriverUser#getPassword()
     */
    @Override
    public String getPassword()
    {
        return password;
    }

    @Override
    public boolean equals(final Object obj)
    {
        boolean returnValue;
        if (this == obj)
        {
            returnValue = true;
        }
        else if (obj == null)
        {
            returnValue = false;
        }
        else if (this.getClass() == obj.getClass())
        {
            final SimpleDriverUser user = (SimpleDriverUser) obj;
            returnValue = this.username.equals(user.username) && this.matchingRegex.equals(user.matchingRegex);
        }
        else
        {
            returnValue = false;
        }
        return returnValue;
    }


    @Override 
    public int hashCode()
    {
       int result =  this.username == null ? 0 : this.username.hashCode()*113;
       result += this.matchingRegex == null ? 0 : this.matchingRegex.hashCode();
       return result;
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




}
