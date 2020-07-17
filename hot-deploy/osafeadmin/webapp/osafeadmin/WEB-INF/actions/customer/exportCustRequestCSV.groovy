package customer;

import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.UtilDateTime;
import com.osafe.util.OsafeAdminUtil;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.GenericValue;
import javolution.util.FastMap;
import javolution.util.FastList;

userLogin = session.getAttribute("userLogin");
custRequestList=FastList.newInstance();
custRequestCond = session.getAttribute("custRequestCond");
isDownloaded = session.getAttribute("custRequestCondIsDownload");
lastName = session.getAttribute("custRequestCondLastName");
lCustRequest = delegator.findList("CustRequest",custRequestCond, null, null, null, false);
if (UtilValidate.isNotEmpty(lCustRequest))
{
    if (UtilValidate.isNotEmpty(isDownloaded) || UtilValidate.isNotEmpty(lastName))
    {
    	for (GenericValue custRequest: lCustRequest) 
    	{
           custRequestAttrList = custRequest.getRelated("CustRequestAttribute");
           custRequestAttrMap = FastMap.newInstance();
           if (UtilValidate.isNotEmpty(custRequestAttrList))
           {
              attrlIter = custRequestAttrList.iterator();
              while (attrlIter.hasNext()) 
              {
                  attr = (GenericValue) attrlIter.next();
                  custRequestAttrMap.put(attr.getString("attrName"),attr.getString("attrValue"));
              }
              
       		 attrLastName =custRequestAttrMap.get("LAST_NAME");
       		 if (UtilValidate.isNotEmpty(attrLastName))
    		 {
    			attrLastName = attrLastName.toUpperCase();
    		 }
       		 attrDownloaded =custRequestAttrMap.get("IS_DOWNLOADED");
        	 if (UtilValidate.isNotEmpty(isDownloaded) && UtilValidate.isNotEmpty(lastName))
        	 {
        		if (isDownloaded.equals(attrDownloaded) && attrLastName.contains(lastName.toUpperCase()))
        		{
        			custRequestList.add(custRequest);
        		}
        	 }else if (UtilValidate.isNotEmpty(lastName))
        	 {
          		if (attrLastName.contains(lastName.toUpperCase()))
        		{
          			custRequestList.add(custRequest);
        		}
        	 }else if (UtilValidate.isNotEmpty(isDownloaded))
        	 {
         		if (isDownloaded.equals(attrDownloaded))
        		{
         			custRequestList.add(custRequest);
        		}
        		 
        	 }
           }
    	}
    }
    else
    {
    	custRequestList = lCustRequest;
    	
    }
}
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
