package common;

import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilMisc;
import javax.servlet.http.HttpServletRequest;
import org.ofbiz.base.util.*;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.party.content.PartyContentWrapper;


String productId = parameters.productId;
String productCategoryId = parameters.productCategoryId;
context.partyContentWrapper = "";
if (UtilValidate.isNotEmpty(productId))
 {
    GenericValue gvProduct =  delegator.findOne("Product", UtilMisc.toMap("productId",productId), true);
    if (UtilValidate.isNotEmpty(gvProduct)) 
    {
        productId = gvProduct.productId;
        partyManufacturer=gvProduct.getRelatedOne("ManufacturerParty");
        if (UtilValidate.isNotEmpty(partyManufacturer))
        {
          context.manufacturerPartyId = partyManufacturer.partyId;
          PartyContentWrapper partyContentWrapper = new PartyContentWrapper(partyManufacturer, request);
          context.partyContentWrapper = partyContentWrapper;
        }
    }
 }
 




