package customer;

import org.ofbiz.base.util.UtilValidate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionBuilder;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.condition.EntityFieldValue;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.product.category.CategoryWorker;
import org.ofbiz.entity.util.EntityFindOptions
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import com.ibm.icu.util.Calendar;

import org.ofbiz.base.util.ObjectType;


//input
srchRunDateFrom = StringUtils.trimToEmpty(parameters.dateFrom);
srchRunDateTo = StringUtils.trimToEmpty(parameters.dateTo);
//checkboxes
isAnon = StringUtils.trimToEmpty(parameters.isAnon);
isReg = StringUtils.trimToEmpty(parameters.isReg);

initializedCB = StringUtils.trimToEmpty(parameters.initializedCB);
preRetrieved = StringUtils.trimToEmpty(parameters.preRetrieved);

if (UtilValidate.isNotEmpty(preRetrieved))
{
   context.preRetrieved=preRetrieved;
}
else
{
  preRetrieved = context.preRetrieved;
}

if (UtilValidate.isNotEmpty(initializedCB))
{
   context.initializedCB=initializedCB;
}

nowTs = UtilDateTime.nowTimestamp();
session = context.session;
productStore = ProductStoreWorker.getProductStore(request);

Map<String, Object> svcCtx = FastMap.newInstance();
userLogin = session.getAttribute("userLogin");
context.userLogin=userLogin;

exprs = FastList.newInstance();
mainCond=null;
prodCond=null;
statusCond=null;

//handle all the dates 
if(UtilValidate.isNotEmpty(srchRunDateFrom))
{
	try 
	{
		  srchRunDateFrom = ObjectType.simpleTypeConvert(srchRunDateFrom, "Timestamp", preferredDateFormat, locale);
	} catch (Exception e) 
	{
		errMsg = "Parse Exception srchRunDateFrom: " + srchRunDateFrom;
		Debug.logError(e, errMsg, "abandonedCartList.groovy");
	}
	runDateFrom=srchRunDateFrom;
	exprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("lastUpdatedStamp"), EntityOperator.GREATER_THAN_EQUAL_TO, runDateFrom));
	context.srchRunDateFrom=srchRunDateFrom;
}
if(UtilValidate.isNotEmpty(srchRunDateTo))
{
	try 
	{
		  srchRunDateTo = ObjectType.simpleTypeConvert(srchRunDateTo, "Timestamp", preferredDateFormat, locale);
	} catch (Exception e) 
	{
		errMsg = "Parse Exception srchRunDateTo: " + srchRunDateTo;
		Debug.logError(e, errMsg, "abandonedCartList.groovy");
	}
    runDateTo=UtilDateTime.getDayEnd(srchRunDateTo)
	exprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("lastUpdatedStamp"), EntityOperator.LESS_THAN_EQUAL_TO, runDateTo));
	context.srchRunDateTo=srchRunDateTo;
}

if (UtilValidate.isNotEmpty(exprs)) 
{
    prodCond=EntityCondition.makeCondition(exprs, EntityOperator.AND);
}

shoppingListItemsList = delegator.findList("ShoppingListItem", null, null, null, null, false);
shoppingListItemsIds = EntityUtil.getFieldListFromEntityList(shoppingListItemsList, "shoppingListId", true);

anonCond = EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, null);
regCond = EntityCondition.makeCondition("partyId", EntityOperator.NOT_EQUAL, null);
itemsCond = EntityCondition.makeCondition("shoppingListId", EntityOperator.IN, shoppingListItemsIds);
anonItemsCond = EntityCondition.makeCondition([anonCond, itemsCond], EntityOperator.AND);
regItemsCond = EntityCondition.makeCondition([regCond, itemsCond], EntityOperator.AND);

// Radio buttons
statusExpr= FastList.newInstance();
if("isAnonItems".equals(isAnon))
{
    statusExpr.add(anonItemsCond);
   context.isAnon=isAnon;
}
if("isAnonAll".equals(isAnon))
{
    statusExpr.add(anonCond);
   context.isAnon=isAnon;
}
if("isNoAnon".equals(isAnon))
{
	statusExpr.add(regCond);
   context.isAnon=isAnon;
}

if("isRegItems".equals(isReg))
{
	statusExpr.add(regItemsCond);
   context.isReg=isReg;
}
if("isRegAll".equals(isReg))
{
	statusExpr.add(regCond);
   context.isReg=isReg;
}
if("isNoReg".equals(isReg))
{
	statusExpr.add(anonCond);
   context.isReg=isReg;
}

if (UtilValidate.isNotEmpty(statusExpr))
{
   shopCond = EntityCondition.makeCondition("shoppingListTypeId", EntityOperator.EQUALS, "SLT_SPEC_PURP");
	
   if(("isNoReg".equals(isReg)) || ("isNoAnon".equals(isAnon)))
   {
	   radioCond = EntityCondition.makeCondition(statusExpr, EntityOperator.AND);
   }
   else
   {
	   radioCond = EntityCondition.makeCondition(statusExpr, EntityOperator.OR);
   }
   
   statusCond = EntityCondition.makeCondition([shopCond, radioCond], EntityOperator.AND);
   
   if (UtilValidate.isNotEmpty(prodCond)) 
   {
      mainCond = EntityCondition.makeCondition([prodCond, statusCond], EntityOperator.AND);
   }
   else
   {
     mainCond=statusCond;
   }
}

shoppingLists=FastList.newInstance();
if(UtilValidate.isNotEmpty(preRetrieved) && preRetrieved != "N") 
{
	shoppingLists = delegator.findList("ShoppingList",mainCond, null, ["lastUpdatedStamp DESC"], null, false); 
}
 
pagingListSize=shoppingLists.size();
context.pagingListSize=pagingListSize;
context.pagingList = shoppingLists;

