package com.osafe.thirdparty.firstdata.globalgatewaye4.util;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import com.osafe.thirdparty.firstdata.globalgatewaye4.GlobalGatewayE4;
import com.osafe.thirdparty.firstdata.globalgatewaye4.util.Http;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.Base64;

/**
 * @author Steve Copous <steve.copous@firstdata.com>
 * <p>INTENDED FOR DEMONSTRATION PURPOSES ONLY.  NOT FOR PRODUCTION USE.</p>
 */
public class Hmac {

	private static final String myEncoding = "UTF-8";
	private static final String myMessageDigest = "SHA-1";
	private static final String myKeySpec = "HmacSHA1";
	
	private static String NEWLINE = "\n";
	
	private String hashTime;
	private String authorizationHeader;
	private String contentSha1;
	
	/**
	 * @param e4 {@link #GlobalGatewayE4} object to be used to generate the HMAC values.
	 * @param content JSON string of the request content.
	 * @throws Exception
	 */
	public Hmac(GlobalGatewayE4 e4, String content) throws Exception {	
		hashTime = hashTime();
		contentSha1 = contentSha1(content);
		authorizationHeader = authHeader(hashTime, contentSha1, e4);
	}
	
	/**
	 * @return Returns the String of the current time format used for the hash calculation.
	 */
	public String getHashTime() {
		return hashTime;
	}
	
	/**
	 * @return Returns the String used in the Authorization header for the {@link #Http} request.
	 */
	public String getAuthorizationHeader() {
		return authorizationHeader;
	}
	
	/**
	 * @return Returns the SHA1 hash of the JSON content string.
	 */
	public String getContentSha1() {
		return contentSha1;
	}
	
	/**
	 * @param contentSha1 The SHA1 hash of the JSON content string.
	 * @return Returns the String to be used for the Authorization header in the {@link #Http} request.
	 * @throws Exception
	 */
	private static String authHeader(String hashTime, String contentSha1, GlobalGatewayE4 e4) {
		String authorizationHeader = null;
		 
		try {
			String hmacString = Http.headerMethod + NEWLINE
			+ Http.headerContentType + NEWLINE
			+ contentSha1 + NEWLINE
			+ hashTime + NEWLINE
			+ new URI(e4.getUrl()).getPath();
			

			Debug.log("HmacString" + hmacString,"Hmac.authHeader");
			authorizationHeader = "GGE4_API " + e4.getConfiguration().keyId + ":" + sha1(hmacString, e4.getConfiguration().hmacKey);
			return authorizationHeader;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
    /**
     * @param content String value of the transaction data in JSON.
     * @return Returns the SHA1 hash value of the transaction data content.
     * @throws Exception
     */
    private static String contentSha1(String content) throws Exception {
        MessageDigest md;
        md = MessageDigest.getInstance(myMessageDigest);
        byte[] sha1hash = new byte[40];
        md.update(content.getBytes(myEncoding), 0, content.length());
        sha1hash = md.digest();
        return convertToHex(sha1hash);
    }
    
    /**
     * @param data {@link #Byte} array to be converted to Hex string.
     * @return Hex string of the byte data.
     */
    private static String convertToHex(byte[] data) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < data.length; i++) {
            int halfbyte = (data[i] >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                if ((0 <= halfbyte) && (halfbyte <= 9))
                    buf.append((char) ('0' + halfbyte));
                else
                    buf.append((char) ('a' + (halfbyte - 10)));
                halfbyte = data[i] & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }
    
    /**
     * @param s String value to be hashed.
     * @param keyString String value of the key to be used to generate the hash.
     * @return Returns the SHA1 hash value of the input string.
     * @throws UnsupportedEncodingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    private static String sha1(String s, String keyString) {
        SecretKeySpec key;
		try {
			key = new SecretKeySpec((keyString).getBytes(myEncoding), myKeySpec);
	        Mac mac = Mac.getInstance(myKeySpec);
	        mac.init(key);
	        byte[] bytes = mac.doFinal(s.getBytes(myEncoding));
	        
	        
	        String encoded = new String(Base64.base64Encode(bytes));
	        return encoded;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
    }

	/**
	 * @return Returns the current time formatted for the x-gge4-date header in UTC.
	 */
	private static String hashTime() {
		String time;

		time = getUTCFormattedDate("yyyy-MM-dd'T'HH:mm:ss'Z'");

		return time;
	}
	
	/**
	 * @param format Format to be used for the current time.
	 * @return Returns the current time in UTC in the specified format.
	 */
	private static String getUTCFormattedDate(String format) {
		SimpleDateFormat dateFormat;

		dateFormat = new SimpleDateFormat(format);
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		return dateFormat.format(new Date());
	}
}
