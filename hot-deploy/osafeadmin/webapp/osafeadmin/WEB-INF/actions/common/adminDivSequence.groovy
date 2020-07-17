package common;

import javolution.util.FastMap;
import javolution.util.FastList;
import com.osafe.services.OsafeManageXml;
import org.ofbiz.base.util.string.FlexibleStringExpander;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilValidate;

excludeDivList = FastList.newInstance();
if (UtilValidate.isNotEmpty(excludeDivs))
{
    excludeDivList = StringUtil.split(excludeDivs, "|");
}

osafeProperties = UtilProperties.getResourceBundleMap("OsafeProperties.xml", locale);
XmlFilePath = FlexibleStringExpander.expandString(osafeProperties.ecommerceUiSequenceXmlFile, context);
searchRestrictionMap = FastMap.newInstance();
searchRestrictionMap.put("screen", "Y");
uiSequenceSearchList =  OsafeManageXml.getSearchListFromXmlFile(XmlFilePath, searchRestrictionMap, uiSequenceScreen,true, false, false);

for(Map uiSequenceScreenMap : uiSequenceSearchList) 
{
	 if (UtilValidate.isNotEmpty(excludeDivList))
	 {
         if (excludeDivList.contains(uiSequenceScreenMap.key))
         {
        	 uiSequenceSearchList.remove(uiSequenceScreenMap);
        	 continue;
         }
	 }
     if ((uiSequenceScreenMap.value instanceof String) && (UtilValidate.isInteger(uiSequenceScreenMap.value))) 
     {
         if (UtilValidate.isNotEmpty(uiSequenceScreenMap.value)) 
         {
             uiSequenceScreenMap.value = Integer.parseInt(uiSequenceScreenMap.value);
         } 
         else 
         {
             uiSequenceScreenMap.value = 0;
         }
     }
     
 }
uiSequenceSearchList = UtilMisc.sortMaps(uiSequenceSearchList, UtilMisc.toList("value"));

uiSequenceGroupMaps = [:] as TreeMap;
for(Map uiSequenceScreenMap : uiSequenceSearchList)
{
    if ((UtilValidate.isNotEmpty(uiSequenceScreenMap.group)) && (UtilValidate.isInteger(uiSequenceScreenMap.group)))
	{
		groupNum = Integer.parseInt(uiSequenceScreenMap.group)
		if (!uiSequenceGroupMaps.containsKey(groupNum))
		{
			searchGroupMapList =  OsafeManageXml.getSearchListFromListMaps(uiSequenceSearchList, UtilMisc.toMap("group", "Y"), uiSequenceScreenMap.group, true, false);
			if (UtilValidate.isNotEmpty(searchGroupMapList))
			{
				uiSequenceGroupMaps.put(groupNum, UtilMisc.sortMaps(searchGroupMapList, UtilMisc.toList("value")))
			}
		}
	}
}
context.divSequenceGroupMaps = uiSequenceGroupMaps;
context.divSequenceList = uiSequenceSearchList;