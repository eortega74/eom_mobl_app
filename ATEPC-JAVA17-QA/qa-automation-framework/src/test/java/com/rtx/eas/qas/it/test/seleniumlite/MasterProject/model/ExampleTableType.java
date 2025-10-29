package com.rtx.eas.qas.it.test.seleniumlite.MasterProject.model;

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
* Simple table for constructing Example Table entries.
* @see com.ray.rmd.it.apps.RLIMS.qa.cucumber.step.cucumber.step.TypeRegistryConfiguration.configureTypeRegistry(final TypeRegistry typeRegistry)
* for how this table is added to the type registry for use in the feature files.
* 
* @author  NRP0236009
*
*/
public class ExampleTableType {

	String item;
	String url;
	
	public ExampleTableType() {}		
										
	public ExampleTableType(String ITEM, String URL) {
		this.item = ITEM;
		this.url = URL;
	}
	
	public void setItem(String item) {
		this.item = item;
	}
	
	public String getItem() {
		return item;
	}
	
	public void setURL(String url) {
		this.url = url;
	}
	
	public String getURL() {
		return url;
	}
}
