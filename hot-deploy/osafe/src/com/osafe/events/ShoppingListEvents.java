package com.osafe.events;

import java.sql.Timestamp;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.LinkedList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilFormatOut;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.order.shoppingcart.ShoppingCart;
import org.ofbiz.order.shoppingcart.ShoppingCartEvents;
import org.ofbiz.order.shoppingcart.ShoppingCartItem;
import org.ofbiz.product.catalog.CatalogWorker;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

/**
 * Shopping cart events.
 */
public class ShoppingListEvents 
{

    public static final String module = ShoppingListEvents.class.getName();
    public static final String resource = "OrderUiLabels";
    public static final String resource_error = "OrderErrorUiLabels";
    public static final String PERSISTANT_LIST_NAME = "auto-save";


    /**
     * Finds or creates a specialized (auto-save) shopping list used to record shopping bag contents between user visits.
     */
    public static String getAutoSaveListId(Delegator delegator, LocalDispatcher dispatcher, String partyId,  String visitorId, GenericValue userLogin, String productStoreId) throws GenericEntityException, GenericServiceException 
    {
        if ( UtilValidate.isEmpty(partyId) && UtilValidate.isNotEmpty(userLogin) ) 
        {
            partyId = userLogin.getString("partyId");
        }

        String autoSaveListId = null;
        GenericValue list = null;
        // TODO: add sorting, just in case there are multiple...
        if (UtilValidate.isNotEmpty(visitorId) ) 
        {
	        Map findMap = UtilMisc.toMap("visitorId", visitorId, "productStoreId", productStoreId, "shoppingListTypeId", "SLT_SPEC_PURP", "listName", PERSISTANT_LIST_NAME);
	        List existingLists = delegator.findByAnd("ShoppingList", findMap);
	        Debug.logInfo("Finding existing auto-save shopping list with:  \nfindMap: " + findMap + "\nlists: " + existingLists, module);
	
	        if ( UtilValidate.isNotEmpty(existingLists)) 
	        {
	            list = EntityUtil.getFirst(existingLists);
	            autoSaveListId = list.getString("shoppingListId");
	        }
        }
        else if (UtilValidate.isNotEmpty(partyId))
        {
	        Map findMap = UtilMisc.toMap("partyId", partyId, "productStoreId", productStoreId, "shoppingListTypeId", "SLT_SPEC_PURP", "listName", PERSISTANT_LIST_NAME);
	        List existingLists = delegator.findByAnd("ShoppingList", findMap);
	        Debug.logInfo("Finding existing auto-save shopping list with:  \nfindMap: " + findMap + "\nlists: " + existingLists, module);
	
	        if (UtilValidate.isNotEmpty(existingLists)) 
	        {
	            list = EntityUtil.getFirst(existingLists);
	            autoSaveListId = list.getString("shoppingListId");
	        }
        	
        }
        if ( UtilValidate.isEmpty(list) && UtilValidate.isNotEmpty(dispatcher)) 
        {
        	if (UtilValidate.isEmpty(userLogin) && UtilValidate.isNotEmpty(visitorId))
        	{
                GenericValue adminLogin = delegator.findByPrimaryKeyCache("UserLogin",UtilMisc.toMap("userLoginId","admin"));
            	if (UtilValidate.isNotEmpty(adminLogin))
            	{
            		userLogin=adminLogin;
            	}

        	}
        	if (UtilValidate.isNotEmpty(userLogin))
        	{
	            Map listFields = UtilMisc.toMap("userLogin", userLogin, "productStoreId", productStoreId, "shoppingListTypeId", "SLT_SPEC_PURP", "listName", PERSISTANT_LIST_NAME);
	            if (UtilValidate.isNotEmpty(partyId))
	            {
	            	listFields.put("partyId", partyId);
	            }
	            if (UtilValidate.isNotEmpty(visitorId))
	            {
	            	listFields.put("visitorId", visitorId);
	            	
	            }
	            Map newListResult = dispatcher.runSync("createShoppingList", listFields);
	
	            if ( UtilValidate.isNotEmpty(newListResult) ) 
	            {
	                autoSaveListId = (String) newListResult.get("shoppingListId");
	            }
        	}
        }
        return autoSaveListId;
    }

