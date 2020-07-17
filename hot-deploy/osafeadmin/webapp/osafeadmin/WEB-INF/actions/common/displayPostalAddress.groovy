package common;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.StringUtil;
import com.osafe.util.OsafeAdminUtil;

postalAddress = request.getAttribute("PostalAddress");
displayFormat = request.getAttribute("DISPLAY_FORMAT");

if(UtilValidate.isEmpty(displayFormat))
{
	displayFormat="MULTI_LINE_FULL_ADDRESS";
}

if(UtilValidate.isNotEmpty(postalAddress))
{
    changed = request.removeAttribute("PostalAddress");
}

if(UtilValidate.isNotEmpty(displayFormat))
{
    changed = request.removeAttribute("DISPLAY_FORMAT");
}

addressFormat = OsafeAdminUtil.getProductStoreParm(request,"FORMAT_ADDRESS");
if(UtilValidate.isNotEmpty(addressFormat))
{
    for(String column : StringUtil.split(StringUtil.removeSpaces(addressFormat), ","))
    {
        if (column.indexOf("_") > 0)
        {
            nameValueList = StringUtil.split(column, "_");
            context.put(nameValueList[0], nameValueList[1]);
        }
    }
}
context.postalAddress = postalAddress;
context.displayFormat = displayFormat;
