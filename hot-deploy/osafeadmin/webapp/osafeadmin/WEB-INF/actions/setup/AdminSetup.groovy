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
import com.osafe.services.OsafeAdminCatalogServices;
import javolution.util.FastMap;
import javolution.util.FastList;
import org.apache.commons.lang.StringUtils;

globalContext.stores = delegator.findList("ProductStore",EntityCondition.makeCondition([isDemoStore : "N"]), null, null, null, false);
adminModuleName ="";
productStore="";
productStoreId="";
selectedProductStoreId = parameters.selectedProductStoreId;
boolean retrieveProductStoreData = false;

//Shopping cart
shopCart = ShoppingCartEvents.getCartObject(request);

ServletContext application = ((ServletContext) request.getAttribute("servletContext"));
if(UtilValidate.isNotEmpty(application))
{
    adminModuleName = application.getInitParameter("adminModuleName");

}
if(UtilValidate.isEmpty(adminModuleName))
{
    website = WebSiteWorker.getWebSite(request);
    if(UtilValidate.isNotEmpty(website))
    {
        adminModuleName = website.siteName;
    }
    if(UtilValidate.isEmpty(adminModuleName))
    {
        adminModuleName = website.webSiteId;
    }
}
context.adminModuleName = adminModuleName;


if (UtilValidate.isNotEmpty(selectedProductStoreId))
{
	//clear all variables when product store is selected
	shopCart.clear();
	shopCart.setProductStoreId(selectedProductStoreId);
	session.removeAttribute("ADMIN_CONTEXT");
	//set productStore info
   store = delegator.findOne("ProductStore",["productStoreId":selectedProductStoreId], false);
   if (UtilValidate.isNotEmpty(store))
   {
	 productStore =store;
	 productStoreId=store.getString("productStoreId");
     globalContext.selectedStore = store;
     globalContext.selectedStoreId=productStoreId;
	 globalContext.productStore = store;
	 globalContext.productStoreName = store.storeName;
	 globalContext.productStoreId = store.productStoreId;
     session.setAttribute("selectedStore",store);
     retrieveProductStoreData=true;
	 
	 if(UtilValidate.isNotEmpty(productStoreId))
	 {
		 webSites =delegator.findByAnd("WebSite", UtilMisc.toMap("productStoreId", productStoreId));
		 webSite = EntityUtil.getFirst(webSites);
		 if (UtilValidate.isNotEmpty(webSite))
		 {
			 globalContext.webSite = webSite;
			 globalContext.webSiteId =webSite.webSiteId;
			 session.setAttribute("selectedWebsite",webSite);
			 
		 }
	 }
   }
}

selectedStore = session.getAttribute("selectedStore");
if (UtilValidate.isEmpty(selectedStore))
{
   store = ProductStoreWorker.getProductStore(request);
   if (UtilValidate.isNotEmpty(store))
   {
		 productStore =store;
		 productStoreId=store.getString("productStoreId");
	     globalContext.selectedStore = store;
	     globalContext.selectedStoreId=productStoreId;
		 globalContext.productStore = store;
		 globalContext.productStoreName = store.storeName;
		 globalContext.productStoreId = store.productStoreId;
	     session.setAttribute("selectedStore",store);
	     retrieveProductStoreData=true;
   }
}

