package com.rtx.eas.qas.it.test.seleniumlite.MasterProject.util;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

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

public class QuickAESKeyGen {

	public static void main(String[] args) {
		try {
			SecureRandom rand = new SecureRandom();
			KeyGenerator keyGen = KeyGenerator.getInstance("AES");
			keyGen.init(256,rand); // for example
			SecretKey secretKey = keyGen.generateKey();		
			System.out.println(secretKey.getFormat());
			System.out.println(print(secretKey.getEncoded()));
		} catch (NoSuchAlgorithmException e) {
			System.out.println(" Haa Haa No algo for key gen.");
		}
	}
	
	public static String print(byte[] bytes) {
	    StringBuilder sb = new StringBuilder();
	    sb.append("[ ");
	    sb.append(String.format("%032X", new BigInteger( +1,bytes)));
	    for (byte b : bytes) {
	        sb.append(String.format("%032X", b));
	    }
	    sb.append("]");
	    return sb.toString();
	}
	
}
