package customer;

import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.UtilValidate;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionBuilder;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;

if (UtilValidate.isNotEmpty(parameters.custReqId)) 
{
    custRequest = delegator.findOne("CustRequest",["custRequestId":parameters.custReqId], false);
    if (UtilValidate.isNotEmpty(custRequest))
	{
    	productStore = custRequest.getRelatedOne("ProductStore");
        if (UtilValidate.isNotEmpty(productStore))
    	{
            if (UtilValidate.isNotEmpty(productStore.storeName))
        	{
                productStoreName = productStore.storeName;
        	}
            else
            {
            	productStoreName = productStore.productStoreId;
            }
        	context.productStoreName = productStoreName;
    	}
	}
    context.custRequest = custRequest;
    custReqAttributeList = custRequest.getRelated("CustRequestAttribute");
    context.custReqAttributeList = custReqAttributeList;
    session.setAttribute("custRequestList", UtilMisc.toList(UtilMisc.toMap("CustRequest", custRequest,"CustRequestAttributeList", custReqAttributeList)));
}
