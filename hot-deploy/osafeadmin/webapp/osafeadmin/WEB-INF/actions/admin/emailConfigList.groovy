package admin;

import org.ofbiz.base.util.UtilValidate;

import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.base.util.UtilMisc;

productStore = ProductStoreWorker.getProductStore(request);
productStoreId="";
orderBy = ["emailType"];
if (UtilValidate.isNotEmpty(productStore))
{
	productStoreId = productStore.productStoreId;
	emailConfigs = delegator.findByAnd("ProductStoreEmailSetting",["productStoreId":productStoreId],orderBy);
	if(emailConfigs)
	{
	    context.resultList = emailConfigs;
	}
}


