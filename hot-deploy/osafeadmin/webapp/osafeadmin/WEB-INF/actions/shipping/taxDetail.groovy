package shipping;

import java.util.List;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.apache.commons.lang.StringUtils;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.*;


productStoreId=globalContext.productStoreId;
taxAuthorityRateSeqId = parameters.taxAuthorityRateSeqId;

if (UtilValidate.isNotEmpty(taxAuthorityRateSeqId))
{
    taxAuthorityRateProduct = delegator.findByPrimaryKey("TaxAuthorityRateProduct", [taxAuthorityRateSeqId : taxAuthorityRateSeqId]);
    
    if(UtilValidate.isNotEmpty(taxAuthorityRateProduct)) 
    {
        context.taxAuthorityRateProduct = taxAuthorityRateProduct;
    }
}





