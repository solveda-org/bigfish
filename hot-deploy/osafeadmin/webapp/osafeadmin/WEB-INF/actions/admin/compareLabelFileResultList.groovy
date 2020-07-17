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

compareLabelList = FastList.newInstance();
tmpDir = FileUtil.getFile("runtime/tmp");
uploadedFile = new File(tmpDir, context.uploadedFileName);

XmlFileFromPath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("osafeAdmin.properties", "ecommerce-deployment-UiLabel-xml-file"), context);
XmlFileToPath =  uploadedFile.getAbsolutePath();
uiLabelsFromList =  UtilMisc.sortMaps(OsafeManageXml.getListMapsFromXmlFile(XmlFileFromPath, "property"), UtilMisc.toList("key"));
uiLabelsToList =  UtilMisc.sortMaps(OsafeManageXml.getListMapsFromXmlFile(XmlFileToPath, "property"), UtilMisc.toList("key"));

if(UtilValidate.isNotEmpty(preRetrieved) && preRetrieved != "N") {
    //type  1 searchKeysNotInYourLabelFile |  2 searchKeysNotInUploadedFile  |  3  searchKeysInBothFile
    for (fromMapEntry in uiLabelsFromList) {
        toMapEntry = OsafeManageXml.findByKeyFromListMaps(uiLabelsToList, "key", fromMapEntry.key);
        if (UtilValidate.isNotEmpty(toMapEntry)) {
            if (searchKeysInBothFile.equals("Y")) {
                compareLabelList.add(UtilMisc.toMap("key", fromMapEntry.key, "fromMap", fromMapEntry, "toMap", toMapEntry, "type", "3"));
            }
            uiLabelsToList.remove(toMapEntry);
        } else {
            if (initial || searchKeysNotInUploadedFile.equals("Y")) {
                compareLabelList.add(UtilMisc.toMap("key", fromMapEntry.key, "fromMap", fromMapEntry, "toMap", toMapEntry, "type", "1"));
            }
        }
    }
    for (toMapEntry in uiLabelsToList) {
        if (initial || searchKeysNotInYourLabelFile.equals("Y")) {
            compareLabelList.add(UtilMisc.toMap("key", toMapEntry.key, "fromMap", FastMap.newInstance(), "toMap", toMapEntry, "type", "2"));
        }
    }
}

compareLabelList = UtilMisc.sortMaps(compareLabelList, UtilMisc.toList("type"));
context.resultList = compareLabelList;
