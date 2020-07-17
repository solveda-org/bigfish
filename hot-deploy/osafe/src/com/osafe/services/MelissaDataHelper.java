package com.osafe.services;

import java.io.IOException;
import java.io.StringReader;
import java.net.URLEncoder;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import javolution.util.FastList;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilIO;
import org.ofbiz.base.util.UtilValidate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.osafe.util.Util;
import com.osafe.services.AddressVerificationResponse;
import com.osafe.services.AddressDocument;

import com.melissadata.mdAddr;

public class MelissaDataHelper {
    
    public static final String module = MelissaDataHelper.class.getName();

    protected String productStoreId = null;
    protected String verificationMode = null;
    protected String webRegistrationId = null;
    protected String webUrl = null;
    protected String dataLicence = null;
    protected String dataFilepathUS = null;
    protected String dataFilepathCAN = null;

    /** don't allow empty constructor */
    private MelissaDataHelper() {}

    private MelissaDataHelper(String productStoreId) {
        setProductStoreId(productStoreId);
        setVerificationMode(Util.getProductStoreParm(getProductStoreId(), "MELISSA_VERIFICATION_MODE"));
        setWebRegistrationId(Util.getProductStoreParm(getProductStoreId(), "MELISSA_REGISTRATION_ID"));
        setWebUrl(Util.getProductStoreParm(getProductStoreId(), "MELISSA_HTTP_URL"));
        setDataLicence(Util.getProductStoreParm(getProductStoreId(), "MELISSA_LICENSE"));
        setDataFilepathUS(Util.getProductStoreParm(getProductStoreId(), "MELISSA_FILE_PATH_US"));
        setDataFilepathCAN(Util.getProductStoreParm(getProductStoreId(), "MELISSA_FILE_PATH_CAN"));
    }

    public static MelissaDataHelper getInstance(String productstoreid) 
    {
        if (UtilValidate.isEmpty(productstoreid)) {
            return null;
        } else {
            return new MelissaDataHelper(productstoreid);
        }
   }

    public void setProductStoreId(String productStoreId) {
        this.productStoreId = productStoreId;
    }
    
    public String getProductStoreId() {
        return productStoreId;
    }
    
    public void setVerificationMode(String verificationMode) {
        this.verificationMode = verificationMode;
    }
    
    public String getVerificationMode() {
        return verificationMode;
    }
    
    public void setWebRegistrationId(String webRegistrationId) {
        this.webRegistrationId = webRegistrationId;
    }
    
