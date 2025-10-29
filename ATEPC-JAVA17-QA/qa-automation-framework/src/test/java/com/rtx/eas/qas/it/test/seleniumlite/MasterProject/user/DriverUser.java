package com.rtx.eas.qas.it.test.seleniumlite.MasterProject.user;

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
 * Lifted from PDM's Alfredo Project,  no author attribution in the original file.
 * 
 * @author NRP0228739
 *
 */
public interface DriverUser {
	
	/** 
     * Retrieves a regex that can be used by Cucumber to match input text data to the User type
     * The input text data will also be matched against the regex to instantiate the correct type
     *
     * @return
     */
    String getMatchingRegex();

    /** 
     * Retrieves the username to use for connections
     *
     * @return
     */
    public String getUsername();

    /** 
     * Retrieves the password to use for connections
     * In the future, we could hopefully provide an implementation that securely stores the passwords for each user type here
     *
     * @return
     * @throws QARuntimeException
     */
    public String getPassword();

    /** 
     * Retrieves the role for the given driver user
     *
     * @return
     */
    public String getRole();
        
    /** 
     * Gets the full name of the user 
     * @return
     */
    public String getFullName();


}
