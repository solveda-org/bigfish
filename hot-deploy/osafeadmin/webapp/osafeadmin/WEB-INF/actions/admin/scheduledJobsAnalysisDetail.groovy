package admin;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.model.DynamicViewEntity;
import org.ofbiz.entity.util.EntityFindOptions;

jobStatusAnalysisList = FastList.newInstance();

now = UtilDateTime.nowTimestamp();
thirtyDaysAgoTime = UtilDateTime.addDaysToTimestamp(now, 30 * -1);

// dynamic view entity
dynamicView = new DynamicViewEntity();
dynamicView.addMemberEntity("JSB", "JobSandbox");
dynamicView.addAlias("JSB", "statusId");
dynamicView.addAlias("JSB", "serviceName", "serviceName", null, null, Boolean.TRUE, null);
dynamicView.addAlias("JSB", "finishDateTime");
dynamicView.addAlias("JSB", "count", "serviceName", null, null, null, "count");

orderBy = UtilMisc.toList("serviceName");

// list of fields to select
fieldsToSelect = FastList.newInstance();
fieldsToSelect.add("serviceName");
fieldsToSelect.add("count");

//find option
findOpts = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, false);


//for finished service
ecl = EntityCondition.makeCondition([
            EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "SERVICE_FINISHED"),
            EntityCondition.makeCondition("finishDateTime", EntityOperator.LESS_THAN, thirtyDaysAgoTime)
        ],
        EntityOperator.AND);
eli = delegator.findListIteratorByCondition(dynamicView, ecl, null, fieldsToSelect, orderBy, findOpts);
jobsFinishedExclList = eli.getCompleteList();
if (eli != null)
{
    try
    {
        eli.close();
    }
    catch (GenericEntityException e) {}
}

//for finished service
ecl = EntityCondition.makeCondition([
            EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "SERVICE_FINISHED"),
            EntityCondition.makeCondition("finishDateTime", EntityOperator.GREATER_THAN_EQUAL_TO, thirtyDaysAgoTime)
        ],
        EntityOperator.AND);
eli = delegator.findListIteratorByCondition(dynamicView, ecl, null, fieldsToSelect, orderBy, findOpts);
jobsFinishedWithinList = eli.getCompleteList();
if (eli != null)
{
    try
    {
        eli.close();
    }
    catch (GenericEntityException e) {}
}

//for cancelled service
ecl = EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "SERVICE_CANCELLED");
eli = delegator.findListIteratorByCondition(dynamicView, ecl, null, fieldsToSelect, orderBy, findOpts);
jobsCancelledList = eli.getCompleteList();
if (eli != null)
{
    try
    {
        eli.close();
    }
    catch (GenericEntityException e) {}
}

//for crashed service
ecl = EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "SERVICE_CRASHED");
eli = delegator.findListIteratorByCondition(dynamicView, ecl, null, fieldsToSelect, orderBy, findOpts);
jobsCrashedList = eli.getCompleteList();
if (eli != null)
{
    try
    {
        eli.close();
    }
    catch (GenericEntityException e) {}
}

//for failed service
ecl = EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "SERVICE_FAILED");
eli = delegator.findListIteratorByCondition(dynamicView, ecl, null, fieldsToSelect, orderBy, findOpts);
jobsFailedList = eli.getCompleteList();
if (eli != null)
{
    try
    {
        eli.close();
    }
    catch (GenericEntityException e) {}
}

//for pending service
ecl = EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "SERVICE_PENDING");
eli = delegator.findListIteratorByCondition(dynamicView, ecl, null, fieldsToSelect, orderBy, findOpts);
jobsPendingList = eli.getCompleteList();
if (eli != null)
{
    try
    {
        eli.close();
    }
    catch (GenericEntityException e) {}
}

//for queued service
ecl = EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "SERVICE_QUEUED");
eli = delegator.findListIteratorByCondition(dynamicView, ecl, null, fieldsToSelect, orderBy, findOpts);
jobsQueuedList = eli.getCompleteList();
if (eli != null)
{
    try
    {
        eli.close();
    }
    catch (GenericEntityException e) {}
}

//for running service
ecl = EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "SERVICE_RUNNING");
eli = delegator.findListIteratorByCondition(dynamicView, ecl, null, fieldsToSelect, orderBy, findOpts);
jobsRunningList = eli.getCompleteList();
if (eli != null)
{
    try
    {
        eli.close();
    }
    catch (GenericEntityException e) {}
}

jobStatusAnalysis = FastMap.newInstance();
jobStatusAnalysis.put("status",uiLabelMap.FinishedExclLabel);
jobStatusAnalysis.put("statusId", "SERVICE_FINISHED");
jobStatusAnalysis.put("result", jobsFinishedExclList);
rowCount = BigDecimal.ZERO;
listIter = UtilMisc.toIterator(jobsFinishedExclList);
while (UtilValidate.isNotEmpty(listIter) && listIter.hasNext())
{
    resultRow = listIter.next();
    rowCount = rowCount.add(new BigDecimal(resultRow.getString("count")) );
}
jobStatusAnalysis.put("rowCount",rowCount);
jobStatusAnalysisList.add(jobStatusAnalysis);