    public String getWebRegistrationId() {
        return webRegistrationId;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public void setDataLicence(String dataLicence) {
        this.dataLicence = dataLicence;
    }
    
    public String getDataLicence() {
        return dataLicence;
    }
    
    public void setDataFilepathUS(String dataFilepathUS) {
        this.dataFilepathUS = dataFilepathUS;
    }
    
    public String getDataFilepathUS() {
        return dataFilepathUS;
    }
    
    public void setDataFilepathCAN(String dataFilepathCAN) {
        this.dataFilepathCAN = dataFilepathCAN;
    }
    
    public String getDataFilepathCAN() {
        return dataFilepathCAN;
    }

    public mdAddr getAddressObject() {
    	mdAddr ao = new mdAddr();
    	ao.SetLicenseString(getDataLicence());
    	ao.SetPathToUSFiles(getDataFilepathUS());
    	if (UtilValidate.isNotEmpty(getDataFilepathCAN()))
        {
        	ao.SetPathToCanadaFiles(getDataFilepathCAN());
        }

        //Initialize Data Files
        mdAddr.ProgramStatus result = ao.InitializeDataFiles();
        if (result != mdAddr.ProgramStatus.ErrorNone)
        {
          //Problem during initialization
          return null;
        }
    	return ao;
    }
    public AddressVerificationResponse verifyAddress(AddressDocument queryAddressdata) {

        AddressVerificationResponse avResponse = new AddressVerificationResponse();

        String verificationMode = getVerificationMode();
        if (UtilValidate.isNotEmpty(verificationMode) && verificationMode.equalsIgnoreCase("HTTP"))
        {
            try 
            {
                String responseString = getHttpResponseAsString(getHttpClient(), getHttpGet(createMelissaRestRequest(queryAddressdata)));
                if (UtilValidate.isNotEmpty(responseString))
                {
	                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	                DocumentBuilder db = dbf.newDocumentBuilder();
	                InputSource is = new InputSource();
	                is.setCharacterStream(new StringReader(responseString));
	                Document xmlDocument = db.parse(is);
	                avResponse = buildVerificationResponse(xmlDocument);
                }
                else
                {
                	avResponse.setResponseCode(AddressVerificationResponse.GE);
                }
            }
            catch (Exception e) {
                Debug.logError(e, "Error occured in Melissa Rest Call", module);
                avResponse.setResponseCode(AddressVerificationResponse.GE);
            }
        }
        else if (UtilValidate.isNotEmpty(verificationMode) && verificationMode.equalsIgnoreCase("FILEPATH"))
        {
            try 
            {
            	mdAddr ao = getAddressObject();
            	if (ao == null)
            	{
                    avResponse.setResponseCode(AddressVerificationResponse.GE);
            	}
            	else
            	{
            		ao = setMelissaFileRequest(ao, queryAddressdata);
            		ao.VerifyAddress();
            		avResponse = buildVerificationResponse(ao);
            	    ao.delete();
            	}
            }
            catch(Exception e)
            {
	            Debug.logError(e, "Error occured in Melissa file Call", module);
	            avResponse.setResponseCode(AddressVerificationResponse.GE);
            }
        }
        avResponse.setQueryAddressdata(queryAddressdata);
        return avResponse;
    }

    private mdAddr setMelissaFileRequest(mdAddr ao, AddressDocument queryAddressdata)
    {
        if (UtilValidate.isNotEmpty(queryAddressdata.getAddress1()))
        {
        	ao.SetAddress(queryAddressdata.getAddress1());
        }
        if (UtilValidate.isNotEmpty(queryAddressdata.getAddress2()))
        {
        	ao.SetAddress2(queryAddressdata.getAddress2());
        }
        if (UtilValidate.isNotEmpty(queryAddressdata.getAddress3()))
        {
            if (UtilValidate.isNotEmpty(queryAddressdata.getAddress2()))
            {
            	ao.SetAddress2(queryAddressdata.getAddress2() + " " + queryAddressdata.getAddress3());
            }
            else
            {
            	ao.SetAddress2(queryAddressdata.getAddress3());
            }
        }
        if (UtilValidate.isNotEmpty(queryAddressdata.getCity()))
        {
        	ao.SetCity(queryAddressdata.getCity());
        }
        if (UtilValidate.isNotEmpty(queryAddressdata.getStateProvinceGeoId()))
        {
        	ao.SetState(queryAddressdata.getStateProvinceGeoId());
        }
        if (UtilValidate.isNotEmpty(queryAddressdata.getPostalCode()))
        {
        	ao.SetZip(queryAddressdata.getPostalCode());
        }
        if (UtilValidate.isNotEmpty(queryAddressdata.getPostalCodeExt()))
        {
        	ao.SetPlus4(queryAddressdata.getPostalCodeExt());
        }
        if (UtilValidate.isNotEmpty(queryAddressdata.getCountryGeoId()))
        {
        	ao.SetCountryCode(queryAddressdata.getCountryGeoId());
        }
        return ao;
    }

    private static AddressVerificationResponse buildVerificationResponse(mdAddr ao){
    	AddressVerificationResponse avResponse = new AddressVerificationResponse();
		//check result success, changed or error
		String results = ao.GetResults();
		setResponseCode(avResponse, results);
		if (UtilValidate.isEmpty(avResponse.getResponseCode())) 
		{
			avResponse.setResponseCode(AddressVerificationResponse.AS);
		}
    	List<AddressDocument> responseAddresseList = FastList.newInstance();
    	AddressDocument responseAddress = new AddressDocument();
    	responseAddress.setAddress1(ao.GetAddress());
    	String addressLine2 = "";
    	String suite = ao.GetSuite();
    	if (UtilValidate.isNotEmpty(suite))
    	{
    		addressLine2 = suite + " ";
    	}
    	String address2 = ao.GetAddress2();
    	if (UtilValidate.isNotEmpty(address2))
    	{
    		addressLine2 = addressLine2 + address2;
    	}
    	responseAddress.setAddress2(addressLine2);
    	responseAddress.setCity(ao.GetCity());
    	responseAddress.setStateProvinceGeoId(ao.GetState());
    	responseAddress.setPostalCode(ao.GetZip());
    	responseAddress.setPostalCodeExt(ao.GetPlus4());
    	responseAddress.setCountryGeoId(ao.GetCountryCode());
		responseAddresseList.add(responseAddress);
		avResponse.setAlternateAddresses(responseAddresseList);
		return avResponse;
    }

    private String createMelissaRestRequest(AddressDocument queryAddressdata)
    {
    	    String a2 = queryAddressdata.getAddress2();
            if (UtilValidate.isNotEmpty(queryAddressdata.getAddress2()))
            {
            	a2 = queryAddressdata.getAddress2();
            }
            if (UtilValidate.isNotEmpty(queryAddressdata.getAddress3()))
            {
                if (UtilValidate.isNotEmpty(queryAddressdata.getAddress2()))
                {
                	a2 = queryAddressdata.getAddress2() + " " + queryAddressdata.getAddress3();
                }
                else
                {
                	a2 = queryAddressdata.getAddress3();
                }
            }
            //Build the rest request here    
            String restRequestString = getWebUrl() +"?" + 
                "t=" + makeEncoded("DQWS XML Sample Code implementation using Multiple record inputs.") + "&" +
                "id=" + makeEncoded(getWebRegistrationId())  + "&" +
                "opt=" + "true" + "&" +
                "comp=" + "" + "&" +
                "u=" + "" + "&" +
                "a1=" + makeEncoded(queryAddressdata.getAddress1()) + "&" +
                "a2=" + makeEncoded(a2) +"&" +
                "ste=" + "" + "&" +
                "city=" + makeEncoded(queryAddressdata.getCity()) + "&" +
                "state=" + makeEncoded(queryAddressdata.getStateProvinceGeoId()) + "&" +
                "zip=" + makeEncoded(queryAddressdata.getPostalCode()) + "&" +
                "p4=" + makeEncoded(queryAddressdata.getPostalCodeExt()) + "&" +
                "ctry=" + makeEncoded(queryAddressdata.getCountryGeoId()) + "&" +
                "last=" + "";
        return restRequestString;
    }

    private String makeEncoded(String value){
    	String encodedValue = value;
    	try 
    	{
    		encodedValue = URLEncoder.encode(encodedValue, "UTF-8");
        }
        catch (Exception e) { }
        return encodedValue;
    }

    private static HttpClient getHttpClient() {
        HttpClient httpClient = new HttpClient(new MultiThreadedHttpConnectionManager());
        return httpClient;
    }

    private static GetMethod getHttpGet(String uri){
        GetMethod getMethod = new GetMethod(uri);
        return getMethod;
    }

    private static String getHttpResponseAsString(HttpClient httpClient, GetMethod getMethod) throws IOException,HttpException {
        String resultString = "";
        httpClient.executeMethod(getMethod);
        if (getMethod.getStatusCode() == HttpStatus.SC_OK) {
            resultString = UtilIO.readString(getMethod.getResponseBodyAsStream(), getMethod.getResponseCharSet());
        }
        getMethod.releaseConnection();
        return resultString;
    }

    private static AddressVerificationResponse buildVerificationResponse(Document doc){
    	AddressVerificationResponse avResponse = new AddressVerificationResponse();
        NodeList nodeList = doc.getElementsByTagName("Results").item(0).getChildNodes();
        Node nValue = (Node) nodeList.item(0);
    	if (nValue.getNodeValue().equals(" "))
    	{
	    	List<AddressDocument> responseAddresseList = FastList.newInstance();
	    	NodeList recordList = doc.getElementsByTagName("Record");
			for (int temp = 0; temp < recordList.getLength(); temp++)
			{
				AddressDocument responseAddresse = new AddressDocument();
				Node nNode = recordList.item(temp);
				Element eElement = (Element) nNode;
				//check result success, changed or error
				nodeList =  eElement.getElementsByTagName("Results").item(0).getChildNodes();
				String results = nodeList.item(0).getNodeValue();
				setResponseCode(avResponse, results);
				if (UtilValidate.isEmpty(avResponse.getResponseCode())) 
				{
					avResponse.setResponseCode(AddressVerificationResponse.AS);
				}
				//get address1
				nodeList =  eElement.getElementsByTagName("Address1").item(0).getChildNodes();
				responseAddresse.setAddress1(nodeList.item(0).getNodeValue());
				String addressLine2 = "";
				//get Suite
				nodeList =  eElement.getElementsByTagName("Suite").item(0).getChildNodes();
				String suite = nodeList.item(0).getNodeValue();
				if (UtilValidate.isNotEmpty(suite))
				{
					addressLine2 = suite + " ";
					
				}
				//get address2
				nodeList =  eElement.getElementsByTagName("Address2").item(0).getChildNodes();
				String address2 = nodeList.item(0).getNodeValue();
				if (UtilValidate.isNotEmpty(address2) && !"NULL".equalsIgnoreCase(address2))
				{
					addressLine2 = addressLine2 + address2 + " ";
					
				}
				nodeList =  eElement.getElementsByTagName("Garbage").item(0).getChildNodes();
				String garbage = nodeList.item(0).getNodeValue();
				if (UtilValidate.isNotEmpty(garbage))
				{
					addressLine2 = addressLine2 + garbage;
					
				}
				if (UtilValidate.isNotEmpty(addressLine2))
				{
					addressLine2 = addressLine2.trim();
				}

				if (UtilValidate.isEmpty(addressLine2) || "NULL".equalsIgnoreCase(addressLine2)) 
				{
					responseAddresse.setAddress2("");
				}
				else
                {
					responseAddresse.setAddress2(addressLine2);
                }
				
				//get City
				nodeList =  eElement.getElementsByTagName("City").item(0).getChildNodes();
				responseAddresse.setCity(nodeList.item(0).getChildNodes().item(0).getNodeValue());
				//get State
				nodeList =  eElement.getElementsByTagName("State").item(0).getChildNodes();
				responseAddresse.setStateProvinceGeoId(nodeList.item(1).getChildNodes().item(0).getNodeValue());
				//get Country
				nodeList =  eElement.getElementsByTagName("Country").item(0).getChildNodes();
				responseAddresse.setCountryGeoId(nodeList.item(0).getChildNodes().item(0).getNodeValue());
				//get Zip
				nodeList =  eElement.getElementsByTagName("Zip").item(0).getChildNodes();
				responseAddresse.setPostalCode(nodeList.item(0).getNodeValue());
				//get Plus4
				nodeList =  eElement.getElementsByTagName("Plus4").item(0).getChildNodes();
				responseAddresse.setPostalCodeExt(nodeList.item(0).getNodeValue());
				responseAddresseList.add(responseAddresse);
			}
			avResponse.setAlternateAddresses(responseAddresseList);
    	}
        else
        {
        	avResponse.setResponseCode(AddressVerificationResponse.GE);
        }
        return avResponse;
    }

    private static void setResponseCode(AddressVerificationResponse avResponse, String resultCodes)
	{
		String[] values = resultCodes.split(",");
        //Do necessary work with the values
        for (String str : values)
		{
			// Results Codes
			if (str.equals("AS01"))
			{
				//Address Matched to Postal Database
			}
			else if (str.equals("AS02"))
			{
				//The default building address was verified but the suite or apartment number is missing or invalid.
			}
			else if (str.equals("AS09"))
			{
				//Foreign Postal Code Detected
			}
			else if (str.equals("AS03"))
			{
				//This address is not deliverable by USPS, but it exists.
			}
			else if (str.equals("AS10"))
			{
				//Address Matched to CMRA
			}
			else if (str.equals("AS12"))
			{
				//Address Verified at the DPV Level
			}
			else if (str.equals("AS13"))
			{
				//Address Updated by LACS
			}
			else if (str.equals("AS14"))
			{
				//Address Updated by Suite Link
			}
			else if (str.equals("AS15"))
			{
				//Address Updated by AddressPlus
			}
			else if (str.equals("AS16"))
			{
				//Address is vacant
			}
			else if (str.equals("AS17"))
			{
				//Alternate delivery
			}
			else if (str.equals("AS18"))
			{
				//Artificially created address detected,DPV processing terminated at this point
			}
			else if (str.equals("AS20"))
			{
				//Address Deliverable by USPS only
			}
			else if (str.equals("AS23"))
			{
				//Extraneous information found
			}

			//Change Codes
			else if (str.equals("AC01"))
			{
				//ZIP Code Change
				avResponse.setResponseCode(AddressVerificationResponse.AC);
			}
			else if (str.equals("AC02"))
			{
				//State Change
				avResponse.setResponseCode(AddressVerificationResponse.AC);
			}
			else if (str.equals("AC03"))
			{
				//City Change
				avResponse.setResponseCode(AddressVerificationResponse.AC);
			}
			else if (str.equals("AC04"))
			{
				//Base/Alternate Changed
			}
			else if (str.equals("AC05"))
			{
				//Alias Name Change
			}
			else if (str.equals("AC06"))
			{
				//Address1/Address2 Swap
				avResponse.setResponseCode(AddressVerificationResponse.AC);
			}
			else if (str.equals("AC07"))
			{
				//Address1/Company Swap
			}
			else if (str.equals("AC08"))
			{
				//Plus4 Change
			}
			else if (str.equals("AC09"))
			{
				//Urbanization Change
			}
			else if (str.equals("AC10"))
			{
				//Street Name Change
			}
			else if (str.equals("AC11"))
			{
				//Street Suffix Change
			}
			else if (str.equals("AC12"))
			{
				//Street Directional Change
			}
			else if (str.equals("AC13"))
			{
				//Suite Name Change
			}
			
			// Error Handling
			else if (str.equals("AE01"))
			{
				//Zip Code Error
				avResponse.setResponseCode(AddressVerificationResponse.AE);
			}
			else if (str.equals("AE02"))
			{
				//Unknown Street
				avResponse.setResponseCode(AddressVerificationResponse.AE);
			}
			else if (str.equals("AE03"))
			{
				//Component Error
			}
			else if (str.equals("AE04"))
			{
				//Non-Deliverable Address
				avResponse.setResponseCode(AddressVerificationResponse.AE);
			}
			else if (str.equals("AE05"))
			{
				//Address Matched to Multiple Records
			}
			else if (str.equals("AE06"))
			{
				//Address Matched to Early Warning System
			}
			else if (str.equals("AE07"))
			{
				//Empty Address Input
				avResponse.setResponseCode(AddressVerificationResponse.AE);
			}
			else if (str.equals("AE08"))
			{
				//Suite Range Error
			}
			else if (str.equals("AE09"))
			{
				//Suite Range Missing
			}
			else if (str.equals("AE10"))
			{
				//Primary Range Error
			}
			else if (str.equals("AE11"))
			{
				//Primary Range Missing
			}
			else if (str.equals("AE12"))
			{
				//PO or RR Box Number Error
			}
			else if (str.equals("AE13"))
			{
				//PO or RR Box Number Missing
			}
			else if (str.equals("AE14"))
			{
				//Input Address Matched to CMRA but Secondary Number not Present
			}
			else if (str.equals("AE17"))
			{
				//A suite number was entered but no suite information found for primary address;
			}
        }
	}

}
