package com.rtx.eas.qas.it.test.seleniumlite.MasterProject.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException; 
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringUtils;

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
 * Generate a hashed 256 bit AES key from user password using SHA256
 * DG
 * 
 * Lifted from PDM alfredo, which evidently lifted it from bifExt.ext.raytheon
 * Stripped down to
 * A.  Work with System.properties as a first rung
 * B.  Then work with properties files from the application
 *   
 * 
 * @author nrp0228739
 */
public class OneClickEncryptor {

	
	public static final String CLASSNAME = OneClickEncryptor.class.getName();
	//public static final Logger LOGGER = LogR.getLogger(CLASSNAME);

	// the secret key
	private static final String KEY = "key=";

	// use option encryptfile|decryptfile|encrypt|decrypt
	private static final String ACTION = "action=";

	// full path to property file, if action=encryptfile|decryptfile
	private static final String FILE = "file=";

	// single value to be encrypted or decrypted when using action=encrypt|decrypt
	private static final String VALUE = "value=";

	private static final String EMPTY_STRING = "";
	
	private static int keySize = 64;

	private static final void printUsage() {
		System.out.println(
				"java com.rtx.eas.qas.it.test.seleniumlite.MasterProject.util.OneClickEncryptor key=<16 characters> action=<encryptfile|decryptfile|encrypt|decrypt> file=<full path to property file, if action=encryptfile|decryptfile> value=<single value to be encrypted or decrypted when using action=encrypt|decrypt>");
		System.out.println(
				"Note: when using encryptfile the file will automatically be updated, however when using decryptfile, the decrypted info will only /n "
						+ "be posted to the console");
		System.out.println("");

		System.out.println(
				"Ex. java com.rtx.eas.qas.it.test.seleniumlite.MasterProject.util.OneClickEncryptor key=secretkeyasdfghi action=encryptfile file=C:/projects/oneclickencrypted.properties");
		System.out.println(
				"Ex. java com.rtx.eas.qas.it.test.seleniumlite.MasterProject.util.OneClickEncryptor key=secretkeyasdfghi action=encryptfile value=asinglepassword");
		System.out.println("");
		System.out.println("Note if you do not enter a value for key, you will be prompted to enter");
		System.out.println(
				"Ex. java com.rtx.eas.qas.it.test.seleniumlite.MasterProject.util.OneClickEncryptor action=encryptfile file=C:/projects/oneclickencrypted.properties");
		System.out.println(
				"Ex. java com.rtx.eas.qas.it.test.seleniumlite.MasterProject.util.OneClickEncryptor INTERACTIVE");

		
	}

