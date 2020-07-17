package com.osafe.services;

import java.io.Serializable;
import java.util.List;

import org.ofbiz.base.util.UtilValidate;
import com.osafe.services.AddressDocument;

public class AddressVerificationResponse implements Serializable {

    public static final int AE = -1;
    public static final int AC = 0;
    public static final int AS = 1;
    public static final int GE = 2;
    public static final String[] responses = {"Address Error", "Address Change", "Address Success", "General Error"};

    private String verificationMethod = "NONE";
    private Integer responseCode = null;
    private AddressDocument queryAddressdata = null;
    private AddressDocument changedAddressdata = null;
    private List<AddressDocument> alternateAddresses = null;

	public AddressVerificationResponse() {
		// TODO Auto-generated constructor stub
	}

    public void setResponseCode(int responseCode) {
		this.responseCode = new Integer(responseCode);
	}

    public Integer getResponseCode() {
        return responseCode;
    }

    public String GetResponseCodeString() {
      if (responseCode == null) {
    	  return null;
      } else {
          return responses[responseCode.intValue()];
      }
    }

    public AddressDocument getQueryAddressdata() {
		return queryAddressdata;
	}

    public void setQueryAddressdata(AddressDocument queryAddressdata) {
		this.queryAddressdata = queryAddressdata;
	}

    public AddressDocument getChangedAddressdata() {
		return changedAddressdata;
	}

    public void setChangedAddressdata(AddressDocument changedAddressdata) {
		this.changedAddressdata = changedAddressdata;
	}

    public List<AddressDocument> getAlternateAddresses() {
		return alternateAddresses;
	}
    
    public void setAlternateAddresses(List<AddressDocument> alternateAddresses) {
    	if (UtilValidate.isNotEmpty(alternateAddresses) && UtilValidate.isEmpty(this.changedAddressdata)) {
    		this.changedAddressdata = alternateAddresses.get(0);
    	}
		this.alternateAddresses = alternateAddresses;
	}
    public String getVerificationMethod() {
		return verificationMethod;
	}
    public void setVerificationMethod(String verificationMethod) {
		this.verificationMethod = verificationMethod;
	}
}
