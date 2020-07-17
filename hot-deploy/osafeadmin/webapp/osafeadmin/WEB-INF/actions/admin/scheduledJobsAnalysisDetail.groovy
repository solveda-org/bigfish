package admin;

import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.entity.*
import org.ofbiz.entity.condition.*
import org.ofbiz.entity.util.EntityFindOptions
import java.sql.Timestamp;

jobStatusAnalysisList = FastList.newInstance();
jobsCancelledList = FastList.newInstance();
jobsCrashedList = FastList.newInstance();
jobsFailedList = FastList.newInstance();
jobsFinishedExclList = FastList.newInstance();
jobsFinishedWithinList = FastList.newInstance();
jobsPendingList = FastList.newInstance();
jobsQueuedList = FastList.newInstance();
jobsFinishedLateList = FastList.newInstance();
jobsRunningList = FastList.newInstance();

Timestamp now = UtilDateTime.nowTimestamp(); 
scheduledJobs = delegator.findList("JobSandbox",null, null, null, null, false);
for(Map scheduledJobMap : scheduledJobs) 
{
        if (UtilValidate.isNotEmpty(scheduledJobMap.statusId)) 
        {
        	statusId = scheduledJobMap.statusId;
        	if (statusId.equalsIgnoreCase("SERVICE_CANCELLED")) 
        	{
                jobsCancelledList.add(statusId);
            } 
        	if (statusId.equalsIgnoreCase("SERVICE_CRASHED")) 
        	{
                jobsCrashedList.add(statusId);
            } 
        	if (statusId.equalsIgnoreCase("SERVICE_FAILED")) 
        	{
                jobsFailedList.add(statusId);
            } 
        	if (statusId.equalsIgnoreCase("SERVICE_FINISHED")) 
        	{
                jobFinishDateTime = scheduledJobMap.finishDateTime;
                interval = UtilDateTime.getIntervalInDays(jobFinishDateTime,now);
                if(interval > 30)
                {
                    jobsFinishedExclList.add(statusId);
                }
                else
                {
                    jobsFinishedWithinList.add(statusId);
                }
            } 
        	if (statusId.equalsIgnoreCase("SERVICE_PENDING")) 
        	{
                jobsPendingList.add(statusId);
            } 
        	if (statusId.equalsIgnoreCase("SERVICE_QUEUED")) 
        	{
                jobsQueuedList.add(statusId);
            }
        	if (statusId.equalsIgnoreCase("SERVICE_RUNNING")) 
        	{
                jobsRunningList.add(statusId);
            } 
        } else 
        {
        	continue;
        }
    }
jobStatusAnalysis = FastMap.newInstance();
jobStatusAnalysis.put("status",uiLabelMap.CancelledLabel);
jobStatusAnalysis.put("statusId","SERVICE_CANCELLED");
jobStatusAnalysis.put("rowCount",jobsCancelledList.size());
jobStatusAnalysisList.add(jobStatusAnalysis);

jobStatusAnalysis = FastMap.newInstance();
jobStatusAnalysis.put("status",uiLabelMap.CrashedLabel);
jobStatusAnalysis.put("statusId","SERVICE_CRASHED");
jobStatusAnalysis.put("rowCount",jobsCrashedList.size());
jobStatusAnalysisList.add(jobStatusAnalysis);

jobStatusAnalysis = FastMap.newInstance();
jobStatusAnalysis.put("status",uiLabelMap.FailedLabel);
jobStatusAnalysis.put("statusId","SERVICE_FAILED");
jobStatusAnalysis.put("rowCount",jobsFailedList.size());
jobStatusAnalysisList.add(jobStatusAnalysis);

jobStatusAnalysis = FastMap.newInstance();
jobStatusAnalysis.put("status",uiLabelMap.FinishedExclLabel);
jobStatusAnalysis.put("statusId","SERVICE_FINISHED_IN_THIRTY");
jobStatusAnalysis.put("rowCount",jobsFinishedExclList.size());
jobStatusAnalysisList.add(jobStatusAnalysis);

jobStatusAnalysis = FastMap.newInstance();
jobStatusAnalysis.put("status",uiLabelMap.FinishedWithinLabel);
jobStatusAnalysis.put("statusId","SERVICE_FINISHED_OUT_THIRTY");
jobStatusAnalysis.put("rowCount",jobsFinishedWithinList.size());
jobStatusAnalysisList.add(jobStatusAnalysis);

jobStatusAnalysis = FastMap.newInstance();
jobStatusAnalysis.put("status",uiLabelMap.PendingLabel);
jobStatusAnalysis.put("statusId","SERVICE_PENDING");
jobStatusAnalysis.put("rowCount",jobsPendingList.size());
jobStatusAnalysisList.add(jobStatusAnalysis);

jobStatusAnalysis = FastMap.newInstance();
jobStatusAnalysis.put("status",uiLabelMap.QueuedLabel);
jobStatusAnalysis.put("statusId","SERVICE_QUEUED");
jobStatusAnalysis.put("rowCount",jobsQueuedList.size());
jobStatusAnalysisList.add(jobStatusAnalysis);

jobStatusAnalysis = FastMap.newInstance();
jobStatusAnalysis.put("status",uiLabelMap.RunningLabel);
jobStatusAnalysis.put("statusId","SERVICE_RUNNING");
jobStatusAnalysis.put("rowCount",jobsRunningList.size());
jobStatusAnalysisList.add(jobStatusAnalysis);

context.jobStatusAnalysisList = jobStatusAnalysisList;
