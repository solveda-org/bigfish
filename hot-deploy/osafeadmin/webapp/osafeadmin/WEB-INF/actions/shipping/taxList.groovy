package shipping;

import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.base.util.*;
import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.UtilValidate;

String taxAuthPartyId = StringUtils.trimToEmpty(parameters.taxAuthPartyId);
String productStoreId = StringUtils.trimToEmpty(parameters.productStoreId);

taxAuthorityRateProductList = delegator.findByAnd("TaxAuthorityRateProduct", UtilMisc.toMap("taxAuthPartyId", taxAuthPartyId, "productStoreId", productStoreId));
 
pagingListSize=taxAuthorityRateProductList.size();
context.pagingListSize=pagingListSize;
context.pagingList = taxAuthorityRateProductList;
