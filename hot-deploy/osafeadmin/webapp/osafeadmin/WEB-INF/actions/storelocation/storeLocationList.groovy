package storelocation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;


import javolution.util.FastList;
import javolution.util.FastMap;

import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.entity.util.EntityUtil;

session = context.session;
partyGroupNameLocal = StringUtils.trimToEmpty(parameters.partyGroupNameLocal);
partyGroupName = StringUtils.trimToEmpty(parameters.partyGroupName);
statusEnabled = StringUtils.trimToEmpty(parameters.statusEnabled);
statusDisabled = StringUtils.trimToEmpty(parameters.statusDisabled);
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

Map<String, Object> svcCtx = FastMap.newInstance();
userLogin = session.getAttribute("userLogin");
svcCtx.put("userLogin", userLogin);
svcCtx.put("lookupFlag", "Y");
svcCtx.put("showAll", "N");
svcCtx.put("statusId", "ANY");
svcCtx.put("extInfo", "N");
svcCtx.put("partyTypeId", "PARTY_GROUP");
svcCtx.put("roleTypeId", "STORE_LOCATION");
svcCtx.put("productStoreId", context.productStoreId);

if (UtilValidate.isNotEmpty(partyGroupName))
{
   svcCtx.put("groupName", partyGroupName.toUpperCase());
}
if (UtilValidate.isNotEmpty(partyGroupNameLocal))
{
   svcCtx.put("groupNameLocal", partyGroupNameLocal.toUpperCase());
}

if (UtilValidate.isNotEmpty(statusEnabled) && UtilValidate.isEmpty(statusDisabled))
{
    svcCtx.put("statusId", "PARTY_ENABLED");
}

if (UtilValidate.isNotEmpty(statusDisabled) && UtilValidate.isEmpty(statusEnabled))
{
    svcCtx.put("statusId", "PARTY_DISABLED");
}
if (UtilValidate.isNotEmpty(partyGroupName))
{
   svcCtx.put("groupName", partyGroupName.toUpperCase());
}

Map<String, Object> svcRes;
List<GenericValue> partyList = FastList.newInstance();
if(UtilValidate.isNotEmpty(preRetrieved) && preRetrieved != "N") {

     svcRes = dispatcher.runSync("findParty", svcCtx);

     context.pagingList = svcRes.get("completePartyList");
     context.pagingListSize = svcRes.get("partyListSize");
}
else
{
     context.pagingList = partyList;
     context.pagingListSize = partyList.size();
}
