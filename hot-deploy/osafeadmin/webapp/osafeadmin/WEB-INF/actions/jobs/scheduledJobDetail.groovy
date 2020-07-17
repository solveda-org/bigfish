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


productStoreId=globalContext.productStoreId;
jobId=parameters.jobId;

//partysOps = new EntityFindOptions();
//partysOps.setDistinct(true);
//Set<String> party = new TreeSet<String>();
//party.add("partyId");

//conditions = FastList.newInstance();
//conditions.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "CARRIER"));
//mainCond = EntityCondition.makeCondition(conditions, EntityOperator.AND);

//partys = delegator.findList("PartyRole", mainCond, party, null, partysOps, false);

//if (UtilValidate.isNotEmpty(partys))
//{
//    context.partys = partys;
//}

//Shipping Method

//shipmentMethodTypeOps = new EntityFindOptions();
//shipmentMethodTypeOps.setDistinct(true);
//Set<String> shipm = new TreeSet<String>();
//shipm.add("shipmentMethodTypeId");
//shipmentMethodTypes = delegator.findList("ShipmentMethodType", null, shipm, null, shipmentMethodTypeOps, false);

//if (UtilValidate.isNotEmpty(shipmentMethodTypes))
//{
//    context.shipmentMethodTypes =shipmentMethodTypes;
//}

//get entity for schedJob
jobId = StringUtils.trimToEmpty(parameters.jobId);
context.jobId = jobId;
if (UtilValidate.isNotEmpty(jobId))
{
    schedJob = delegator.findByAnd("JobSandbox", [jobId : jobId]);
    schedJob = EntityUtil.getFirst(schedJob);
    context.schedJob = schedJob;
	
	
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









