
package customer;

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
roleId = StringUtils.trimToEmpty(parameters.roleId);
partyId = StringUtils.trimToEmpty(parameters.partyId);
partyUserLoginId = StringUtils.trimToEmpty(parameters.partyUserLoginId);
partyName = StringUtils.trimToEmpty(parameters.partyName);
partyEmail = StringUtils.trimToEmpty(parameters.partyEmail);
statusEnabled = StringUtils.trimToEmpty(parameters.statusEnabled);
statusDisabled = StringUtils.trimToEmpty(parameters.statusDisabled);
roleCustomerId = StringUtils.trimToEmpty(parameters.roleCustomerId);
roleEmailId = StringUtils.trimToEmpty(parameters.roleEmailId);
roleGuestId = StringUtils.trimToEmpty(parameters.roleGuestId);
partyExportNew = StringUtils.trimToEmpty(parameters.partyExportNew);
partyExported = StringUtils.trimToEmpty(parameters.partyExported);
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
svcCtx.put("roleTypeId", "ANY");
svcCtx.put("partyTypeId", "ANY");
svcCtx.put("statusId", "ANY");
svcCtx.put("extInfo", "N");
svcCtx.put("partyTypeId", "PERSON");


if (partyId)
 {
    svcCtx.put("partyId", partyId.toUpperCase());
 }

if (UtilValidate.isNotEmpty(partyUserLoginId))
 {
    svcCtx.put("userLoginId", partyUserLoginId);
 }

if (UtilValidate.isNotEmpty(partyName))
 {
	if (partyName.indexOf(",")>0 || partyName.indexOf(" ")>0)
	 {
    	if (partyName.indexOf(",") > 0)
	    {
	         nameIdx = partyName.indexOf(",");
	         partyLastName = partyName.substring(0,nameIdx);
	         partyFirstName =  partyName.substring((nameIdx+1), partyName.length());
             svcCtx.put("firstName",partyFirstName.trim());
             svcCtx.put("lastName",partyLastName.trim());
	    }
	    else
	    {
	         nameIdx = partyName.indexOf(" ");
	         partyFirstName = partyName.substring(0,(nameIdx));
	         partyLastName =  partyName.substring((nameIdx+1), partyName.length());
             svcCtx.put("firstName",partyFirstName.trim());
             svcCtx.put("lastName",partyLastName.trim());
	    }
	 }
	else
	 {
             svcCtx.put("lastName",partyName);
	 }
 }

if (UtilValidate.isNotEmpty(partyEmail))
 {
    svcCtx.put("infoString", partyEmail);
    svcCtx.put("extInfo", "O");
 }
if (UtilValidate.isNotEmpty(statusEnabled) && UtilValidate.isEmpty(statusDisabled))
{
    svcCtx.put("statusId", "PARTY_ENABLED");
}

if (UtilValidate.isNotEmpty(statusDisabled) && UtilValidate.isEmpty(statusEnabled))
{
    svcCtx.put("statusId", "PARTY_DISABLED");
}

List<String> roleTypes = FastList.newInstance();

if (UtilValidate.isNotEmpty(roleCustomerId))
{
    //svcCtx.put("roleTypeId", "CUSTOMER");
    roleTypes.add("CUSTOMER");
}

if (UtilValidate.isNotEmpty(roleEmailId))
{
    //svcCtx.put("roleTypeId", "EMAIL_SUBSCRIBER");
    roleTypes.add("EMAIL_SUBSCRIBER");
}

if (UtilValidate.isNotEmpty(roleGuestId))
{
    //svcCtx.put("roleTypeId", "EMAIL_SUBSCRIBER");
    roleTypes.add("GUEST_CUSTOMER");
}

if (roleTypes.size() > 0)
{
    svcCtx.put("roleTypeIds", roleTypes);
}

if(UtilValidate.isEmpty(parameters.downloadnew) & UtilValidate.isNotEmpty(parameters.downloadloaded)) {
    svcCtx.put("isDownloaded", "Y");
}
if(UtilValidate.isNotEmpty(parameters.downloadnew) & UtilValidate.isEmpty(parameters.downloadloaded)) {
    svcCtx.put("isDownloaded", "N");
}

Map<String, Object> svcRes;

List<GenericValue> partyList = FastList.newInstance();

if(UtilValidate.isNotEmpty(preRetrieved) && preRetrieved != "N") {

     svcRes = dispatcher.runSync("findParty", svcCtx);
     session.setAttribute("customerPDFMap", svcCtx);

     context.pagingList = svcRes.get("completePartyList");
     context.pagingListSize = svcRes.get("partyListSize");
}
else
{
     context.pagingList = partyList;
     context.pagingListSize = partyList.size();
}




