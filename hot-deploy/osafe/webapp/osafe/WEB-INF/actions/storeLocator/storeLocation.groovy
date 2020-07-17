package common;

import org.ofbiz.base.util.UtilValidate;
import javolution.util.FastMap;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import com.osafe.util.Util;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.entity.Delegator;

storeRow = request.getAttribute("storeRow");
storeRowIndex = request.getAttribute("storeRowIndex");
rowNum = request.getAttribute("rowNum");
rowClass = request.getAttribute("rowClass");
storeContentSpot = request.getAttribute("storeContentSpot");
pickupStoreButtonVisible = request.getAttribute("pickupStoreButtonVisible");
userLocation = request.getAttribute("userLocation");
if (UtilValidate.isNotEmpty(storeRow))
{

	context.storeRow = storeRow;	
	context.storeRowIndex = storeRowIndex; 
	context.rowNum = rowNum;	
	context.rowClass = rowClass;	
	context.storeContentSpot = storeContentSpot;	
	context.pickupStoreButtonVisible = pickupStoreButtonVisible;	
	context.userLocation = userLocation;	
}
















