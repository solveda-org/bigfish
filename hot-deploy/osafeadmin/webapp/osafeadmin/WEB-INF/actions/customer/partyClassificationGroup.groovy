package customer;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityFunction
import org.ofbiz.entity.condition.EntityOperator;
import javolution.util.FastList;

import org.ofbiz.entity.GenericEntityException
import org.ofbiz.entity.model.DynamicViewEntity
import org.ofbiz.entity.model.ModelKeyMap
import org.ofbiz.entity.util.EntityFindOptions
import org.ofbiz.base.util.UtilMisc;
import org.apache.commons.lang.StringUtils;

partyClassificationGroupId = StringUtils.trimToEmpty(parameters.partyClassificationGroupId);
partyClassificationTypeId = StringUtils.trimToEmpty(parameters.partyClassificationTypeId);
description = StringUtils.trimToEmpty(parameters.description);
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

partyClassificationCond = null;
paramsExpr = FastList.newInstance();

if (UtilValidate.isNotEmpty(partyClassificationGroupId))
{
	paramsExpr.add(EntityCondition.makeCondition("partyClassificationGroupId", EntityOperator.EQUALS, partyClassificationGroupId));
}
if (UtilValidate.isNotEmpty(partyClassificationTypeId))
{
	paramsExpr.add(EntityCondition.makeCondition("partyClassificationTypeId", EntityOperator.EQUALS, partyClassificationTypeId));
}
if (UtilValidate.isNotEmpty(description))
{
	paramsExpr.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("description"), EntityOperator.LIKE, "%"+description.toUpperCase()+"%"));
}
if (UtilValidate.isNotEmpty(paramsExpr))
{
	partyClassificationCond=EntityCondition.makeCondition(paramsExpr, EntityOperator.AND);
}
eli = null;
// set distinct
partyClassificationFindOpts = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, true);
partyClassificationSearchList=FastList.newInstance();
if(UtilValidate.isNotEmpty(preRetrieved) && preRetrieved != "N")
{
	// Party Classification Group dynamic view entity
	DynamicViewEntity partyClassificationDve = new DynamicViewEntity();
	partyClassificationDve.addMemberEntity("PCG", "PartyClassificationGroup");
	partyClassificationDve.addAlias("PCG", "partyClassificationGroupId", "partyClassificationGroupId", null, null, null, null);
	partyClassificationDve.addAlias("PCG", "partyClassificationTypeId", "partyClassificationTypeId", null, null, null, null);
	partyClassificationDve.addAlias("PCG", "productFeatureCategoryId", "productFeatureCategoryId", null, null, null, null);
	partyClassificationDve.addAlias("PCG", "description", "description", null, null, null, null);
	//make relation with ProductFeatureType
	partyClassificationDve.addRelation("one", "", "PartyClassificationType", UtilMisc.toList(new ModelKeyMap("partyClassificationTypeId", "partyClassificationTypeId")));
	partyClassificationDve.addMemberEntity("PCT", "PartyClassificationType");
	partyClassificationDve.addAlias("PCT", "productFeatureTypeDescription", "description", null, null, null, null);
	partyClassificationDve.addViewLink("PCG", "PCT", Boolean.FALSE, UtilMisc.toList(new ModelKeyMap("partyClassificationTypeId", "partyClassificationTypeId")));

	eli = delegator.findListIteratorByCondition(partyClassificationDve, partyClassificationCond, null, null, null, partyClassificationFindOpts);
	partyClassificationSearchList = eli.getCompleteList();
}
if (UtilValidate.isNotEmpty(eli))
{
	try
	{
		eli.close();
	}
	catch (GenericEntityException e)
	{}
}

pagingListSize=partyClassificationSearchList.size();
context.pagingListSize=pagingListSize;
context.pagingList = partyClassificationSearchList;