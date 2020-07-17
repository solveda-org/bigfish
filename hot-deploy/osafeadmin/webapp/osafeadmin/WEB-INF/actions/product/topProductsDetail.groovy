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
import org.ofbiz.product.product.ProductContentWrapper;
import org.ofbiz.product.product.ProductWorker;

import org.apache.commons.lang.StringUtils;

void sumCol(GenericValue gv1, GenericValue gv2, String columnName) {
    BigDecimal result = BigDecimal.ZERO;
    result = gv1.getBigDecimal(columnName);
    if (result != null) {
      result = result.add(gv2.getBigDecimal(columnName));
    } else {
       result = gv2.getBigDecimal(columnName);
    }
    gv1.set(columnName, result);
}

srchall = StringUtils.trimToEmpty(parameters.srchall);
srchShipTo = StringUtils.trimToEmpty(parameters.srchShipTo);
srchStorePickup = StringUtils.trimToEmpty(parameters.srchStorePickup);
deliveryOption = "";
if (UtilValidate.isNotEmpty(srchShipTo) && UtilValidate.isEmpty(srchStorePickup)) 
{
    deliveryOption = "SHIP_TO";
} else if(UtilValidate.isEmpty(srchShipTo) && UtilValidate.isNotEmpty(srchStorePickup)) 
{
    deliveryOption = "STORE_PICKUP";
} else 
{
    deliveryOption = "ANY";
}

periodFromTs= context.periodFromTs
periodToTs= context.periodToTs
isValidDate = context.isValidDate;
if(isValidDate)
{
// Top Products
    List <GenericValue> topProductsList  = FastList.newInstance();
    Map<String, GenericValue> allTopProductsMap = FastMap.newInstance();
    Map<String, String> allTopProductsOrderMap = FastMap.newInstance();
    GenericValue workingReportProduct = null;
    String productId = null;

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

    // dynamic view entity
    DynamicViewEntity dve = new DynamicViewEntity();
    dve.addMemberEntity("OH", "OrderHeader");
    dve.addAliasAll("OH", ""); // no prefix
    dve.addRelation("many", "", "OrderAttribute", UtilMisc.toList(new ModelKeyMap("orderId", "orderId")));
    dve.addRelation("many", "", "OrderItem", UtilMisc.toList(new ModelKeyMap("orderId", "orderId")));
    dve.addRelation("many", "", "OrderRole", UtilMisc.toList(new ModelKeyMap("orderId", "orderId")));
    dve.addMemberEntity("OA", "OrderAttribute");
    dve.addAliasAll("OA", ""); // no prefix
    dve.addViewLink("OH", "OA", Boolean.FALSE, UtilMisc.toList(new ModelKeyMap("orderId", "orderId")));
    dve.addMemberEntity("OI", "OrderItem");
    dve.addAliasAll("OI", ""); // no prefix
    dve.addViewLink("OH", "OI", Boolean.FALSE, UtilMisc.toList(new ModelKeyMap("orderId", "orderId")));
    dve.addMemberEntity("OT", "OrderRole");
    dve.addAliasAll("OT", ""); // no prefix
    dve.addViewLink("OH", "OT", Boolean.FALSE, UtilMisc.toList(new ModelKeyMap("orderId", "orderId")));
    List fieldsToSelect = FastList.newInstance();
    fieldsToSelect.add("orderId");
    // set distinct on so we only get one row per order
    EntityFindOptions findOpts = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, true);

    if (deliveryOption.equals("ANY"))
    {
        //for ship to
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
        orderReportSalesList = eli.getCompleteList();
        if (eli != null) {
            try {
                eli.close();
            } catch (GenericEntityException e) {}
        }
        varientProductOrder = 0;
        virtualProductOrder = 0;
        noOfOrders = 0;
        // for each product look up content wrapper
        if (UtilValidate.isNotEmpty(orderReportSalesList))
        {
            for (GenericValue orderReportProduct : orderReportSalesList) 
            {
                GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", orderReportProduct.getString("productId")), false);
                
                /*get the unit price of a product and multiply it with the quantity ordered */
                orderItemList = delegator.findByAnd("OrderItem", UtilMisc.toMap("productId", orderReportProduct.getString("productId")));
                orderItemListItr = orderItemList.iterator();
                GenericValue orderItemGv = null;
                while (orderItemListItr.hasNext())
                {
                    orderItemGv = (GenericValue)orderItemListItr.next();
                }
                totalPrice = orderReportProduct.getBigDecimal("quantityOrdered") * orderItemGv.getBigDecimal("unitPrice");
                
                /*set this totalprice to the genericvalue */
                orderReportProduct.set("unitPrice", totalPrice);
                productId = product.getString("productId");
                
                /*get the total noOfOrders for a particular product*/
                eclOrder = EntityCondition.makeCondition([
                    EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS, globalContext.productStoreId),
                    EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId), 
                    EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "BILL_TO_CUSTOMER"),
                    EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "SALES_ORDER"),
                    EntityCondition.makeCondition("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO, periodFromTs),
                    EntityCondition.makeCondition("orderDate", EntityOperator.LESS_THAN_EQUAL_TO, periodToTs),
                    EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, "DELIVERY_OPTION"),
                    EntityCondition.makeCondition("attrValue", EntityOperator.EQUALS, "SHIP_TO")
                ],
                EntityOperator.AND);