    /**
     * Fills the specialized shopping list with the current shopping cart if one exists (if not leaves it alone)
     */
    public static void fillAutoSaveList(ShoppingCart cart, LocalDispatcher dispatcher, HttpServletRequest request) throws GeneralException 
    {
        if ( UtilValidate.isNotEmpty(cart) && UtilValidate.isNotEmpty(dispatcher)) 
        {
            GenericValue userLogin = ShoppingListEvents.getCartUserLogin(cart);
            Delegator delegator = cart.getDelegator();
            HttpSession session = request.getSession();
            String visitorId=null;

            GenericValue visitor = (GenericValue) session.getAttribute("visitor");
            if (UtilValidate.isNotEmpty(visitor)) 
            {
            	visitorId = visitor.getString("visitorId");
            }

            String autoSaveListId = cart.getAutoSaveListId();
            if (UtilValidate.isEmpty(autoSaveListId)) 
            {
                autoSaveListId = getAutoSaveListId(delegator, dispatcher, null, visitorId,userLogin, cart.getProductStoreId());
                cart.setAutoSaveListId(autoSaveListId);
            }
            try 
            {
                String[] itemsArray = makeCartItemsArray(cart);
                if ( UtilValidate.isNotEmpty(itemsArray) && itemsArray.length > 0) 
				{
                    org.ofbiz.order.shoppinglist.ShoppingListEvents.addBulkFromCart(delegator, dispatcher, cart, userLogin, autoSaveListId, null, itemsArray, false, false);
                }
            } 
            catch (IllegalArgumentException e) 
            {
                throw new GeneralException(e.getMessage(), e);
            }
        }
    }

    /**
     * Saves the shopping cart to the specialized (auto-save) shopping list
     */
    public static String saveCartToAutoSaveList(HttpServletRequest request, HttpServletResponse response) 
    {
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        ShoppingCart cart = ShoppingCartEvents.getCartObject(request);
        try 
        {
            fillAutoSaveList(cart, dispatcher, request);
        } 
        catch (GeneralException e) 
        {
            Debug.logError(e, "Error saving the cart to the auto-save list: " + e.toString(), module);
        }

        return "success";
    }

    /**
     * Saves the shopping cart to the specialized (auto-save) shopping list for a visitor
     */
    public static String saveCartToAutoSaveVisitorList(HttpServletRequest request, HttpServletResponse response) {
    	Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        GenericValue visitor = (GenericValue) request.getSession().getAttribute("visitor");
        GenericValue productStore = ProductStoreWorker.getProductStore(request);

        if (!ProductStoreWorker.autoSaveCart(productStore)) {
            // if auto-save is disabled just return here
            return "success";
        }

        if (UtilValidate.isNotEmpty(userLogin))
        {
            return "success";
        	
        }
        		
        ShoppingCart cart = ShoppingCartEvents.getCartObject(request);
        List cartItems = cart.items();
        String autoSaveListId=cart.getAutoSaveListId();
        if (UtilValidate.isNotEmpty(visitor) && UtilValidate.isNotEmpty(autoSaveListId)) 
        {
           try 
           {
               GenericValue adminLogin = delegator.findByPrimaryKey("UserLogin",UtilMisc.toMap("userLoginId","admin"));
               if (UtilValidate.isNotEmpty(adminLogin))
               {
            	   if (cartItems.size() == 0)
            	   {
                       List<GenericValue> lShopItems = delegator.findByAnd("ShoppingListItem", UtilMisc.toMap("shoppingListId", autoSaveListId));
                       if (lShopItems.size() > 0)
                       {
                          delegator.removeByAnd("ShoppingListItem", UtilMisc.toMap("shoppingListId", autoSaveListId));
                       }
            		   
            	   }
            	   else
            	   {
		               String[] itemsArray = makeCartItemsArray(cart);
		               if (UtilValidate.isNotEmpty(itemsArray) && itemsArray.length > 0) 
						{
		                   org.ofbiz.order.shoppinglist.ShoppingListEvents.addBulkFromCart(delegator, dispatcher, cart, adminLogin, autoSaveListId, null, itemsArray, false, false);
		                }
            	   }
               }
          }
              catch (Exception e)
              {
                  Debug.logError(e, module);
                  
              }
        }
        return "success";
    }
    

