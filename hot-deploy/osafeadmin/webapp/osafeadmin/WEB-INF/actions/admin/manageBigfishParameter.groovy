package admin;

import javolution.util.FastList
import javolution.util.FastMap

import org.apache.commons.lang.StringUtils
import org.ofbiz.base.util.FileUtil
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilProperties
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.base.util.string.FlexibleStringExpander

import com.osafe.services.OsafeManageXml

currentMode=context.mode;

ecommerceConfigPath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("osafeAdmin.properties", "ecommerce-config-path"), context);

parameterFileList = FastList.newInstance();
bigfishParamList = FastList.newInstance();
parameterFileName = "";

// Get the all ecommerce config file
fileList = FileUtil.findFiles("xml", ecommerceConfigPath, null, null);
if(UtilValidate.isNotEmpty(fileList))
{
    for (parameterFile in fileList)
    {
        if (parameterFile.getName().startsWith("Parameters_") || parameterFile.getName().startsWith("parameters_"))
        {
            infoMap = FastMap.newInstance();
            infoMap.put("file", parameterFile);
            infoMap.put("fileName", parameterFile.getName());
            infoMap.put("fileNameUpperCase", parameterFile.getName().toUpperCase());
            parameterFileList.add(infoMap);
        }
    }
    parameterFileList = UtilMisc.sortMaps(parameterFileList, UtilMisc.toList("fileNameUpperCase"));
    parameterFileName = parameterFileList.get(0).get("fileName");
}

// search bigfish parameter according to passed file name
searchFileName = StringUtils.trimToEmpty(parameters.parameterFileName);
if (UtilValidate.isNotEmpty(searchFileName))
{
    parameterFileName = searchFileName;
    for (parameterFile in parameterFileList)
    {
        if (parameterFile.get("fileName").equalsIgnoreCase(searchFileName))
        {
			bigfishParamList =  OsafeManageXml.getListMapsFromXmlFile(parameterFile.get("file").getAbsolutePath());
			break;
        }
    }
	if("add".equalsIgnoreCase(currentMode))
	{
		emptyAddMap = FastMap.newInstance();
		bigfishParamList.add(emptyAddMap);
	}
}

context.parameterFileList = parameterFileList; 
context.parameterFileName = parameterFileName; 
context.bigfishParamList = bigfishParamList; 
