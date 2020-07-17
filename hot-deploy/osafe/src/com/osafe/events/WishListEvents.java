package com.osafe.events;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.MessageString;
import org.ofbiz.base.util.ObjectType;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.order.shoppingcart.ShoppingCart;
import org.ofbiz.order.shoppingcart.ShoppingCartHelper;
import org.ofbiz.product.catalog.CatalogWorker;
import org.ofbiz.product.product.ProductContentWrapper;
import org.ofbiz.product.product.ProductWorker;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.security.Security;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.osafe.services.InventoryServices;
import com.osafe.util.Util;

public class WishListEvents {

    public static String module = WishListEvents.class.getName();
    public static final String resource = "OrderUiLabels";
    public static final String resource_error = "OrderErrorUiLabels";
    public static final String PERSISTANT_LIST_NAME = "wish-list";

    /** Event to add an item to the wish list shopping cart. */
    public static String addToWishList(HttpServletRequest request, HttpServletResponse response) 
    {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        String productId = null;
        String categoryId = null;
        String quantityStr = null;
        String searchText = null;
        String showSuccess = null;
        String productName = null;
        BigDecimal quantity = BigDecimal.ZERO;

        // not used right now: Map attributes = null;
        String catalogId = CatalogWorker.getCurrentCatalogId(request);
        Locale locale = UtilHttp.getLocale(request);

        // Get the parameters as a MAP, remove the productId and quantity params.
        Map paramMap = UtilHttp.getCombinedMap(request);

        if (paramMap.containsKey("ADD_PRODUCT_ID")) 
        {
            productId = (String) paramMap.remove("ADD_PRODUCT_ID");
        } 
        else if (paramMap.containsKey("add_product_id")) 
        {
            Object object = paramMap.remove("add_product_id");
            try 
            {
                productId = (String) object;
            } 
            catch (ClassCastException e) 
            {
                productId = (String) ((List) object).get(0);
            }
        }
        
        if (paramMap.containsKey("ADD_CATEGORY_ID")) 
        {
        	categoryId = (String) paramMap.remove("ADD_CATEGORY_ID");
        } 
        else if (paramMap.containsKey("add_category_id")) 
        {
            Object object = paramMap.remove("add_category_id");
            try 
            {
            	categoryId = (String) object;
            } catch (ClassCastException e) 
            {
            	categoryId = (String) ((List) object).get(0);
            }
        }
        
        Debug.logInfo("adding item product " + productId, module);

        if (UtilValidate.isEmpty(productId)) 
        {
            // before returning error; check make sure we aren't adding a special item type
            request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error, "cart.addToCart.noProductInfoPassed", locale));
            return "success"; // not critical return to same page
        } 
        else 
        {
            try 
            {
                String pId = ProductWorker.findProductId(delegator, productId);
                if (pId != null) 
                {
                    productId = pId;
                }
            } 
            catch (Throwable e) 
            {
                Debug.logWarning(e, module);
            }
        }

        //Check for virtual products
        if (ProductWorker.isVirtual(delegator, productId)) 
        {

            if ("VV_FEATURETREE".equals(ProductWorker.getProductVirtualVariantMethod(delegator, productId))) 
            {
                // get the selected features.
                List<String> selectedFeatures = new LinkedList<String>();
                java.util.Enumeration paramNames = request.getParameterNames();
                while (paramNames.hasMoreElements()) 
                {
                    String paramName = (String) paramNames.nextElement();
                    if (paramName.startsWith("FT")) 
                    {
                        selectedFeatures.add(request.getParameterValues(paramName)[0]);
                    }
                }

                // check if features are selected
                if (UtilValidate.isEmpty(selectedFeatures)) 
                {
                    request.setAttribute("product_id", productId);
                    request.setAttribute("_EVENT_MESSAGE_", UtilProperties.getMessage(resource_error, "cart.addToCart.chooseVariationBeforeAddingToCart", locale));
                    return "error";
                }

                String variantProductId = ProductWorker.getVariantFromFeatureTree(productId, selectedFeatures, delegator);
                if (UtilValidate.isNotEmpty(variantProductId)) 
                {
                    productId = variantProductId;
                } 
                else 
                {
                    request.setAttribute("product_id", productId);
                    request.setAttribute("_EVENT_MESSAGE_", UtilProperties.getMessage(resource_error, "cart.addToCart.incompatibilityVariantFeature", locale));
                    return "error";
                }

            } 
            else 
            {
                request.setAttribute("product_id", productId);
                request.setAttribute("_EVENT_MESSAGE_", UtilProperties.getMessage(resource_error, "cart.addToCart.chooseVariationBeforeAddingToCart", locale));
                return "error";
            }
        }

        request.setAttribute("product_id", productId);
        // get the quantity
        if (paramMap.containsKey("QUANTITY")) {
            quantityStr = (String) paramMap.remove("QUANTITY");
        } else if (paramMap.containsKey("quantity")) {
            quantityStr = (String) paramMap.remove("quantity");
        }
        if (UtilValidate.isEmpty(quantityStr)) {
            quantityStr = "1";  // default quantity is 1
        }

        // parse the quantity
        try {
            quantity = (BigDecimal) ObjectType.simpleTypeConvert(quantityStr, "BigDecimal", null, locale);
        } catch (Exception e) {
            Debug.logWarning(e, "Problems parsing quantity string: " + quantityStr, module);
            quantity = BigDecimal.ONE;
        }

        String shoppingListId = getWishListId(request, true);
        // if no list was created throw an error
        if (shoppingListId == null || shoppingListId.equals("")) {
            request.setAttribute("_EVENT_MESSAGE_", UtilProperties.getMessage(resource_error, "shoppinglistevents.no_items_added", locale));
            return "error";
        }
        
        // create shopping wish list item
        Debug.logInfo("Adding item to shopping wish list [" + shoppingListId + "], ProductId =" + productId + ", Quantity =" + quantity, module);
        Map serviceResult = null;
        try {
            Map ctx = UtilMisc.toMap("userLogin", userLogin, "shoppingListId", shoppingListId, "productId", productId, "quantity", quantity);
            serviceResult = dispatcher.runSync("createShoppingListItem", ctx);
        } catch (GenericServiceException e) {
            Debug.logError(e, "Problems creating Shopping wish List item entity", module);
            request.setAttribute("product_id", productId);
            request.setAttribute("_EVENT_MESSAGE_", UtilProperties.getMessage(resource_error, "shoppinglistevents.error_adding_item_to_shopping_list", locale));
            return "error";
        }

        // check for errors
        if (ServiceUtil.isError(serviceResult)) {
            request.setAttribute("_EVENT_MESSAGE_", UtilProperties.getMessage(resource_error, "shoppinglistevents.error_adding_item_to_shopping_list", locale));
            return "error";
        }
        
        if (paramMap.containsKey("productListFormSearchText")) {
        	searchText = (String) paramMap.remove("productListFormSearchText");
        }
        
        request.setAttribute("product_id", productId);
        request.setAttribute("productCategoryId", categoryId);
        request.setAttribute("searchText", searchText);
        
        return "success";
    }
    
    
    /** Event to add an item to the wish list shopping cart. */
    public static String addPlpItemToWishList(HttpServletRequest request, HttpServletResponse response) 
    {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        String productId = null;
        String categoryId = null;
        String quantityStr = null;
        String searchText = null;
        String showSuccess = null;
        BigDecimal quantity = BigDecimal.ZERO;

        // not used right now: Map attributes = null;
        String catalogId = CatalogWorker.getCurrentCatalogId(request);
        Locale locale = UtilHttp.getLocale(request);

        // Get the parameters as a MAP, remove the productId and quantity params.
        Map<String, Object> paramMap = UtilHttp.getCombinedMap(request);
        
        if (paramMap.containsKey("plp_add_product_id")) 
        {
        	productId = (String) paramMap.remove("plp_add_product_id");
        }
        
        if (paramMap.containsKey("plp_add_category_id")) 
        {
        	categoryId = (String) paramMap.remove("plp_add_category_id");
        }
        
        Debug.logInfo("adding item product " + productId, module);

        if (UtilValidate.isEmpty(productId)) 
        {
            // before returning error; check make sure we aren't adding a special item type
            request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error, "cart.addToCart.noProductInfoPassed", locale));
            return "success"; // not critical return to same page
        } 
        else 
        {
            try 
            {
                String pId = ProductWorker.findProductId(delegator, productId);
                if (pId != null) 
                {
                    productId = pId;
                }
            } 
            catch (Throwable e) 
            {
                Debug.logWarning(e, module);
            }
        }

        //Check for virtual products
        if (ProductWorker.isVirtual(delegator, productId)) 
        {

            if ("VV_FEATURETREE".equals(ProductWorker.getProductVirtualVariantMethod(delegator, productId))) 
            {
                // get the selected features.
                List<String> selectedFeatures = new LinkedList<String>();
                java.util.Enumeration paramNames = request.getParameterNames();
                while (paramNames.hasMoreElements()) 
                {
                    String paramName = (String) paramNames.nextElement();
                    if (paramName.startsWith("FT")) 
                    {
                        selectedFeatures.add(request.getParameterValues(paramName)[0]);
                    }
                }

                // check if features are selected
                if (UtilValidate.isEmpty(selectedFeatures)) 
                {
                    request.setAttribute("product_id", productId);
                    request.setAttribute("_EVENT_MESSAGE_", UtilProperties.getMessage(resource_error, "cart.addToCart.chooseVariationBeforeAddingToCart", locale));
                    return "error";
                }

                String variantProductId = ProductWorker.getVariantFromFeatureTree(productId, selectedFeatures, delegator);
                if (UtilValidate.isNotEmpty(variantProductId)) 
                {
                    productId = variantProductId;
                } 
                else 
                {
                    request.setAttribute("product_id", productId);
                    request.setAttribute("_EVENT_MESSAGE_", UtilProperties.getMessage(resource_error, "cart.addToCart.incompatibilityVariantFeature", locale));
                    return "error";
                }

            } 
            else 
            {
                request.setAttribute("product_id", productId);
                request.setAttribute("_EVENT_MESSAGE_", UtilProperties.getMessage(resource_error, "cart.addToCart.chooseVariationBeforeAddingToCart", locale));
                return "error";
            }
        }

        // get the quantity
        if (paramMap.containsKey("plp_qty")) 
        {
        	quantityStr = (String) paramMap.remove("plp_qty");
        }
        
        if (UtilValidate.isEmpty(quantityStr)) 
        {
            quantityStr = "1";  // default quantity is 1
        }

        // parse the quantity
        try {
            quantity = (BigDecimal) ObjectType.simpleTypeConvert(quantityStr, "BigDecimal", null, locale);
        } catch (Exception e) {
            Debug.logWarning(e, "Problems parsing quantity string: " + quantityStr, module);
            quantity = BigDecimal.ONE;
        }

        String shoppingListId = getWishListId(request, true);
        // if no list was created throw an error
        if (shoppingListId == null || shoppingListId.equals("")) {
            request.setAttribute("_EVENT_MESSAGE_", UtilProperties.getMessage(resource_error, "shoppinglistevents.no_items_added", locale));
            return "error";
        }
        
        // create shopping wish list item
        Debug.logInfo("Adding item to shopping wish list [" + shoppingListId + "], ProductId =" + productId + ", Quantity =" + quantity, module);
        Map serviceResult = null;
        try {
            Map ctx = UtilMisc.toMap("userLogin", userLogin, "shoppingListId", shoppingListId, "productId", productId, "quantity", quantity);
            serviceResult = dispatcher.runSync("createShoppingListItem", ctx);
        } catch (GenericServiceException e) {
            Debug.logError(e, "Problems creating Shopping wish List item entity", module);
            request.setAttribute("product_id", productId);
            request.setAttribute("_EVENT_MESSAGE_", UtilProperties.getMessage(resource_error, "shoppinglistevents.error_adding_item_to_shopping_list", locale));
            return "error";
        }

        // check for errors
        if (ServiceUtil.isError(serviceResult)) {
            request.setAttribute("_EVENT_MESSAGE_", UtilProperties.getMessage(resource_error, "shoppinglistevents.error_adding_item_to_shopping_list", locale));
            return "error";
        }
        
        boolean multiSearchExists = false;
        for(Entry<String, Object> entry : paramMap.entrySet()) 
        {
    		String parameterName = entry.getKey();
    		if (parameterName.toUpperCase().startsWith("SEARCHITEM"))
        	{
    			String searchItem = Util.stripHTML((String)paramMap.get(parameterName));
    			if(UtilValidate.isNotEmpty(searchItem))
    			{
    				multiSearchExists = true;
    				request.setAttribute(parameterName, searchItem);
    			}
        	}
        }
        
        if (paramMap.containsKey("productListFormSearchText")) 
        {
        	searchText = (String) paramMap.remove("productListFormSearchText");
        }
        String last_page_productId = null;
        if (paramMap.containsKey("plp_last_viewed_pdp_id")) 
        {
        	last_page_productId = (String) paramMap.remove("plp_last_viewed_pdp_id");
        }
        String manufacturer_party_id = null;
        if (paramMap.containsKey("manufacturer_party_id")) 
        {
        	manufacturer_party_id = (String) paramMap.remove("manufacturer_party_id");
        }
        request.setAttribute("product_id", last_page_productId);
        if(UtilValidate.isEmpty(searchText) && !multiSearchExists)
        {
        	request.setAttribute("productCategoryId", categoryId);
        }
        request.setAttribute("manufacturerPartyId", manufacturer_party_id);
        request.setAttribute("searchText", searchText);
        
        if (paramMap.containsKey("showSuccess")) {
        	showSuccess = (String) paramMap.remove("showSuccess");
        }
        if (UtilValidate.isNotEmpty(showSuccess) && "Y".equals(showSuccess)) 
        {
        	GenericValue product = null;
            try {
                product = delegator.findByPrimaryKey("Product", UtilMisc.toMap("productId", productId));
            } catch (GenericEntityException e) {
                Debug.logWarning(e.getMessage(), module);
            }
        	String productName = ProductContentWrapper.getProductContentAsText(product, "PRODUCT_NAME", locale, dispatcher);
        	if(UtilValidate.isEmpty(productName))
        	{
        		GenericValue virtualProduct = ProductWorker.getParentProduct(productId, delegator);
        		if(UtilValidate.isNotEmpty(virtualProduct))
            	{
        			productName = ProductContentWrapper.getProductContentAsText(virtualProduct, "PRODUCT_NAME", locale, dispatcher);
            	}
        	}
        	//Get values for success message variables
        	Map<String, String> messageMap = UtilMisc.toMap("productName", productName);
        	String urlLabel = UtilProperties.getMessage(ShoppingCartEvents.label_resource, "ShowWishlistLink", locale); 
        	messageMap.put("wishlistLink", "<a href='eCommerceShowWishList'>"+ urlLabel +"</a>");
	        //Set the success message
	    	String successMess = UtilProperties.getMessage(ShoppingCartEvents.label_resource, "AddToWishlistPLPSuccess", messageMap, locale); 
	        List osafeSuccessMessageList = UtilMisc.toList(successMess);
	        request.setAttribute("osafeSuccessMessageList", osafeSuccessMessageList);
        }
        return "success";
    }
    

    /** Gets or creates the Wish List Id */
    public static String getWishListId(HttpServletRequest request, boolean createNew) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        //  return null if user is not logged in or anonymous
        if (userLogin == null && "anonymous".equals(userLogin.get("userLoginId")))
        {
            return null;
        }

        String wishListId = null;
        String partyId = userLogin.getString("partyId");
        String productStoreId = ProductStoreWorker.getProductStoreId(request);

        Map findMap = UtilMisc.toMap("partyId", partyId, "productStoreId", productStoreId, "shoppingListTypeId", "SLT_BF_WISH_LIST", "listName", PERSISTANT_LIST_NAME);
        try {
	        List<GenericValue> existingLists = delegator.findByAndCache("ShoppingList", findMap);
	        GenericValue list = null;
	        if (existingLists != null && !existingLists.isEmpty()) {
	            list = EntityUtil.getFirst(existingLists);
	            wishListId = list.getString("shoppingListId");
	            checkWishList(delegator, wishListId);
	        }
	        if (createNew) {
		        if (list == null && dispatcher != null && userLogin != null) {
		            Map listFields = UtilMisc.toMap("userLogin", userLogin, "productStoreId", productStoreId, "shoppingListTypeId", "SLT_BF_WISH_LIST", "listName", PERSISTANT_LIST_NAME);
		            Map newListResult = dispatcher.runSync("createShoppingList", listFields);
	
		            if (newListResult != null) {
		            	wishListId = (String) newListResult.get("shoppingListId");
		            }
		        }
	        }
        } catch (Exception e) {
            Debug.logError(e, "Problems getting Shopping wish List id", module);
        }
        return wishListId;
    }

    /**
     * Remove all Discontinued items from the given list.
     */
    public static void checkWishList(Delegator delegator, String shoppingListId) {
        
        GenericValue shoppingList = null;
        List shoppingListItems = null;
        try {
            shoppingList = delegator.findByPrimaryKey("ShoppingList", UtilMisc.toMap("shoppingListId", shoppingListId));
            if (shoppingList != null) {
                shoppingListItems = shoppingList.getRelatedCache("ShoppingListItem");
                if (shoppingListItems == null) {
                    shoppingListItems = new LinkedList();
                }
                Iterator sli = shoppingListItems.iterator();
                while (sli.hasNext()) {
                    GenericValue shoppingListItem = (GenericValue) sli.next();
                    String productId = shoppingListItem.getString("productId");
                    if (productId != null) {
                        java.sql.Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
                        GenericValue product = shoppingListItem.getRelatedOneCache("Product");
                        if (product.getTimestamp("introductionDate") != null && nowTimestamp.after(product.getTimestamp("introductionDate"))) {
                            if (product.getTimestamp("salesDiscontinuationDate") != null && nowTimestamp.after(product.getTimestamp("salesDiscontinuationDate"))) {
                        		shoppingListItem.remove();
                            }
                        }
                    }
                }
            }
        } catch (GenericEntityException e) {
            Debug.logError(e, "Problems getting ShoppingList and ShoppingListItem records", module);
        }
    }

    /** Update the items in the shopping wish list. */
    public static String modifyWishList(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        Locale locale = UtilHttp.getLocale(request);
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        String shoppingListId = getWishListId(request, false);

        ArrayList deleteList = new ArrayList();
        Map paramMap = UtilHttp.getParameterMap(request);

        Set parameterNames = paramMap.keySet();
        Iterator parameterNameIter = parameterNames.iterator();
        while (parameterNameIter.hasNext()) {
            String parameterName = (String) parameterNameIter.next();
            int underscorePos = parameterName.lastIndexOf('_');
            if (underscorePos >= 0 && (!parameterName.endsWith("_i18n"))) {
                try {
                    String shoppingListItemSeqId = parameterName.substring(underscorePos + 1);
                    String quantString = (String) paramMap.get(parameterName);
                    BigDecimal quantity = BigDecimal.ONE.negate();
                    if (quantString != null) quantString = quantString.trim();
                    quantity = (BigDecimal) ObjectType.simpleTypeConvert(quantString, "BigDecimal", null, locale);
                    if (parameterName.toUpperCase().startsWith("UPDATE")) {
                        if (quantity.compareTo(BigDecimal.ZERO) == 0) {
                            deleteList.add(shoppingListItemSeqId);
                        } else {
                            try {
                                Map ctx = UtilMisc.toMap("userLogin", userLogin, "shoppingListId", shoppingListId, "shoppingListItemSeqId", shoppingListItemSeqId, "quantity", quantity);
                                dispatcher.runSync("updateShoppingListItem", ctx);
                            } catch (GenericServiceException e) {
                                Debug.logError(e, "Problems updating ShoppingList item entity", module);
                            }
                        }
                    }
                } catch(Exception e) {
                    Debug.logError(e, "Problems updating ShoppingList item entity", module);
                }
            }        	
        }

        Iterator di = deleteList.iterator();
        while (di.hasNext()) {
        	String shoppingListItemSeqId = (String) di.next();
            try {
                Map ctx = UtilMisc.toMap("userLogin", userLogin, "shoppingListId", shoppingListId, "shoppingListItemSeqId", shoppingListItemSeqId);
                dispatcher.runSync("removeShoppingListItem", ctx);
            } catch (GenericServiceException e) {
                Debug.logError(e, "Problems deleting ShoppingList item entity", module);
            }
        }
        return "success";
    }

    /** Delete an item from the shopping wish list. */
    public static String deleteFromWishList(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
        String shoppingListId = getWishListId(request, false);

        Map paramMap = UtilHttp.getParameterMap(request);
        Set parameterNames = paramMap.keySet();
        Iterator parameterNameIter = parameterNames.iterator();
        Map serviceResult = null;

        while (parameterNameIter.hasNext()) {
            String parameterName = (String) parameterNameIter.next();
            if (parameterName.toUpperCase().startsWith("DELETE")) {
                try {
                    String shoppingListItemSeqId = parameterName.substring(parameterName.lastIndexOf('_') + 1);
                    try {
                        Map ctx = UtilMisc.toMap("userLogin", userLogin, "shoppingListId", shoppingListId, "shoppingListItemSeqId", shoppingListItemSeqId);
                        serviceResult = dispatcher.runSync("removeShoppingListItem", ctx);
                    } catch (GenericServiceException e) {
                        Debug.logError(e, "Problems deleting ShoppingList item entity", module);
                    }
                } catch(Exception e) {
                    Debug.logError(e, "Problems deleting ShoppingList item entity", module);
                }
            }
        }
        if (ServiceUtil.isError(serviceResult)) {
            return "error";
        }
        return "success";
    }
    

    /** Delete an item from the shopping wish list and set deleted product id in request. */
    public static String addToCartFromWishList(HttpServletRequest request, HttpServletResponse response)
    {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        HttpSession session = request.getSession();
        Locale locale = UtilHttp.getLocale(request);
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
        String shoppingListId = getWishListId(request, false);
        BigDecimal quantity = BigDecimal.ZERO;
        ShoppingCart shoppingCart = org.ofbiz.order.shoppingcart.ShoppingCartEvents.getCartObject(request);
        ShoppingCartHelper cartHelper = new ShoppingCartHelper(delegator, dispatcher, shoppingCart); 

        Map paramMap = UtilHttp.getParameterMap(request);
        Set parameterNames = paramMap.keySet();
        Iterator parameterNameIter = parameterNames.iterator();
        Map serviceResult = null;
        MessageString tmpMessage = null;
        List<MessageString> errMsgList = new ArrayList<MessageString>();
    	BigDecimal inventoryInStockFrom = new BigDecimal("-1");
    	BigDecimal inventoryOutOfStockTo = new BigDecimal("-1");
    	BigDecimal inventoryLevel = BigDecimal.ZERO;
    	BigDecimal inventoryWarehouseLevel = BigDecimal.ZERO;
        String productId = "";
        GenericValue product = null;
        String productName = null;
        while (parameterNameIter.hasNext())
        {
            String parameterName = (String) parameterNameIter.next();
            if (parameterName.toUpperCase().startsWith("ADD_ITEM_ID"))
            {
                try
                {
                    String shoppingListItemSeqId = (String) paramMap.get(parameterName);
                    Map findMap = UtilMisc.toMap("shoppingListId", shoppingListId, "shoppingListItemSeqId", shoppingListItemSeqId);
                    GenericValue shoppingListItem = delegator.findByPrimaryKeyCache("ShoppingListItem", findMap);
                    if (shoppingListItem == null)
                    {
                    	return "error";
                    }
                    String quantityStr = (String) paramMap.get("update_"+shoppingListItemSeqId);
                    if (UtilValidate.isEmpty(quantityStr))
                    {
                    	quantityStr = shoppingListItem.getString("quantity");
                    }
                    String categoryId = (String) paramMap.get("add_category_id_"+shoppingListItemSeqId);
                    productId = shoppingListItem.getString("productId");
                    try
                    {
                        product = delegator.findByPrimaryKey("Product", UtilMisc.toMap("productId", productId));
                    	productName = ProductContentWrapper.getProductContentAsText(product, "PRODUCT_NAME", locale, dispatcher);
                    	if(UtilValidate.isEmpty(productName))
                    	{
                    		GenericValue virtualProduct = ProductWorker.getParentProduct(productId, delegator);
                    		if(UtilValidate.isNotEmpty(virtualProduct))
                        	{
                    			productName = ProductContentWrapper.getProductContentAsText(virtualProduct, "PRODUCT_NAME", locale, dispatcher);
                        	}
                    	}
                    	

                    }
                    catch (Exception e)
                    {
                        Debug.logError(e, "Problems deleting ShoppingList item entity", module);
                    }
                    if (UtilValidate.isNotEmpty(quantityStr)) 
                    {
                        try 
                        {
                            quantity = new BigDecimal(quantityStr);
                        } 
                        catch (NumberFormatException nfe) 
                        {
                            quantity = BigDecimal.ZERO;
                        }
                    }
            		if(quantity.compareTo(BigDecimal.ZERO) > 0)
                    {
            			
            			
                    	//Check Inventory before adding
            			Map<String,Object> inventoryLevelMap = InventoryServices.getProductInventoryLevel(productId, request);
                        if (UtilValidate.isNotEmpty(inventoryLevelMap))
                        {
                        	inventoryInStockFrom = (BigDecimal) inventoryLevelMap.get("inventoryLevelInStockFrom");
                        	inventoryOutOfStockTo = (BigDecimal) inventoryLevelMap.get("inventoryLevelOutOfStockTo");
                        	inventoryLevel = (BigDecimal) inventoryLevelMap.get("inventoryLevel");
                        	inventoryWarehouseLevel = (BigDecimal) inventoryLevelMap.get("inventoryWarehouseLevel");
             			    if ((inventoryLevel.doubleValue() < inventoryOutOfStockTo.doubleValue()) || (inventoryLevel.doubleValue() == inventoryOutOfStockTo.doubleValue()))
            			    {
                            	Map<String, String> messageMap = UtilMisc.toMap("productName", productName);
                            	tmpMessage = new MessageString(UtilProperties.getMessage("OSafeUiLabels", "OutOfStockProductInfo",messageMap, locale),"",true);
                            	errMsgList.add(tmpMessage);
                            	request.setAttribute("_ERROR_MESSAGE_LIST_", errMsgList);
            			    }
             			    else
             			    {
                    			
                    			// add item and quantity to cart using the addToCart method
                    			Map<String, Object> context = FastMap.newInstance(); 
                            	cartHelper.addToCart(null, null, null, productId, categoryId, null, null, null, null, quantity, null, null, null, null, null, null, null, null, null, context, null);
        	                	com.osafe.events.ShoppingCartEvents.setProductFeaturesOnCart(shoppingCart,productId);

                                Map ctx = UtilMisc.toMap("userLogin", userLogin, "shoppingListId", shoppingListId, "shoppingListItemSeqId", shoppingListItemSeqId);
                                serviceResult = dispatcher.runSync("removeShoppingListItem", ctx);

             			    }
                        	
                        }
            			
                    }
                }
                catch(Exception e)
                {
                    Debug.logError(e, "Problems deleting ShoppingList item entity", module);
                }
            }
        }
        if (ServiceUtil.isError(serviceResult)|| errMsgList.size() > 0)
        {
            return "error";
        }
        else
        {
        	if (UtilValidate.isNotEmpty(productId))
        	{
            	//Get values for success message variables
            	Map<String, String> messageMap = UtilMisc.toMap("productName", productName);
                //Set the success message
            	String successMess = UtilProperties.getMessage(ShoppingCartEvents.label_resource, "AddToCartFromWishListSuccess", messageMap, locale); 
                List osafeSuccessMessageList = UtilMisc.toList(successMess);
                request.setAttribute("osafeSuccessMessageList", osafeSuccessMessageList);
        		
        	}
        }

        return "success";
    }
    public static String addMultiItemsToCartFromWishList(HttpServletRequest request, HttpServletResponse response)
    {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        HttpSession session = request.getSession();
        Locale locale = UtilHttp.getLocale(request);
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
        String shoppingListId = getWishListId(request, false);

        Map paramMap = UtilHttp.getParameterMap(request);
        Set parameterNames = paramMap.keySet();
        Iterator parameterNameIter = parameterNames.iterator();
        Map serviceResult = null;
        BigDecimal quantity = BigDecimal.ZERO;
        ShoppingCart shoppingCart = org.ofbiz.order.shoppingcart.ShoppingCartEvents.getCartObject(request);
        ShoppingCartHelper cartHelper = new ShoppingCartHelper(delegator, dispatcher, shoppingCart); 
        MessageString tmpMessage = null;
        List<MessageString> errMsgList = new ArrayList<MessageString>();
    	BigDecimal inventoryInStockFrom = new BigDecimal("-1");
    	BigDecimal inventoryOutOfStockTo = new BigDecimal("-1");
    	BigDecimal inventoryLevel = BigDecimal.ZERO;
    	BigDecimal inventoryWarehouseLevel = BigDecimal.ZERO;
        String productId = "";
        GenericValue product = null;
        String productName = null;

        
        while (parameterNameIter.hasNext())
        {
            String parameterName = (String) parameterNameIter.next();
            if (parameterName.toUpperCase().startsWith("ADD_MULTI_PRODUCT_ID"))
            {
                try
                {
                    String shoppingListItemSeqId = parameterName.substring(parameterName.lastIndexOf('_') + 1);
                    Map findMap = UtilMisc.toMap("shoppingListId", shoppingListId, "shoppingListItemSeqId", shoppingListItemSeqId);
                    GenericValue shoppingListItem = delegator.findByPrimaryKeyCache("ShoppingListItem", findMap);
                    if (shoppingListItem == null)
                    {
                    	return "error";
                    }
                    String quantityStr = (String) paramMap.get("update_"+shoppingListItemSeqId);
                    if (UtilValidate.isEmpty(quantityStr))
                    {
                    	quantityStr = shoppingListItem.getString("quantity");
                    }
                    String categoryId = (String) paramMap.get("add_category_id_"+shoppingListItemSeqId);
                    request.setAttribute("add_multi_product_quantity_"+shoppingListItemSeqId, quantityStr);
                    
                    productId = shoppingListItem.getString("productId");
                    try
                    {
                        product = delegator.findByPrimaryKey("Product", UtilMisc.toMap("productId", productId));
                    	productName = ProductContentWrapper.getProductContentAsText(product, "PRODUCT_NAME", locale, dispatcher);
                    	if(UtilValidate.isEmpty(productName))
                    	{
                    		GenericValue virtualProduct = ProductWorker.getParentProduct(productId, delegator);
                    		if(UtilValidate.isNotEmpty(virtualProduct))
                        	{
                    			productName = ProductContentWrapper.getProductContentAsText(virtualProduct, "PRODUCT_NAME", locale, dispatcher);
                        	}
                    	}
                    }
                    catch (Exception e)
                    {
                        Debug.logError(e, "Problems deleting ShoppingList item entity", module);
                    }
                    if (UtilValidate.isNotEmpty(quantityStr)) 
                    {
                        try 
                        {
                            quantity = new BigDecimal(quantityStr);
                        } 
                        catch (NumberFormatException nfe) 
                        {
                            quantity = BigDecimal.ZERO;
                        }
                    }
            		if(quantity.compareTo(BigDecimal.ZERO) > 0)
                    {
                    	//Check Inventory before adding
            			Map<String,Object> inventoryLevelMap = InventoryServices.getProductInventoryLevel(productId, request);
                        if (UtilValidate.isNotEmpty(inventoryLevelMap))
                        {
                        	inventoryInStockFrom = (BigDecimal) inventoryLevelMap.get("inventoryLevelInStockFrom");
                        	inventoryOutOfStockTo = (BigDecimal) inventoryLevelMap.get("inventoryLevelOutOfStockTo");
                        	inventoryLevel = (BigDecimal) inventoryLevelMap.get("inventoryLevel");
                        	inventoryWarehouseLevel = (BigDecimal) inventoryLevelMap.get("inventoryWarehouseLevel");
             			    if ((inventoryLevel.doubleValue() < inventoryOutOfStockTo.doubleValue()) || (inventoryLevel.doubleValue() == inventoryOutOfStockTo.doubleValue()))
            			    {
                            	Map<String, String> messageMap = UtilMisc.toMap("productName", productName);
                            	tmpMessage = new MessageString(UtilProperties.getMessage("OSafeUiLabels", "OutOfStockProductInfo",messageMap, locale),"",true);
                            	errMsgList.add(tmpMessage);
                            	request.setAttribute("_ERROR_MESSAGE_LIST_", errMsgList);
            			    }
             			    else
             			    {
                            	// add item and quantity to cart using the addToCart method
                    			Map<String, Object> context = FastMap.newInstance(); 
                            	cartHelper.addToCart(null, null, null, shoppingListItem.getString("productId"), categoryId, null, null, null, null, quantity, null, null, null, null, null, null, null, null, null, context, null);
        	                	com.osafe.events.ShoppingCartEvents.setProductFeaturesOnCart(shoppingCart,shoppingListItem.getString("productId"));

                                Map ctx = UtilMisc.toMap("userLogin", userLogin, "shoppingListId", shoppingListId, "shoppingListItemSeqId", shoppingListItemSeqId);
                                serviceResult = dispatcher.runSync("removeShoppingListItem", ctx);
        	                	
             			    }
                        	
                        }
                    }
                }
                catch(Exception e)
                {
                    Debug.logError(e, "Problems deleting ShoppingList item entity", module);
                }
            }
        }
        if (ServiceUtil.isError(serviceResult) || errMsgList.size() > 0)
        {
            return "error";
        }
        else
        {
        	String successMess = UtilProperties.getMessage(ShoppingCartEvents.label_resource, "AddMultiItemsToCartFromWishListSuccess", locale); 
            List osafeSuccessMessageList = UtilMisc.toList(successMess);
            request.setAttribute("osafeSuccessMessageList", osafeSuccessMessageList);
        }
        return "success";
    }
}