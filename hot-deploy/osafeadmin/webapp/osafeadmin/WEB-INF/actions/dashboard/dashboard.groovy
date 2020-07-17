package dashboard;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import java.util.*;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.model.DynamicViewEntity;
import org.ofbiz.entity.model.ModelKeyMap;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.model.ModelViewEntity.ComplexAlias;
import org.ofbiz.entity.model.ModelViewEntity.ComplexAliasField;
import org.ofbiz.product.product.ProductContentWrapper;
import org.ofbiz.product.product.ProductWorker;
import org.ofbiz.product.category.CategoryWorker;
import com.ibm.icu.util.Calendar;
import org.ofbiz.order.order.OrderReadHelper;
import com.osafe.util.OsafeAdminUtil;

orderStatusIncDashboard = globalContext.get("ORDER_STATUS_INC_DASHBOARD");
List includedOrderStatusList = FastList.newInstance();
if(UtilValidate.isNotEmpty(orderStatusIncDashboard))
{
    orderStatusIncDashboardList = StringUtil.split(orderStatusIncDashboard,",")
	  for (String orderStatus : orderStatusIncDashboardList) 
	  {
	      includedOrderStatusList.add(orderStatus.trim());
	  }
}

BigDecimal calcItemTotal(List headers) 
{
    BigDecimal total = BigDecimal.ZERO;
    headers.each { header ->
        orderReadHelper = new OrderReadHelper(OrderReadHelper.getOrderHeader(delegator, header.orderId));
        total = total.plus(orderReadHelper.getOrderItemsSubTotal() ?: BigDecimal.ZERO);
    }
    return total;
}

void sumCol(GenericValue gv1, GenericValue gv2, String columnName) 
{
    BigDecimal result = BigDecimal.ZERO;
    result = gv1.getBigDecimal(columnName);
    if (result != null) 
    {
        result = result.add(gv2.getBigDecimal(columnName));
    } 
    else 
    {
        result = gv2.getBigDecimal(columnName);
    }
    gv1.set(columnName, result);
}
String displayDateFormat = preferredDateFormat;
rounding = UtilNumber.getBigDecimalRoundingMode("order.rounding");
nowTs = UtilDateTime.nowTimestamp();
periodFromTs= context.periodFromTs
periodToTs= context.periodToTs
if(UtilValidate.isNotEmpty(periodFromTs) && UtilValidate.isNotEmpty(periodToTs))
{
    // Get Currency UOM
    defaultCurrencyUomId = UtilProperties.getPropertyValue("general.properties", "currency.uom.id.default", "USD");
    context.put("defaultCurrencyUomId", defaultCurrencyUomId);
    productStore = globalContext.productStore;
    if(UtilValidate.isNotEmpty(productStore))
    {
        context.defaultCurrencyUomId = productStore.defaultCurrencyUomId;
    }

    // dynamic view entity
    DynamicViewEntity dve = new DynamicViewEntity();
    dve.addMemberEntity("OH", "OrderHeader");
    dve.addAliasAll("OH", ""); // no prefix
    dve.addRelation("many", "", "OrderAttribute", UtilMisc.toList(new ModelKeyMap("orderId", "orderId")));
    dve.addMemberEntity("OA", "OrderAttribute");
    dve.addAliasAll("OA", ""); // no prefix
    dve.addViewLink("OH", "OA", Boolean.FALSE, UtilMisc.toList(new ModelKeyMap("orderId", "orderId")));
    List fieldsToSelect = FastList.newInstance();
    fieldsToSelect.add("orderId");
    // set distinct on so we only get one row per order
    EntityFindOptions findOpts = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, true);

//ONLINE SALE SUMMARY
    ecl = EntityCondition.makeCondition([
        EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS, globalContext.productStoreId),
        EntityCondition.makeCondition("statusId", EntityOperator.IN, includedOrderStatusList),
        EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "SALES_ORDER"),
        EntityCondition.makeCondition("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO, periodFromTs),
        EntityCondition.makeCondition("orderDate", EntityOperator.LESS_THAN_EQUAL_TO, periodToTs),
        EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, "DELIVERY_OPTION"),
        EntityCondition.makeCondition("attrValue", EntityOperator.EQUALS, "SHIP_TO")
    ],
    EntityOperator.AND);

    eli = delegator.findListIteratorByCondition(dve, ecl, null, fieldsToSelect, null, findOpts);
    allOnlineSaleHeaders = eli.getCompleteList();
    if (eli != null) 
    {
        try 
        {
            eli.close();
        } 
        catch (GenericEntityException e) 
        {}
    }
    if (UtilValidate.isNotEmpty(allOnlineSaleHeaders))
    {
        orderCount = allOnlineSaleHeaders.size();
        context.orderCount = orderCount;

        totalRevenue = calcItemTotal(allOnlineSaleHeaders);
        context.totalRevenue = totalRevenue;

        averageRevenue= totalRevenue.divide(orderCount, 2);
        context.averageRevenue = averageRevenue;
    }
    else
    {
       totalRevenue = BigDecimal.ZERO;
       orderCount = BigDecimal.ZERO;
    }