if (retrieveProductStoreData && UtilValidate.isNotEmpty(productStore))
{
	 webSites =delegator.findByAnd("WebSite", UtilMisc.toMap("productStoreId", productStore.productStoreId));
	 webSite = EntityUtil.getFirst(webSites);
     if (UtilValidate.isNotEmpty(webSite))
	 {
		 globalContext.webSite = webSite;
		 globalContext.webSiteId =webSite.webSiteId;
	     session.setAttribute("selectedWebsite",webSite);
		 
	 }
	 
	 //PRODUCT STORE CATALOG
	 storeCatalogs = delegator.findByAnd("ProductStoreCatalog", UtilMisc.toMap("productStoreId", productStore.productStoreId), UtilMisc.toList("sequenceNum", "prodCatalogId"));
	 storeCatalogs = EntityUtil.filterByDate(storeCatalogs, true);
	 currentCatalog= EntityUtil.getFirst(storeCatalogs);
	 if (UtilValidate.isNotEmpty(currentCatalog))
	 {
		 globalContext.prodCatalogId = currentCatalog.prodCatalogId;
         prodCatalog = currentCatalog.getRelatedOne("ProdCatalog");
    	 globalContext.prodCatalogName = prodCatalog.catalogName;
	     session.setAttribute("selectedProdCatalog",prodCatalog);
    	 
         prodCatalogCategories = delegator.findByAnd("ProdCatalogCategory",UtilMisc.toMap("prodCatalogId", currentCatalog.prodCatalogId,"prodCatalogCategoryTypeId","PCCT_BROWSE_ROOT"),UtilMisc.toList("sequenceNum", "productCategoryId"));
         prodCatalogCategories = EntityUtil.filterByDate(prodCatalogCategories, true);
         if (UtilValidate.isNotEmpty(prodCatalogCategories))
         {
             prodCatalogCategory = EntityUtil.getFirst(prodCatalogCategories);
    	     session.setAttribute("selectedProdCatalogCategory",prodCatalogCategory);
             globalContext.rootProductCategoryId=prodCatalogCategory.productCategoryId;
             
    	     currentCategories = FastList.newInstance();
    	     allUnexpiredCategories = OsafeAdminCatalogServices.getRelatedCategories(delegator, prodCatalogCategory.productCategoryId, null, true, false, true);
    	     for (Map<String, Object> workingCategoryMap : allUnexpiredCategories) 
    	     {
    	        workingCategory = (GenericValue) workingCategoryMap.get("ProductCategory");
    	        currentCategories.add(workingCategory);
    	     }
    	     globalContext.currentCategories = currentCategories;
    	     session.setAttribute("selectedCategories",currentCategories);

         }

	 }
	 
	
}
else
{
	selectedStore = session.getAttribute("selectedStore");
    if (UtilValidate.isNotEmpty(selectedStore))
	{
        globalContext.selectedStore = selectedStore;
        globalContext.selectedStoreId=selectedStore.productStoreId;
    	 globalContext.productStore = selectedStore;
    	globalContext.productStoreName = selectedStore.storeName;
    	globalContext.productStoreId = selectedStore.productStoreId;
	}

	selectedWebsite = session.getAttribute("selectedWebsite");
    if (UtilValidate.isNotEmpty(selectedWebsite))
	{
		globalContext.webSite = selectedWebsite;
		globalContext.webSiteId =selectedWebsite.webSiteId;
	}

    selectedProdCatalog = session.getAttribute("selectedProdCatalog");
    if (UtilValidate.isNotEmpty(selectedProdCatalog))
    {
    	globalContext.prodCatalogId = selectedProdCatalog.prodCatalogId;
    	globalContext.prodCatalogName = selectedProdCatalog.catalogName;
    }
		
    selectedCategories = session.getAttribute("selectedCategories");
    if (UtilValidate.isNotEmpty(selectedCategories))
    {
    	globalContext.currentCategories = selectedCategories;
    }

	selectedProdCatalogCategory = session.getAttribute("selectedProdCatalogCategory");
    if (UtilValidate.isNotEmpty(selectedProdCatalogCategory))
    {
    	globalContext.rootProductCategoryId=selectedProdCatalogCategory.productCategoryId;
    }
	
}

