package admin;

import com.osafe.services.OsafeManageXml;
import java.util.Map;
import java.util.List;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.base.util.*
import org.ofbiz.base.util.string.*;
import org.apache.commons.lang.StringUtils;
import org.ofbiz.entity.condition.EntityCondition;

String searchParamKeysNotInUploadedFile = StringUtils.trimToEmpty(parameters.searchParamKeysNotInUploadedFile);
String searchParamKeysNotInYourDatabase = StringUtils.trimToEmpty(parameters.searchParamKeysNotInYourDatabase);
String searchParamKeysInBoth = StringUtils.trimToEmpty(parameters.searchParamKeysInBoth);
boolean initial = false;
if (searchParamKeysNotInUploadedFile.equals("") && searchParamKeysNotInYourDatabase.equals("") && searchParamKeysInBoth.equals("")) 
{
    initial = true;
}
if(UtilValidate.isNotEmpty(searchParamKeysNotInUploadedFile))
{
    context.searchParamKeysNotInUploadedFile = searchParamKeysNotInUploadedFile;
}
if(UtilValidate.isNotEmpty(searchParamKeysNotInYourDatabase))
{
    context.searchParamKeysNotInYourDatabase = searchParamKeysNotInYourDatabase;
}
if(UtilValidate.isNotEmpty(searchParamKeysInBoth))
{
    context.searchParamKeysInBoth = searchParamKeysInBoth;
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

compareParamList = FastList.newInstance();
tmpDir = FileUtil.getFile("runtime/tmp");
uploadedFile = new File(tmpDir, context.uploadedFileName);

//List of Existing Params
productStoreId = globalContext.productStoreId;
systemParamList = delegator.findList("XProductStoreParm", EntityCondition.makeCondition([productStoreId : productStoreId]), null, null, null, false);
XmlFileToPath =  uploadedFile.getAbsolutePath();

//List of UploadedFile Params
paramToList =  UtilMisc.sortMaps(OsafeManageXml.getListMapsFromXmlFile(XmlFileToPath, "XProductStoreParm"), UtilMisc.toList("parmKey"));
if(UtilValidate.isNotEmpty(preRetrieved) && preRetrieved != "N") 
{
    //type  1 searchParamKeysNotInYourDatabase |  2 searchParamKeysNotInUploadedFile  |  3  searchParamKeysInBoth
    for (fromMapEntry in systemParamList) 
    {
        toMapEntry = OsafeManageXml.findByKeyFromListMaps(paramToList, "parmKey", fromMapEntry.parmKey);
        if (UtilValidate.isNotEmpty(toMapEntry)) 
        {
            if (searchParamKeysInBoth.equals("Y")) 
            {
                compareParamList.add(UtilMisc.toMap("key", fromMapEntry.parmKey, "fromMap", fromMapEntry, "toMap", toMapEntry, "type", "3"));
            }
            paramToList.remove(toMapEntry);
        } 
        else 
        {
            if (initial || searchParamKeysNotInUploadedFile.equals("Y")) 
            {
                compareParamList.add(UtilMisc.toMap("key", fromMapEntry.parmKey, "fromMap", fromMapEntry, "toMap", toMapEntry, "type", "1"));
            }
        }
    }
    for (toMapEntry in paramToList) 
    {
        if (initial || searchParamKeysNotInYourDatabase.equals("Y")) 
        {
            compareParamList.add(UtilMisc.toMap("key", toMapEntry.parmKey, "fromMap", FastMap.newInstance(), "toMap", toMapEntry, "type", "2"));
        }
    }
}

compareParamList = UtilMisc.sortMaps(compareParamList, UtilMisc.toList("type"));
context.resultList = compareParamList;