    /**
     * Restores the specialized (auto-save) shopping list back into the shopping cart
     */
    public static String restoreAutoSaveList(HttpServletRequest request, HttpServletResponse response) 
    {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        GenericValue productStore = ProductStoreWorker.getProductStore(request);
        HttpSession session = request.getSession();
        String visitorId=null;

        if (!ProductStoreWorker.autoSaveCart(productStore)) 
        {
            // if auto-save is disabled just return here
            return "success";
        }

        ShoppingCart cart = ShoppingCartEvents.getCartObject(request);
        // safety check for missing required parameter.
        if (UtilValidate.isEmpty(cart.getWebSiteId())) 
        {
            cart.setWebSiteId(CatalogWorker.getWebSiteId(request));
        }

        // locate the user's identity
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
        if (UtilValidate.isEmpty(userLogin)) 
        {
            userLogin = (GenericValue) session.getAttribute("autoUserLogin");
        }

        // Check for Visitor the user's identity
        GenericValue visitor = (GenericValue) session.getAttribute("visitor");
        if (UtilValidate.isNotEmpty(visitor)) 
        {
        	visitorId = visitor.getString("visitorId");
        }

        // find the list ID
        String autoSaveListId = cart.getAutoSaveListId();
        if (UtilValidate.isEmpty(autoSaveListId)) 
        {
            try 
            {
                autoSaveListId = getAutoSaveListId(delegator, dispatcher, null, visitorId, userLogin, cart.getProductStoreId());
            } 
            catch (GeneralException e) 
            {
                Debug.logError(e, module);
            }
            cart.setAutoSaveListId(autoSaveListId);
        } 
        else if (UtilValidate.isNotEmpty(userLogin)) 
        {
            String existingAutoSaveListId = null;
            try 
            {
                existingAutoSaveListId = getAutoSaveListId(delegator, dispatcher, null, visitorId, userLogin, cart.getProductStoreId());
            } 
            catch (GeneralException e) 
            {
                Debug.logError(e, module);
            }
            if (UtilValidate.isNotEmpty(existingAutoSaveListId)) 
            {
                if (!existingAutoSaveListId.equals(autoSaveListId))
                {
                    // Replace with existing shopping list
                    cart.setAutoSaveListId(existingAutoSaveListId);
                    autoSaveListId = existingAutoSaveListId;
                    cart.setLastListRestore(null);
                } 
                else 
                {
                    // CASE: User first login and logout and then re-login again. This condition does not require a restore at all
                    // because at this point items in the cart and the items in the shopping list are same so just return.
                    return "success";
                }
            }
        }
 
        
        // check to see if we are okay to load this list
        java.sql.Timestamp lastLoad = cart.getLastListRestore();
        boolean okayToLoad = autoSaveListId == null ? false : (lastLoad == null ? true : false);
        if (!okayToLoad && UtilValidate.isNotEmpty(lastLoad)) 
        {
            GenericValue shoppingList = null;
            try 
            {
                shoppingList = delegator.findByPrimaryKey("ShoppingList", UtilMisc.toMap("shoppingListId", autoSaveListId));
            } 
            catch (GenericEntityException e) 
            {
                Debug.logError(e, module);
            }
            if (UtilValidate.isNotEmpty(shoppingList))
            {
                java.sql.Timestamp lastModified = shoppingList.getTimestamp("lastAdminModified");
                if ( UtilValidate.isNotEmpty(lastModified)) 
                {
                    if (lastModified.after(lastLoad)) 
                    {
                        okayToLoad = true;
                    }
                    if (cart.size() == 0 && lastModified.after(cart.getCartCreatedTime())) 
                    {
                        okayToLoad = true;
                    }
                }
            }
        }
        
        // load (restore) the list of we have determined it is okay to load
        if (okayToLoad) 
        {
            String prodCatalogId = CatalogWorker.getCurrentCatalogId(request);
            try 
            {
            	GenericValue shoppingList = delegator.findByPrimaryKey("ShoppingList", UtilMisc.toMap("shoppingListId", autoSaveListId));
                List shoppingListItems = null;
                if( UtilValidate.isNotEmpty(shoppingList))
                {
	                shoppingListItems = shoppingList.getRelated("ShoppingListItem");
	                if (UtilValidate.isEmpty(shoppingListItems)) 
	                {
	                    shoppingListItems = new LinkedList();
	                }
 					request.setAttribute("sizeOfCartBeforeLogin", shoppingListItems.size());
                }

               //addListToCart(delegator, dispatcher, cart, prodCatalogId, autoSaveListId, false, false, false);
                org.ofbiz.order.shoppinglist.ShoppingListEvents.addListToCart(delegator, dispatcher, cart, prodCatalogId, autoSaveListId, false, false, userLogin != null ? true : false);
                cart.setLastListRestore(UtilDateTime.nowTimestamp());
            	/*Now we need to insure features of products added to the cart are updated
            	 * add product Features
            	 */
                
               	for (Iterator<?> item = cart.iterator(); item.hasNext();) 
                {
                	ShoppingCartItem cartItem = (ShoppingCartItem)item.next();
                	com.osafe.events.ShoppingCartEvents.setProductFeaturesOnCart(cart,cartItem.getProductId());
                }
                
                
            } 
            catch (Exception e) 
            {
                Debug.logError(e, module);
            }
        }
        return "success";
    }
    