//                orderList = delegator.findList("OrderHeaderItemAndRoles", eclOrder, null, null, null, false);
                eli = delegator.findListIteratorByCondition(dve, eclOrder, null, fieldsToSelect, null, findOpts);
                orderList = eli.getCompleteList();
                if (eli != null) 
                {
                    try {
                        eli.close();
                    } catch (GenericEntityException e) {}
                }
                noOfOrders = orderList.size();
                if ("Y".equals(product.getString("isVariant"))) 
                {
                    // look up the virtual product
                    GenericValue parent = ProductWorker.getParentProduct(productId, delegator);
                    if (UtilValidate.isNotEmpty(parent))
                    {
                        productId = parent.getString("productId");
                        productId = productId+"^SHIP_TO";
                        workingReportProduct = allTopProductsMap.get(productId);
                        if (workingReportProduct == null) 
                        {
                            workingReportProduct = GenericValue.create(orderReportProduct);
                            workingReportProduct.setString("productId", productId);
                            varientProductOrder = noOfOrders;
                        } else 
                        {
                            sumCol(workingReportProduct, orderReportProduct, "quantityOrdered");
                            sumCol(workingReportProduct, orderReportProduct, "unitPrice");
                            varientProductOrder = noOfOrders + allTopProductsOrderMap.get(productId);
                        }
                        allTopProductsMap.put(productId, workingReportProduct);
                        allTopProductsOrderMap.put(productId, varientProductOrder);
                    }
                } else 
                {
                    productId = product.getString("productId")+"^SHIP_TO";
                    workingReportProduct = allTopProductsMap.get(productId);
                    if (workingReportProduct == null) 
                    {
                        workingReportProduct = GenericValue.create(orderReportProduct);
                        workingReportProduct.setString("productId", productId);
                        virtualProductOrder = noOfOrders;
                    } else 
                    {
                        sumCol(workingReportProduct, orderReportProduct, "quantityOrdered");
                        sumCol(workingReportProduct, orderReportProduct, "unitPrice");
                        virtualProductOrder = noOfOrders + allTopProductsOrderMap.get(productId);
                    }
                    allTopProductsMap.put(productId, workingReportProduct);
                    allTopProductsOrderMap.put(productId, virtualProductOrder);
                }
            }
        }

        //for store pickup
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
        orderReportSalesList = eli.getCompleteList();
        if (eli != null) 
        {
            try {
                eli.close();
            } catch (GenericEntityException e) {}
        }
        varientProductOrder = 0;
        virtualProductOrder = 0;
        noOfOrders = 0;
        // for each product look up content wrapper
        if (UtilValidate.isNotEmpty(orderReportSalesList))
        {
            for (GenericValue orderReportProduct : orderReportSalesList) 
            {
                GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", orderReportProduct.getString("productId")), false);
                
                /*get the unit price of a product and multiply it with the quantity ordered */
                orderItemList = delegator.findByAnd("OrderItem", UtilMisc.toMap("productId", orderReportProduct.getString("productId")));
                orderItemListItr = orderItemList.iterator();
                GenericValue orderItemGv = null;
                while (orderItemListItr.hasNext())
                {
                    orderItemGv = (GenericValue)orderItemListItr.next();
                }
                totalPrice = orderReportProduct.getBigDecimal("quantityOrdered") * orderItemGv.getBigDecimal("unitPrice");
                
                /*set this totalprice to the genericvalue */
                orderReportProduct.set("unitPrice", totalPrice);
                productId = product.getString("productId");
                
                /*get the total noOfOrders for a particular product*/
                eclOrder = EntityCondition.makeCondition([
                    EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS, globalContext.productStoreId),
                    EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId), 
                    EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "BILL_TO_CUSTOMER"),
                    EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "SALES_ORDER"),
                    EntityCondition.makeCondition("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO, periodFromTs),
                    EntityCondition.makeCondition("orderDate", EntityOperator.LESS_THAN_EQUAL_TO, periodToTs),
                    EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, "DELIVERY_OPTION"),
                    EntityCondition.makeCondition("attrValue", EntityOperator.EQUALS, "STORE_PICKUP")
                ],
                EntityOperator.AND);