//STORE PICK UP CONDITION
    ecl = EntityCondition.makeCondition([
        EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS, globalContext.productStoreId),
        EntityCondition.makeCondition("statusId", EntityOperator.IN, includedOrderStatusList),
        EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "SALES_ORDER"),
        EntityCondition.makeCondition("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO, periodFromTs),
        EntityCondition.makeCondition("orderDate", EntityOperator.LESS_THAN_EQUAL_TO, periodToTs),
        EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, "DELIVERY_OPTION"),
        EntityCondition.makeCondition("attrValue", EntityOperator.EQUALS, "STORE_PICKUP")
    ],
    EntityOperator.AND);
    eli = delegator.findListIteratorByCondition(dve, ecl, null, fieldsToSelect, null, findOpts);
    allStorePickupHeaders = eli.getCompleteList();
    if (eli != null) 
    {
        try 
        {
            eli.close();
        } 
        catch (GenericEntityException e) 
        {}
    }
    if (UtilValidate.isNotEmpty(allStorePickupHeaders))
    {
        storePickupOrderCount = allStorePickupHeaders.size();
        context.storePickupOrderCount = storePickupOrderCount;

        storePickupTotalRevenue = calcItemTotal(allStorePickupHeaders);
        context.storePickupTotalRevenue = storePickupTotalRevenue;

        storePickupAverageRevenue= storePickupTotalRevenue.divide(storePickupOrderCount, 2);
        context.storePickupAverageRevenue = storePickupAverageRevenue;
    }
    else
    {
       storePickupTotalRevenue = BigDecimal.ZERO;
       storePickupOrderCount = BigDecimal.ZERO;
    }

    diffCurrentDays = UtilDateTime.getInterval(UtilDateTime.getDayEnd(nowTs),periodToTs);
    diffCurrentDays = diffCurrentDays / (1000 * 60 *60 * 24);
    diffCurrentDays=diffCurrentDays.intValue();
    if (diffCurrentDays == 0)
    {
      periodToTs=nowTs;
    }
//STORE PICK UP CONDITION

