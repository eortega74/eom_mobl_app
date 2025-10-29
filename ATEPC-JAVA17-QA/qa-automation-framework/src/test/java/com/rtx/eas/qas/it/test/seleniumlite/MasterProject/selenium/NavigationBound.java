package com.rtx.eas.qas.it.test.seleniumlite.MasterProject.selenium;

import java.io.IOException;

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
* Change Log:
*
* Lifted from PDM's Alfredo Project,  no author attribution in the original file.
* 
* Light interface to allow classes representing 
* distinct fixed urls within the application to be treated 
* as a type
*
* @author nrp0228739 ( RGEMS adaptation )
* @author nrp0243799 ( PDM original as NavigableComponent )
*/
public interface NavigationBound {
    
	/**
     * Retrieves the url which can be used to navigate to this url
     *
     * @param driver
     * @return
     */
    public String getNavigationUrl() throws IOException;
}