if (UtilValidate.isNotEmpty(globalContext.productStoreId))
{
	productStoreParmList = delegator.findByAnd("XProductStoreParm",UtilMisc.toMap("productStoreId",globalContext.productStoreId));
	if (UtilValidate.isNotEmpty(productStoreParmList))
	{
	   parmIter = productStoreParmList.iterator();
	   while (parmIter.hasNext())
	   {
		 prodStoreParm = (GenericValue) parmIter.next();
		 globalContext.put(prodStoreParm.getString("parmKey"),prodStoreParm.getString("parmValue"));
	   }
		session.setAttribute("selectedProductStoreParm",productStoreParmList);
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
if (UtilValidate.isNotEmpty(userLogin))
{
    party = userLogin.getRelatedOne("Party");
    if(UtilValidate.isNotEmpty(party))
    {
        userLoginFullName = PartyHelper.getPartyName(party);
        context.userLoginFullName =userLoginFullName;
    }
}
currencyRounding=2;
roundCurrency = globalContext.CURRENCY_UOM_ROUNDING;
if (UtilValidate.isNotEmpty(roundCurrency) && OsafeAdminUtil.isNumber(roundCurrency))
{
	currencyRounding = Integer.parseInt(roundCurrency);
}
globalContext.currencyRounding =currencyRounding;

preferredDateFormat = globalContext.FORMAT_DATE;
preferredDateTimeFormat = globalContext.FORMAT_DATE_TIME;
globalContext.preferredDateFormat = OsafeAdminUtil.isValidDateFormat(preferredDateFormat)?preferredDateFormat:"MM/dd/yy";
globalContext.preferredDateTimeFormat = OsafeAdminUtil.isValidDateFormat(preferredDateTimeFormat)?preferredDateTimeFormat:"MM/dd/yy h:mma";
globalContext.preferredTimeFormat = "h:mma";

globalContext.entryDateFormat = preferredDateFormat;
globalContext.entryDateTimeFormat = preferredDateFormat+" HH:mm:ss";
globalContext.entryTimeFormat = "HH:mm:ss";

//ADMIN CONTEXT PROCESSING
adminContext = FastMap.newInstance();
if (UtilValidate.isNotEmpty(session.getAttribute("ADMIN_CONTEXT")))
{
    adminContext = UtilGenerics.checkMap(session.getAttribute("ADMIN_CONTEXT"), String.class, String.class);
}

partyId = parameters.partyId;
orderId = parameters.orderId;
storePartyId = parameters.storePartyId;
productId = parameters.productId;

//Passing null to StringUtils threw an exception therefore checking first before calling Util.
if (UtilValidate.isNotEmpty(partyId))
{
	partyId = StringUtils.trimToEmpty(parameters.partyId);
}
if (UtilValidate.isNotEmpty(orderId))
{
	orderId = StringUtils.trimToEmpty(parameters.orderId);
	
}
if (UtilValidate.isNotEmpty(storePartyId))
{
	storePartyId = StringUtils.trimToEmpty(parameters.storePartyId);
}
if (UtilValidate.isNotEmpty(productId))
{
	productId = StringUtils.trimToEmpty(parameters.productId);
}
if (UtilValidate.isNotEmpty(partyId))
{
	//if the partyId is changed, then clear the shopping cart
	if(UtilValidate.isNotEmpty(adminContext.get("CONTEXT_PARTY_ID")) && !((adminContext.get("CONTEXT_PARTY_ID")).equals(partyId)))
	{
		shopCart.clear();
	}
	adminContext.put("CONTEXT_PARTY_ID", partyId);
}
if (UtilValidate.isNotEmpty(orderId))
{
    adminContext.put("CONTEXT_ORDER_ID", orderId);
}
if (UtilValidate.isNotEmpty(storePartyId))
{
    adminContext.put("CONTEXT_STORE_PARTY_ID", storePartyId);
}
if (UtilValidate.isNotEmpty(productId))
{
    adminContext.put("CONTEXT_PRODUCT_ID", productId);
}
context.adminContext = adminContext;
session.setAttribute("ADMIN_CONTEXT", adminContext);

context.shoppingCart =shopCart;






