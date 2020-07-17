package customer;

import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.order.order.OrderReadHelper;
import org.ofbiz.party.contact.ContactHelper;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.party.contact.ContactMechWorker;
import org.ofbiz.base.util.*;
import javolution.util.FastMap;
import org.ofbiz.base.util.UtilDateTime;
import com.osafe.util.OsafeAdminUtil;

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

import org.ofbiz.base.util.ObjectType;

partyId = parameters.partyId;
userLogin = session.getAttribute("userLogin");

session = context.session;
svcCtx = session.getAttribute("customerPDFMap");
if (UtilValidate.isEmpty(svcCtx)) 
{
    svcCtx = FastMap.newInstance();
}

if (UtilValidate.isNotEmpty(partyId))
{
    svcCtx.put("partyId", partyId);
}

if (UtilValidate.isNotEmpty(svcCtx)) 
{
	svcCtx.put("VIEW_SIZE", "10000");
	svcCtx.put("lookupFlag", "Y");
	svcCtx.put("showAll", "N");
	svcCtx.put("roleTypeId", "ANY");
	svcCtx.put("partyTypeId", "ANY");
	svcCtx.put("statusId", "ANY");
	svcCtx.put("extInfo", "N");
	svcCtx.put("partyTypeId", "PERSON");

    Map<String, Object> svcRes;
    svcRes = dispatcher.runSync("findParty", svcCtx);
    List<GenericValue> customerPDFList =  UtilGenerics.checkList(svcRes.get("completePartyList"), GenericValue.class);
    customerPDFList = EntityUtil.orderBy(customerPDFList, ["partyId"]);
    context.customerList = customerPDFList;
    if (UtilValidate.isNotEmpty(customerPDFList)) 
    {
        if (UtilValidate.isNotEmpty(customerPDFName)) 
        {
            customerPDFName = customerPDFName+(OsafeAdminUtil.convertDateTimeFormat(UtilDateTime.nowTimestamp(), "yyyy-MM-dd-HHmm"));
            response.setHeader("Content-Disposition","attachment; filename=\"" + UtilValidate.stripWhitespace(customerPDFName) + ".pdf" + "\";");
        }
        
        Map<String, Object> upPartyCtx = FastMap.newInstance();
        upPartyCtx.put("userLogin",userLogin);
        upPartyCtx.put("isDownloaded","Y");
        upPartyCtx.put("datetimeDownloaded",UtilDateTime.nowTimestamp());

        for(GenericValue party : customerPDFList)
        {
            person = delegator.findOne("Person",["partyId":party.partyId], false);
            
            partyAttrIsDownload = delegator.findOne("PartyAttribute", ["partyId" : party.partyId, "attrName" : "IS_DOWNLOADED"], false);
            Map<String, Object> isDownloadedPartyAttrCtx = FastMap.newInstance();
            isDownloadedPartyAttrCtx.put("partyId", party.partyId);
            isDownloadedPartyAttrCtx.put("userLogin",userLogin);
            isDownloadedPartyAttrCtx.put("attrName","IS_DOWNLOADED");
            isDownloadedPartyAttrCtx.put("attrValue","Y");
            Map<String, Object> isDownloadedPartyAttrMap = null;
            if (UtilValidate.isNotEmpty(partyAttrIsDownload)) 
            {
                isDownloadedPartyAttrMap = dispatcher.runSync("updatePartyAttribute", isDownloadedPartyAttrCtx);
            } else 
            {
                isDownloadedPartyAttrMap = dispatcher.runSync("createPartyAttribute", isDownloadedPartyAttrCtx);
            }
            
            partyAttrDateTimeDownload = delegator.findOne("PartyAttribute", ["partyId" : party.partyId, "attrName" : "DATETIME_DOWNLOADED"], false);
            Map<String, Object> dateTimeDownloadedPartyAttrCtx = FastMap.newInstance();
            dateTimeDownloadedPartyAttrCtx.put("partyId", party.partyId);
            dateTimeDownloadedPartyAttrCtx.put("userLogin",userLogin);
            dateTimeDownloadedPartyAttrCtx.put("attrName","DATETIME_DOWNLOADED");
            dateTimeDownloadedPartyAttrCtx.put("attrValue",UtilDateTime.nowTimestamp().toString());
            Map<String, Object> dateTimeDownloadedPartyAttrMap = null;
            if (UtilValidate.isNotEmpty(partyAttrDateTimeDownload)) 
            {
                dateTimeDownloadedPartyAttrMap = dispatcher.runSync("updatePartyAttribute", dateTimeDownloadedPartyAttrCtx);
            } else 
            {
                dateTimeDownloadedPartyAttrMap = dispatcher.runSync("createPartyAttribute", dateTimeDownloadedPartyAttrCtx);
            }
 
        }
        
    }
}