//TRENDS
    diffDays = OsafeAdminUtil.daysBetween(periodFromTs,periodToTs);
    context.diffDays=diffDays;
    if (diffDays > 0)
    {
        //average daily
        dailyAverageOrderCount = orderCount/(diffDays);
        dailyAverageOrderCount= dailyAverageOrderCount.setScale(0,rounding);
        context.dailyAverageOrderCount = dailyAverageOrderCount;
        dailyAverageRevenue = totalRevenue/(diffDays);
        context.dailyAverageRevenue = dailyAverageRevenue;

        storePickupDailyAvgOrderCount = storePickupOrderCount/(diffDays);
        storePickupDailyAvgOrderCount= storePickupDailyAvgOrderCount.setScale(0,rounding);
        context.storePickupDailyAvgOrderCount = storePickupDailyAvgOrderCount;
        storePickupDailyAvgRevenue = storePickupTotalRevenue/(diffDays);
        context.storePickupDailyAvgRevenue = storePickupDailyAvgRevenue;

        context.dailyRevenue = totalRevenue;
        context.dailyDiff = diffDays;
        context.dailyFrom = periodFromTs;
        context.dailyTo = periodToTs;
    
       if (diffDays <= 7)
       {
       //Recent
         periodFromRecTrendTs=UtilDateTime.getDayStart(periodFromTs,-7);
         context.periodFromRecTrendTs = periodFromRecTrendTs;
         periodToRecTrendTs=OsafeAdminUtil.addDaysToTimestamp(periodToTs,-7);
         context.periodToRecTrendTs = periodToRecTrendTs;
            // Summary
            ecl = EntityCondition.makeCondition([
                EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS, globalContext.productStoreId),
                EntityCondition.makeCondition("statusId", EntityOperator.IN, includedOrderStatusList),
                EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "SALES_ORDER"),
                EntityCondition.makeCondition("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO, periodFromRecTrendTs),
                EntityCondition.makeCondition("orderDate", EntityOperator.LESS_THAN_EQUAL_TO, periodToRecTrendTs),
                EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, "DELIVERY_OPTION"),
                EntityCondition.makeCondition("attrValue", EntityOperator.EQUALS, "SHIP_TO")
            ],
            EntityOperator.AND);

            eli = delegator.findListIteratorByCondition(dve, ecl, null, fieldsToSelect, null, findOpts);
            allOnlineSaleHeaders = eli.getCompleteList();
            if (eli != null) 
            {
                try 
                {
                    eli.close();
                } 
                catch (GenericEntityException e) 
                {}
            }
            if (UtilValidate.isNotEmpty(allOnlineSaleHeaders))
            {

                trendTotalRevenue = calcItemTotal(allOnlineSaleHeaders);
                context.recentTrendTotalRevenue = trendTotalRevenue;
                recentOrderCount = allOnlineSaleHeaders.size();
                context.recentOrderCount = recentOrderCount;
                if (totalRevenue > 0)
                {
                    if (totalRevenue < trendTotalRevenue)
                    {
                        recentTrendRevenue= ((totalRevenue - trendTotalRevenue) / totalRevenue) * 100
                        recentTrendRevenue= recentTrendRevenue.setScale(1,rounding);
                        context.recentTrendRevenue = recentTrendRevenue;
                    }
                    else
                    {
                    	if (trendTotalRevenue != 0)
                    	{
                            recentTrendRevenue= ((totalRevenue - trendTotalRevenue) / trendTotalRevenue) * 100
                            recentTrendRevenue= recentTrendRevenue.setScale(1,rounding);
                            context.recentTrendRevenue = recentTrendRevenue;
                    	}
                    }
                }
            }
            //store pick up
            ecl = EntityCondition.makeCondition([
                EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS, globalContext.productStoreId),
                EntityCondition.makeCondition("statusId", EntityOperator.IN, includedOrderStatusList),
                EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "SALES_ORDER"),
                EntityCondition.makeCondition("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO, periodFromRecTrendTs),
                EntityCondition.makeCondition("orderDate", EntityOperator.LESS_THAN_EQUAL_TO, periodToRecTrendTs),
                EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, "DELIVERY_OPTION"),
                EntityCondition.makeCondition("attrValue", EntityOperator.EQUALS, "STORE_PICKUP")
            ],
            EntityOperator.AND);

            eli = delegator.findListIteratorByCondition(dve, ecl, null, fieldsToSelect, null, findOpts);
            allStorePickupHeaders = eli.getCompleteList();
            if (eli != null) 
            {
                try 
                {
                    eli.close();
                } 
                catch (GenericEntityException e) 
                {}
            }
            if (UtilValidate.isNotEmpty(allStorePickupHeaders))
            {
                storePickupTrendTotalRevenue = calcItemTotal(allStorePickupHeaders);
                context.storePickupRecentTrendTotalRevenue = storePickupTrendTotalRevenue;
                storePickupRecentOrderCount = allStorePickupHeaders.size();
                context.storePickupRecentOrderCount = storePickupRecentOrderCount;
                if (storePickupTotalRevenue > 0)
                {
                    if (storePickupTotalRevenue < storePickupTrendTotalRevenue)
                    {
                        storePickupRecentTrendRevenue= ((storePickupTotalRevenue - storePickupTrendTotalRevenue) / storePickupTotalRevenue) * 100
                        storePickupRecentTrendRevenue= storePickupRecentTrendRevenue.setScale(1,rounding);
                        context.storePickupRecentTrendRevenue = storePickupRecentTrendRevenue;
                    }
                    else
                    {
                    	if (storePickupTrendTotalRevenue != 0) 
                    	{
                            storePickupRecentTrendRevenue= ((storePickupTotalRevenue - storePickupTrendTotalRevenue) / storePickupTrendTotalRevenue) * 100
                            storePickupRecentTrendRevenue= storePickupRecentTrendRevenue.setScale(1,rounding);
                            context.storePickupRecentTrendRevenue = storePickupRecentTrendRevenue;
                    		
                    	}
                    }
                }
            }
           if (diffDays == 1)
           {
                context.periodRecTrendRange=UtilDateTime.toDateString(periodFromRecTrendTs,displayDateFormat);
           }
           else
           {
                context.periodRecTrendRange=UtilDateTime.toDateString(periodFromRecTrendTs,displayDateFormat) + " to " + UtilDateTime.toDateString(periodToRecTrendTs,displayDateFormat);
           }

       //Prior
         periodFromPriorTrendTs=UtilDateTime.getDayStart(periodFromTs,-28);
         context.periodFromPriorTrendTs = periodFromPriorTrendTs;
         periodToPriorTrendTs=OsafeAdminUtil.addDaysToTimestamp(periodToTs,-28);
         context.periodToPriorTrendTs = periodToPriorTrendTs;
            // Summary
            ecl = EntityCondition.makeCondition([
                EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS, globalContext.productStoreId),
                EntityCondition.makeCondition("statusId", EntityOperator.IN, includedOrderStatusList),
                EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "SALES_ORDER"),
                EntityCondition.makeCondition("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO, periodFromPriorTrendTs),
                EntityCondition.makeCondition("orderDate", EntityOperator.LESS_THAN_EQUAL_TO, periodToPriorTrendTs),
                EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, "DELIVERY_OPTION"),
                EntityCondition.makeCondition("attrValue", EntityOperator.EQUALS, "SHIP_TO")
            ],
            EntityOperator.AND);

            eli = delegator.findListIteratorByCondition(dve, ecl, null, fieldsToSelect, null, findOpts);
            allOnlineSaleHeaders = eli.getCompleteList();
            if (eli != null) 
            {
                try 
                {
                    eli.close();
                } 
                catch (GenericEntityException e) 
                {}
            }
            if (UtilValidate.isNotEmpty(allOnlineSaleHeaders))
            {

                trendTotalRevenue = calcItemTotal(allOnlineSaleHeaders);
                context.priorTrendTotalRevenue = trendTotalRevenue;
                priorOrderCount = allOnlineSaleHeaders.size();
                context.priorOrderCount = priorOrderCount;
                if (totalRevenue > 0)
                {
                    if (totalRevenue < trendTotalRevenue)
                    {
                        priorTrendRevenue= ((totalRevenue - trendTotalRevenue) / totalRevenue) * 100
                        priorTrendRevenue= priorTrendRevenue.setScale(1,rounding);
                        context.priorTrendRevenue = priorTrendRevenue;
                    }
                    else
                    {
                    	if (trendTotalRevenue != 0)
                    	{
                            priorTrendRevenue= ((totalRevenue - trendTotalRevenue) / trendTotalRevenue) * 100
                            priorTrendRevenue= priorTrendRevenue.setScale(1,rounding);
                            context.priorTrendRevenue = priorTrendRevenue;
                    		
                    	}
                    }
                }
            }

            // store pick up
            ecl = EntityCondition.makeCondition([
                EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS, globalContext.productStoreId),
                EntityCondition.makeCondition("statusId", EntityOperator.IN, includedOrderStatusList),
                EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "SALES_ORDER"),
                EntityCondition.makeCondition("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO, periodFromPriorTrendTs),
                EntityCondition.makeCondition("orderDate", EntityOperator.LESS_THAN_EQUAL_TO, periodToPriorTrendTs),
                EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, "DELIVERY_OPTION"),
                EntityCondition.makeCondition("attrValue", EntityOperator.EQUALS, "STORE_PICKUP")
            ],
            EntityOperator.AND);

            eli = delegator.findListIteratorByCondition(dve, ecl, null, fieldsToSelect, null, findOpts);
            allStorePickupHeaders = eli.getCompleteList();
            if (eli != null) 
            {
                try 
                {
                    eli.close();
                } 
                catch (GenericEntityException e) 
                {}
            }
            if (UtilValidate.isNotEmpty(allStorePickupHeaders))
            {

                storePickupTrendTotalRevenue = calcItemTotal(allStorePickupHeaders);
                context.storePickupPriorTrendTotalRevenue = storePickupTrendTotalRevenue;
                storePickupPriorOrderCount = allStorePickupHeaders.size();
                context.storePickupPriorOrderCount = storePickupPriorOrderCount;
                if (storePickupTotalRevenue > 0)
                {
                    if (storePickupTotalRevenue < storePickupTrendTotalRevenue)
                    {
                        storePickupPriorTrendRevenue= ((storePickupTotalRevenue - storePickupTrendTotalRevenue) / storePickupTotalRevenue) * 100
                        storePickupPriorTrendRevenue= storePickupPriorTrendRevenue.setScale(1,rounding);
                        context.storePickupPriorTrendRevenue = storePickupPriorTrendRevenue;
                    }
                    else
                    {
                   	 if (storePickupTrendTotalRevenue != 0)
                	 {
                         storePickupPriorTrendRevenue= ((storePickupTotalRevenue - storePickupTrendTotalRevenue) / storePickupTrendTotalRevenue) * 100
                         storePickupPriorTrendRevenue= storePickupPriorTrendRevenue.setScale(1,rounding);
                         context.storePickupPriorTrendRevenue = storePickupPriorTrendRevenue;
                	 }
                    }
                }
            }
           if (diffDays == 1)
           {
              context.periodPriorTrendRange=UtilDateTime.toDateString(periodFromPriorTrendTs,displayDateFormat);
           }
           else
           {
              context.periodPriorTrendRange=UtilDateTime.toDateString(periodFromPriorTrendTs,displayDateFormat) + " to " +  UtilDateTime.toDateString(periodToPriorTrendTs,displayDateFormat);
           }


       }
       else if (diffDays <=31)
       {
         //Recent From
         Calendar fromRecCal = UtilDateTime.toCalendar(periodFromTs);
         fromRecCal.add(Calendar.MONTH, -1);
         periodFromRecTrendTs=new Timestamp(fromRecCal.getTimeInMillis());
         context.periodFromRecTrendTs = periodFromRecTrendTs;
         //To
         Calendar toRecCal = UtilDateTime.toCalendar(periodToTs);
         toRecCal.add(Calendar.MONTH, -1);
         periodToRecTrendTs=new Timestamp(toRecCal.getTimeInMillis());
         context.periodToRecTrendTs = periodToRecTrendTs;
         // Summary
         ecl = EntityCondition.makeCondition([
             EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS, globalContext.productStoreId),
             EntityCondition.makeCondition("statusId", EntityOperator.IN, includedOrderStatusList),
             EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "SALES_ORDER"),
             EntityCondition.makeCondition("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO, periodFromRecTrendTs),
             EntityCondition.makeCondition("orderDate", EntityOperator.LESS_THAN_EQUAL_TO, periodToRecTrendTs),
             EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, "DELIVERY_OPTION"),
             EntityCondition.makeCondition("attrValue", EntityOperator.EQUALS, "SHIP_TO")
         ],
         EntityOperator.AND);

         eli = delegator.findListIteratorByCondition(dve, ecl, null, fieldsToSelect, null, findOpts);
         allOnlineSaleHeaders = eli.getCompleteList();
         if (eli != null) 
         {
             try 
             {
                 eli.close();
             } 
             catch (GenericEntityException e) 
             {}
         }
         if (UtilValidate.isNotEmpty(allOnlineSaleHeaders))
         {

             trendTotalRevenue = calcItemTotal(allOnlineSaleHeaders);
             context.recentTrendTotalRevenue = trendTotalRevenue;
             recentOrderCount = allOnlineSaleHeaders.size();
             context.recentOrderCount = recentOrderCount;
             if (totalRevenue > 0)
             {
                 if (totalRevenue < trendTotalRevenue)
                 {
                     recentTrendRevenue= ((totalRevenue - trendTotalRevenue) / totalRevenue) * 100
                     recentTrendRevenue= recentTrendRevenue.setScale(1,rounding);
                     context.recentTrendRevenue = recentTrendRevenue;
                 }
                 else
                 {
                	 if (trendTotalRevenue != 0)
                	 {
                         recentTrendRevenue= ((totalRevenue - trendTotalRevenue) / trendTotalRevenue) * 100
                         recentTrendRevenue= recentTrendRevenue.setScale(1,rounding);
                         context.recentTrendRevenue = recentTrendRevenue;
                	 }
                 }
             }
         }
         //store pick up
         ecl = EntityCondition.makeCondition([
             EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS, globalContext.productStoreId),
             EntityCondition.makeCondition("statusId", EntityOperator.IN, includedOrderStatusList),
             EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "SALES_ORDER"),
             EntityCondition.makeCondition("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO, periodFromRecTrendTs),
             EntityCondition.makeCondition("orderDate", EntityOperator.LESS_THAN_EQUAL_TO, periodToRecTrendTs),
             EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, "DELIVERY_OPTION"),
             EntityCondition.makeCondition("attrValue", EntityOperator.EQUALS, "STORE_PICKUP")
         ],
         EntityOperator.AND);

         eli = delegator.findListIteratorByCondition(dve, ecl, null, fieldsToSelect, null, findOpts);
         allStorePickupHeaders = eli.getCompleteList();
         if (eli != null) 
         {
             try 
             {
                 eli.close();
             } 
             catch (GenericEntityException e) 
             {}
         }
         if (UtilValidate.isNotEmpty(allStorePickupHeaders))
         {

             storePickupTrendTotalRevenue = calcItemTotal(allStorePickupHeaders);
             context.storePickupRecentTrendTotalRevenue = storePickupTrendTotalRevenue;
             storePickupRecentOrderCount = allStorePickupHeaders.size();
             context.storePickupRecentOrderCount = storePickupRecentOrderCount;
             if (storePickupTotalRevenue > 0)
             {
                 if (storePickupTotalRevenue < storePickupTrendTotalRevenue)
                 {
                     storePickupRecentTrendRevenue= ((storePickupTotalRevenue - storePickupTrendTotalRevenue) / storePickupTotalRevenue) * 100
                     storePickupRecentTrendRevenue= storePickupRecentTrendRevenue.setScale(1,rounding);
                     context.storePickupRecentTrendRevenue = storePickupRecentTrendRevenue;
                 }
                 else
                 {
                	 if (storePickupTrendTotalRevenue != 0)
                	 {
                         storePickupRecentTrendRevenue= ((storePickupTotalRevenue - storePickupTrendTotalRevenue) / storePickupTrendTotalRevenue) * 100
                         storePickupRecentTrendRevenue= storePickupRecentTrendRevenue.setScale(1,rounding);
                         context.storePickupRecentTrendRevenue = storePickupRecentTrendRevenue;
                		 
                	 }
                 }
             }
         }
         context.periodRecTrendRange=UtilDateTime.toDateString(periodFromRecTrendTs,displayDateFormat) + " to " + UtilDateTime.toDateString(periodToRecTrendTs,displayDateFormat);

         //Prior From
         Calendar fromPriorCal = UtilDateTime.toCalendar(periodFromTs);
         fromPriorCal.add(Calendar.MONTH, -2);
         periodFromPriorTrendTs=new Timestamp(fromPriorCal.getTimeInMillis());
         context.periodFromPriorTrendTs = periodFromPriorTrendTs;
         //To
         Calendar toPriorCal = UtilDateTime.toCalendar(periodToTs);
         toPriorCal.add(Calendar.MONTH, -2);
         periodToPriorTrendTs=new Timestamp(toPriorCal.getTimeInMillis());
         context.periodToPriorTrendTs = periodToPriorTrendTs;
         // Summary
         ecl = EntityCondition.makeCondition([
             EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS, globalContext.productStoreId),
             EntityCondition.makeCondition("statusId", EntityOperator.IN, includedOrderStatusList),
             EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "SALES_ORDER"),
             EntityCondition.makeCondition("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO, periodFromPriorTrendTs),
             EntityCondition.makeCondition("orderDate", EntityOperator.LESS_THAN_EQUAL_TO, periodToPriorTrendTs),
             EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, "DELIVERY_OPTION"),
             EntityCondition.makeCondition("attrValue", EntityOperator.EQUALS, "SHIP_TO")
         ],
         EntityOperator.AND);

         eli = delegator.findListIteratorByCondition(dve, ecl, null, fieldsToSelect, null, findOpts);
         allOnlineSaleHeaders = eli.getCompleteList();
         if (eli != null) 
         {
             try 
             {
                 eli.close();
             } 
             catch (GenericEntityException e) 
             {}
         }
         if (UtilValidate.isNotEmpty(allOnlineSaleHeaders))
         {

             trendTotalRevenue = calcItemTotal(allOnlineSaleHeaders);
             context.priorTrendTotalRevenue = trendTotalRevenue;
             priorOrderCount = allOnlineSaleHeaders.size();
             context.priorOrderCount = priorOrderCount;
             if (totalRevenue > 0)
             {
                 if (totalRevenue < trendTotalRevenue)
                 {
                     priorTrendRevenue= ((totalRevenue - trendTotalRevenue) / totalRevenue) * 100
                     priorTrendRevenue= priorTrendRevenue.setScale(1,rounding);
                     context.priorTrendRevenue = priorTrendRevenue;
                 }
                 else
                 {
                	 if (trendTotalRevenue != 0)
                	 {
                         priorTrendRevenue= ((totalRevenue - trendTotalRevenue) / trendTotalRevenue) * 100
                         priorTrendRevenue= priorTrendRevenue.setScale(1,rounding);
                         context.priorTrendRevenue = priorTrendRevenue;
                		 
                	 }
                 }
             }
         }

         // store pick up
         ecl = EntityCondition.makeCondition([
             EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS, globalContext.productStoreId),
             EntityCondition.makeCondition("statusId", EntityOperator.IN, includedOrderStatusList),
             EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "SALES_ORDER"),
             EntityCondition.makeCondition("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO, periodFromPriorTrendTs),
             EntityCondition.makeCondition("orderDate", EntityOperator.LESS_THAN_EQUAL_TO, periodToPriorTrendTs),
             EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, "DELIVERY_OPTION"),
             EntityCondition.makeCondition("attrValue", EntityOperator.EQUALS, "STORE_PICKUP")
         ],
         EntityOperator.AND);

         eli = delegator.findListIteratorByCondition(dve, ecl, null, fieldsToSelect, null, findOpts);
         allStorePickupHeaders = eli.getCompleteList();
         if (eli != null) 
         {
             try 
             {
                 eli.close();
             } 
             catch (GenericEntityException e) 
             {}
         }
         if (UtilValidate.isNotEmpty(allStorePickupHeaders))
         {

             storePickupTrendTotalRevenue = calcItemTotal(allStorePickupHeaders);
             context.storePickupPriorTrendTotalRevenue = storePickupTrendTotalRevenue;
             storePickupPriorOrderCount = allStorePickupHeaders.size();
             context.storePickupPriorOrderCount = storePickupPriorOrderCount;
             if (storePickupTotalRevenue > 0)
             {
                 if (storePickupTotalRevenue < storePickupTrendTotalRevenue)
                 {
                     storePickupPriorTrendRevenue= ((storePickupTotalRevenue - storePickupTrendTotalRevenue) / storePickupTotalRevenue) * 100
                     storePickupPriorTrendRevenue= storePickupPriorTrendRevenue.setScale(1,rounding);
                     context.storePickupPriorTrendRevenue = storePickupPriorTrendRevenue;
                 }
                 else
                 {
                	 if (storePickupTrendTotalRevenue != 0)
                	 {
                         storePickupPriorTrendRevenue= ((storePickupTotalRevenue - storePickupTrendTotalRevenue) / storePickupTrendTotalRevenue) * 100
                         storePickupPriorTrendRevenue= storePickupPriorTrendRevenue.setScale(1,rounding);
                         context.storePickupPriorTrendRevenue = storePickupPriorTrendRevenue;
                		 
                	 }
                 }
             }
         }
         context.periodPriorTrendRange=UtilDateTime.toDateString(periodFromPriorTrendTs,displayDateFormat) + " to " +  UtilDateTime.toDateString(periodToPriorTrendTs,displayDateFormat);
       }
       else if (diffDays <= 365)
       {
       //Recent
         //From
         Calendar fromRecCal = UtilDateTime.toCalendar(periodFromTs);
         fromRecCal.add(Calendar.YEAR, -1);
         periodFromRecTrendTs=new Timestamp(fromRecCal.getTimeInMillis());
         context.periodFromRecTrendTs = periodFromRecTrendTs;
         //To
         Calendar toRecCal = UtilDateTime.toCalendar(periodToTs);
         toRecCal.add(Calendar.YEAR, -1);
         periodToRecTrendTs=new Timestamp(toRecCal.getTimeInMillis());
         context.periodToRecTrendTs = periodToRecTrendTs;
         // Summary
         ecl = EntityCondition.makeCondition([
             EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS, globalContext.productStoreId),
             EntityCondition.makeCondition("statusId", EntityOperator.IN, includedOrderStatusList),
             EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "SALES_ORDER"),
             EntityCondition.makeCondition("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO, periodFromRecTrendTs),
             EntityCondition.makeCondition("orderDate", EntityOperator.LESS_THAN_EQUAL_TO, periodToRecTrendTs),
             EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, "DELIVERY_OPTION"),
             EntityCondition.makeCondition("attrValue", EntityOperator.EQUALS, "SHIP_TO")
         ],
         EntityOperator.AND);

         eli = delegator.findListIteratorByCondition(dve, ecl, null, fieldsToSelect, null, findOpts);
         allOnlineSaleHeaders = eli.getCompleteList();
         if (eli != null) 
         {
             try 
             {
                 eli.close();
             } 
             catch (GenericEntityException e) 
             {}
         }
         if (UtilValidate.isNotEmpty(allOnlineSaleHeaders))
         {

             trendTotalRevenue = calcItemTotal(allOnlineSaleHeaders);
             context.recentTrendTotalRevenue = trendTotalRevenue;
             recentOrderCount = allOnlineSaleHeaders.size();
             context.recentOrderCount = recentOrderCount;
             if (totalRevenue > 0)
             {
                 if (totalRevenue < trendTotalRevenue)
                 {
                     recentTrendRevenue= ((totalRevenue - trendTotalRevenue) / totalRevenue) * 100
                     recentTrendRevenue= recentTrendRevenue.setScale(1,rounding);
                     context.recentTrendRevenue = recentTrendRevenue;
                 }
                 else
                 {
                	 if (trendTotalRevenue != 0) 
                	 {
                         recentTrendRevenue= ((totalRevenue - trendTotalRevenue) / trendTotalRevenue) * 100
                         recentTrendRevenue= recentTrendRevenue.setScale(1,rounding);
                         context.recentTrendRevenue = recentTrendRevenue;
                		 
                	 }
                 }
             }
         }
         //store pick up
         ecl = EntityCondition.makeCondition([
             EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS, globalContext.productStoreId),
             EntityCondition.makeCondition("statusId", EntityOperator.IN, includedOrderStatusList),
             EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "SALES_ORDER"),
             EntityCondition.makeCondition("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO, periodFromRecTrendTs),
             EntityCondition.makeCondition("orderDate", EntityOperator.LESS_THAN_EQUAL_TO, periodToRecTrendTs),
             EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, "DELIVERY_OPTION"),
             EntityCondition.makeCondition("attrValue", EntityOperator.EQUALS, "STORE_PICKUP")
         ],
         EntityOperator.AND);

         eli = delegator.findListIteratorByCondition(dve, ecl, null, fieldsToSelect, null, findOpts);
         allStorePickupHeaders = eli.getCompleteList();
         if (eli != null) 
         {
             try 
             {
                 eli.close();
             } 
             catch (GenericEntityException e) 
             {}
         }
         if (UtilValidate.isNotEmpty(allStorePickupHeaders))
         {

             storePickupTrendTotalRevenue = calcItemTotal(allStorePickupHeaders);
             context.storePickupRecentTrendTotalRevenue = storePickupTrendTotalRevenue;
             storePickupRecentOrderCount = allStorePickupHeaders.size();
             context.storePickupRecentOrderCount = storePickupRecentOrderCount;
             if (storePickupTotalRevenue > 0)
             {
                 if (storePickupTotalRevenue < storePickupTrendTotalRevenue)
                 {
                     storePickupRecentTrendRevenue= ((storePickupTotalRevenue - storePickupTrendTotalRevenue) / storePickupTotalRevenue) * 100
                     storePickupRecentTrendRevenue= storePickupRecentTrendRevenue.setScale(1,rounding);
                     context.storePickupRecentTrendRevenue = storePickupRecentTrendRevenue;
                 }
                 else
                 {
                	 if (storePickupTrendTotalRevenue != 0)
                	 {
                         storePickupRecentTrendRevenue= ((storePickupTotalRevenue - storePickupTrendTotalRevenue) / storePickupTrendTotalRevenue) * 100
                         storePickupRecentTrendRevenue= storePickupRecentTrendRevenue.setScale(1,rounding);
                         context.storePickupRecentTrendRevenue = storePickupRecentTrendRevenue;
                		 
                	 }
                 }
             }
         }
         context.periodRecTrendRange=UtilDateTime.toDateString(periodFromRecTrendTs,displayDateFormat) + " to " + UtilDateTime.toDateString(periodToRecTrendTs,displayDateFormat);

       }
    }
