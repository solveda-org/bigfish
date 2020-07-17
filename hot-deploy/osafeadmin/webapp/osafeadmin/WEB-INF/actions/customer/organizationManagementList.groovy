package customer;

import java.util.List;
import java.util.Map;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.UtilMisc;

session = context.session;
groupName = StringUtils.trimToEmpty(parameters.groupName);
initializedCB = StringUtils.trimToEmpty(parameters.initializedCB);
preRetrieved = StringUtils.trimToEmpty(parameters.preRetrieved);
productStoreall = StringUtils.trimToEmpty(parameters.productStoreall);

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

// Paging variables
viewIndex = Integer.valueOf(parameters.viewIndex  ?: 1);
viewSize = Integer.valueOf(parameters.viewSize ?: UtilProperties.getPropertyValue("osafeAdmin", "default-view-size"));

Map<String, Object> svcCtx = FastMap.newInstance();
userLogin = session.getAttribute("userLogin");
svcCtx.put("userLogin", userLogin);

svcCtx.put("VIEW_INDEX", "" + viewIndex);
svcCtx.put("VIEW_SIZE", ""+ viewSize);
svcCtx.put("lookupFlag", "Y");
svcCtx.put("showAll", "N");
svcCtx.put("partyTypeId", "ANY");
svcCtx.put("statusId", "ANY");
svcCtx.put("extInfo", "N");
svcCtx.put("partyTypeId", "PARTY_GROUP");

if (UtilValidate.isNotEmpty(groupName))
{
    svcCtx.put("groupName", groupName);
	svcCtx.put("groupNameStartWithFlag", "Y");
    context.groupName = groupName;
}
if (UtilValidate.isEmpty(productStoreall))
{
    svcCtx.put("productStoreId", context.productStoreId);
}


List<String> roleTypeIds = FastList.newInstance();
roleTypeIds.add("INTERNAL_ORGANIZATIO");

if(UtilValidate.isNotEmpty(roleTypeIds))
{
	svcCtx.put("roleTypeIds", roleTypeIds);
	context.roleTypeIds = roleTypeIds;
}

Map<String, Object> svcRes;

List<GenericValue> partyList = FastList.newInstance();

if(UtilValidate.isNotEmpty(preRetrieved) && preRetrieved != "N") 
{
     svcRes = dispatcher.runSync("findParty", svcCtx);

     context.pagingList = svcRes.get("completePartyList");
     context.pagingListSize = svcRes.get("partyListSize");
}
else
{
     context.pagingList = partyList;
     context.pagingListSize = partyList.size();
}