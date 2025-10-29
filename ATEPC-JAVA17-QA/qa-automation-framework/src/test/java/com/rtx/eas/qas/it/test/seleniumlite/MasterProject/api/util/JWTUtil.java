package com.rtx.eas.qas.it.test.seleniumlite.MasterProject.api.util;

import java.io.IOException;
import java.security.Key;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.rtx.eas.qas.it.test.seleniumlite.MasterProject.api.constants.AppConstants;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;

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
* @authors nrp0253348/nrp0253353
*
* Change Log:
* 
* 02/14/2022 nrp0253353
* Improved try/catch block to generate the appropriate errors on execution failure
* -----------------------------------------------------------------------------
*/

public class JWTUtil {

	private static final String ISSUER = "rgems.app.ray.com";

	private static final int TIMEOUT_SECONDS = 60;
	private static final int TIME_SKEW_FUDGE_SECONDS = 10;

	private static final String PKI_CERT_PASSWORD = "3yhi%X%";
	private static final String CERT_TYPE = "JKS";
	private static final String CERT_NAME = "synergy-testapp.nonprod";
	private static final String CERT_NAME_PROD = "synergy-testapp.prod";

	private static final Map<String, String> keyNameMap = new HashMap<String, String>();
	static {

		keyNameMap.put("synergy-testapp.nonprod", "synergy-testapp");
		keyNameMap.put("synergy-testapp.prod", "synergy-testapp");
	}

	public static String createJwtToken(String subject) throws JwtException {
		return createJwtToken(subject, null);
	}

	public static String createJwtToken(String subject, String audience) throws JwtException {
		// The signature algorithm used to sign the token
		SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.RS256;
		Date now = new Date();
		Key privateKey;
		try {
			privateKey = PkiCertificateUtil.getPrivateKeyFromKeyStore(AppConstants.PKI_CERT_RESOURCE, PKI_CERT_PASSWORD,
					CERT_TYPE, CERT_NAME);
		} catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | UnrecoverableEntryException
				| IOException e) {
			e.getMessage();
			e.printStackTrace();
			throw new JwtException("Error getting JWT RSA private key", e);
		}

		// set the JWT Claims
		JwtBuilder builder = Jwts.builder().setIssuedAt(new Date(now.getTime() - (TIME_SKEW_FUDGE_SECONDS * 1000)))
				.setSubject(subject).setIssuer(ISSUER).setExpiration(new Date(now.getTime() + (TIMEOUT_SECONDS * 1000)))
				.setHeaderParam("typ", "JWT").setAudience(audience).signWith(signatureAlgorithm, privateKey);
		System.out.println(builder);

		return builder.compact();
	}

	public static String createJwtToken_Prod(String subject, String audience) throws JwtException {
		// The signature algorithm used to sign the token
		SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.RS256;

		Date now = new Date();

		Key privateKey;
		try {
			privateKey = PkiCertificateUtil.getPrivateKeyFromKeyStore(AppConstants.PKI_CERT_RESOURCE_PROD,
					PKI_CERT_PASSWORD, CERT_TYPE, CERT_NAME_PROD);
		} catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | UnrecoverableEntryException
				| IOException e) {
			throw new JwtException("Error getting JWT RSA private key", e);
		}

		// set the JWT Claims
		JwtBuilder builder = Jwts.builder().setIssuedAt(new Date(now.getTime() - (TIME_SKEW_FUDGE_SECONDS * 1000)))
				.setSubject(subject).setIssuer(ISSUER).setExpiration(new Date(now.getTime() + (TIMEOUT_SECONDS * 1000)))
				.setHeaderParam("typ", "JWT").setAudience(audience).signWith(signatureAlgorithm, privateKey);
		System.out.println(builder);

		return builder.compact();

	}

	public static String getKeyName(String issuer) {
		return keyNameMap.get(issuer);
	}

	public static String getIssuer(String jwtToken) throws JwtException {
		return parseClaims(jwtToken, null, false).getIssuer();
	}

	private static Claims parseClaims(String jwtToken, Object signingKey, boolean enforceSignature)
			throws JwtException {

		if (StringUtils.isBlank(jwtToken)) {
			throw new JwtException("Supplied JWT is null or blank.");
		}
		try {
			if (enforceSignature) {
				return parseSignedClaims(jwtToken, signingKey);
			}
			return parseWithoutValidatingSignature(jwtToken);
		} catch (io.jsonwebtoken.SignatureException e) {
			throw new JwtException("Invalid JWT signature -> Message: {" + e.getMessage() + "}", e);
		} catch (MalformedJwtException | IllegalArgumentException e) {
			throw new JwtException("Invalid JWT token -> Message: {" + e.getMessage() + "}", e);
		} catch (ExpiredJwtException e) {
			throw new JwtException("Expired JWT token -> Message: {" + e.getMessage() + "}", e);
		} catch (UnsupportedJwtException e) {
			throw new JwtException("Unsupported JWT token -> Message: {" + e.getMessage() + "}", e);
		} catch (JwtException e) {
			throw e;
		} catch (Exception e) {
			throw new JwtException("JWT token is not properly formatted -> Message: {" + e.getMessage() + "}", e);
		}
	}

	private static Claims parseWithoutValidatingSignature(String jwtToken) throws JwtException {
		// if not enforcing signature, remove the signature section so it does not fail
		// parsing
		String[] splitToken = jwtToken.split("\\.", -1);
		if (splitToken.length < 3) {
			throw new JwtException("JWT token does not contain all 3 sections.");
		}
		return Jwts.parser().parseClaimsJwt(splitToken[0] + "." + splitToken[1] + ".").getBody();
	}

	private static Claims parseSignedClaims(String jwtToken, Object signingKey) throws JwtException {
		validateSigningKey(signingKey);
		Jws<Claims> jws = null;
		if (signingKey instanceof String) {
			jws = Jwts.parser().setSigningKey((String) signingKey).parseClaimsJws(jwtToken);
		} else if (signingKey instanceof Key) {
			jws = Jwts.parser().setSigningKey((Key) signingKey).parseClaimsJws(jwtToken);
		} else if (signingKey instanceof byte[]) {
			jws = Jwts.parser().setSigningKey((byte[]) signingKey).parseClaimsJws(jwtToken);
		}

		// analyzer think jws can be null here but it cannot because validateSigningKey
		// will throw an exception if the signingKey does not match one of the
		// 'instanceof' types.
		return jws != null ? jws.getBody() : null;
	}

	private static void validateSigningKey(Object signingKey) throws JwtException {
		boolean isValid;

		if (signingKey instanceof String) {
			isValid = StringUtils.isNotBlank((String) signingKey);
		} else if (signingKey instanceof Key) {
			isValid = true;
		} else if (signingKey instanceof byte[]) {
			isValid = ((byte[]) signingKey).length > 0;
		} else {
			isValid = false;
		}

		if (!isValid) {
			throw new JwtException("Cannot validate JWT because signing key is missing.");
		}
	}
}

