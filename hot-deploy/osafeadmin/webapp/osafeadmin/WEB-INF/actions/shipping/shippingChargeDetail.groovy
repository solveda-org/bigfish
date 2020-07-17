package promotion;

import java.util.List;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.apache.commons.lang.StringUtils;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.entity.model.DynamicViewEntity;
import org.ofbiz.entity.model.ModelKeyMap;
import org.ofbiz.entity.util.EntityFindOptions;


productStoreId=globalContext.productStoreId;
productStoreShipMethId=parameters.productStoreShipMethId;

partysOps = new EntityFindOptions();
partysOps.setDistinct(true);
Set<String> party = new TreeSet<String>();
party.add("partyId");

conditions = FastList.newInstance();
conditions.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "CARRIER"));
mainCond = EntityCondition.makeCondition(conditions, EntityOperator.AND);

partys = delegator.findList("PartyRole", mainCond, party, null, partysOps, false);

if (UtilValidate.isNotEmpty(partys))
{
    context.partys = partys;
}

//Shipping Method

shipmentMethodTypeOps = new EntityFindOptions();
shipmentMethodTypeOps.setDistinct(true);
Set<String> shipm = new TreeSet<String>();
shipm.add("shipmentMethodTypeId");
shipmentMethodTypes = delegator.findList("ShipmentMethodType", null, shipm, null, shipmentMethodTypeOps, false);

if (UtilValidate.isNotEmpty(shipmentMethodTypes))
{
    context.shipmentMethodTypes =shipmentMethodTypes;
}

//get entity for shipCharge
productStoreShipMethId = StringUtils.trimToEmpty(parameters.productStoreShipMethId);
context.productStoreShipMethId = productStoreShipMethId;
if (UtilValidate.isNotEmpty(productStoreShipMethId))
{
    shipCharge = delegator.findByAnd("ProductStoreShipmentMeth", [productStoreShipMethId : productStoreShipMethId]);
    shipCharge = EntityUtil.getFirst(shipCharge);
    context.shipCharge = shipCharge;
    
}

//get the flat rate 
if (UtilValidate.isNotEmpty(productStoreShipMethId))
{
    shipCostEst = delegator.findByAnd("ShipmentCostEstimate", [productStoreShipMethId : productStoreShipMethId]);
    shipCostEst = EntityUtil.getFirst(shipCostEst);
    context.shipCostEst = shipCostEst;
    
}





