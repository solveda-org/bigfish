package jobs;

import org.ofbiz.base.util.UtilValidate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionBuilder;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.condition.EntityFieldValue;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.product.category.CategoryWorker;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import com.ibm.icu.util.Calendar;
import com.osafe.util.OsafeAdminUtil;
import com.osafe.util.Util;

import org.ofbiz.base.util.ObjectType;

// Paging variables
//viewIndex = Integer.valueOf(parameters.viewIndex  ?: 1);
//viewSize = Integer.valueOf(parameters.viewSize ?: UtilProperties.getPropertyValue("osafeAdmin", "default-view-size"));
//context.viewSize=viewSize;
//context.viewIndex=viewIndex;

//input
String srchJobName = StringUtils.trimToEmpty(parameters.srchJobName);
srchRunDateFrom = StringUtils.trimToEmpty(parameters.srchRunDateFrom);
srchRunDateTo = StringUtils.trimToEmpty(parameters.srchRunDateTo);
srchServiceName = StringUtils.trimToEmpty(parameters.srchServiceName);
srchStartDateFrom = StringUtils.trimToEmpty(parameters.srchStartDateFrom);
srchStartDateTo = StringUtils.trimToEmpty(parameters.srchStartDateTo);
srchJobId = StringUtils.trimToEmpty(parameters.srchJobId);
srchEndDateFrom = StringUtils.trimToEmpty(parameters.srchEndDateFrom);
srchEndDateTo = StringUtils.trimToEmpty(parameters.srchEndDateTo);
//checkboxes
srchAll=StringUtils.trimToEmpty(parameters.srchall);
srchCanceled=StringUtils.trimToEmpty(parameters.srchCanceled);
srchCrashed=StringUtils.trimToEmpty(parameters.srchCrashed);
srchFailed=StringUtils.trimToEmpty(parameters.srchFailed);
srchFinished=StringUtils.trimToEmpty(parameters.srchFinished);
srchPending=StringUtils.trimToEmpty(parameters.srchPending);
srchQueued=StringUtils.trimToEmpty(parameters.srchQueued);
srchRunning=StringUtils.trimToEmpty(parameters.srchRunning);
List infoMsgList = FastList.newInstance();
Boolean isValidDate = true;
context.srchAll=srchAll;
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

nowTs = UtilDateTime.nowTimestamp();
session = context.session;
productStore = ProductStoreWorker.getProductStore(request);

Map<String, Object> svcCtx = FastMap.newInstance();
userLogin = session.getAttribute("userLogin");
context.userLogin=userLogin;

exprs = FastList.newInstance();
mainCond=null;
prodCond=null;
statusCond=null;
endDateCond=null;

// Job Id
if(UtilValidate.isNotEmpty(srchJobId))
{
    jobId=srchJobId;
    findJobCond = EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("jobId")), EntityOperator.EQUALS, srchJobId.toUpperCase());
    jobs = delegator.findList("JobSandbox",findJobCond, null, null, null, false);
    if (UtilValidate.isNotEmpty(jobs)) 
    {
        jobs=EntityUtil.getFirst(jobs);
        jobId=jobs.jobId;
    }

    exprs.add(EntityCondition.makeCondition("jobId", EntityOperator.EQUALS, jobId));
    context.srchJobId=srchJobId
}

// Job Name
if(UtilValidate.isNotEmpty(srchJobName))
{
    jobName=srchJobName;
    exprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("jobName"), EntityOperator.LIKE, "%" + jobName.toUpperCase() + "%"));
    context.srchJobName=srchJobName;
}

