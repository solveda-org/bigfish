package jobs;

import java.util.List;
import java.util.Map;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.FileUtil;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.string.FlexibleStringExpander;
import com.osafe.services.OsafeManageXml;
import org.ofbiz.base.util.Debug;

scheduledJobList = FastList.newInstance();
scheduledJobListFromXml = FastList.newInstance();
adminXmlPath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("osafeAdmin.properties", "admin-xml-defintion-directory"), context);
String scheduledJobListFilePath = FlexibleStringExpander.expandString(adminXmlPath + "/scheduled_job_names.xml", null);
// Get the admin xml file
if(UtilValidate.isNotEmpty(scheduledJobListFilePath))
{
	scheduledJobListFromXml =  (List<Map<Object, Object>>)OsafeManageXml.getListMapsFromXmlFile(scheduledJobListFilePath);
}

String scheduledJobListFilePathCustom = FlexibleStringExpander.expandString(adminXmlPath + "/scheduled_job_names_custom.xml", null);
if(UtilValidate.isNotEmpty(scheduledJobListFilePathCustom))
{
	scheduledJobCustomList =  (List<Map<Object, Object>>)OsafeManageXml.getListMapsFromXmlFile(scheduledJobListFilePathCustom);
	if(UtilValidate.isNotEmpty(scheduledJobCustomList))
	{
		scheduledJobListFromXml.addAll(scheduledJobCustomList);
	}
}

if(UtilValidate.isNotEmpty(scheduledJobListFromXml))
{
	for(Map scheduledJobListMap : scheduledJobListFromXml)
	{
		if (UtilValidate.isNotEmpty(scheduledJobListMap.sequenceNum))
		{
			if (UtilValidate.isInteger(scheduledJobListMap.sequenceNum))
			{
				scheduledJobListMap.sequenceNum = Integer.parseInt(scheduledJobListMap.sequenceNum);
				if(scheduledJobListMap.sequenceNum > 0)
				{
					scheduledJobList.add(scheduledJobListMap);
				}
			}
			else
			{
				scheduledJobListMap.sequenceNum = 0;
			}
		}
		else
		{
			scheduledJobListMap.sequenceNum = 0;
		}
	}
	scheduledJobList = UtilMisc.sortMaps(scheduledJobList, UtilMisc.toList("sequenceNum"));
}

selectedService = "";
if(UtilValidate.isNotEmpty(StringUtils.trimToEmpty(parameters.SERVICE_NAME)))
{
	selectedService = StringUtils.trimToEmpty(parameters.SERVICE_NAME);
}

context.scheduledJobList = scheduledJobList;
context.selectedService = selectedService;

