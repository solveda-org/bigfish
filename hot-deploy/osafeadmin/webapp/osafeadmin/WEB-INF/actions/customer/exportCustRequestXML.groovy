package customer;

import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.UtilDateTime;
import com.osafe.util.OsafeAdminUtil;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.GenericValue;
import javolution.util.FastMap;
import javolution.util.FastList;
import org.ofbiz.product.store.ProductStoreWorker;

custRequestList=session.getAttribute("custRequestList");

List custRequestIdList = new ArrayList();
if (UtilValidate.isNotEmpty(custRequestList)) 
{
    for(Map custRequestInfo : custRequestList)
    {
        custRequest = custRequestInfo.CustRequest;
        custRequestIdList.add(custRequest.custRequestId);
    }
}
context.exportIdList = custRequestIdList
