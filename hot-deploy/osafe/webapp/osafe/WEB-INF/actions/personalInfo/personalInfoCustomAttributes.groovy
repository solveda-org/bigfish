package personalInfo;

import org.ofbiz.base.util.string.FlexibleStringExpander;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import com.osafe.services.OsafeManageXml;
import com.osafe.services.CustomPartyAttributeServices;

customPartyAttributeList = CustomPartyAttributeServices.getPartyCustomAttributeList();

/*customPartyAttributeFilePath = FlexibleStringExpander.expandString(System.getProperty("ofbiz.home") + "/hot-deploy/osafe/import/data/xml/CustomPartyAttributes.xml", context);
customPartyAttributeList = OsafeManageXml.getListMapsFromXmlFileUseCache(customPartyAttributeFilePath);


for(Map customPartyAttributeMap : customPartyAttributeList) 
{
     if ((customPartyAttributeMap.SequenceNum instanceof String) && (UtilValidate.isInteger(customPartyAttributeMap.SequenceNum))) 
     {
         if (UtilValidate.isNotEmpty(customPartyAttributeMap.SequenceNum)) 
         {
        	 customPartyAttributeMap.SequenceNum = Integer.parseInt(customPartyAttributeMap.SequenceNum);
         } 
         else 
         {
        	 customPartyAttributeMap.SequenceNum = 0;
         }
     }
 }
customPartyAttributeList = UtilMisc.sortMaps(customPartyAttributeList, UtilMisc.toList("SequenceNum"));*/
context.customPartyAttributeList = customPartyAttributeList;

