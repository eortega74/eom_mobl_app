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
*
* Change Log:
* @1148194--1-2-20--Added AuthURl navigation function which will route to auth page as test account
* 
*/

public abstract class NavigationBoundPage extends BasePage  implements NavigationBound {

	public boolean openAuthPage() {
		try {
			String AuthURL = getNavigationUrl();
			driver.get(AuthURL);
			waitForPageLoad(driver);
			this.initElements();
			return true;
		} catch (IOException e) {
			LOG.error(e.getMessage());
			return false;
		}
	}
	public boolean openPage() {
		try {
			String testURL = getNavigationUrl();
			driver.get(testURL);
			waitForPageLoad(driver);
			this.initElements();
			return true;
		} catch (IOException e) {
			LOG.error(e.getMessage());
			return false;
		}
	}
}
