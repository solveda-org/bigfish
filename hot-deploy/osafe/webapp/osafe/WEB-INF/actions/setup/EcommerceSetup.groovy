package setup;

import java.util.*;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.product.catalog.CatalogWorker;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.common.CommonWorkers;
import org.ofbiz.order.shoppingcart.*;
import org.ofbiz.webapp.control.*;
import com.osafe.util.Util;

productStore = ProductStoreWorker.getProductStore(request);
prodCatalog = CatalogWorker.getProdCatalog(request);
if (prodCatalog) 
{
    catalogStyleSheet = prodCatalog.styleSheet;
    if (catalogStyleSheet) globalContext.catalogStyleSheet = catalogStyleSheet;
    catalogHeaderLogo = prodCatalog.headerLogo;
    if (catalogHeaderLogo) globalContext.catalogHeaderLogo = catalogHeaderLogo;

    
    //Set Default Keywords to the prod catalog product categories
    if (UtilValidate.isEmpty(context.metaKeywords))
    {
        keywords = [];
        keywords.add(productStore.storeName);
        keywords.add(prodCatalog.prodCatalogId);
        context.metaKeywords = StringUtil.join(keywords, ", ");
    }
}
if (UtilValidate.isNotEmpty(productStore))
{
  String productStoreId=productStore.getString("productStoreId");
  productStoreParmList = delegator.findByAnd("XProductStoreParm",UtilMisc.toMap("productStoreId",productStoreId));
  if (UtilValidate.isNotEmpty(productStoreParmList))
  {
    parmIter = productStoreParmList.iterator();
    while (parmIter.hasNext()) 
    {
      prodStoreParm = (GenericValue) parmIter.next();
      globalContext.put(prodStoreParm.getString("parmKey"),prodStoreParm.getString("parmValue"));
    }
  }
  String companyName = productStore.companyName;
  if (UtilValidate.isEmpty(companyName))
  {
     companyName=productStore.storeName;
  }
  globalContext.companyName = companyName;
}

globalContext.productStore = productStore;
globalContext.productStoreId = productStore.productStoreId;
globalContext.checkLoginUrl = LoginWorker.makeLoginUrl(request, "checkLogin");
globalContext.catalogQuickaddUse = CatalogWorker.getCatalogQuickaddUse(request);

preferredDateFormat = globalContext.FORMAT_DATE;
preferredDateTimeFormat = globalContext.FORMAT_DATE_TIME;
globalContext.preferredDateFormat = Util.isValidDateFormat(preferredDateFormat)?preferredDateFormat:"MM/dd/yy";
globalContext.preferredDateTimeFormat = Util.isValidDateFormat(preferredDateTimeFormat)?preferredDateTimeFormat:"MM/dd/yy h:mma";