//                orderList = delegator.findList("OrderHeaderItemAndRoles", eclOrder, null, null, null, false);
                eli = delegator.findListIteratorByCondition(dve, eclOrder, null, fieldsToSelect, null, findOpts);
                orderList = eli.getCompleteList();
                if (eli != null) 
                {
                    try {
                        eli.close();
                    } catch (GenericEntityException e) {}
                }
                noOfOrders = orderList.size();
                if ("Y".equals(product.getString("isVariant"))) 
                {
                    // look up the virtual product
                    GenericValue parent = ProductWorker.getParentProduct(productId, delegator);
                    if (UtilValidate.isNotEmpty(parent))
                    {
                        productId = parent.getString("productId");
                        productId = productId+"^STORE_PICKUP";
                        workingReportProduct = allTopProductsMap.get(productId);
                        if (workingReportProduct == null) 
                        {
                            workingReportProduct = GenericValue.create(orderReportProduct);
                            workingReportProduct.setString("productId", productId);
                            varientProductOrder = noOfOrders;
                        } else 
                        {
                            sumCol(workingReportProduct, orderReportProduct, "quantityOrdered");
                            sumCol(workingReportProduct, orderReportProduct, "unitPrice");
                            varientProductOrder = noOfOrders + allTopProductsOrderMap.get(productId);
                        }
                        allTopProductsMap.put(productId, workingReportProduct);
                        allTopProductsOrderMap.put(productId, varientProductOrder);
                    }
                } else 
                {
                    productId = product.getString("productId")+"^STORE_PICKUP";
                    workingReportProduct = allTopProductsMap.get(productId);
                    if (workingReportProduct == null) 
                    {
                        workingReportProduct = GenericValue.create(orderReportProduct);
                        workingReportProduct.setString("productId", productId);
                        virtualProductOrder = noOfOrders;
                    } else 
                    {
                        sumCol(workingReportProduct, orderReportProduct, "quantityOrdered");
                        sumCol(workingReportProduct, orderReportProduct, "unitPrice");
                        virtualProductOrder = noOfOrders + allTopProductsOrderMap.get(productId);
                    }
                    allTopProductsMap.put(productId, workingReportProduct);
                    allTopProductsOrderMap.put(productId, virtualProductOrder);
                }
            }
        }
    }
    else 
    {
        ecl = EntityCondition.makeCondition([
            EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS, globalContext.productStoreId),
            EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "BILL_TO_CUSTOMER"),
            EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "SALES_ORDER"),
            EntityCondition.makeCondition("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO, periodFromTs),
            EntityCondition.makeCondition("orderDate", EntityOperator.LESS_THAN_EQUAL_TO, periodToTs),
            EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, "DELIVERY_OPTION"),
            EntityCondition.makeCondition("attrValue", EntityOperator.EQUALS, deliveryOption)
        ],
        EntityOperator.AND);
        eli = delegator.findListIteratorByCondition(topProductDve, ecl, null, topProductFields, orderBy, topProductFindOpts);
        orderReportSalesList = eli.getCompleteList();
        if (eli != null) 
        {
            try {
                eli.close();
            } catch (GenericEntityException e) {}
        }
        varientProductOrder = 0;
        virtualProductOrder = 0;
        noOfOrders = 0;
        // for each product look up content wrapper
        if (UtilValidate.isNotEmpty(orderReportSalesList)) 
        {
            for (GenericValue orderReportProduct : orderReportSalesList) 
            {
                GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", orderReportProduct.getString("productId")), false);
                
                /*get the unit price of a product and multiply it with the quantity ordered */
                orderItemList = delegator.findByAnd("OrderItem", UtilMisc.toMap("productId", orderReportProduct.getString("productId")));
                orderItemListItr = orderItemList.iterator();
                GenericValue orderItemGv = null;
                while (orderItemListItr.hasNext())
                {
                    orderItemGv = (GenericValue)orderItemListItr.next();
                }
                totalPrice = orderReportProduct.getBigDecimal("quantityOrdered") * orderItemGv.getBigDecimal("unitPrice");
                
                /*set this totalprice to the genericvalue */
                orderReportProduct.set("unitPrice", totalPrice);
                productId = product.getString("productId");
                
                /*get the total noOfOrders for a particular product*/
                eclOrder = EntityCondition.makeCondition([
                    EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS, globalContext.productStoreId),
                    EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId), 
                    EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "BILL_TO_CUSTOMER"),
                    EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "SALES_ORDER"),
                    EntityCondition.makeCondition("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO, periodFromTs),
                    EntityCondition.makeCondition("orderDate", EntityOperator.LESS_THAN_EQUAL_TO, periodToTs),
                    EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, "DELIVERY_OPTION"),
                    EntityCondition.makeCondition("attrValue", EntityOperator.EQUALS, deliveryOption)
                ],
                EntityOperator.AND);
