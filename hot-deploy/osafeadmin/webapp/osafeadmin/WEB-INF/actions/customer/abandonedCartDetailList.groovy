package customer;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.UtilValidate;
import org.apache.commons.lang.StringUtils;

import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;

userLogin = session.getAttribute("userLogin");
shoppingListId = StringUtils.trimToEmpty(parameters.cartId);

context.partyId=partyId;

messageMap=[:];
messageMap.put("partyId", partyId);
messageMap.put("shoppingListId", shoppingListId);

context.partyId=partyId;
context.pageTitle = UtilProperties.getMessage("OSafeAdminUiLabels","AbandonedCartDetailTitle",messageMap, locale )
context.generalInfoBoxHeading = UtilProperties.getMessage("OSafeAdminUiLabels","CustomerDetailInfoHeading",messageMap, locale )
context.customerNoteInfoBoxHeading = UtilProperties.getMessage("OSafeAdminUiLabels","CustomerNoteHeading",messageMap, locale )

party = delegator.findByPrimaryKey("Party", [partyId : partyId]);

messageMap=[:];
messageMap.put("partyId", partyId);

context.detailInfoBoxHeading = UtilProperties.getMessage("OSafeAdminUiLabels","CustomerDetailInfoHeading",messageMap, locale )

List contentList = FastList.newInstance();
if (UtilValidate.isNotEmpty(shoppingListId))
{
   orderBy = ["shoppingListItemSeqId ASC"];
   mainCond = EntityCondition.makeCondition("shoppingListId", EntityOperator.EQUALS, shoppingListId);
   contentList = delegator.findList("ShoppingListItem",mainCond, null, orderBy, null, false);
   context.resultList = contentList;
}



context.userLoginId = userLogin.userLoginId;



