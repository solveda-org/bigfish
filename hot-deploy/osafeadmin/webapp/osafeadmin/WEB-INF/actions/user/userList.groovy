
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

//securityGroup drop down values for the search section
securityGroups = delegator.findList("SecurityGroup", null, null, null, null, false);

if (UtilValidate.isNotEmpty(securityGroups))
{
	context.securityGroups =securityGroups;
}

//search filtering
session = context.session;
srchUserLoginId = StringUtils.trimToEmpty(parameters.srchUserLoginId);
searchGroupId = StringUtils.trimToEmpty(parameters.searchGroupId);
exprs = FastList.newInstance();
mainCond=null;

// User Login Id
if(srchUserLoginId)
{
	userLoginId=srchUserLoginId;
	exprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("userLoginId"), EntityOperator.LIKE, "%" + userLoginId.toUpperCase() + "%"));
	context.srchUserLoginId=srchUserLoginId;
}

// groupId
if(searchGroupId)
{
	userLogSecGroupCond = FastList.newInstance();
	userLogSecGroupCond.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("groupId"), EntityOperator.EQUALS, searchGroupId.toUpperCase()));
	userLoginSecurityGroups = delegator.findList("UserLoginSecurityGroup", EntityCondition.makeCondition([groupId : searchGroupId]), null, null, null, false);
	contentIds = EntityUtil.getFieldListFromEntityList(userLoginSecurityGroups, "userLoginId", true);
	exprs.add(EntityCondition.makeCondition("userLoginId", EntityOperator.IN, contentIds));
	context.searchGroupId=searchGroupId;
}

osafeAdminList = delegator.findList("UserLoginSecurityGroup", EntityCondition.makeCondition([groupId : "OSAFEADMIN"]), null, null, null, false);
osafeAdminListIds = EntityUtil.getFieldListFromEntityList(osafeAdminList, "userLoginId", true);
exprs.add(EntityCondition.makeCondition("userLoginId", EntityOperator.IN, osafeAdminListIds));


if (UtilValidate.isNotEmpty(exprs)) {
	prodCond=EntityCondition.makeCondition(exprs, EntityOperator.AND);
	mainCond=prodCond;
}

orderBy = ["userLoginId"];

users=FastList.newInstance();
if(UtilValidate.isNotEmpty(preRetrieved) && preRetrieved != "N")
 {
	users = delegator.findList("UserLogin",mainCond, null, orderBy, null, false);
 }

 
pagingListSize=users.size();
context.pagingListSize=pagingListSize;
context.pagingList = users;




