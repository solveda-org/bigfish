package com.osafe.events;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
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

/**
 * Shopping cart events.
 */
public class ShoppingListEvents {

    public static final String module = ShoppingListEvents.class.getName();
    public static final String resource = "OrderUiLabels";
    public static final String resource_error = "OrderErrorUiLabels";
    public static final String PERSISTANT_LIST_NAME = "auto-save";


    /**
     * Finds or creates a specialized (auto-save) shopping list used to record shopping bag contents between user visits.
     */
    public static String getAutoSaveListId(Delegator delegator, LocalDispatcher dispatcher, String partyId, GenericValue userLogin, String productStoreId) throws GenericEntityException, GenericServiceException {
        if (partyId == null && userLogin != null) {
            partyId = userLogin.getString("partyId");
        }

        String autoSaveListId = null;
        GenericValue list = null;
        // TODO: add sorting, just in case there are multiple...
        if (partyId != null) {
        Map findMap = UtilMisc.toMap("partyId", partyId, "productStoreId", productStoreId, "shoppingListTypeId", "SLT_SPEC_PURP", "listName", PERSISTANT_LIST_NAME);
        List existingLists = delegator.findByAnd("ShoppingList", findMap);
        Debug.logInfo("Finding existing auto-save shopping list with:  \nfindMap: " + findMap + "\nlists: " + existingLists, module);

        if (existingLists != null && !existingLists.isEmpty()) {
            list = EntityUtil.getFirst(existingLists);
            autoSaveListId = list.getString("shoppingListId");
        }
        }
        if (list == null && dispatcher != null) {
            Map listFields = UtilMisc.toMap("userLogin", userLogin, "productStoreId", productStoreId, "shoppingListTypeId", "SLT_SPEC_PURP", "listName", PERSISTANT_LIST_NAME);
            Map newListResult = dispatcher.runSync("createShoppingList", listFields);

            if (newListResult != null) {
                autoSaveListId = (String) newListResult.get("shoppingListId");
            }
        }

        return autoSaveListId;
    }

    /**
     * Fills the specialized shopping list with the current shopping cart if one exists (if not leaves it alone)
     */
    public static void fillAutoSaveList(ShoppingCart cart, LocalDispatcher dispatcher) throws GeneralException {
        if (cart != null && dispatcher != null) {
            GenericValue userLogin = ShoppingListEvents.getCartUserLogin(cart);
            //if (userLogin == null) return; //only save carts when a user is logged in....
            Delegator delegator = cart.getDelegator();
            //String autoSaveListId = getAutoSaveListId(delegator, dispatcher, null, userLogin, cart.getProductStoreId());
            String autoSaveListId = cart.getAutoSaveListId();
            if (autoSaveListId == null) {
                autoSaveListId = getAutoSaveListId(delegator, dispatcher, null, userLogin, cart.getProductStoreId());
                cart.setAutoSaveListId(autoSaveListId);
            }
            try {
                String[] itemsArray = makeCartItemsArray(cart);
                if (itemsArray != null && itemsArray.length != 0) {
                    org.ofbiz.order.shoppinglist.ShoppingListEvents.addBulkFromCart(delegator, dispatcher, cart, userLogin, autoSaveListId, null, itemsArray, false, false);
                }
            } catch (IllegalArgumentException e) {
                throw new GeneralException(e.getMessage(), e);
            }
        }
    }

    /**
     * Saves the shopping cart to the specialized (auto-save) shopping list
     */
    public static String saveCartToAutoSaveList(HttpServletRequest request, HttpServletResponse response) {
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        ShoppingCart cart = ShoppingCartEvents.getCartObject(request);

        try {
            fillAutoSaveList(cart, dispatcher);
        } catch (GeneralException e) {
            Debug.logError(e, "Error saving the cart to the auto-save list: " + e.toString(), module);
        }

        return "success";
    }

    /**
     * Restores the specialized (auto-save) shopping list back into the shopping cart
     */
    public static String restoreAutoSaveList(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        GenericValue productStore = ProductStoreWorker.getProductStore(request);

        if (!ProductStoreWorker.autoSaveCart(productStore)) {
            // if auto-save is disabled just return here
            return "success";
        }

        HttpSession session = request.getSession();
        ShoppingCart cart = ShoppingCartEvents.getCartObject(request);

        // safety check for missing required parameter.
        if (cart.getWebSiteId() == null) {
            cart.setWebSiteId(CatalogWorker.getWebSiteId(request));
        }

        // locate the user's identity
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
        if (userLogin == null) {
            userLogin = (GenericValue) session.getAttribute("autoUserLogin");
        }

/*        if (userLogin == null) {
            // not logged in; cannot identify the user
            return "success";
        }*/

        // find the list ID
        String autoSaveListId = cart.getAutoSaveListId();
        if (autoSaveListId == null) {
            try {
                autoSaveListId = getAutoSaveListId(delegator, dispatcher, null, userLogin, cart.getProductStoreId());
            } catch (GeneralException e) {
                Debug.logError(e, module);
            }
            cart.setAutoSaveListId(autoSaveListId);
        } else if (userLogin != null) {
            String existingAutoSaveListId = null;
            try {
                existingAutoSaveListId = getAutoSaveListId(delegator, dispatcher, null, userLogin, cart.getProductStoreId());
            } catch (GeneralException e) {
                Debug.logError(e, module);
            }
            if (existingAutoSaveListId != null) {
                if (!existingAutoSaveListId.equals(autoSaveListId)) {
                    // Replace with existing shopping list
                    cart.setAutoSaveListId(existingAutoSaveListId);
                    autoSaveListId = existingAutoSaveListId;
                    cart.setLastListRestore(null);
                } else {
                    // CASE: User first login and logout and then re-login again. This condition does not require a restore at all
                    // because at this point items in the cart and the items in the shopping list are same so just return.
                    return "success";
                }
            }
        }

        // check to see if we are okay to load this list
        java.sql.Timestamp lastLoad = cart.getLastListRestore();
        boolean okayToLoad = autoSaveListId == null ? false : (lastLoad == null ? true : false);
        if (!okayToLoad && lastLoad != null) {
            GenericValue shoppingList = null;
            try {
                shoppingList = delegator.findByPrimaryKey("ShoppingList", UtilMisc.toMap("shoppingListId", autoSaveListId));
            } catch (GenericEntityException e) {
                Debug.logError(e, module);
            }
            if (shoppingList != null) {
                java.sql.Timestamp lastModified = shoppingList.getTimestamp("lastAdminModified");
                if (lastModified != null) {
                    if (lastModified.after(lastLoad)) {
                        okayToLoad = true;
                    }
                    if (cart.size() == 0 && lastModified.after(cart.getCartCreatedTime())) {
                        okayToLoad = true;
                    }
                }
            }
        }

        // load (restore) the list of we have determined it is okay to load
        if (okayToLoad) {
            String prodCatalogId = CatalogWorker.getCurrentCatalogId(request);
            try {
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
                
                
            } catch (IllegalArgumentException e) {
                Debug.logError(e, module);
            }
        }

        return "success";
    }

    private static GenericValue getCartUserLogin(ShoppingCart cart) {
        GenericValue ul = cart.getUserLogin();
        if (ul == null) {
            ul = cart.getAutoUserLogin();
        }
        return ul;
    }

    private static String[] makeCartItemsArray(ShoppingCart cart) {
        int len = cart.size();
        String[] arr = new String[len];
        for (int i = 0; i < len; i++) {
            arr[i] = Integer.toString(i);
        }
        return arr;
    }
}
