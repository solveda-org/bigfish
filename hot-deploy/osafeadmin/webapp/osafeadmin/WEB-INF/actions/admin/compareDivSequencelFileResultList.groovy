package admin;

import com.osafe.services.OsafeManageXml;
import java.util.Map;
import java.util.List;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.base.util.*
import org.ofbiz.base.util.string.*;
import org.apache.commons.lang.StringUtils;

String searchKeysNotInUploadedFile = StringUtils.trimToEmpty(parameters.searchKeysNotInUploadedFile);
String searchKeysNotInYourLabelFile = StringUtils.trimToEmpty(parameters.searchKeysNotInYourLabelFile);
String searchKeysInBothFile = StringUtils.trimToEmpty(parameters.searchKeysInBothFile);
boolean initial = false;
if (searchKeysNotInUploadedFile.equals("") && searchKeysNotInYourLabelFile.equals("") && searchKeysInBothFile.equals("")) 
{
    initial = true;
}
if(UtilValidate.isNotEmpty(searchKeysInBothFile))
{
    context.searchKeysInBothFile = searchKeysInBothFile;
}
if(UtilValidate.isNotEmpty(searchKeysNotInUploadedFile))
{
    context.searchKeysNotInUploadedFile = searchKeysNotInUploadedFile;
}
if(UtilValidate.isNotEmpty(searchKeysNotInYourLabelFile))
{
    context.searchKeysNotInYourLabelFile = searchKeysNotInYourLabelFile;
}

initializedCB = StringUtils.trimToEmpty(parameters.initializedCB);
preRetrieved = StringUtils.trimToEmpty(parameters.preRetrieved);

if (UtilValidate.isNotEmpty(preRetrieved))
{
   context.preRetrieved=preRetrieved;
}
else
{
  preRetrieved = context.preRetrieved;
}

if (UtilValidate.isNotEmpty(initializedCB))
{
   context.initializedCB=initializedCB;
}

compareDivSeqList = FastList.newInstance();
tmpDir = FileUtil.getFile("runtime/tmp");
uploadedFile = new File(tmpDir, context.uploadedFileName);

XmlFileFromPath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("osafeAdmin.properties", "osafe-deployment-uiSequence-xml-file"), context);
XmlFileToPath =  uploadedFile.getAbsolutePath();
divSeqFromList =  UtilMisc.sortMaps(OsafeManageXml.getListMapsFromXmlFile(XmlFileFromPath, "property"), UtilMisc.toList("key"));
divSeqToList =  UtilMisc.sortMaps(OsafeManageXml.getListMapsFromXmlFile(XmlFileToPath, "property"), UtilMisc.toList("key"));
if(UtilValidate.isNotEmpty(preRetrieved) && preRetrieved != "N") {
    //type  1 searchKeysNotInYourLabelFile |  2 searchKeysNotInUploadedFile  |  3  searchKeysInBothFile
    for (fromMapEntry in divSeqFromList) {
        toMapEntry = OsafeManageXml.findByKeyAndScreenFromListMaps(divSeqToList, "key", fromMapEntry.key, "screen", fromMapEntry.screen);
        if (UtilValidate.isNotEmpty(toMapEntry)) {
            if (searchKeysInBothFile.equals("Y")) {
                compareDivSeqList.add(UtilMisc.toMap("key", fromMapEntry.key, "screen",fromMapEntry.screen,  "fromMap", fromMapEntry, "toMap", toMapEntry, "type", "3"));
            }
            divSeqToList.remove(toMapEntry);
        }
        else {
            if (initial || searchKeysNotInUploadedFile.equals("Y")) {
                compareDivSeqList.add(UtilMisc.toMap("key", fromMapEntry.key,"screen",fromMapEntry.screen, "fromMap", fromMapEntry, "toMap", toMapEntry, "type", "1"));
            }
        }
    }
    for (toMapEntry in divSeqToList) {
        if (initial || searchKeysNotInYourLabelFile.equals("Y")) {
            compareDivSeqList.add(UtilMisc.toMap("key", toMapEntry.key,"screen",toMapEntry.screen, "fromMap", FastMap.newInstance(), "toMap", toMapEntry, "type", "2"));
        }
    }
}

compareDivSeqList = UtilMisc.sortMaps(compareDivSeqList, UtilMisc.toList("type"));
context.resultList = compareDivSeqList;
