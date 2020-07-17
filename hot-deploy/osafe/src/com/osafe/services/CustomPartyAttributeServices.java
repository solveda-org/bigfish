package com.osafe.services;

import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.string.FlexibleStringExpander;
import com.osafe.services.OsafeManageXml;

public class CustomPartyAttributeServices {

    public static final String module = OrderServices.class.getName();

    public static List getPartyCustomAttributeList() 
    {
    	String customPartyAttributeFilePath = FlexibleStringExpander.expandString(System.getProperty("ofbiz.home") + "/hot-deploy/osafe/import/data/xml/CustomPartyAttributes.xml", null);
    	List<Map<Object, Object>> customPartyAttributeList = OsafeManageXml.getListMapsFromXmlFileUseCache(customPartyAttributeFilePath);


    	for(Map customPartyAttributeMap : customPartyAttributeList) 
    	{
    	     if ((customPartyAttributeMap.get("SequenceNum") instanceof String) && (UtilValidate.isInteger((String)customPartyAttributeMap.get("SequenceNum")))) 
    	     {
    	         if (UtilValidate.isNotEmpty(customPartyAttributeMap.get("SequenceNum"))) 
    	         {
    	        	 customPartyAttributeMap.put("SequenceNum", Integer.parseInt((String)customPartyAttributeMap.get("SequenceNum")));
    	         } 
    	         else 
    	         {
    	        	 customPartyAttributeMap.put("SequenceNum", 0);
    	         }
    	     }
    	 }
    	customPartyAttributeList = UtilMisc.sortMaps(customPartyAttributeList, UtilMisc.toList("SequenceNum"));
    	
    	return customPartyAttributeList;
    }
    
}
