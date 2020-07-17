package com.osafe.feeds;

import java.io.File;
import java.util.List;
import java.util.Map;

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
		
		if(UtilValidate.isNotEmpty(feedType)) 
		{
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
				if(UtilValidate.isNotEmpty(partyPhoneDetail))
				{
					partyPhoneNumber = OsafeAdminUtil.formatTelephone((String)partyPhoneDetail.get("areaCode"),(String)partyPhoneDetail.get("contactNumber"));
				}
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
				if(UtilValidate.isNotEmpty(partyPhoneExtDetail))
				{
					partyPhoneExt = (String)partyPhoneExtDetail.get("extension");
				}
				
    	    }
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return partyPhoneExt;
	}
}
