package common;

import java.math.BigDecimal;
import java.util.List;

import org.ofbiz.base.util.UtilValidate;

import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.product.product.ProductContentWrapper;
import org.ofbiz.product.product.ProductWorker;
import com.osafe.util.Util;
import org.ofbiz.base.util.UtilMisc;
import com.osafe.services.CatalogUrlServlet;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.entity.Delegator;
import com.osafe.services.InventoryServices;
import org.ofbiz.order.shoppingcart.ShoppingCart;
import org.ofbiz.order.shoppingcart.ShoppingCartItem;
import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.Debug;
import org.ofbiz.product.catalog.CatalogWorker;


ShoppingCart shoppingCart = session.getAttribute("shoppingCart");
userLogin = context.userLogin;
partyId = null;
multiAddressList = FastList.newInstance();
shippingContactMechList = FastList.newInstance();
totalQuantity=0;
numberOfItems=0;
lineIndexGiftMessageMap = FastMap.newInstance();

if(UtilValidate.isNotEmpty(shoppingCart))
{
	shoppingCartItems = shoppingCart.items();
	if(UtilValidate.isNotEmpty(shoppingCartItems))
	{
		totalQuantity = shoppingCart.getTotalQuantity();
		numberOfItems = shoppingCart.items().size();
		int iLineIdx=0;
		for (ShoppingCartItem shoppingCartItem : shoppingCartItems)
		{
			itemQuantity = shoppingCartItem.getQuantity();
			for (int idx = 0; idx < itemQuantity; idx++) 
			{
				multiAddressList.add(shoppingCartItem)
				Map cartAttrMap = shoppingCartItem.getOrderItemAttributes();
				if(UtilValidate.isNotEmpty(cartAttrMap))
				{
					Map giftMsgMap = FastMap.newInstance();
					int iProductMsgIdx=idx +1;
					String sProductMsgIdx = iProductMsgIdx;
					for (Map.Entry itemAttr : cartAttrMap.entrySet())
					{
						sAttrName = (String)itemAttr.getKey();
						if (sAttrName.startsWith("GIFT_MSG_FROM_" + sProductMsgIdx))
						{
							giftMsgMap.put("from",(String)itemAttr.getValue());
						}
						if (sAttrName.startsWith("GIFT_MSG_TO_"+sProductMsgIdx))
						{
							giftMsgMap.put("to",(String)itemAttr.getValue());
						}
						if (sAttrName.startsWith("GIFT_MSG_TEXT_"+sProductMsgIdx))
						{
							giftMsgMap.put("msg",(String)itemAttr.getValue());
						}
					}
					if (UtilValidate.isNotEmpty(giftMsgMap))
					{
						lineIndexGiftMessageMap.put(""+ iLineIdx,giftMsgMap);
					}
				}
				iLineIdx=iLineIdx +1;
				
			}
		}
	}
}
if (UtilValidate.isNotEmpty(userLogin)) 
{
    partyId = userLogin.partyId;
}

if (UtilValidate.isNotEmpty(partyId)) 
{
    party = delegator.findByPrimaryKeyCache("Party", [partyId : partyId]);
    if (UtilValidate.isNotEmpty(party)) 
    {
        partyContactMechPurpose = party.getRelatedCache("PartyContactMechPurpose");
        partyContactMechPurpose = EntityUtil.filterByDate(partyContactMechPurpose,true);

        partyShippingLocations = EntityUtil.filterByAnd(partyContactMechPurpose, UtilMisc.toMap("contactMechPurposeTypeId", "SHIPPING_LOCATION"));
        partyShippingLocations = EntityUtil.getRelatedCache("PartyContactMech", partyShippingLocations);
        partyShippingLocations = EntityUtil.filterByDate(partyShippingLocations,true);
        partyShippingLocations = EntityUtil.orderBy(partyShippingLocations, UtilMisc.toList("fromDate DESC"));
        if (UtilValidate.isNotEmpty(partyShippingLocations)) 
        {
            shippingContactMechList=EntityUtil.getRelated("ContactMech",partyShippingLocations);
        }
        
    }

}
			

context.shoppingCart=shoppingCart;
context.multiAddressList=multiAddressList;
context.lineIndexGiftMessageMap=lineIndexGiftMessageMap;
context.shippingContactMechList = shippingContactMechList;
context.itemTotalQuantity = totalQuantity;
context.numberOfItems = numberOfItems;
context.shippingApplies=shoppingCart.shippingApplies();




