package shipping;;

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
partyId=parameters.carrierPartyId;

//get carrier info
if (UtilValidate.isNotEmpty(partyId))
{
    carrier = delegator.findByPrimaryKey("PartyGroup", [partyId : partyId]);
    context.carrier = carrier;
    
}
partyContent = EntityUtil.getFirst(delegator.findByAnd("PartyContent", [partyId : partyId, 	partyContentTypeId : "TRACKING_URL"]));
if (UtilValidate.isNotEmpty(partyContent)) 
{
	content = partyContent.getRelatedOne("Content");
	if (UtilValidate.isNotEmpty(content))
	{
	   context.trackingUrlId = content.contentId;
	   dataResource = content.getRelatedOne("DataResource");
	   if (UtilValidate.isNotEmpty(dataResource))
	   {
		  context.trackingUrlDataResourceId = dataResource.dataResourceId;
	   }
	}
}




