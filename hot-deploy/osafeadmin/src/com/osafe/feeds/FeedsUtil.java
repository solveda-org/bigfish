package com.osafe.feeds;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;


import com.osafe.util.OsafeAdminUtil;

import com.osafe.feeds.customer.BillingAddress;
import com.osafe.feeds.customer.ShippingAddress;
import com.osafe.feeds.customer.Address;

public class FeedsUtil {
	public static final String module = FeedsUtil.class.getName();
	
	public static List<Address> getAddress(List<GenericValue> partyContactDetails,String contactMechPurposeTypeId, Delegator delegator) {
		
		List<Address> addressList = null;
		addressList = new ArrayList<Address>();
		try {
		
		List<GenericValue> partyLocationDetails = EntityUtil.filterByAnd(partyContactDetails, UtilMisc.toMap("contactMechPurposeTypeId",contactMechPurposeTypeId));
        for(GenericValue partyLocationDetail : partyLocationDetails) 
        {
        	Address address = null;
    		if (contactMechPurposeTypeId == "BILLING_LOCATION") {
    			address = new BillingAddress();
    		} else if(contactMechPurposeTypeId == "SHIPPING_LOCATION") {
    			address = new ShippingAddress();
    		}
    		
		    List<GenericValue> contactMechLinkList = delegator.findByAnd("ContactMechLink", UtilMisc.toMap("contactMechIdFrom", (String)partyLocationDetail.get("contactMechId")));
            GenericValue partyDayPhoneDetail = null;
            GenericValue partyEveningPhoneDetail = null;
            String dayPhone = null;
            String eveningPhone = null;
        
            for(GenericValue contactMechLink : contactMechLinkList) {
        	    List<GenericValue> partyDayPhoneDetails = delegator.findByAnd("PartyContactDetailByPurpose", UtilMisc.toMap("contactMechPurposeTypeId","PHONE_HOME","contactMechId", (String)contactMechLink.get("contactMechIdTo")));
        	    if(UtilValidate.isNotEmpty(partyDayPhoneDetails)) {
                    partyDayPhoneDetail = EntityUtil.getFirst(partyDayPhoneDetails);
                    dayPhone = OsafeAdminUtil.formatTelephone((String)partyDayPhoneDetail.get("areaCode"),(String)partyDayPhoneDetail.get("contactNumber"));
        	    }
            
                List<GenericValue> partyEveningPhoneDetails = delegator.findByAnd("PartyContactDetailByPurpose", UtilMisc.toMap("contactMechPurposeTypeId","PHONE_MOBILE","contactMechId", (String)contactMechLink.get("contactMechIdTo")));
                if(UtilValidate.isNotEmpty(partyEveningPhoneDetails)) {
                    partyEveningPhoneDetail = EntityUtil.getFirst(partyEveningPhoneDetails);
                    eveningPhone = OsafeAdminUtil.formatTelephone((String)partyEveningPhoneDetail.get("areaCode"),(String)partyEveningPhoneDetail.get("contactNumber"));
                }
            }
            String address1 = (String)partyLocationDetail.get("address1");
            if(UtilValidate.isEmpty(address1)) {
            	address1 = "";
            }
            
            String address2 = (String)partyLocationDetail.get("address2");
            if(UtilValidate.isEmpty(address2)) {
            	address2 = "";
            }
            
            String address3 = (String)partyLocationDetail.get("address3");
            if(UtilValidate.isEmpty(address3)) {
            	address3 = "";
            }
            
            String city = (String)partyLocationDetail.get("city");
            if(UtilValidate.isEmpty(city)) {
            	city = "";
            }
            if(UtilValidate.isEmpty(dayPhone)) {
            	dayPhone = "";
            }
            if(UtilValidate.isEmpty(eveningPhone)) {
            	eveningPhone = "";
            }
            address.setAddress1(address1);
            address.setAddress2(address2);
            address.setAddress3(address3);
            address.setCountry((String)partyLocationDetail.get("countryGeoId"));
            address.setCityTown(city);
            address.setStateProvience((String)partyLocationDetail.get("stateProvinceGeoId"));
            address.setZipPostCode((String)partyLocationDetail.get("postalCode"));
            address.setDayPhone(dayPhone);
            address.setEveningPhone(eveningPhone);
            addressList.add(address);
        }
		} catch (Exception e) {
			e.printStackTrace();
		}
		return addressList;
	}
	
	public static void marshalObject(Object obj, File file) {
	    try {
	        JAXBContext jaxbContext = JAXBContext.newInstance(obj.getClass());
	  	    Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
	  	    jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
	  	    jaxbMarshaller.setProperty(Marshaller.JAXB_ENCODING, "Unicode");
	  	    jaxbMarshaller.marshal(obj, file);
            
	    } catch (JAXBException e) {
	        e.printStackTrace();
	    }
	}
	
	public static void marshalObject(Object obj, String fileStr) {
	    File file = new File(fileStr);
	    marshalObject(obj, file);
	}
	
	public static String getFeedDirectory(String feedType) {
		
		String feedDirectory = System.getProperty("ofbiz.home") + "/hot-deploy/osafeadmin/data/feeds/";
		
		if(UtilValidate.isNotEmpty(feedType)) {
			feedDirectory = feedDirectory + feedType + "/";
		}
		return feedDirectory;
	}
}
