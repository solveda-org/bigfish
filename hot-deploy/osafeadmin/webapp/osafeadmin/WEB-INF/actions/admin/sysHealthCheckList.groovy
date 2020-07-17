package admin;

import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.base.util.UtilMisc;

//entity health check
systemHealthCheckList = FastList.newInstance();
sysHealthCheck = FastMap.newInstance();
sysHealthCheck.put("sysHealthCheckType", uiLabelMap.EntityHealthCheckLabel);
sysHealthCheck.put("sysHealthCheckDesc", uiLabelMap.EntityHealthCheckInfo);
sysHealthCheck.put("sysHealthCheckDetail", "entityRowSizeHealthCheckDetail");
systemHealthCheckList.add(sysHealthCheck);

//scheduled job health check
sysHealthCheck = FastMap.newInstance();
sysHealthCheck.put("sysHealthCheckType", uiLabelMap.ScheduledJobHealthCheckLabel);
sysHealthCheck.put("sysHealthCheckDesc", uiLabelMap.ScheduledJobHealthCheckInfo);
sysHealthCheck.put("sysHealthCheckDetail", "scheduledJobsHealthCheckDetail");
systemHealthCheckList.add(sysHealthCheck);

//logging parameters health check
sysHealthCheck = FastMap.newInstance();
sysHealthCheck.put("sysHealthCheckType", uiLabelMap.LoggingParamsHealthCheckLabel);
sysHealthCheck.put("sysHealthCheckDesc", uiLabelMap.LoggingParamsHealthCheckInfo);
sysHealthCheck.put("sysHealthCheckDetail", "loggingParamsHealthCheckDetail");
systemHealthCheckList.add(sysHealthCheck);

context.systemHealthCheckList = UtilMisc.sortMaps(systemHealthCheckList, UtilMisc.toList("sysHealthCheckType"));