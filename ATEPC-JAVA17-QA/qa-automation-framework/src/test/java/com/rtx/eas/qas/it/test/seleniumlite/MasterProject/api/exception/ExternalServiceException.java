package com.rtx.eas.qas.it.test.seleniumlite.MasterProject.api.exception;

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

public class ExternalServiceException extends Exception {
	private static final long serialVersionUID = 1L;
	
	/**
	 * Wraps a caught exception into a ExternalServiceException
	 * 
	 * @param e
	 */
	public ExternalServiceException(Exception e) {
		super(e);
	}
	/**
	 * Create an ExternalServiceException with the given message.
	 * 
	 * @param message
	 */
	public ExternalServiceException(String message) {
		super(message);
	}
	/**
	 * Creates an ExternalServiceException with the given message and adds the caught
	 * exception to the stack trace.
	 * 
	 * @param message
	 * @param e
	 */
	public ExternalServiceException(String message, Exception e) {
		super(message);
}
	
}