	/* main method invoked from command line utility.  
	 * following are some examples
	 * 
	 * 	args = new String[]{"key=secretkeyabcdefg", "action=encryptfile", "file=C:\\D\\projects\\OneClickManager2\\src\\main\\java\\oneclickencrypted.properties"};
	 *	args = new String[]{"key=secretkeyabcdefg", "action=decryptfile", "file=C:\\D\\projects\\OneClickManager2\\src\\main\\java\\oneclickencrypted.properties"};
	 *	args = new String[]{"key=secretkeyabcdefg", "action=decrypt", "value=V9IvZLqHvu95rhrAkYXjaw=="};
	 *	args = new String[] { "key=secretkeyabcdefg", "action=encrypt", "value=admin" };
	 * 
	 */
	public static void main(String[] args) {
		
		try {
			String key = null;
			String action = null;
			String fileLocation = null;
			String value = null;


			if(args.length == 1 && "INTERACTIVE".equalsIgnoreCase(args[0])) {
				interactive();
			}
			if (args.length > 1) {
				for (String arg : args) {
					if (arg.startsWith(KEY)) {

						key = arg.replace(KEY, EMPTY_STRING);
						if (key.length() != keySize) {
							System.out.println("********************************************************");
							System.out.println("secret key must be exactly " + keySize + " characters");
							System.out.println("********************************************************");
							printUsage();
							return;
						}
					} else if (arg.startsWith(ACTION)) {
						action = arg.replace(ACTION, EMPTY_STRING);
					} else if (arg.startsWith(FILE)) {
						fileLocation = arg.replace(FILE, EMPTY_STRING);
					} else if (arg.startsWith(VALUE)) {
						value = arg.replace(VALUE, EMPTY_STRING);
					}
				}
				
				if(key == null  || key.isEmpty()) {
					//prompt to enter key
					key = getPassword("Please Enter secret key: " );
					if (key.length() != keySize) {
						System.out.println("********************************************************");
						System.out.println("secret key must be exactly " + keySize + " characters");
						System.out.println("********************************************************");
						printUsage();
						return;
					}
				}
				

				if ("encryptfile".equals(action)) {
					// encrypt the property file
					System.out.println("Processing File: " + fileLocation);
					OneClickEncryptor app = new OneClickEncryptor(fileLocation, key);
					app.encryptPropertyFile();
				} else if ("decryptfile".equals(action)) {
					// decrypt the property file
					System.out.println("Processing File: " + fileLocation);
					OneClickEncryptor app = new OneClickEncryptor(fileLocation, key);
					app.decryptPropertyFile();
				} else if ("encrypt".equals(action)) {
					// decrypt the property file
					OneClickEncryptor app = new OneClickEncryptor(key);
					String newvalue = app.encryptValue(value);
					System.out.println("Original Text: " + value + "\nEncrypted Text: " + newvalue);
				} else if ("decrypt".equals(action)) {
					OneClickEncryptor app = new OneClickEncryptor(key);
					String newvalue = app.decryptValue(value);
					System.out.println("Encrypted Text: " + value + "\nOriginal Text: " + newvalue);
				} else{
					printUsage();
				}
			} else {
				printUsage();
			}
 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void interactive() {

		try {
			boolean repeat = true;
			while( repeat ) {
				System.out.println("You are in interactive mode");
				System.out.println("");
				System.out.println("Please Select one of the following options");
				System.out.println("1. Encrypt a Property File. This will convert all values to encrypted strings. "
						+ "Caution: do not run this on a file that has already been encrypted");
				System.out.println("2. Decrypt and display content of a file on the console.  Used to validate the encryption key.");
				System.out.println("3. Encrypt a single value.");
				System.out.println("4. Decrypt a single value.");
				System.out.println("5. Update an existing encrypted file.  Add/Remove property, update key.");
				
				String consoleValue = consoleValue("Enter 1-5: ");
				if(consoleValue.equals("1")) {
					String filePath = consoleValue("Enter Full path to file: ");
					String key = getPassword("Please Enter secret key: " );				
					OneClickEncryptor app = new OneClickEncryptor(filePath, key);
					app.encryptPropertyFile();
				}if(consoleValue.equals("2")) {
					String filePath = consoleValue("Enter Full path to file: ");
					String key = getPassword("Please Enter secret key: " );				
					OneClickEncryptor app = new OneClickEncryptor(filePath, key);
					app.decryptPropertyFile();
				}if(consoleValue.equals("3")) {
					String value = consoleValue("Enter the value to be encrypted: ");
					String key = getPassword("Please Enter secret key: " );	
					OneClickEncryptor app = new OneClickEncryptor(key);
					String newvalue = app.encryptValue(value);
					System.out.println("Original Text: " + value + "\nEncrypted Text: " + newvalue);					
				}if(consoleValue.equals("4")) {
					String value = consoleValue("Enter the value to be encrypted:");
					String key = getPassword("Please Enter secret key: " );	
					OneClickEncryptor app = new OneClickEncryptor(key);
					String newvalue = app.decryptValue(value);
					System.out.println("Encrypted Text: " + value + "\nOriginal Text: " + newvalue);					
				}else if (consoleValue.equals("5")) {
					String filePath = consoleValue("Enter Full path to file:");
					String key = getPassword("Please Enter secret key: " );				
					OneClickEncryptor app = new OneClickEncryptor(filePath, key);
					boolean repeatUpdate = true;
					
					while(repeatUpdate) {
						System.out.println("");
						System.out.println("0. Go Back. Done With File Update.");
						System.out.println("1. Add Property.");
						System.out.println("2. Update Property value.");
						System.out.println("3. Change Encryption Key.");
						consoleValue = consoleValue("Enter 0-3: ");
						
						if(consoleValue.equals("0")) {
							repeatUpdate = false;							
						}else if(consoleValue.equals("1")) {
							String propertyKey = consoleValue("Enter Property Name: ");
							String propertyValue = consoleValue("Enter new property value: ");
							String newEncryptedValue = app.encryptValue(propertyValue);
							app.getConfig().addProperty(propertyKey, "ENC(" + newEncryptedValue + ")");
							app.getConfig().save();
							
							app.decryptPropertyFile();
							
						}else if (consoleValue.equals("2")) {
							//Update value 
							String propertyKey = consoleValue("Enter Property Name: ");
							String propertyValue = consoleValue("Enter new property value: ");
							String newEncryptedValue = app.encryptValue(propertyValue);
							app.getConfig().setProperty(propertyKey, "ENC(" + newEncryptedValue + ")");
							app.getConfig().save();
							
							app.decryptPropertyFile();
						} else if (consoleValue.equals("3")) {
							//Change Encryption Key
							String newSecretKey = consoleValue("Enter New Encryption Key: ");
							
							OneClickEncryptor newOCE = new OneClickEncryptor(newSecretKey);
							
							Iterator<String> keys = app.getConfig().getKeys();
							while (keys.hasNext()) {
								String propertyKey = (String) keys.next();
								String keyValue = app.config.getString(propertyKey);
								
								if(StringUtils.startsWith(keyValue, "ENC(")){
									keyValue = stripEncFlag(keyValue);
									String strCipherText = newOCE.encryptValue(app.decryptValue(keyValue));
			
									// Overwrite password with encrypted password in the properties file using
									// Apache Commons Configuration library
									app.config.setProperty(key, "ENC(" + strCipherText +")");
								}
							}
							
							app.getConfig().save();
							app.keyObj = new SecretKeySpec(newSecretKey.getBytes(), "AES");
							
							app.decryptPropertyFile();
						}
							
					}
				}
				System.out.println("Would you like to run this tool again? ");
				String repeatValue = consoleValue("Enter to close or enter \"YES\" to run again: ");
				if(repeatValue == null || !repeatValue.equalsIgnoreCase("YES")) {
					repeat = false;
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static String stripEncFlag(String keyValue) {
		// the encrypted value is wrapped in the following string ENC(xxx)
		return keyValue.substring("ENC(".length(), keyValue.length()-1);
	}
	
	private static String consoleValue(String prompt) {
			 
        String consoleValue = "";
        System.out.print(prompt);
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        try {
            consoleValue = in.readLine();
        }
        catch (IOException e){
            System.out.println("Error trying to read your value!");
            System.exit(1);
        }
 
        return consoleValue;
	    
	}

	private PropertiesConfiguration config;
	private Key keyObj;

	public OneClickEncryptor(String propertyFileName, String secretkey) throws ConfigurationException {
		this.keyObj = new SecretKeySpec(getSecretKeyBytes(secretkey), "AES");
		config = new PropertiesConfiguration();
		config.setDelimiterParsingDisabled(true);
		config.setFile(new File(propertyFileName));
		config.load(propertyFileName);
	}

	public OneClickEncryptor(InputStream javaProps, String secretkey) throws ConfigurationException {
		this.keyObj = new SecretKeySpec(getSecretKeyBytes(secretkey), "AES");
		config = new PropertiesConfiguration();
		config.setDelimiterParsingDisabled(true);
		config.load(javaProps);
	}
	
	public byte[] getSecretKeyBytes(String secretKey) {
		byte[] results;
		if ( secretKey.length() >= 64 ) {
			results = new byte[secretKey.length()/2];
			for (int i=0; i< results.length; i++ ) {
				int index=i*2;
				int j = Integer.parseInt(secretKey.substring(index, index+2),16);
				results[i] = (byte) j;
			}
		} else if (secretKey.length() == 16) {
			results = secretKey.getBytes();			
		} else {
			results = "DEADBEEFDEADBEEF".getBytes();
		}
		return results;
	}
	public OneClickEncryptor(String secretkey) throws ConfigurationException {
		this.keyObj = new SecretKeySpec(getSecretKeyBytes(secretkey), "AES");
	}

	/**
	 * The method that encrypt password in the properties file. This method will
	 * first check if the password is already encrypted or not. If not then only it
	 * will encrypt the password.
	 *
	 * @throws ConfigurationException
	 * @throws IllegalBlockSizeException
	 * @throws NoSuchPaddingException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 */
	public void encryptPropertyFile() throws ConfigurationException, BadPaddingException, IllegalBlockSizeException,
			NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
		logger("Starting encryption operation");
		logger("Start reading properties file");

		// Apache Commons Configuration
		Iterator<String> keys = config.getKeys();

		while (keys.hasNext()) {
			String key = (String) keys.next();
			String keyValue = config.getString(key);

			String strCipherText = encryptValue(keyValue);

			// Overwrite password with encrypted password in the properties file using
			// Apache Commons Configuration library
			config.setProperty(key, "ENC(" + strCipherText + ")");

		}

		// Save the properties file
		config.save();
		logger("Completed encryption process");
	}

	private String encryptValue(String keyValue) throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

		// Create a Cipher by specifying the following parameters
		// a. Algorithm name - here it is AES
		// b. Mode - here it is ECB mode
		// c. Padding - e.g. PKCS7 or PKCS5
		Cipher aesCipherForEncryption = Cipher.getInstance("AES/ECB/PKCS5PADDING");
		aesCipherForEncryption.init(Cipher.ENCRYPT_MODE, keyObj);

		byte[] byteDataToEncrypt = keyValue.getBytes();

		byte[] byteCipherText = aesCipherForEncryption.doFinal(byteDataToEncrypt);
		String strCipherText = DatatypeConverter.printBase64Binary(byteCipherText);
		return strCipherText;
	}

	public Map<String, String> getEncryptedUsersPasswords() throws ConfigurationException, NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		
		String password = "";
		Map<String, String> users_passwords = new HashMap<String, String>();		
		Iterator<String> users = config.getKeys();
		
		while (users.hasNext()) {
			String user = (String) users.next();			
			password = config.getString(user);			
			users_passwords.put(user, password);					
		}
		
		return users_passwords;
	}
	
	public String decryptPropertyFile() throws ConfigurationException, NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		logger("Starting decryption");
		logger("######################################");

		String decryptedPropertyValue = "";

		Iterator<String> keys = config.getKeys();

		while (keys.hasNext()) {
			String key = (String) keys.next();
			
			decryptedPropertyValue = retrieveDecryptedPropValue(key);

			logger(key + "=" + decryptedPropertyValue);

		}
		logger("######################################");
		logger("Completed decryption process");
		return null;
	}

	public String retrieveDecryptedPropValue(String key) throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		
		String decryptedPropertyValue;
		String keyValue = config.getString(key);
		if(StringUtils.startsWith(keyValue, "ENC(")){
			// decrypt the value
			decryptedPropertyValue = decryptValue(stripEncFlag(keyValue));
			return decryptedPropertyValue;
		}else {
			return keyValue;
		}
		
	}

	public String decryptValue(String value) throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		//String keyValue = config.getString(key);

		// Create a Cipher by specifying the following parameters
		// a. Algorithm name - here it is AES
		// b. Mode - here it is ECB mode
		// c. Padding - e.g. PKCS7 or PKCS5
		if(StringUtils.startsWith(value, "ENC(")){
			value = stripEncFlag(value);
		}
			
		Cipher aesCipherForDecryption = Cipher.getInstance("AES/ECB/PKCS5PADDING");
		
		byte[] byteDecryptedText = null;
		
		// built to let the user re-enter the secret key or quit if a bad key is detected
		while(true) {
			try {
				// Initialize the Cipher for Encryption
				aesCipherForDecryption.init(Cipher.DECRYPT_MODE, keyObj);
				
				// Decrypt the Data
				// a. Initialize a new instance of Cipher for Decryption (normally don't reuse
				// the same object)
				// b. Decrypt the cipher bytes using doFinal method
				byteDecryptedText = aesCipherForDecryption.doFinal(DatatypeConverter.parseBase64Binary(value));
				break;
			} catch (BadPaddingException e) {
				if(e.getMessage().contains("bad key")) {
					System.out.println("Possible BAD Secret Key Entered!!");
					String key = getSecretKeyFromUser("Re-Enter Secret Key or 'q' to Quit!");
					this.keyObj = new SecretKeySpec(key.getBytes(), "AES");
				}
			}			
		}
		
		String decryptedPropertyValue = new String(byteDecryptedText);

		return decryptedPropertyValue;
	}

