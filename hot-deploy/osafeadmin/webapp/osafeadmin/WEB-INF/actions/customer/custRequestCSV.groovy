package customer;

import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.UtilDateTime;
import com.osafe.util.OsafeAdminUtil;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.GenericValue;
import javolution.util.FastMap;
import javolution.util.FastList;

import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;

userLogin = session.getAttribute("userLogin");
custRequestList=session.getAttribute("custRequestList");

if (UtilValidate.isNotEmpty(custRequestList)) 
{
    for(Map custRequestInfo : custRequestList)
    {
        custRequest = custRequestInfo.CustRequest;
        custRequestAttributeList = custRequestInfo.CustRequestAttributeList;
        custReqAttribute = EntityUtil.filterByCondition(custRequestAttributeList, EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, "IS_DOWNLOADED"));

        custRequestId = custRequest.custRequestId;
        Map<String, Object> updateCustReqAttrCtx = FastMap.newInstance();
        updateCustReqAttrCtx.put("userLogin",userLogin);
        updateCustReqAttrCtx.put("custRequestId",custRequestId);
        updateCustReqAttrCtx.put("attrName","IS_DOWNLOADED");
        updateCustReqAttrCtx.put("attrValue","Y");
        if(UtilValidate.isNotEmpty(custReqAttribute))
        {
            dispatcher.runSync("updateCustRequestAttribute", updateCustReqAttrCtx);
        }
        else
        {
            dispatcher.runSync("createCustRequestAttribute", updateCustReqAttrCtx);
        }
        
        custReqAttribute = EntityUtil.filterByCondition(custRequestAttributeList, EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, "DATETIME_DOWNLOADED"));
        updateCustReqAttrCtx.put("attrName","DATETIME_DOWNLOADED");
        updateCustReqAttrCtx.put("attrValue",UtilDateTime.nowTimestamp().toString());
        if(UtilValidate.isNotEmpty(custReqAttribute))
        {
            dispatcher.runSync("updateCustRequestAttribute", updateCustReqAttrCtx);
        }
        else
        {
            dispatcher.runSync("createCustRequestAttribute", updateCustReqAttrCtx);
        }
        custRequestAttributeList = custRequest.getRelated("CustRequestAttribute");
    }
}
context.custRequestList=custRequestList;
