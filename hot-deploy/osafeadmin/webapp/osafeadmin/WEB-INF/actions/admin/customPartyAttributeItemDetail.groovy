package admin;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.string.FlexibleStringExpander;
import org.ofbiz.base.util.UtilProperties;
import com.osafe.services.OsafeManageXml;
import javolution.util.FastMap;
import java.util.Map;
import org.ofbiz.base.util.FileUtil;

XmlFilePath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("osafeAdmin.properties", "osafe-customPartyAttribute-xml-file"), context);
customPartyAttributeList =  OsafeManageXml.getListMapsFromXmlFile(XmlFilePath);

if (UtilValidate.isNotEmpty(parameters.attrName))
{
	for(Map customPartyAttributeMap : customPartyAttributeList) 
	{
	     if (customPartyAttributeMap.AttrName.equals(parameters.attrName)) 
	     {
	    	 context.customPartyAttribute = customPartyAttributeMap 
	         break;
	     }
	}	
}