//TRENDS


//TOP PRODUCTS

    // top product dynamic view entity
    DynamicViewEntity topProductDve = new DynamicViewEntity();
    topProductDve.addMemberEntity("OH", "OrderHeader");
    topProductDve.addAlias("OH", "orderTypeId", "orderTypeId", null, null, null, null);
    topProductDve.addAlias("OH", "orderDate", "orderDate", null, null, null, null);
    //make relation with OrderItem
    topProductDve.addRelation("many", "", "OrderItem", UtilMisc.toList(new ModelKeyMap("orderId", "orderId")));
    topProductDve.addMemberEntity("OI", "OrderItem");
    topProductDve.addAlias("OI", "quantityOrdered", "quantity", null, null, null, "sum")
    topProductDve.addAlias("OI", "unitPrice", "unitPrice", null, null, null, "sum");
    topProductDve.addAlias("OI", "productId", "productId", null, null, Boolean.TRUE, null);
    topProductDve.addViewLink("OH", "OI", Boolean.FALSE, UtilMisc.toList(new ModelKeyMap("orderId", "orderId")));
    //make relation with OrderAttribute
    topProductDve.addRelation("many", "", "OrderAttribute", UtilMisc.toList(new ModelKeyMap("orderId", "orderId")));
    topProductDve.addMemberEntity("OA", "OrderAttribute");
    topProductDve.addAlias("OA", "attrName", "attrName", null, null, null, null);
    topProductDve.addAlias("OA", "attrValue", "attrValue", null, null, null, null);
    topProductDve.addViewLink("OH", "OA", Boolean.FALSE, UtilMisc.toList(new ModelKeyMap("orderId", "orderId")));
    //make relation with OrderRole
    topProductDve.addRelation("many", "", "OrderRole", UtilMisc.toList(new ModelKeyMap("orderId", "orderId")));
    topProductDve.addMemberEntity("RL", "OrderRole");
    topProductDve.addAlias("RL", "roleTypeId", "roleTypeId", null, null, null, null);
    topProductDve.addViewLink("OH", "RL", Boolean.FALSE, UtilMisc.toList(new ModelKeyMap("orderId", "orderId")));
    //make relation with ProductStore
    topProductDve.addRelation("one-fk", "", "ProductStore", UtilMisc.toList(new ModelKeyMap("productStoreId", "productStoreId")));
    topProductDve.addMemberEntity("PS", "ProductStore");
    topProductDve.addAlias("PS", "productStoreId", "productStoreId", null, null, null, null);
    topProductDve.addViewLink("OH", "PS", Boolean.FALSE, UtilMisc.toList(new ModelKeyMap("productStoreId", "productStoreId")));

    orderBy = UtilMisc.toList("-quantityOrdered");
    //FieldsToSelect
    List topProductFields = FastList.newInstance();
    topProductFields.add("productId");
    topProductFields.add("quantityOrdered");
    topProductFields.add("unitPrice");
    // set distinct
    topProductFindOpts = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, true);

    List <GenericValue> topProductsList  = FastList.newInstance();
    Map<String, GenericValue> allTopProductsMap = FastMap.newInstance();
    GenericValue workingReportProduct = null;
    String productId = null;

    ecl = EntityCondition.makeCondition([
        EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS, globalContext.productStoreId),
        EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "BILL_TO_CUSTOMER"),
        EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "SALES_ORDER"),
        EntityCondition.makeCondition("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO, periodFromTs),
        EntityCondition.makeCondition("orderDate", EntityOperator.LESS_THAN_EQUAL_TO, periodToTs),
        EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, "DELIVERY_OPTION"),
        EntityCondition.makeCondition("attrValue", EntityOperator.EQUALS, "SHIP_TO")
    ],
    EntityOperator.AND);
    eli = delegator.findListIteratorByCondition(topProductDve, ecl, null, topProductFields, orderBy, topProductFindOpts);
    topOrderSalesProductList = eli.getCompleteList();
    if (eli != null) 
    {
        try 
        {
            eli.close();
        } 
        catch (GenericEntityException e) 
        {}
    }

    // for each product look up content wrapper
    if (topOrderSalesProductList != null) 
    {
        for (GenericValue orderReportProduct : topOrderSalesProductList) 
        {
            GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", orderReportProduct.getString("productId")), false);
            orderItemList = delegator.findByAnd("OrderItem", UtilMisc.toMap("productId", orderReportProduct.getString("productId")));
            orderItemListItr = orderItemList.iterator();
            GenericValue orderItemGv = null;
            while (orderItemListItr.hasNext())
            {
                orderItemGv = (GenericValue)orderItemListItr.next();
            }
            totalPrice = orderReportProduct.getBigDecimal("quantityOrdered") * orderItemGv.getBigDecimal("unitPrice");
            orderReportProduct.set("unitPrice", totalPrice);
            productId = product.getString("productId");
            if ("Y".equals(product.getString("isVariant"))) 
            {
                // look up the virtual product
                GenericValue parent = ProductWorker.getParentProduct(productId, delegator);
                if (parent != null) 
                {
                    productId = parent.getString("productId");
                    workingReportProduct = allTopProductsMap.get(productId);
                    if (workingReportProduct == null) 
                    {
                        workingReportProduct = GenericValue.create(orderReportProduct);
                        workingReportProduct.setString("productId", productId);
                    } else 
                    {
                        sumCol(workingReportProduct, orderReportProduct, "quantityOrdered");
                        sumCol(workingReportProduct, orderReportProduct, "unitPrice");
                    }
                    allTopProductsMap.put(productId, workingReportProduct);
                }
            } 
            else 
            {
                workingReportProduct = allTopProductsMap.get(productId);
                if (workingReportProduct == null) 
                {
                    workingReportProduct = GenericValue.create(orderReportProduct);
                }
                else 
                {
                    sumCol(workingReportProduct, orderReportProduct, "quantityOrdered");
                    sumCol(workingReportProduct, orderReportProduct, "unitPrice");
                }
                allTopProductsMap.put(productId, workingReportProduct);
            }
        }
    }

    topProductsList.addAll(allTopProductsMap.values());

    // for each product look up content wrapper
    Map topProductContentWrappers = null;
    if (UtilValidate.isNotEmpty(topProductsList))
    {
        // Trim list
        if(topProductsList.size() >5) 
        {
            topProductsList = topProductsList.subList(0,5);
        }

        topProductContentWrappers = FastMap.newInstance();
        for (GenericValue topProduct: topProductsList) 
        {
            GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", topProduct.productId), false);
            ProductContentWrapper productContentWrapper = new ProductContentWrapper(product, request);
            topProductContentWrappers.put(topProduct.productId, productContentWrapper);
        }
        context.topProductContentWrappers = topProductContentWrappers;
    }
    context.topProductsList = topProductsList;

    // Store Pick Up Top Products
    List <GenericValue> storePickupTopProductsList  = FastList.newInstance();
    allTopProductsMap = FastMap.newInstance();
    workingReportProduct = null;
    productId = null;

    ecl = EntityCondition.makeCondition([
        EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS, globalContext.productStoreId),
        EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "BILL_TO_CUSTOMER"),
        EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "SALES_ORDER"),
        EntityCondition.makeCondition("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO, periodFromTs),
        EntityCondition.makeCondition("orderDate", EntityOperator.LESS_THAN_EQUAL_TO, periodToTs),
        EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, "DELIVERY_OPTION"),
        EntityCondition.makeCondition("attrValue", EntityOperator.EQUALS, "STORE_PICKUP")
    ],
    EntityOperator.AND);
    eli = delegator.findListIteratorByCondition(topProductDve, ecl, null, topProductFields, orderBy, topProductFindOpts);
    topOrderSalesProductList = eli.getCompleteList();
    if (eli != null) 
    {
        try 
        {
            eli.close();
        } 
        catch (GenericEntityException e) 
        {}
    }

    // for each product look up content wrapper
    if (topOrderSalesProductList != null) 
    {
        for (GenericValue orderReportProduct : topOrderSalesProductList) 
        {
            GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", orderReportProduct.getString("productId")), false);
            orderItemList = delegator.findByAnd("OrderItem", UtilMisc.toMap("productId", orderReportProduct.getString("productId")));
            orderItemListItr = orderItemList.iterator();
            GenericValue orderItemGv = null;
            while (orderItemListItr.hasNext())
            {
                orderItemGv = (GenericValue)orderItemListItr.next();
            }
            totalPrice = orderReportProduct.getBigDecimal("quantityOrdered") * orderItemGv.getBigDecimal("unitPrice");
            orderReportProduct.set("unitPrice", totalPrice);
            productId = product.getString("productId");
            if ("Y".equals(product.getString("isVariant"))) 
            {
                // look up the virtual product
                GenericValue parent = ProductWorker.getParentProduct(productId, delegator);
                if (parent != null) 
                {
                    productId = parent.getString("productId");
                    workingReportProduct = allTopProductsMap.get(productId);
                    if (workingReportProduct == null) {
                        workingReportProduct = GenericValue.create(orderReportProduct);
                        workingReportProduct.setString("productId", productId);
                    }
                    else 
                    {
                        sumCol(workingReportProduct, orderReportProduct, "quantityOrdered");
                        sumCol(workingReportProduct, orderReportProduct, "unitPrice");
                    }
                    allTopProductsMap.put(productId, workingReportProduct);
                }
            } 
            else 
            {
                workingReportProduct = allTopProductsMap.get(productId);
                if (workingReportProduct == null) 
                {
                    workingReportProduct = GenericValue.create(orderReportProduct);
                }
                else 
                {
                    sumCol(workingReportProduct, orderReportProduct, "quantityOrdered");
                    sumCol(workingReportProduct, orderReportProduct, "unitPrice");
                }
                allTopProductsMap.put(productId, workingReportProduct);
            }
        }
    }

    storePickupTopProductsList.addAll(allTopProductsMap.values());

    // for each product look up content wrapper
    Map storePickupTopProductContentWrappers = null;
    if (UtilValidate.isNotEmpty(storePickupTopProductsList))
    {
        // Trim list
        if(storePickupTopProductsList.size() > 5) 
        {
            storePickupTopProductsList = storePickupTopProductsList.subList(0,5);
        }

        storePickupTopProductContentWrappers = FastMap.newInstance();
        for (GenericValue topProduct: storePickupTopProductsList) 
        {
            GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", topProduct.productId), false);
            ProductContentWrapper productContentWrapper = new ProductContentWrapper(product, request);
            storePickupTopProductContentWrappers.put(topProduct.productId, productContentWrapper);
        }
        context.storePickupTopProductContentWrappers = storePickupTopProductContentWrappers;
    }
    context.storePickupTopProductsList = storePickupTopProductsList;
