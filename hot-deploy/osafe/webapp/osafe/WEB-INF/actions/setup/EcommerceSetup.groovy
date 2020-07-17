package setup;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.product.catalog.CatalogWorker;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.common.CommonWorkers;
import com.osafe.util.Util;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.util.EntityUtil;

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

currencyRounding=2;
roundCurrency = Util.getProductStoreParm(request,"CURRENCY_UOM_ROUNDING");
if (UtilValidate.isNotEmpty(roundCurrency) && Util.isNumber(roundCurrency))
{
	currencyRounding = Integer.parseInt(roundCurrency);
}
globalContext.currencyRounding =currencyRounding;
globalContext.preferredDateFormat = Util.isValidDateFormat(preferredDateFormat)?preferredDateFormat:"MM/dd/yy";
globalContext.preferredDateTimeFormat = Util.isValidDateFormat(preferredDateTimeFormat)?preferredDateTimeFormat:"MM/dd/yy h:mma";


if (UtilValidate.isNotEmpty(productStore))
{
  pageTrackingList = productStore.getRelatedCache("XPixelTracking");
  pageTrackingList = EntityUtil.filterByDate(pageTrackingList,true);
  context.pageTrackingList = pageTrackingList;
}