// Service Name
if(UtilValidate.isNotEmpty(srchServiceName))
{
    serviceName=srchServiceName;
    exprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("serviceName"), EntityOperator.LIKE, "%" + serviceName.toUpperCase() + "%"));
    context.srchServiceName=srchServiceName;
}
//handle all the dates 
if (UtilValidate.isNotEmpty(srchRunDateFrom))
{
    if (OsafeAdminUtil.isDateTime(srchRunDateFrom, entryDateTimeFormat))
    {
        try {
              srchRunDateFrom = ObjectType.simpleTypeConvert(srchRunDateFrom, "Timestamp", entryDateTimeFormat, locale);
        } catch (Exception e) {
            errMsg = "Parse Exception srchRunDateFrom: " + srchRunDateFrom;
            Debug.logError(e, errMsg, "scheduledJobList.groovy");
        }
    }
    else
    {
        infoMsgList.add(UtilProperties.getMessage("OSafeAdminUiLabels","InvalidFromDateInfo",locale));
    }
    runDateFrom=srchRunDateFrom;
    exprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("runTime"), EntityOperator.GREATER_THAN_EQUAL_TO, runDateFrom));
    context.srchRunDateFrom=srchRunDateFrom;
}
if (UtilValidate.isNotEmpty(srchRunDateTo))
{
    if (OsafeAdminUtil.isDateTime(srchRunDateTo, entryDateTimeFormat))
    {
        try {
              srchRunDateTo = ObjectType.simpleTypeConvert(srchRunDateTo, "Timestamp", entryDateTimeFormat, locale);
        } catch (Exception e) {
            errMsg = "Parse Exception srchRunDateTo: " + srchRunDateTo;
            Debug.logError(e, errMsg, "scheduledJobList.groovy");
        }
    }
    else
    {
        infoMsgList.add(UtilProperties.getMessage("OSafeAdminUiLabels","InvalidToDateInfo",locale));
    }
    runDateTo=srchRunDateTo;
    runDateTo=runDateTo.next();//includes the "To" date
    exprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("runTime"), EntityOperator.LESS_THAN_EQUAL_TO, runDateTo));
    context.srchRunDateTo=srchRunDateTo;
}
if (UtilValidate.isNotEmpty(srchStartDateFrom))
{
    if (OsafeAdminUtil.isDateTime(srchStartDateFrom, entryDateTimeFormat))
    {
        try {
              srchStartDateFrom = ObjectType.simpleTypeConvert(srchStartDateFrom, "Timestamp", entryDateTimeFormat, locale);
        } catch (Exception e) {
            errMsg = "Parse Exception srchStartDateFrom: " + srchStartDateFrom;
            Debug.logError(e, errMsg, "scheduledJobList.groovy");
        }
    }
    else
    {
        infoMsgList.add(UtilProperties.getMessage("OSafeAdminUiLabels","InvalidFromDateInfo",locale));
    }
    startDateFrom=srchStartDateFrom;
    exprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("startDateTime"), EntityOperator.GREATER_THAN_EQUAL_TO, startDateFrom));
    context.srchStartDateFrom=srchStartDateFrom;
}
if (UtilValidate.isNotEmpty(srchStartDateTo))
{
    if (OsafeAdminUtil.isDateTime(srchStartDateTo, entryDateTimeFormat))
    {
        try {
              srchStartDateTo = ObjectType.simpleTypeConvert(srchStartDateTo, "Timestamp", entryDateTimeFormat, locale);
        } catch (Exception e) {
            errMsg = "Parse Exception srchStartDateTo: " + srchStartDateTo;
            Debug.logError(e, errMsg, "scheduledJobList.groovy");
        }
    }
    else
    {
        infoMsgList.add(UtilProperties.getMessage("OSafeAdminUiLabels","InvalidToDateInfo",locale));
    }
    startDateTo=srchStartDateTo;
    startDateTo=startDateTo.next();//includes the "To" date
    exprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("startDateTime"), EntityOperator.LESS_THAN_EQUAL_TO, startDateTo));
    //exprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("startDateTime"), EntityOperator.EQUALS, startDateTo));
    context.srchStartDateTo=srchStartDateTo;
}

