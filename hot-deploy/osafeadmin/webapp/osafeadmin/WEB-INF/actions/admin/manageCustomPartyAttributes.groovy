package admin;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.string.FlexibleStringExpander;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilMisc;
import com.osafe.services.OsafeManageXml;
import org.apache.commons.lang.StringUtils;
import javolution.util.FastList;
import javolution.util.FastMap;
import java.util.Map;
import java.util.List;
import java.util.Set;

import org.ofbiz.base.util.string.FlexibleStringExpander;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import com.osafe.services.OsafeManageXml;
import com.osafe.services.CustomPartyAttributeServices;

XmlFilePath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("osafeAdmin.properties", "osafe-customPartyAttribute-xml-file"), context);

if (UtilValidate.isNotEmpty(context.retrieveAll) && context.retrieveAll == "Y")
{
	customPartyAttributeList = OsafeManageXml.getListMapsFromXmlFile(XmlFilePath);
	for(Map customPartyAttributeListMap : customPartyAttributeList) 
	{
        if (UtilValidate.isInteger(customPartyAttributeListMap.SequenceNum)) 
        {
            if (UtilValidate.isNotEmpty(customPartyAttributeListMap.SequenceNum)) 
            {
            	customPartyAttributeListMap.SequenceNum = Integer.parseInt(customPartyAttributeListMap.SequenceNum);
            } 
            else 
            {
            	customPartyAttributeListMap.SequenceNum = 0;
            }
        }
    }
	customPartyAttributeList = UtilMisc.sortMaps(customPartyAttributeList, UtilMisc.toList("SequenceNum"));
    context.resultList = customPartyAttributeList;
}
