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
import org.ofbiz.base.util.FileUtil;
import org.ofbiz.base.util.Debug;

XmlFilePath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("osafeAdmin.properties", "osafe-uiSequence-xml-file"), context);
String searchString = StringUtils.trimToEmpty(parameters.screenType);
currentMode=context.mode;
if (UtilValidate.isNotEmpty(searchString))
{
    screenSearchList = FastList.newInstance();
    allScreenSearchLis = FastList.newInstance();
    searchRestrictionMap = FastMap.newInstance();
    searchRestrictionMap.put("screen", "Y");
    screenSearchList =  OsafeManageXml.getSearchListFromXmlFile(XmlFilePath, searchRestrictionMap, searchString, true, false);
    for(Map screenListMap : screenSearchList) 
	{
        if (UtilValidate.isInteger(screenListMap.value)) 
		{
            if (UtilValidate.isNotEmpty(screenListMap.value)) 
			{
                screenListMap.value = Integer.parseInt(screenListMap.value);
            } 
			else 
			{
                screenListMap.value = 0;
            }
        }
    }

    // make group search list sorted by value
    screenGroupSearchList = FastList.newInstance();
    processedGroupList = FastList.newInstance();
    for(Map screenListMap : screenSearchList) 
	{
        if (!processedGroupList.contains(screenListMap.group)) 
		{
            searchGroupMapList =  OsafeManageXml.getSearchListFromListMaps(screenSearchList, UtilMisc.toMap("group", "Y"), screenListMap.group, true, false);
            screenGroupSearchList.addAll( UtilMisc.sortMaps(searchGroupMapList, UtilMisc.toList("value")));
            processedGroupList.add(screenListMap.group);
        }
    }
    // sort group search list sorted by group
    screenSearchList = screenGroupSearchList;
    for(Map screenListMap : screenSearchList) 
	{
        if (UtilValidate.isInteger(screenListMap.group)) 
		{
            if (UtilValidate.isNotEmpty(screenListMap.group)) 
			{
                screenListMap.group = Integer.parseInt(screenListMap.group);
            } 
			else 
			{
                screenListMap.group = 0;
            }
        }
    }
    screenSearchList = UtilMisc.sortMaps(screenSearchList, UtilMisc.toList("group"));
	
	if("add".equalsIgnoreCase(currentMode))
	{
		emptyAddMap = FastMap.newInstance();
		screenSearchList.add(emptyAddMap);
	}
	
    context.resultScreenList = screenSearchList;
    pagingListSize=screenSearchList.size();
    context.pagingListSize=pagingListSize;
    context.selectedScreen = searchString; 
} 
if (UtilValidate.isNotEmpty(context.retrieveAll) && context.retrieveAll == "Y")
{
    allScreenSearchList =  OsafeManageXml.getListMapsFromXmlFile(XmlFilePath);
    context.resultList = UtilMisc.sortMaps(allScreenSearchList, UtilMisc.toList("screen"));
}

addDivSeqItemKey = parameters.addKey;
addDivSeqItemScreen = parameters.screen;
if (UtilValidate.isNotEmpty(addDivSeqItemKey) && UtilValidate.isNotEmpty(searchString)) 
{
	tmpDir = FileUtil.getFile("runtime/tmp");
	uploadedFile = new File(tmpDir, context.uploadedFileName);
	XmlFilePath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("osafeAdmin.properties", "osafe-uiSequence-xml-file"), context);
	allScreenSearchList =  OsafeManageXml.getListMapsFromXmlFile(XmlFilePath);
	context.allScreens = allScreenSearchList;
	List<Map<Object, Object>> listMaps = OsafeManageXml.getListMapsFromXmlFile(uploadedFile.getAbsolutePath());
	context.divSeqItemEntry = OsafeManageXml.findByKeyAndScreenFromListMaps(listMaps, "key", addDivSeqItemKey, "screen", searchString);
}
