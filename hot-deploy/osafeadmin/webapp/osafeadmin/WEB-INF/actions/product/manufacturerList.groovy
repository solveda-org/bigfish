package user;

import javolution.util.FastList;
import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.condition.EntityFunction;

if (UtilValidate.isNotEmpty(preRetrieved))
{
   context.preRetrieved=preRetrieved;
}
else
{
  preRetrieved = context.preRetrieved;
}

srchManufacturerId = StringUtils.trimToEmpty(parameters.srchManufacturerId);
srchManufacturerName = StringUtils.trimToEmpty(parameters.srchManufacturerName);

exprs = FastList.newInstance();
mainCond=null;

if(UtilValidate.isNotEmpty(srchManufacturerId))
{
	partyId=srchManufacturerId;
	exprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("partyId"), EntityOperator.EQUALS, partyId.toUpperCase()));
	context.srchManufacturerId=srchManufacturerId;
}

if(UtilValidate.isNotEmpty(srchManufacturerName))
{
	groupName=srchManufacturerName;
	exprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("groupName"), EntityOperator.LIKE, "%" + groupName.toUpperCase() + "%"));
	context.srchManufacturerName=srchManufacturerName;
}

exprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("roleTypeId"), EntityOperator.EQUALS, "MANUFACTURER"));
prodCond=EntityCondition.makeCondition(exprs, EntityOperator.AND);
mainCond=prodCond;


orderBy = ["partyId"];

manufacturers = FastList.newInstance();
if(UtilValidate.isNotEmpty(preRetrieved) && preRetrieved != "N")
{
	manufacturers = delegator.findList("PartyRoleAndPartyDetail", mainCond, null, orderBy, null, false);
}

if(UtilValidate.isNotEmpty(manufacturers))
{
	context.manufacturers =manufacturers;
}

pagingListSize=manufacturers.size();
context.pagingListSize=pagingListSize;
context.pagingList = manufacturers;