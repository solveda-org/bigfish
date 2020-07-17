package com.osafe.services.ebs;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Map;
import java.util.StringTokenizer;

import javax.xml.parsers.ParserConfigurationException;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.HttpClient;
import org.ofbiz.base.util.HttpClientException;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilXml;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javolution.util.FastMap;

public class EBSPaymentUtil
{
    public static final String module = EBSPaymentUtil.class.getName();

    public static String getSecureHash(String secretKey, Map<String, Object> parameters) throws Exception{
		String md5HashData = secretKey;
        String account_id = (String) parameters.get("account_id");
        String amount = (String) parameters.get("amount");
        String reference_no = (String) parameters.get("reference_no");
        String return_url = (String) parameters.get("return_url");
        String mode = (String) parameters.get("mode");
		md5HashData += '|'+ account_id+ '|'+amount+'|'+ reference_no+'|'+return_url+'|'+mode;

		MessageDigest m = MessageDigest.getInstance("MD5");
		byte[] data = md5HashData.getBytes();
		m.update(data,0,data.length);
		BigInteger i = new BigInteger(1,m.digest());
		String hash = String.format("%1$032X", i);
		return hash;
    }

    public static void validateParam(Map<String, Object> parameters, String paramName, String paramValue, boolean isRequired) throws IllegalArgumentException {
		if (UtilValidate.isNotEmpty(paramValue)) {
			parameters.put(paramName, paramValue);
		} else {
			if (isRequired) {
				throw new IllegalArgumentException(paramName + " has an invalid value '" + paramValue + "'");
			} else {
				parameters.put(paramName, "NOT_APPLICABLE");
			}
		}
	}

    public static Map<String, Object> buildEbsPaymentResponse (String responseString, String secretKey) {
        Map<String, Object> responseMap = FastMap.newInstance();

        StringBuffer responseData = new StringBuffer().append(responseString);
		for (int i = 0; i < responseData.length(); i++) {
			if (responseData.charAt(i) == ' ') {
				responseData.setCharAt(i, '+');
			}
		}
    
        Base64 base64 = new Base64();
        byte[] data = base64.decode(responseData.toString());
        RC4 rc4 = new RC4(secretKey);
        byte[] result = rc4.rc4(data);
        
        ByteArrayInputStream byteIn = new ByteArrayInputStream (result, 0, result.length);
        BufferedReader dataIn = new BufferedReader(new InputStreamReader(byteIn));
        String recvString1 = "";
        String recvString = "";
		try {
			recvString1 = dataIn.readLine();
		} catch (IOException e) {
			Debug.logError(e, e.toString(), module);
		}

        int i =0;
		while (recvString1 != null) {
			i++;
			if (i > 705)
				break;
			recvString += recvString1 + "\n";
			try {
				recvString1 = dataIn.readLine();
			} catch (IOException e) {
				Debug.logError(e, e.toString(), module);
			}
		}
        recvString  = recvString.replace( "=&","=--&" );
        StringTokenizer st = new StringTokenizer(recvString, "=&");
		while (st.hasMoreTokens()) {
			responseMap.put(st.nextToken(), st.nextToken());
		}
        return responseMap;
    }

    public static Document sendRequest(String serverURL, Map<String, Object> parameters) throws GeneralException {
        HttpClient http = new HttpClient(serverURL, parameters);
        http.setAllowUntrusted(true);
        http.setDebug(true);
        String response = null;
        try {
            response = http.post(); //might be an exception warn log for host certification
        } catch (HttpClientException hce) {
			Debug.logError(hce, hce.toString(), module);
            throw new GeneralException("EBS api connection problem", hce);
        }

        Document responseDocument = null;
        try {
            responseDocument = UtilXml.readXmlDocument(response, false);
        } catch (SAXException se) {
            throw new GeneralException("Error reading response Document from a String: " + se.getMessage());
        } catch (ParserConfigurationException pce) {
            throw new GeneralException("Error reading response Document from a String: " + pce.getMessage());
        } catch (IOException ioe) {
            throw new GeneralException("Error reading response Document from a String: " + ioe.getMessage());
        }
        return responseDocument;
    }

}