if (UtilValidate.isNotEmpty(srchEndDateFrom))
{
    if (OsafeAdminUtil.isDateTime(srchEndDateFrom, entryDateTimeFormat))
    {
        try {
              srchEndDateFrom = ObjectType.simpleTypeConvert(srchEndDateFrom, "Timestamp", entryDateTimeFormat, locale);
        } catch (Exception e) {
            errMsg = "Parse Exception srchEndDateFrom: " + srchEndDateFrom;
            Debug.logError(e, errMsg, "scheduledJobList.groovy");
        }
    }
    else
    {
        infoMsgList.add(UtilProperties.getMessage("OSafeAdminUiLabels","InvalidFromDateInfo",locale));
    }
    endDateFrom=srchEndDateFrom;
    exprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("finishDateTime"), EntityOperator.GREATER_THAN_EQUAL_TO, endDateFrom));
    context.srchEndDateFrom=srchEndDateFrom;
}
if (UtilValidate.isNotEmpty(srchEndDateTo))
{
    if (OsafeAdminUtil.isDateTime(srchEndDateTo, entryDateTimeFormat))
    {
        try {
              srchEndDateTo = ObjectType.simpleTypeConvert(srchEndDateTo, "Timestamp", entryDateTimeFormat, locale);
        } catch (Exception e) {
            errMsg = "Parse Exception srchEndDateTo: " + srchEndDateTo;
            Debug.logError(e, errMsg, "scheduledJobList.groovy");
        }
    }
    else
    {
        infoMsgList.add(UtilProperties.getMessage("OSafeAdminUiLabels","InvalidToDateInfo",locale));
    }
    endDateTo=srchEndDateTo;
    endDateTo=endDateTo.next();//includes the "To" date
    exprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("finishDateTime"), EntityOperator.LESS_THAN_EQUAL_TO, endDateTo));
    context.srchEndDateTo=srchEndDateTo;
}
if (UtilValidate.isNotEmpty(infoMsgList)) 
{
    isValidDate = false;
    request.setAttribute("_INFO_MESSAGE_LIST_", infoMsgList);
}

if (UtilValidate.isNotEmpty(exprs)) 
{
    prodCond=EntityCondition.makeCondition(exprs, EntityOperator.AND);
    mainCond=prodCond;
}

// Review Status with CheckBox implementation
statusExpr= FastList.newInstance();
if (UtilValidate.isNotEmpty(srchCanceled))
{
    statusExpr.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "SERVICE_CANCELLED"));
   context.srchCanceled=srchCanceled
}
if (UtilValidate.isNotEmpty(srchCrashed))
{
    statusExpr.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "SERVICE_CRASHED"));
   context.srchCrashed=srchCrashed
}
if (UtilValidate.isNotEmpty(srchFailed))
{
    statusExpr.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "SERVICE_FAILED"));
   context.srchFailed=srchFailed
}
if (UtilValidate.isNotEmpty(srchFinished))
{
    statusExpr.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "SERVICE_FINISHED"));
   context.srchFinished=srchFinished
}
if (UtilValidate.isNotEmpty(srchPending))
{
    statusExpr.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "SERVICE_PENDING"));
   context.srchPending=srchPending
}
if(UtilValidate.isNotEmpty(srchQueued))
{
    statusExpr.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "SERVICE_QUEUED"));
   context.srchQueued=srchQueued
}
if (UtilValidate.isNotEmpty(srchRunning))
{
    statusExpr.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "SERVICE_RUNNING"));
   context.srchRunning=srchRunning
}


if (UtilValidate.isNotEmpty(statusExpr))
{
   statusCond = EntityCondition.makeCondition(statusExpr, EntityOperator.OR);
   if (UtilValidate.isNotEmpty(prodCond)) {
      mainCond = EntityCondition.makeCondition([prodCond, statusCond], EntityOperator.AND);
   }
   else
   {
     mainCond=statusCond;
   }
}

//orderBy = ["runTime"];

scheduledJobs=FastList.newInstance();
if (UtilValidate.isNotEmpty(preRetrieved) && preRetrieved != "N" && isValidDate) 
{
    scheduledJobs = delegator.findList("JobSandbox",mainCond, null, ["runTime DESC"], null, false); 
}
 

 
pagingListSize=scheduledJobs.size();
context.pagingListSize=pagingListSize;
context.pagingList = scheduledJobs;

