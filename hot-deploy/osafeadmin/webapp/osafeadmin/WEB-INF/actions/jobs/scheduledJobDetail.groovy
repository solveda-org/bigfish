package jobs;

import java.util.List;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.apache.commons.lang.StringUtils;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.entity.model.DynamicViewEntity;
import org.ofbiz.entity.model.ModelKeyMap;
import org.ofbiz.entity.util.EntityFindOptions;
import com.osafe.util.OsafeAdminUtil;

productStoreId=globalContext.productStoreId;
jobId=parameters.jobId;

//get entity for schedJob
jobId = StringUtils.trimToEmpty(parameters.jobId);
context.jobId = jobId;
if (UtilValidate.isNotEmpty(jobId))
{
    schedJob = delegator.findByPrimaryKey("JobSandbox", [jobId : jobId]);
    context.schedJob = schedJob;
    
    //break up the timestamp into a date for display(date, time, AMPM)
    runDateTime = schedJob.runTime.toString();

    if (UtilValidate.isNotEmpty(schedJob.runTime))
    {
        runDate = OsafeAdminUtil.convertDateTimeFormat(schedJob.runTime, entryDateTimeFormat);
        runHour = UtilDateTime.getHour(UtilDateTime.toTimestamp(schedJob.runTime), timeZone, locale);
        runMinute = UtilDateTime.getMinute(UtilDateTime.toTimestamp(schedJob.runTime), timeZone, locale);
        runTimeAMPM = ((runHour/12) < 1)?1:2;// 1 for AM and 2 for PM
        runHour = ((runHour%12) == 0)?12:(runHour%12);
        context.runDate = runDate;
        context.runHour = Integer.toString(runHour);
        context.runMinute = Integer.toString(runMinute);
        context.runTimeAMPM = Integer.toString(runTimeAMPM);
    }
    //get recurrence info
    if (UtilValidate.isNotEmpty(schedJob.recurrenceInfoId))
    {
        recurrenceInfo = delegator.findByAnd("RecurrenceInfo", [recurrenceInfoId : schedJob.recurrenceInfoId]);
        recurrenceInfo = EntityUtil.getFirst(recurrenceInfo);
        context.recurrenceInfo = recurrenceInfo;
        
        //get recurrence rule
        if (UtilValidate.isNotEmpty(recurrenceInfo.recurrenceRuleId))
        {
            recurrenceRule = delegator.findByAnd("RecurrenceRule", [recurrenceRuleId : recurrenceInfo.recurrenceRuleId]);
            recurrenceRule = EntityUtil.getFirst(recurrenceRule);
            context.recurrenceRule = recurrenceRule;
        }
    }
    
}
