package setup;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.product.catalog.CatalogWorker;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.common.CommonWorkers;
import com.osafe.util.Util;

productStore = ProductStoreWorker.getProductStore(request);
if (UtilValidate.isNotEmpty(productStore))
{
  String companyName = productStore.companyName;
  if (UtilValidate.isEmpty(companyName))
  {
     companyName=productStore.storeName;
  }
  globalContext.companyName = companyName;
  
  //Set Default Keywords to the prod catalog product categories
  if (UtilValidate.isEmpty(context.metaKeywords))
  {
        prodCatalog = CatalogWorker.getProdCatalog(request);
        keywords = [];
        keywords.add(productStore.storeName);
        keywords.add(prodCatalog.prodCatalogId);
        context.metaKeywords = StringUtil.join(keywords, ", ");
  }
  
}

globalContext.productStore = productStore;
globalContext.productStoreId = productStore.productStoreId;

preferredDateFormat = Util.getProductStoreParm(request,"FORMAT_DATE");
preferredDateTimeFormat = Util.getProductStoreParm(request,"FORMAT_DATE_TIME");
globalContext.preferredDateFormat = Util.isValidDateFormat(preferredDateFormat)?preferredDateFormat:"MM/dd/yy";
globalContext.preferredDateTimeFormat = Util.isValidDateFormat(preferredDateTimeFormat)?preferredDateTimeFormat:"MM/dd/yy h:mma";
