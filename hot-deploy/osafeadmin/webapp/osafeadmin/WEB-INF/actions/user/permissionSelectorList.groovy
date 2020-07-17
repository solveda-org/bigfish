
package user;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;


import javolution.util.FastList;
import javolution.util.FastMap;

import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.base.util.UtilDateTime;

if (UtilValidate.isNotEmpty(preRetrieved))
{
   context.preRetrieved=preRetrieved;
}
else
{
  preRetrieved = context.preRetrieved;
}

srchPermission = StringUtils.trimToEmpty(parameters.srchPermission);

exprs = FastList.newInstance();
mainCond=null;

if(srchPermission)
{
	permissionId=srchPermission;
	exprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("permissionId"), EntityOperator.EQUALS, permissionId.toUpperCase()));
	context.permissionId=permissionId;
}

if (UtilValidate.isNotEmpty(exprs)) {
	prodCond=EntityCondition.makeCondition(exprs, EntityOperator.AND);
	mainCond=prodCond;
}

orderBy = ["permissionId"];
permissions = FastList.newInstance();

if(UtilValidate.isNotEmpty(preRetrieved) && preRetrieved != "N")
{
	permissions = delegator.findList("SecurityPermission", mainCond, null, orderBy, null, false);
}

if (UtilValidate.isNotEmpty(permissions))
{
	context.permissions =permissions;
}


 
pagingListSize=permissions.size();
context.pagingListSize=pagingListSize;
context.pagingList = permissions;




