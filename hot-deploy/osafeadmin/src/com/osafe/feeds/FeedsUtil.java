package com.osafe.feeds;

import java.io.File;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;


import com.osafe.util.OsafeAdminUtil;

import com.osafe.feeds.osafefeeds.BillingAddressType;
import com.osafe.feeds.osafefeeds.CustomerType;
import com.osafe.feeds.osafefeeds.ObjectFactory;
import com.osafe.feeds.osafefeeds.ShippingAddressType;

public class FeedsUtil {
	public static final String module = FeedsUtil.class.getName();
	
	public static void getCustomerBillingAddress(ObjectFactory objFactory, CustomerType customer, List<GenericValue> partyContactDetails,String contactMechPurposeTypeId, Delegator delegator) {
		
		List addressList = null;
		try {
		
		List<GenericValue> partyLocationDetails = EntityUtil.filterByAnd(partyContactDetails, UtilMisc.toMap("contactMechPurposeTypeId",contactMechPurposeTypeId));
        for(GenericValue partyLocationDetail : partyLocationDetails) 
        {
        	//Object address = null;
        	BillingAddressType address = objFactory.createBillingAddressType();
    		
    	    addressList = customer.getBillingAddress();
    		
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
            
            address.setAddress1(address1);
            address.setAddress2(address2);
            address.setAddress3(address3);
            address.setCountry((String)partyLocationDetail.get("countryGeoId"));
            address.setCityTown(city);
            address.setStateProvince((String)partyLocationDetail.get("stateProvinceGeoId"));
            address.setZipPostCode((String)partyLocationDetail.get("postalCode"));
            addressList.add(address);
        }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
    public static void getCustomerShippingAddress(ObjectFactory objFactory, CustomerType customer, List<GenericValue> partyContactDetails,String contactMechPurposeTypeId, Delegator delegator) {
		
		List addressList = null;
		try {
		
		List<GenericValue> partyLocationDetails = EntityUtil.filterByAnd(partyContactDetails, UtilMisc.toMap("contactMechPurposeTypeId",contactMechPurposeTypeId));
        for(GenericValue partyLocationDetail : partyLocationDetails) 
        {
        	//Object address = null;
        	ShippingAddressType address = objFactory.createShippingAddressType();
    		
    	    addressList = customer.getShippingAddress();
    		
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
            
            address.setAddress1(address1);
            address.setAddress2(address2);
            address.setAddress3(address3);
            address.setCountry((String)partyLocationDetail.get("countryGeoId"));
            address.setCityTown(city);
            address.setStateProvince((String)partyLocationDetail.get("stateProvinceGeoId"));
            address.setZipPostCode((String)partyLocationDetail.get("postalCode"));
            addressList.add(address);
        }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void marshalObject(Object obj, File file) {
	    try {
	        JAXBContext jaxbContext = JAXBContext.newInstance("com.osafe.feeds.osafefeeds");
	  	    Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
	  	    jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
	  	    //jaxbMarshaller.setProperty(Marshaller.JAXB_ENCODING, "Unicode");
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
		
		String feedDirectory = System.getProperty("ofbiz.home") + "/runtime/tmp/download/";
		
		if(UtilValidate.isNotEmpty(feedType)) {
			feedDirectory = feedDirectory + feedType + "/";
		}
		return feedDirectory;
	}
	
	public static String getPartyPhoneNumber(String partyId, String contactMechPurposeTypeId, Delegator delegator) {
		String partyPhoneNumber = "";
		try {
			GenericValue partyPhoneDetail = null;
			List<GenericValue> partyPhoneDetails = delegator.findByAnd("PartyContactDetailByPurpose", UtilMisc.toMap("contactMechPurposeTypeId", contactMechPurposeTypeId, "partyId", partyId));
			if(UtilValidate.isNotEmpty(partyPhoneDetails)) {
				partyPhoneDetails = EntityUtil.filterByDate(partyPhoneDetails);
				partyPhoneDetail = EntityUtil.getFirst(partyPhoneDetails);
				partyPhoneNumber = OsafeAdminUtil.formatTelephone((String)partyPhoneDetail.get("areaCode"),(String)partyPhoneDetail.get("contactNumber"));
    	    }
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return partyPhoneNumber;
	}
	
	public static String getPartyPhoneExt(String partyId, String contactMechPurposeTypeId, Delegator delegator) {
		String partyPhoneExt = "";
		try {
			GenericValue partyPhoneExtDetail = null;
			List<GenericValue> partyPhoneExtDetails = delegator.findByAnd("PartyContactDetailByPurpose", UtilMisc.toMap("contactMechPurposeTypeId", contactMechPurposeTypeId, "partyId", partyId));
			if(UtilValidate.isNotEmpty(partyPhoneExtDetails)) {
				partyPhoneExtDetails = EntityUtil.filterByDate(partyPhoneExtDetails);
				partyPhoneExtDetail = EntityUtil.getFirst(partyPhoneExtDetails);
				partyPhoneExt = (String)partyPhoneExtDetail.get("extension");
    	    }
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return partyPhoneExt;
	}
}
