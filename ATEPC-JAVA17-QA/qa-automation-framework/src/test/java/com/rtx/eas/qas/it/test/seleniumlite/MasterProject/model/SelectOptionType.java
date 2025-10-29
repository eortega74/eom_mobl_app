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
* The interface and the abstract base class are very similar, but require separate definition.
*  @see SimplySelectOptionType for Abstract Base Class of this interface.
* @author NRP0228739
*
*/
public interface SelectOptionType {

	/**
	 * What are the allowable terms for getting cucumber to recognize this Agreement Type
	 * @return
	 */
	public String getMatchingRegex();
	/**
	 * Gets the Option Value
	 * @return
	 */

	public String getOptionValue();
	/**
	 * Gets the Option Text
	 * @return
	 */
	public String getOptionText();
}
