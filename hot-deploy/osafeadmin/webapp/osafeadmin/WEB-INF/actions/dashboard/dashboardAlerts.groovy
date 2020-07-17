package dashboard;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import java.util.*;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.GenericValue;


//ALERTS
displayAlerts = "N";		
//JobSandbox warning
alertMap = FastMap.newInstance();
alertMapList = FastList.newInstance();
entityRowCount = delegator.findCountByCondition("JobSandbox",null, null, null);
parameters_alerts = UtilProperties.getResourceBundleMap("parameters_alerts.xml", locale);
long alertJobSandboxRowsLong = 100000;
alertJobSandboxRows = parameters_alerts.ALERT_JOB_SANDBOX_ROWS;
if(UtilValidate.isNotEmpty(alertJobSandboxRows) && UtilValidate.isInteger(alertJobSandboxRows))
{
	alertJobSandboxRowsLong = alertJobSandboxRows.toLong();
}
if(entityRowCount > alertJobSandboxRowsLong)
{
	jobSandBoxAlertInfo = UtilProperties.getMessage("OSafeAdminUiLabels","JobSandBoxAlertInfo",["entityRowCount" : entityRowCount], locale )
	alertMap.put("infoMessage", jobSandBoxAlertInfo);
	alertMap.put("actionPath", uiLabelMap.JobSandBoxAlertActionPathInfo);
	alertMap.put("action", "scheduledJobsAnalysis");
	alertMap.put("actionLabel", uiLabelMap.ScheduledJobHealthCheckLabel);
	alertMap.put("actionInfo", uiLabelMap.JobSandBoxAlertActionInfo);
	alertMap.put("iconClass", "cautionIcon");
	alertMap.put("sequenceNum", 1);
	displayAlerts = "Y";
}
else
{
	alertMap.put("sequenceNum", 0);
}

alertMapList.add(alertMap);

//alert info
alertMap = FastMap.newInstance();
alertMap.put("infoMessage", uiLabelMap.AlertInfo);
alertMap.put("actionPath", uiLabelMap.AlertActionPathInfo);
alertMap.put("action", "adminToolDetail?detailScreen=manageBigfishParameter");
alertMap.put("actionLabel", uiLabelMap.BigfishParameterLabel);
alertMap.put("actionInfo", uiLabelMap.AlertActionInfo);
alertMap.put("iconClass", "informationDetailIcon");
alertMap.put("sequenceNum", 2);
alertMapList.add(alertMap);

context.alertList = UtilMisc.sortMaps(alertMapList, UtilMisc.toList("sequenceNum"));
context.displayAlerts = displayAlerts;
//ALERTS



