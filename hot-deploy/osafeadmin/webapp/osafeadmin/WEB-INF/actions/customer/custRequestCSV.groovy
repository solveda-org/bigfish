package customer;

import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.UtilDateTime;
import com.osafe.util.OsafeAdminUtil;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.GenericValue;
import javolution.util.FastMap;

userLogin = session.getAttribute("userLogin");
custRequestCond = session.getAttribute("custRequestCond");
custRequestList = delegator.findList("CustRequest",custRequestCond, null, null, null, false);

if (UtilValidate.isNotEmpty(custRequestList)) 
{
    if (UtilValidate.isNotEmpty(custRequestCSVName)) 
    {
        custRequestCSVName = custRequestCSVName+(OsafeAdminUtil.convertDateTimeFormat(UtilDateTime.nowTimestamp(), "yyyy-MM-dd-HH:mm"));
        response.setHeader("Content-Disposition","attachment; filename=\"" + UtilValidate.stripWhitespace(custRequestCSVName) + ".csv" + "\";");
    }
    for(GenericValue custRequest : custRequestList)
    {
        custRequestId = custRequest.custRequestId;
        Map<String, Object> updateCustReqAttrCtx = FastMap.newInstance();
        updateCustReqAttrCtx.put("userLogin",userLogin);
        updateCustReqAttrCtx.put("custRequestId",custRequestId);
        updateCustReqAttrCtx.put("attrName","IS_DOWNLOADED");
        updateCustReqAttrCtx.put("attrValue","Y");
        custReqAttribute = delegator.findByPrimaryKey("CustRequestAttribute",UtilMisc.toMap("custRequestId", custRequestId,"attrName","IS_DOWNLOADED"));
        if(UtilValidate.isNotEmpty(custReqAttribute))
        {
            dispatcher.runSync("updateCustRequestAttribute", updateCustReqAttrCtx);
        }
        else
        {
            dispatcher.runSync("createCustRequestAttribute", updateCustReqAttrCtx);
        }
        
        custReqAttribute = delegator.findByPrimaryKey("CustRequestAttribute",UtilMisc.toMap("custRequestId", custRequestId,"attrName","DATETIME_DOWNLOADED"));
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
    }
    context.custRequestList=custRequestList;
}
