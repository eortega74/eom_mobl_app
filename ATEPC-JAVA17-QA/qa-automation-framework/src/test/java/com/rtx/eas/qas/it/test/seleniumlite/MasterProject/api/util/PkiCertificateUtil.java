package com.rtx.eas.qas.it.test.seleniumlite.MasterProject.api.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;

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

public class PkiCertificateUtil {

	public static Key getPrivateKeyFromKeyStore(String keystorePath, String password, String type,
			String certificateName) throws IOException, KeyStoreException, NoSuchAlgorithmException,
			CertificateException, UnrecoverableEntryException {

		try (FileInputStream ins = new FileInputStream(keystorePath)) {
			KeyStore keyStore = KeyStore.getInstance(type);
			keyStore.load(ins, password.toCharArray());
			KeyStore.PasswordProtection keyPassword = new KeyStore.PasswordProtection(password.toCharArray()); // Key
																												// password

			KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(certificateName,
					keyPassword);
			PrivateKey privateKey = privateKeyEntry.getPrivateKey();

			return privateKey;
		}
	}
}
