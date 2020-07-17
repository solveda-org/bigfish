package customer;

import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.UtilDateTime;
import com.osafe.util.OsafeAdminUtil;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.GenericValue;
import javolution.util.FastMap;
import org.ofbiz.product.store.ProductStoreWorker;

userLogin = session.getAttribute("userLogin");
custRequestCond = session.getAttribute("custRequestCond");
custRequestList = delegator.findList("CustRequest",custRequestCond, null, null, null, false);

custRequest = EntityUtil.getFirst(custRequestList);;

List custRequestIdsList = new ArrayList();
List custRequestIdList = new ArrayList();

if (UtilValidate.isNotEmpty(custRequestList)) 
{
    custRequestIdsList = EntityUtil.getFieldListFromEntityList(custRequestList, "custRequestId", true);
    
    for(String custRequestId : custRequestIdsList)
    {
        custRequestIdList.add(custRequestId);
    }
    context.exportIdList = custRequestIdList
}
