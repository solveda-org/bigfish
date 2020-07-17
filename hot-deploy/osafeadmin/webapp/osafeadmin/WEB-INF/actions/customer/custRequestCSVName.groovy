package customer;


import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.base.util.UtilValidate;
import com.osafe.util.OsafeAdminUtil;
import javolution.util.FastList;
import javolution.util.FastMap;

custRequestList=session.getAttribute("custRequestList");
custRequestName = "CustRequest_";
custRequestTypeId = "";

if (UtilValidate.isNotEmpty(custRequestList)) 
{
    for(GenericValue custRequestInfo : custRequestList)
    {
    	custRequest = custRequestInfo.CustRequest;
        if(UtilValidate.isEmpty(custRequestTypeId))
        {
        	custRequestTypeId = custRequest.custRequestTypeId;
        	break;
        }
    }
}
if (custRequestTypeId.equals("RF_CONTACT_US"))
{
	custRequestName = "ContactUs_";
}
else
{
	custRequestName = "Request-Catalog_";
}
if (UtilValidate.isNotEmpty(custRequestName)) 
{
	custRequestName = custRequestName+(OsafeAdminUtil.convertDateTimeFormat(UtilDateTime.nowTimestamp(), "yyyy-MM-dd HH:mm:ss"));
    response.setHeader("Content-Disposition","attachment; filename=\"" + custRequestName + ".csv" + "\";");
}