    public static String restoreAutoSaveVisitorList(HttpServletRequest request, HttpServletResponse response) 
    {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        GenericValue productStore = ProductStoreWorker.getProductStore(request);
        HttpSession session = request.getSession();
        GenericValue visitor = (GenericValue) session.getAttribute("visitor");

        if (!ProductStoreWorker.autoSaveCart(productStore)) 
        {
            // if auto-save is disabled just return here
            return "success";
        }
        ShoppingCart cart = ShoppingCartEvents.getCartObject(request);
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        if (UtilValidate.isNotEmpty(userLogin)) 
        {
            // if logged in just return here
            return "success";
        	
        }

        // Check for Visitor 
        if (UtilValidate.isNotEmpty(visitor)) 
        {
            String autoSaveListId = cart.getAutoSaveListId();
            if (UtilValidate.isEmpty(autoSaveListId)) 
            {
                try 
                {
                    autoSaveListId = getAutoSaveListId(delegator, dispatcher, null, visitor.getString("visitorId"), userLogin, cart.getProductStoreId());
                } 
                catch (GeneralException e) 
                {
                    Debug.logError(e, module);
                }
                cart.setAutoSaveListId(autoSaveListId);
            } 
            
            if (UtilValidate.isNotEmpty(autoSaveListId)) 
            {
                try 
                {
                    java.sql.Timestamp lastLoad = cart.getLastListRestore();
                    if (lastLoad == null)
                    {
                        List lShopItems = delegator.findByAnd("ShoppingListItem", UtilMisc.toMap("shoppingListId", autoSaveListId));
                        if (lShopItems.size() > 0)
                        {
                            org.ofbiz.order.shoppinglist.ShoppingListEvents.addListToCart(delegator, dispatcher, cart, CatalogWorker.getCurrentCatalogId(request), autoSaveListId, false, false, true);
                            
                        }
                        cart.setLastListRestore(UtilDateTime.nowTimestamp());
                    }
                }
                catch (GeneralException e) 
                {
                    Debug.logError(e, module);
                }

                

            }
        }
        return "success";
    }    

    
    

    public static String updateAutoSaveListParty(HttpServletRequest request, HttpServletResponse response) {
    	Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        GenericValue visitor = (GenericValue) request.getSession().getAttribute("visitor");
        Locale locale      = request.getLocale();
        String currencyUom = UtilHttp.getCurrencyUom(request);
        try 
        {
        	
            if (UtilValidate.isNotEmpty(visitor) && UtilValidate.isNotEmpty(userLogin))
            {
                ShoppingCart sc = org.ofbiz.order.shoppingcart.ShoppingCartEvents.getCartObject(request, locale, currencyUom);
                String sAutoSaveListId = sc.getAutoSaveListId();
                if (UtilValidate.isNotEmpty(sAutoSaveListId))
                {
                    GenericValue gvShopList = delegator.findByPrimaryKey("ShoppingList",UtilMisc.toMap("shoppingListId", sAutoSaveListId ));
                    if (UtilValidate.isNotEmpty(gvShopList))
                    {
                        gvShopList.set("partyId", userLogin.getString("partyId"));
                        gvShopList.store();
                    }
                    
                }
                
            }
           
       }
        catch (Exception e) {
            Debug.logError(e, module);      
            
        }
       return "success";
    }
    

    private static GenericValue getCartUserLogin(ShoppingCart cart) 
    {
        GenericValue ul = cart.getUserLogin();
        if (UtilValidate.isEmpty(ul)) 
        {
            ul = cart.getAutoUserLogin();
        }
        return ul;
    }

    private static String[] makeCartItemsArray(ShoppingCart cart) 
    {
        int len = cart.size();
        String[] arr = new String[len];
        for (int i = 0; i < len; i++) 
        {
            arr[i] = Integer.toString(i);
        }
        return arr;
    }
}