//TOP PRODUCTS
    
//TOP PROMOTIONS
    // top promotion dynamic view entity
    DynamicViewEntity topPromotionDve = new DynamicViewEntity();
    topPromotionDve.addMemberEntity("OH", "OrderHeader");
    topPromotionDve.addAlias("OH", "totalOrders", "orderTypeId", null, null, null, "count");
    topPromotionDve.addAlias("OH", "orderDate", "orderDate", null, null, null, null);
    //make relation with OrderItem
    topPromotionDve.addRelation("many", "", "OrderProductPromoCode", UtilMisc.toList(new ModelKeyMap("orderId", "orderId")));
    topPromotionDve.addMemberEntity("OPPC", "OrderProductPromoCode");
    topPromotionDve.addAlias("OPPC", "productPromoCodeId", "productPromoCodeId", null, null, Boolean.TRUE, null);
    topPromotionDve.addViewLink("OH", "OPPC", Boolean.FALSE, UtilMisc.toList(new ModelKeyMap("orderId", "orderId")));
    //make relation with ProductStore
    topPromotionDve.addRelation("one-fk", "", "ProductStore", UtilMisc.toList(new ModelKeyMap("productStoreId", "productStoreId")));
    topPromotionDve.addMemberEntity("PS", "ProductStore");
    topPromotionDve.addAlias("PS", "productStoreId", "productStoreId", null, null, null, null);
    topPromotionDve.addViewLink("OH", "PS", Boolean.FALSE, UtilMisc.toList(new ModelKeyMap("productStoreId", "productStoreId")));


    orderBy = UtilMisc.toList("-totalOrders");
    //FieldsToSelect
    List topPromotionFields = FastList.newInstance();
    topPromotionFields.add("totalOrders");
    topPromotionFields.add("productPromoCodeId");
    // set distinct
    topPromotionFindOpts = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, true);

    ecl = EntityCondition.makeCondition([
        EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS, globalContext.productStoreId),
        EntityCondition.makeCondition("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO, periodFromTs),
        EntityCondition.makeCondition("orderDate", EntityOperator.LESS_THAN_EQUAL_TO, periodToTs)
    ],
    EntityOperator.AND);

    eli = delegator.findListIteratorByCondition(topPromotionDve, ecl, null, topPromotionFields, orderBy, topPromotionFindOpts);
    topPromotionsList = eli.getCompleteList();
    if (eli != null) 
    {
        try 
        {
            eli.close();
        } 
        catch (GenericEntityException e) 
        {}
    }
    if (UtilValidate.isNotEmpty(topPromotionsList))
    {
        if(topPromotionsList.size() >5) 
        {
            topPromotionsList = topPromotionsList.subList(0,5);
        }
    }
    context.topPromotionsList = topPromotionsList;
//TOP PROMOTIONS    
}