jobStatusAnalysis = FastMap.newInstance();
jobStatusAnalysis.put("status",uiLabelMap.FinishedWithinLabel);
jobStatusAnalysis.put("statusId", "SERVICE_FINISHED");
jobStatusAnalysis.put("result", jobsFinishedWithinList);
rowCount = BigDecimal.ZERO;
listIter = UtilMisc.toIterator(jobsFinishedWithinList);
while (UtilValidate.isNotEmpty(listIter) && listIter.hasNext())
{
    resultRow = listIter.next();
    rowCount = rowCount.add(new BigDecimal(resultRow.getString("count")) );
}
jobStatusAnalysis.put("rowCount",rowCount);
jobStatusAnalysisList.add(jobStatusAnalysis);

jobStatusAnalysis = FastMap.newInstance();
jobStatusAnalysis.put("status",uiLabelMap.FailedLabel);
jobStatusAnalysis.put("statusId", "SERVICE_FAILED");
jobStatusAnalysis.put("result",jobsFailedList);
rowCount = BigDecimal.ZERO;
listIter = UtilMisc.toIterator(jobsFailedList);
while (UtilValidate.isNotEmpty(listIter) && listIter.hasNext())
{
    resultRow = listIter.next();
    rowCount = rowCount.add(new BigDecimal(resultRow.getString("count")) );
}
jobStatusAnalysis.put("rowCount",rowCount);
jobStatusAnalysisList.add(jobStatusAnalysis);

jobStatusAnalysis = FastMap.newInstance();
jobStatusAnalysis.put("status",uiLabelMap.CrashedLabel);
jobStatusAnalysis.put("statusId", "SERVICE_CRASHED");
jobStatusAnalysis.put("result", jobsCrashedList);
rowCount = BigDecimal.ZERO;
listIter = UtilMisc.toIterator(jobsCrashedList);
while (UtilValidate.isNotEmpty(listIter) && listIter.hasNext())
{
    resultRow = listIter.next();
    rowCount = rowCount.add(new BigDecimal(resultRow.getString("count")) );
}
jobStatusAnalysis.put("rowCount",rowCount);
jobStatusAnalysisList.add(jobStatusAnalysis);

jobStatusAnalysis = FastMap.newInstance();
jobStatusAnalysis.put("status",uiLabelMap.QueuedLabel);
jobStatusAnalysis.put("statusId", "SERVICE_QUEUED");
jobStatusAnalysis.put("result",jobsQueuedList);
rowCount = BigDecimal.ZERO;
listIter = UtilMisc.toIterator(jobsQueuedList);
while (UtilValidate.isNotEmpty(listIter) && listIter.hasNext())
{
    resultRow = listIter.next();
    rowCount = rowCount.add(new BigDecimal(resultRow.getString("count")) );
}
jobStatusAnalysis.put("rowCount",rowCount);
jobStatusAnalysisList.add(jobStatusAnalysis);

jobStatusAnalysis = FastMap.newInstance();
jobStatusAnalysis.put("status",uiLabelMap.RunningLabel);
jobStatusAnalysis.put("statusId", "SERVICE_RUNNING");
jobStatusAnalysis.put("result",jobsRunningList);
rowCount = BigDecimal.ZERO;
listIter = UtilMisc.toIterator(jobsRunningList);
while (UtilValidate.isNotEmpty(listIter) && listIter.hasNext())
{
    resultRow = listIter.next();
    rowCount = rowCount.add(new BigDecimal(resultRow.getString("count")) );
}
jobStatusAnalysis.put("rowCount",rowCount);
jobStatusAnalysisList.add(jobStatusAnalysis);

jobStatusAnalysis = FastMap.newInstance();
jobStatusAnalysis.put("status",uiLabelMap.CancelledLabel);
jobStatusAnalysis.put("statusId", "SERVICE_CANCELLED");
jobStatusAnalysis.put("result", jobsCancelledList);
rowCount = BigDecimal.ZERO;
listIter = UtilMisc.toIterator(jobsCancelledList);
while (UtilValidate.isNotEmpty(listIter) && listIter.hasNext())
{
    resultRow = listIter.next();
    rowCount = rowCount.add(new BigDecimal(resultRow.getString("count")) );
}
jobStatusAnalysis.put("rowCount",rowCount);
jobStatusAnalysisList.add(jobStatusAnalysis);

jobStatusAnalysis = FastMap.newInstance();
jobStatusAnalysis.put("status",uiLabelMap.PendingLabel);
jobStatusAnalysis.put("statusId", "SERVICE_PENDING");
jobStatusAnalysis.put("result",jobsPendingList);
rowCount = BigDecimal.ZERO;
listIter = UtilMisc.toIterator(jobsPendingList);
while (UtilValidate.isNotEmpty(listIter) && listIter.hasNext())
{
    resultRow = listIter.next();
    rowCount = rowCount.add(new BigDecimal(resultRow.getString("count")) );
}
jobStatusAnalysis.put("rowCount",rowCount);
jobStatusAnalysisList.add(jobStatusAnalysis);

context.jobStatusAnalysisList = jobStatusAnalysisList;