//                orderList = delegator.findList("OrderHeaderItemAndRoles", eclOrder, null, null, null, false);
                eli = delegator.findListIteratorByCondition(dve, eclOrder, null, fieldsToSelect, null, findOpts);
                orderList = eli.getCompleteList();
                if (eli != null) 
                {
                    try {
                        eli.close();
                    } catch (GenericEntityException e) {}
                }
                noOfOrders = orderList.size();
                if ("Y".equals(product.getString("isVariant"))) 
                {
                    // look up the virtual product
                    GenericValue parent = ProductWorker.getParentProduct(productId, delegator);
                    if (UtilValidate.isNotEmpty(parent))
                    {
                        productId = parent.getString("productId");
                        productId = productId+"^"+deliveryOption;
                        workingReportProduct = allTopProductsMap.get(productId);
                        if (workingReportProduct == null) 
                        {
                            workingReportProduct = GenericValue.create(orderReportProduct);
                            workingReportProduct.setString("productId", productId);
                            varientProductOrder = noOfOrders;
                        } else 
                        {
                            sumCol(workingReportProduct, orderReportProduct, "quantityOrdered");
                            sumCol(workingReportProduct, orderReportProduct, "unitPrice");
                            varientProductOrder = noOfOrders + allTopProductsOrderMap.get(productId);
                        }
                        allTopProductsMap.put(productId, workingReportProduct);
                        allTopProductsOrderMap.put(productId, varientProductOrder);
                    }
                } else 
                {
                    productId = product.getString("productId")+"^"+deliveryOption;
                    workingReportProduct = allTopProductsMap.get(productId);
                    if (workingReportProduct == null) 
                    {
                        workingReportProduct = GenericValue.create(orderReportProduct);
                        workingReportProduct.setString("productId", productId);
                        virtualProductOrder = noOfOrders;
                    } else 
                    {
                        sumCol(workingReportProduct, orderReportProduct, "quantityOrdered");
                        sumCol(workingReportProduct, orderReportProduct, "unitPrice");
                        virtualProductOrder = noOfOrders + allTopProductsOrderMap.get(productId);
                    }
                    allTopProductsMap.put(productId, workingReportProduct);
                    allTopProductsOrderMap.put(productId, virtualProductOrder);
                }
            }
        }
    }
    topProductsList.addAll(allTopProductsMap.values());
    // for each product look up content wrapper
    Map topProductContentWrappers = null;
    if (UtilValidate.isNotEmpty(topProductsList))
    {
        topProductContentWrappers = FastMap.newInstance();
        for (GenericValue topProduct: topProductsList) 
        {
            productId = topProduct.productId;
            if(productId.indexOf("^")!= -1) 
            {
                productId = productId.substring(0,productId.indexOf("^"))
            }
            GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
            ProductContentWrapper productContentWrapper = new ProductContentWrapper(product, request);
            topProductContentWrappers.put(productId, productContentWrapper);
        }
        context.topProductContentWrappers = topProductContentWrappers;
    }
    context.topProductsList = topProductsList;
    context.allTopProductsOrderMap = allTopProductsOrderMap;
}