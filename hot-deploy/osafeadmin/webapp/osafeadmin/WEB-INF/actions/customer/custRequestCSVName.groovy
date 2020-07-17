package customer;


import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.base.util.UtilValidate;
import com.osafe.util.OsafeAdminUtil;

custRequestCond = session.getAttribute("custRequestCond");
custRequestList = delegator.findList("CustRequest",custRequestCond, null, null, null, false);
custRequestName = "CustRequest_";
custRequestTypeId = "";

if (UtilValidate.isNotEmpty(custRequestList)) 
{
    for(GenericValue custRequest : custRequestList)
    {
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
	custRequestName = custRequestName+(OsafeAdminUtil.convertDateTimeFormat(UtilDateTime.nowTimestamp(), "yyyy-MM-dd-HH:mm"));
    response.setHeader("Content-Disposition","attachment; filename=\"" + UtilValidate.stripWhitespace(custRequestName) + ".csv" + "\";");
}