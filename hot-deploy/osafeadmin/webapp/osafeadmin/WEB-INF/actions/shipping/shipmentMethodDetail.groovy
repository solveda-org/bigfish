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
shipmentMethodTypeId=parameters.shipmentMethodTypeId;

//get shipment method info
if (UtilValidate.isNotEmpty(shipmentMethodTypeId))
{
    shipmentMethod = delegator.findByPrimaryKey("ShipmentMethodType", [shipmentMethodTypeId : shipmentMethodTypeId]);
    context.shipmentMethod = shipmentMethod;
    
}





