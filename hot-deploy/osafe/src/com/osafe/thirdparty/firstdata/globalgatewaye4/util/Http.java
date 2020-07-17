package com.osafe.thirdparty.firstdata.globalgatewaye4.util;

import javax.net.ssl.*;

import org.ofbiz.base.util.Debug;

import com.osafe.thirdparty.firstdata.globalgatewaye4.CheckRequest;
import com.osafe.thirdparty.firstdata.globalgatewaye4.Request;
import com.osafe.thirdparty.firstdata.globalgatewaye4.exceptions.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.Arrays;
import java.util.zip.GZIPInputStream;
import org.ofbiz.base.util.Debug;

/**
 * @author Steve Copous <steve.copous@firstdata.com>
 * <p>INTENDED FOR DEMONSTRATION PURPOSES ONLY.  NOT FOR PRODUCTION USE.</p>
 */
public class Http {

	private String url;
	private String version;
	
	public static String headerMethod = "POST";
	public static String headerContentType = "application/json; charset=utf-8";
	public static String headerAccept = "application/json";
	
	private Hmac hmac;
	
	/**
	 * @param url Endpoint URL to send the transaction.
	 * @param version Current version of the API library
	 * @param hmac {@link #Hmac} object containing the API HMAC Hash values.
	 */
	public Http(String url, String version, Hmac hmac) {
		this.url = url;
		this.hmac = hmac;
		this.version = version;
	}
	
	/**
	 * @return Returns HTTP Method used for the request.
	 */
	public String getHeaderMethod() {
		return headerMethod;
	}
	
	/**
	 * @return Returns the ContentType header used for the request.
	 */
	public String getHeaderContentType() {
		return headerContentType;
	}
	
	public String doRequest(Request request) {
		String response =  null;
		
		response = httpRequest(request.toJson());
		
		return response;
	}
	
	public String doRequest(CheckRequest request) {
		String response = null;
		
		response = httpRequest(request.toJson());
		
		return response;
		
	}
	
	private String httpRequest(String json) {
		String response = null;
		
		try {
			HttpURLConnection connection = buildConnection();
			connection.getOutputStream().write(json.getBytes("UTF-8"));
			connection.getOutputStream().close();
			
			if(isErrorCode(connection.getResponseCode())) 
			{
				throwHttpStatusException(connection.getResponseCode(), StringUtils.inputStreamToString(connection.getErrorStream()));				
			}
			
			InputStream responseStream = connection.getInputStream();
			
			response = StringUtils.inputStreamToString(responseStream);
			return response;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new UnexpectedException(e.getMessage(), e);
		}
	}

	private static void throwHttpStatusException(int statusCode, String message) {
		switch(statusCode) {
		case 400:
			throw new InvalidTransactionException(message);
		case 401:
			throw new AuthenticationException(statusCode + ": " + message);
		default:
			throw new UnexpectedException("Unexpected HTTP Response Code: " + statusCode + ": " + message);
		}
	}
	
	/**
	 * @param responseCode Response Code from the HTTP request
	 * @return Returns true if the HTTP code is an error, false if not.
	 */
	private static boolean isErrorCode(int responseCode) {
		return responseCode != 200 && responseCode != 201;
	}
	
	/**
	 * Builds an {@link #HttpURLConnection} object containing the required headers for the transaction.
	 * 
	 * @return {@link #HttpURLConnection}
	 * @throws java.io.IOException
	 */
	private HttpURLConnection buildConnection() throws java.io.IOException {
		URL url = new URL(this.url);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod(headerMethod);
		connection.setRequestProperty("Accept", headerAccept);
		connection.setRequestProperty("User-Agent", "GGe4 Java " + version);
		connection.setRequestProperty("Content-Type", headerContentType);
		connection.setRequestProperty("x-gge4-content-sha1", hmac.getContentSha1());
		connection.setRequestProperty("x-gge4-date", hmac.getHashTime());
		connection.setRequestProperty("Authorization", hmac.getAuthorizationHeader());
		connection.setDoOutput(true);
		connection.setReadTimeout(30000);
		return connection;
	}
}
