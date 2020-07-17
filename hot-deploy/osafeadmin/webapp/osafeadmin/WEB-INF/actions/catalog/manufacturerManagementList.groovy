package catalog;

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
import org.ofbiz.party.content.PartyContentWrapper;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.util.EntityUtil;

session = context.session;

partyId = StringUtils.trimToEmpty(parameters.manufacturerPartyId);
partyName = StringUtils.trimToEmpty(parameters.partyName);

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
svcCtx.put("roleTypeId", "ANY");
svcCtx.put("partyTypeId", "ANY");
svcCtx.put("statusId", "ANY");
svcCtx.put("extInfo", "N");
svcCtx.put("partyTypeId", "PARTY_GROUP");
if(UtilValidate.isEmpty(productStoreall))
{
    svcCtx.put("productStoreId", context.productStoreId);
}

if(UtilValidate.isNotEmpty(partyId))
{
   svcCtx.put("partyId", partyId.toUpperCase());
}


svcCtx.put("roleTypeId", "MANUFACTURER");


Map<String, Object> svcRes;

List<GenericValue> partyList = FastList.newInstance();
List<GenericValue> completePartyList = FastList.newInstance();
List<GenericValue> matchNamePartyList = FastList.newInstance();
if(UtilValidate.isNotEmpty(preRetrieved) && preRetrieved != "N") 
{
     svcRes = dispatcher.runSync("findParty", svcCtx);
	 completePartyList = svcRes.get("completePartyList");
	 pagingListSize = svcRes.get("partyListSize");
	 //if partyName is used as search criteria, then filter out rows with matching name
	 if(UtilValidate.isNotEmpty(partyName))
	 {
		 pagingListSize = 0;
		 for(GenericValue foundParty : completePartyList)
		 {
			 party = delegator.findByPrimaryKey("Party",UtilMisc.toMap("partyId",foundParty.partyId));
			 partyContentWrapper = PartyContentWrapper.makePartyContentWrapper(party, request);
			 if(UtilValidate.isNotEmpty(partyContentWrapper))
			 {
				 manufacturerProfileName = partyContentWrapper.get("PROFILE_NAME");
				 if(UtilValidate.isNotEmpty(manufacturerProfileName))
				 {
					 if(manufacturerProfileName.toString().toLowerCase().contains(partyName.toLowerCase()))
					 {
						 matchNamePartyList.add(foundParty);
						 pagingListSize = pagingListSize + 1;
					 }
				 }
			 }
		 }
		 if(UtilValidate.isNotEmpty(matchNamePartyList))
		 {
			 completePartyList = EntityUtil.orderBy(matchNamePartyList,UtilMisc.toList('partyId'));
		 }
		 else
		 {
		   	 completePartyList = null;
		 }	
	 }
	 
     context.pagingList = completePartyList;
     context.pagingListSize = pagingListSize;
}
else
{
     context.pagingList = partyList;
     context.pagingListSize = partyList.size();
}