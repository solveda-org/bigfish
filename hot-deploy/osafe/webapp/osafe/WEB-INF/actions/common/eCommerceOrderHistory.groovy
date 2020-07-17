import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.entity.condition.*;

orderRoleCollection = delegator.findByAndCache("OrderRole", [partyId : userLogin.partyId, roleTypeId : "PLACING_CUSTOMER"]);
orderHeaderList = EntityUtil.orderBy(EntityUtil.filterByAnd(EntityUtil.getRelated("OrderHeader", orderRoleCollection),
        [EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "ORDER_REJECTED")]), ["orderDate DESC"]);
context.orderHeaderList = orderHeaderList;

