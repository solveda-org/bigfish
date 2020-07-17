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
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionBuilder;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.*
import org.ofbiz.entity.transaction.*
import org.ofbiz.product.product.ProductWorker;

paramsExpr = FastList.newInstance();
exprBldr =  new EntityConditionBuilder();

String manufacturerPartyId = parameters.manufacturerPartyId;
if (UtilValidate.isNotEmpty(manufacturerPartyId))
 {
    GenericValue gvParty =  delegator.findOne("Party", UtilMisc.toMap("partyId",manufacturerPartyId), true);
 
    if (UtilValidate.isNotEmpty(gvParty)) 
    {
          context.manufacturerPartyId = gvParty.partyId;
          PartyContentWrapper partyContentWrapper = new PartyContentWrapper(gvParty, request);
          context.partyContentWrapper = partyContentWrapper;

          paramsExpr.add(EntityCondition.makeCondition(exprBldr.EQUALS(manufacturerPartyId: manufacturerPartyId)));
	      productSearchList = [];
		  orderBy = ["productId"];
          paramCond=null;
      	  prodCond = EntityCondition.makeCondition(paramsExpr, EntityOperator.AND);
          paramCond = EntityCondition.makeCondition([prodCond], EntityOperator.AND);
		  
		  productSearchList = delegator.findList("Product",paramCond, null, orderBy, null, true);
          productList = FastList.newInstance();
		  if (UtilValidate.isNotEmpty(productSearchList))
		  {
               for (GenericValue product: productSearchList)
               {
                   String isVariant = product.getString("isVariant");
                   if (UtilValidate.isEmpty(isVariant)) 
                   {
                     isVariant = "N";
                   }
                  if ("N".equals(isVariant) && ProductWorker.isSellable(product))
                  {
                     productList.add(product);
                     
                  }
               }
		     
		  }
          context.manufacturerProductList = productList;
    }
 }
 