	public void logger(String s) {
		System.out.println(s);
	}	
	
	public PropertiesConfiguration getConfig() {
		return config;
	}

	// following added so that user is prompted to enter key, and not enter key via inital command line arguments, that can be 
	// stored in history.
	public static String getPassword(String prompt) {
		 
        String password = "";
        ConsoleEraser consoleEraser = new ConsoleEraser();
        System.out.print(prompt);
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        consoleEraser.start();
        try {
            password = in.readLine();
        }
        catch (IOException e){
            System.out.println("Error trying to read your password!");
            System.exit(1);
        }
 
        consoleEraser.halt();
        System.out.print("\b");
 
        return password;
    }
 
	public static String getSecretKeyFromUser(String prompt) {
		 
        String secret_key = "";
        ConsoleEraser consoleEraser = new ConsoleEraser();
        System.out.print(prompt);
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        consoleEraser.start();
        try {
        	while(true) {
        		secret_key = in.readLine();
        		if(secret_key.equalsIgnoreCase("q")) {
        			System.out.println("Exiting Auto Test!");
                    System.exit(0);
        		}
        		if(secret_key != null && secret_key.length() == keySize) {
        			break;
        		}
				System.out.println("secret key must be exactly " + keySize + " characters in length!!");
        	}
        	
        }
        catch (IOException e){
            System.out.println("Error trying to read your secret key!");
            System.exit(1);
        }
 
        consoleEraser.halt();
        System.out.print("\b");
 
        return secret_key;
    }
	 
    private static class ConsoleEraser extends Thread {
        private boolean running = true;
        public void run() {
            while (running) {

                System.out.print("\b ");
                try {
                    Thread.currentThread().sleep(1);
                }
                catch(InterruptedException e) {
                    break;
                }

            }
        }
        public synchronized void halt() {
            running = false;
        }
    }
	
}
