package setup;
import org.ofbiz.base.util.UtilValidate;
import java.math.BigDecimal;
import java.util.*;
import javax.servlet.ServletContext;
import org.ofbiz.base.util.UtilProperties;

import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.party.party.PartyHelper;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.product.catalog.CatalogWorker;
import org.ofbiz.order.shoppingcart.*;
import org.ofbiz.webapp.control.*;
import org.ofbiz.webapp.website.WebSiteWorker;
import org.ofbiz.order.order.OrderReadHelper;
import com.osafe.util.OsafeAdminUtil;
import com.osafe.services.CategoryServices;
import javolution.util.FastMap;
import javolution.util.FastList;



globalContext.stores = delegator.findList("ProductStore",EntityCondition.makeCondition([isDemoStore : "N"]), null, null, null, true);

selectedProductStoreId = parameters.selectedProductStoreId;
if (UtilValidate.isNotEmpty(selectedProductStoreId))
{

   store = delegator.findOne("ProductStore",["productStoreId":selectedProductStoreId], true);
   if (UtilValidate.isNotEmpty(store))
   {
     globalContext.selectedStore = store;
     session.setAttribute("selectedStore",store);
   }
}



selectedStore = session.getAttribute("selectedStore");
if (UtilValidate.isNotEmpty(selectedStore))
{
   productStore = selectedStore;
   globalContext.selectedStoreId=productStore.productStoreId;
}
else
{
   productStore = ProductStoreWorker.getProductStore(request);

}


if (UtilValidate.isNotEmpty(productStore))
{
      session.setAttribute("selectedStore",productStore);
      
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
	
	
	 globalContext.productStore = productStore;
	 globalContext.productStoreId = productStore.productStoreId;
	 globalContext.productStoreName = productStore.storeName;

	 storeCatalogs = EntityUtil.filterByDate(delegator.findByAndCache("ProductStoreCatalog", UtilMisc.toMap("productStoreId", productStoreId), UtilMisc.toList("sequenceNum", "prodCatalogId")), true);
	 currentCatalog= EntityUtil.getFirst(storeCatalogs);
	 
	 globalContext.prodCatalogId = currentCatalog.prodCatalogId;
	 globalContext.prodCatalogName = CatalogWorker.getCatalogName(request,currentCatalog.prodCatalogId);
	 globalContext.rootProductCategoryId = CatalogWorker.getCatalogTopCategoryId(request,currentCatalog.prodCatalogId);
	 
	 webSites =delegator.findByAndCache("WebSite", UtilMisc.toMap("productStoreId", productStoreId));
	 webSite = EntityUtil.getFirst(webSites);
     if (UtilValidate.isNotEmpty(webSite))
	 {
		 globalContext.webSite = webSite;
		 globalContext.webSiteId =webSite.webSiteId;
	 }

	 

	 if (UtilValidate.isNotEmpty(globalContext.rootProductCategoryId))
	 {
	     currentCategories = FastList.newInstance();
	     allUnexpiredCategories = CategoryServices.getRelatedCategories(delegator, globalContext.rootProductCategoryId, null, true, false, true);
	     for (Map<String, Object> workingCategoryMap : allUnexpiredCategories) 
	     {
	        workingCategory = (GenericValue) workingCategoryMap.get("ProductCategory");
	        currentCategories.add(workingCategory);
	     }
	   globalContext.currentCategories = currentCategories;
	 }
}
defaultCurrencyUomId = globalContext.get("CURRENCY_UOM_DEFAULT");

if (UtilValidate.isEmpty(defaultCurrencyUomId)) 
{
    defaultCurrencyUomId = UtilProperties.getPropertyValue('general', 'currency.uom.id.default');
}
globalContext.defaultCurrencyUomId = defaultCurrencyUomId;
currencySymbol = OsafeAdminUtil.showCurrency(defaultCurrencyUomId, locale);
globalContext.currencySymbol=currencySymbol;
    

context.userLoginFullName ="";
userLogin = session.getAttribute("userLogin");
if(UtilValidate.isNotEmpty(userLogin)){
    party = userLogin.getRelatedOne("Party");
    if(UtilValidate.isNotEmpty(party)){
        userLoginFullName = PartyHelper.getPartyName(party);
        context.userLoginFullName =userLoginFullName;
    }
}
adminModuleName ="";
ServletContext application = ((ServletContext) request.getAttribute("servletContext"));
if(UtilValidate.isNotEmpty(application)){
    adminModuleName = application.getInitParameter("adminModuleName");

}
if(UtilValidate.isEmpty(adminModuleName)){
    website = WebSiteWorker.getWebSite(request);
    if(UtilValidate.isNotEmpty(website)){
        adminModuleName = website.siteName;
    }
    if(UtilValidate.isEmpty(adminModuleName)){
        adminModuleName = website.webSiteId;
    }
}
context.adminModuleName = adminModuleName;

preferredDateFormat = globalContext.FORMAT_DATE;
preferredDateTimeFormat = globalContext.FORMAT_DATE_TIME;
globalContext.preferredDateFormat = OsafeAdminUtil.isValidDateFormat(preferredDateFormat)?preferredDateFormat:"MM/dd/yy";
globalContext.preferredDateTimeFormat = OsafeAdminUtil.isValidDateFormat(preferredDateTimeFormat)?preferredDateTimeFormat:"MM/dd/yy h:mma";

