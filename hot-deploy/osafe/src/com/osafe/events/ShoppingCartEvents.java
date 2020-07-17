package com.osafe.events;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.Map.Entry;  
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.jdom.JDOMException;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.FileUtil;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.MessageString;
import org.ofbiz.base.util.ObjectType;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilFormatOut;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilNumber;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.string.FlexibleStringExpander;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.order.shoppingcart.CartItemModifyException;
import org.ofbiz.order.shoppingcart.CheckOutHelper;
import org.ofbiz.order.shoppingcart.ItemNotFoundException;
import org.ofbiz.order.shoppingcart.ShoppingCart;
import org.ofbiz.order.shoppingcart.ShoppingCart.CartPaymentInfo;
import org.ofbiz.order.shoppingcart.ShoppingCart.CartShipInfo;
import org.ofbiz.order.shoppingcart.ShoppingCartItem;
import org.ofbiz.product.config.ProductConfigWrapper;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.order.shoppingcart.ShoppingCartHelper;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang.StringUtils;
import org.ofbiz.product.product.ProductContentWrapper;  
import org.ofbiz.product.store.ProductStoreWorker;
import org.apache.commons.lang.StringEscapeUtils;
import com.osafe.util.Util; 
import org.ofbiz.order.shoppingcart.product.ProductPromoWorker;  
import org.ofbiz.product.product.ProductWorker;

import com.osafe.control.SeoUrlHelper;

public class ShoppingCartEvents {
	
    public static final String label_resource = "OSafeUiLabels";
    public static String module = ShoppingCartEvents.class.getName();
    public static final int rounding = UtilNumber.getBigDecimalRoundingMode("order.rounding");
    private static final ResourceBundle OSAFE_PROPS = UtilProperties.getResourceBundle("OsafeProperties.xml", Locale.getDefault());

    public static String setPaymentMethodOnCart(HttpServletRequest request, HttpServletResponse response) 
    {
        ShoppingCart sc = org.ofbiz.order.shoppingcart.ShoppingCartEvents.getCartObject(request);
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        String securityCode = request.getParameter("verificationNo");
        
        if (sc.items().size() > 0) 
        {
            Map selectedPaymentMethods = new HashMap();
            Map paymentMethodInfo = FastMap.newInstance();
            List singleUsePayments = new ArrayList();
            paymentMethodInfo.put("amount", null);
            if (UtilValidate.isNotEmpty(securityCode))
            {
                paymentMethodInfo.put("securityCode", securityCode);
            }
            String paymentMethodId = (String) request.getAttribute("paymentMethodId");
            selectedPaymentMethods.put(paymentMethodId, paymentMethodInfo);
            sc.addPayment(paymentMethodId);
            CheckOutHelper checkOutHelper = new CheckOutHelper(dispatcher, delegator, sc);
            checkOutHelper.setCheckOutPayment(selectedPaymentMethods, singleUsePayments, null);
            
            //if shipping does not apply, do not calculate shipping (skip to calculating tax)
            if(!sc.shippingApplies())
            {
            	return "calcTax";
            }
        }

        return "success";
    }

    public static String resetPaymentMethod(HttpServletRequest request, HttpServletResponse response) 
    {
        ShoppingCart sc = org.ofbiz.order.shoppingcart.ShoppingCartEvents.getCartObject(request);
        List<GenericValue> paymentMethods = sc.getPaymentMethods();
        if (UtilValidate.isNotEmpty(paymentMethods)) 
        {
    		for(GenericValue paymentMethod : paymentMethods) 
            {
                CartPaymentInfo paymentInfo = sc.getPaymentInfo(paymentMethod.getString("paymentMethodId"));
                if (!paymentInfo.paymentMethodTypeId.equalsIgnoreCase("GIFT_CARD") && !paymentInfo.paymentMethodTypeId.equalsIgnoreCase("FIN_ACCOUNT"))
                {
                    paymentInfo.amount = null;
                }
            }
        }
        if(UtilValidate.isEmpty(sc.items())) 
        {
        	removeLoyaltyPoints(request, response);
            java.sql.Timestamp lastLoad = sc.getLastListRestore();
            org.ofbiz.order.shoppingcart.ShoppingCartEvents.clearCart(request, response);
            sc.setLastListRestore(lastLoad);
        }
        return "success";
    }

    public static String setProductCategoryId(HttpServletRequest request, HttpServletResponse response) 
    {
        ShoppingCart sc = org.ofbiz.order.shoppingcart.ShoppingCartEvents.getCartObject(request);
        String productCategoryId = request.getParameter("add_category_id");
        String productId = request.getParameter("add_product_id");
        
        if(UtilValidate.isNotEmpty(productCategoryId) && UtilValidate.isNotEmpty(productId)) 
        {
        	for (Iterator<?> item = sc.iterator(); item.hasNext();) 
        	{
            	ShoppingCartItem sci = (ShoppingCartItem)item.next();
            	if (sci.getProductId().equals(productId)) 
            	{
            		sci.setProductCategoryId(productCategoryId);
            		setProductFeaturesOnCart(sc,productId);
            		break;
            	}
            }	
        }
        return "success";
    }
    
    public static void setProductFeaturesOnCart(ShoppingCart cart, String productId) 
    {
    	
    
        if(UtilValidate.isNotEmpty(productId)) 
        {
    	  GenericValue product = null;
          try 
          {
        	List cartItems = cart.findAllCartItems(productId);
        	for (Iterator<?> item = cartItems.iterator(); item.hasNext();) 
        	{
            	ShoppingCartItem sci = (ShoppingCartItem)item.next();
        		product = sci.getProduct();
            		try 
            		{
            			/* Add All Product Feature Appls to the Cart Types (STANDARD_FEATURE, DISTINGUISHING_FEATURE)
            			 * 
            			 */
                		List<GenericValue> lProductFeatureAndAppl = product.getRelatedCache("ProductFeatureAndAppl");
                		lProductFeatureAndAppl = EntityUtil.filterByDate(lProductFeatureAndAppl);
                		for(GenericValue productFeatureAndAppl : lProductFeatureAndAppl)
            	    	{
                			sci.putAdditionalProductFeatureAndAppl(productFeatureAndAppl);
            	    	}

            			/* The ShoppingCartItem method [putAdditionalProductFeatureAndAppl] creates order adjustments of type 'ADDITIONAL_FEATURE'
            			 * These are not needing so removing
            			 */
                		
                		List<GenericValue> itemAdjustments = sci.getAdjustments();
            	    	for(GenericValue itemAdjustment : itemAdjustments)
            	    	{
            	    		String itemAdjustmentTypeId = itemAdjustment.getString("orderAdjustmentTypeId");
            	    		if(UtilValidate.isNotEmpty(itemAdjustmentTypeId) && "ADDITIONAL_FEATURE".equalsIgnoreCase(itemAdjustmentTypeId))
            	    		{
            	    			sci.removeAdjustment(itemAdjustment);
            	    		}
            	    	}

            		}
            		catch (Exception e)
            		{
            			Debug.logError(e,"Error: Setting Product Feature And Appl:"+ productId,module);
            		}
            }
          }
           catch (Exception e)
           {
        	    Debug.logError(e, "Error: setting Product Features on cart:" + product.getString("productId"), module);
        	   
           }
        }
    }
    
    
    public static String addMultiItemsToCart(HttpServletRequest request, HttpServletResponse response) 
    {
    	Map<String, Object> context = FastMap.newInstance(); 
    	Map<String, Object> params = UtilHttp.getParameterMap(request);
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        ShoppingCart shoppingCart = org.ofbiz.order.shoppingcart.ShoppingCartEvents.getCartObject(request);
        ShoppingCartHelper cartHelper = new ShoppingCartHelper(delegator, dispatcher, shoppingCart); 
        
        for(Entry<String, Object> entry : params.entrySet()) 
        {
            String parameterName = entry.getKey();
        	BigDecimal quantity = BigDecimal.ZERO;
        	String productId = null;
        	String quantityStr = null;
            String categoryId = null;
        	
        	if (parameterName.toUpperCase().startsWith("ADD_MULTI_PRODUCT_ID"))
        	{
        		productId = (String) entry.getValue();
        		//get the index so that we can get the related quantity
        		int underscorePos = parameterName.lastIndexOf('_');
        		if (underscorePos >= 0) 
        		{
        			try 
        			{
        				String indexStr = parameterName.substring(underscorePos + 1);
                        int index = Integer.parseInt(indexStr);
                        quantityStr = (String) params.get("add_multi_product_quantity_"+index);
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

                        categoryId = (String) params.get("add_category_id_"+index);
        			
        			}
        			catch (NumberFormatException nfe) 
        			{
        				return "error";
                    } 
        			catch (Exception e) 
        			{
        				return "error";
                    }
        		}
        		if(UtilValidate.isNotEmpty(productId) && quantity.compareTo(BigDecimal.ZERO) > 0)
                {
                	// add item and quantity to cart using the addToCart method
                	cartHelper.addToCart(null, null, null, productId, categoryId, null, null, null, null, quantity, null, null, null, null, null, null, null, null, null, context, null);
            		setProductFeaturesOnCart(shoppingCart,productId);
                }
        	}
        }
        return "success";
    }
    
    public static String addMultiItemsToWishlist(HttpServletRequest request, HttpServletResponse response) 
    {
    	Map<String, Object> context = FastMap.newInstance(); 
    	Map<String, Object> params = UtilHttp.getParameterMap(request);
        String categoryId = (String) params.get("add_category_id");
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        ShoppingCart sc = org.ofbiz.order.shoppingcart.ShoppingCartEvents.getCartObject(request);
        ShoppingCartHelper cartHelper = new ShoppingCartHelper(delegator, dispatcher, sc); 
        
        for(Entry<String, Object> entry : params.entrySet()) 
        {
            String parameterName = entry.getKey();
        	BigDecimal quantity = BigDecimal.ZERO;
        	String productId = null;
        	String quantityStr = null;
        	if (parameterName.toUpperCase().startsWith("ADD_MULTI_PRODUCT_ID"))
        	{
        		productId = (String) entry.getValue();
        		//get the index so that we can get the related quantity
        		int underscorePos = parameterName.lastIndexOf('_');
        		if (underscorePos >= 0) 
        		{
        			try 
        			{
        				String indexStr = parameterName.substring(underscorePos + 1);
                        int index = Integer.parseInt(indexStr);
                        quantityStr = (String) params.get("add_multi_product_quantity_"+index);
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
        			}
        			catch (NumberFormatException nfe) 
        			{
        				return "error";
                    } 
        			catch (Exception e) 
        			{
        				return "error";
                    }
        		}
        		if(UtilValidate.isNotEmpty(productId) && quantity.compareTo(BigDecimal.ZERO) > 0)
                {
        			request.setAttribute("add_product_id", productId);
        			request.setAttribute("add_category_id", categoryId);
        			WishListEvents.addToWishList(request, response);
                }
        	}
        }
        return "success";
    }

    public static String resetMultiShipGroups(HttpServletRequest request, HttpServletResponse response)
    {
        ShoppingCart shoppingCart = org.ofbiz.order.shoppingcart.ShoppingCartEvents.getCartObject(request);
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
      
        if (shoppingCart.items().size() > 0)
    	{
    		int iCartLineIdx =1;
    		String sCartLineIdx ="";
    		String sAttrValue="";
    		String sAttrName="";
        	String sShipGroupIdx="_1";
    		
        	Iterator<ShoppingCartItem> cartItemIter = shoppingCart.items().iterator();
        	while (cartItemIter.hasNext())
    		{
        		ShoppingCartItem cartItem = (ShoppingCartItem) cartItemIter.next();
        		shoppingCart.clearItemShipInfo(cartItem);
        		shoppingCart.setItemShipGroupQty(cartItem,cartItem.getQuantity(),0);
        		        		
        		
        		Map<String, String> orderItemAttributesMap = cartItem.getOrderItemAttributes();
        		List<String> lOrderItemAttributeDel = FastList.newInstance();
				Map<String, String> mOrderItemAttributeAdd=FastMap.newInstance();
        		if (UtilValidate.isNotEmpty(orderItemAttributesMap))
        		{
        			sCartLineIdx = ""+iCartLineIdx;
            		for(Entry<String, String> itemAttr : orderItemAttributesMap.entrySet()) 
            		{
            			sAttrName = (String)itemAttr.getKey();
            			if (sAttrName.startsWith("GIFT_MSG_FROM_") || sAttrName.startsWith("GIFT_MSG_TO_") || sAttrName.startsWith("GIFT_MSG_TEXT_"))
            			{
                			int iShipId=sAttrName.lastIndexOf('_');
                			int iSeqId=sAttrName.substring(0,iShipId).lastIndexOf('_');
                			String sOrderItemSeq = sAttrName.substring(iSeqId + 1,iShipId);
                			try 
                			{
                				int iOrderItemSeq = Integer.valueOf(sOrderItemSeq);
                				if (iOrderItemSeq > cartItem.getQuantity().intValue())
                				{
                					lOrderItemAttributeDel.add(sAttrName);
                        			continue;

                				}
            					lOrderItemAttributeDel.add(sAttrName);
            					mOrderItemAttributeAdd.put(sAttrName.substring(0,iShipId) + sShipGroupIdx ,(String)itemAttr.getValue());

                			}
                			catch (Exception e)
                			{
                				
                			}
            				
            			}
            		}
            		
            		if (UtilValidate.isNotEmpty(lOrderItemAttributeDel))
            		{
                        for (String attrName : lOrderItemAttributeDel)
                        {
                        	cartItem.removeOrderItemAttribute(attrName);
                        }
            			
            		}
            		if (UtilValidate.isNotEmpty(mOrderItemAttributeAdd))
            		{
                		for(Entry<String, String> itemAttr : mOrderItemAttributeAdd.entrySet()) 
                		{
                			cartItem.setOrderItemAttribute((String)itemAttr.getKey(), (String)itemAttr.getValue());
                		}
            			
            		}
        		}
        		iCartLineIdx++;
    		}
        	
        	shoppingCart.cleanUpShipGroups();
    	}

        
        return "success";	
    }
    
    public static String formatGiftMessageOrderItemAttribute(HttpServletRequest request, HttpServletResponse response)
    {
        ShoppingCart shoppingCart = org.ofbiz.order.shoppingcart.ShoppingCartEvents.getCartObject(request);
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
      
        if (shoppingCart.items().size() > 0)
    	{
    		
        	Iterator<ShoppingCartItem> cartItemIter = shoppingCart.items().iterator();
        	while (cartItemIter.hasNext())
    		{
        		ShoppingCartItem cartItem = (ShoppingCartItem) cartItemIter.next();

        		Map<String, String> orderItemAttributesMap = cartItem.getOrderItemAttributes();
        		List<String> lOrderItemAttributeDel = FastList.newInstance();
				Map<String, String> mOrderItemAttributeAdd=FastMap.newInstance();
        		if (UtilValidate.isNotEmpty(orderItemAttributesMap))
        		{
            		Map shipItemQtyGroupMap = shoppingCart.getShipGroups(cartItem);
            		Map mShipItemQtyGroupIndexMap = FastMap.newInstance();
            		
            		Iterator shipGroupQtyIter = shipItemQtyGroupMap.keySet().iterator();
            		int shipGroupQty = cartItem.getQuantity().intValue();
                	int iShipGroupQtyIdx = 0;
                	String sShipGroupSeq="";
                	/* Get each of the Ship Groups the item is in
                	 * This is a map of the ship group sequence id and the quantity being shipped in the group 
                	 */
                	while (shipGroupQtyIter.hasNext())
                	{
                		String sShipGroupIndex = shipGroupQtyIter.next().toString();
                		int iShipGroupIndex=Integer.valueOf(sShipGroupIndex);
                		BigDecimal bShipGroupQty =(BigDecimal)shipItemQtyGroupMap.get(iShipGroupIndex);

                		Map mShipItemGroupInfoMap = FastMap.newInstance();
                		mShipItemGroupInfoMap.put("shipGroupSeqId",sShipGroupIndex);
                		mShipItemGroupInfoMap.put("shipGroupQty", bShipGroupQty.intValue());

                		mShipItemQtyGroupIndexMap.put(""+ iShipGroupQtyIdx, mShipItemGroupInfoMap);
                		if (iShipGroupQtyIdx == 0)
                		{
                    		shipGroupQty = bShipGroupQty.intValue();
                    		sShipGroupSeq = sShipGroupIndex;
                			
                		}
                		iShipGroupQtyIdx++;
                	}
        			
                	iShipGroupQtyIdx = 0;
                	
            		for(Entry<String, String> itemAttr : orderItemAttributesMap.entrySet()) 
            		{
            			String sAttrName = (String)itemAttr.getKey();
            			if (sAttrName.startsWith("GIFT_MSG_FROM_") || sAttrName.startsWith("GIFT_MSG_TO_") || sAttrName.startsWith("GIFT_MSG_TEXT_"))
            			{
                			try 
                			{
                    			int iShipId=sAttrName.lastIndexOf('_');
                    			int iSeqId=sAttrName.substring(0,iShipId).lastIndexOf('_');
                    			
                    			String sOrderItemSeq = sAttrName.substring(iSeqId + 1,iShipId);
                    			int iOrderItemSeq = Integer.valueOf(sOrderItemSeq).intValue();
                    			
                    			/* The order item sequence indicates how many gift messages there are for this item
                    			 * For example if the sequence is 4 there were three previous messages.
                    			 * Since the item quantity could be spread across many ship groups
                    			 * If the sequence is greater than the ship group quantity set the gift message 
                    			 * to the items next ship group
                    			 */
                    			if (iOrderItemSeq > shipGroupQty)
                    			{
                    				iShipGroupQtyIdx++;
                    				Map mShipItemGroupInfoMap =(Map)mShipItemQtyGroupIndexMap.get(""+ iShipGroupQtyIdx);
                    				sShipGroupSeq = "" + mShipItemGroupInfoMap.get("shipGroupSeqId");
                    				int shipGroupQtyTemp = Integer.valueOf("" + mShipItemGroupInfoMap.get("shipGroupQty")).intValue();
                    				shipGroupQty = shipGroupQty + shipGroupQtyTemp;
                    			}
                    			
                    			int iShipGroupSeq = Integer.valueOf(sShipGroupSeq).intValue();

                    			/* Pad with zeroes both the order item sequence and ship group sequence ids
                    			 * First Add one since the ship group sdequence starts at zero, for example display 1 instead of 0.
                    			 */
                    			iShipGroupSeq++;
                    			String orderItemSeqId = UtilFormatOut.formatPaddedNumber(Long.valueOf(sOrderItemSeq), 2);
                		        String shipGroupSeqId = UtilFormatOut.formatPaddedNumber(Long.valueOf(iShipGroupSeq), 5);
                		        
            					lOrderItemAttributeDel.add(sAttrName);
            					mOrderItemAttributeAdd.put(sAttrName.substring(0,iSeqId) + "_" + orderItemSeqId + "_" +   shipGroupSeqId ,(String)itemAttr.getValue());

                    			/* Subtract one to set the index back to its correct position*/
                    			iShipGroupSeq--;
                			}
                			catch (Exception e)
                			{
                				Debug.logError("formatGiftMessageOrderItemAttribute:" + e, module);
                			}
            				
            			}
            		}
            		if (UtilValidate.isNotEmpty(lOrderItemAttributeDel))
            		{
                        for (String attrName : lOrderItemAttributeDel)
                        {
                        	cartItem.removeOrderItemAttribute(attrName);
                        }
            			
            		}
            		if (UtilValidate.isNotEmpty(mOrderItemAttributeAdd))
            		{
                		for(Entry<String, String> itemAttr : mOrderItemAttributeAdd.entrySet()) 
                		{
                			String itemAttrValue = (String)itemAttr.getValue().replaceAll("&#39;", "'");
                			cartItem.setOrderItemAttribute((String)itemAttr.getKey(), itemAttrValue);
                		}
            			
            		}
            		
            		

        		}
    		}
    	}

        
        return "success";	
    }    

    
    public static String setGiftMessage(HttpServletRequest request, HttpServletResponse response) 
    {
    	Map<String, Object> context = FastMap.newInstance(); 
    	Map<String, Object> params = UtilHttp.getParameterMap(request);
        String sCartLineIndexStr = (String) params.get("cartLineIndex");
        ShoppingCart shoppingCart = org.ofbiz.order.shoppingcart.ShoppingCartEvents.getCartObject(request);
        int iCartLineIndex = -1;
        if (UtilValidate.isNotEmpty(sCartLineIndexStr)) 
        {
            try 
            {
            	iCartLineIndex = Integer.parseInt(sCartLineIndexStr);
            } 
            catch (NumberFormatException nfe) 
            {
            	String errMsg = "Error when parsing cart line index :" + nfe.toString();
	            Debug.logError(nfe, errMsg, module);
                return "error";
            }
        }
        
        
        
        if(UtilValidate.isNotEmpty(shoppingCart) && UtilValidate.isNotEmpty(iCartLineIndex) && iCartLineIndex>-1)
        {
        	ShoppingCartItem cartItem = shoppingCart.findCartItem(iCartLineIndex);
        	if(UtilValidate.isNotEmpty(cartItem)) 
            {
        		/*First remove all prior Gift Message Attributes
        		 * 
        		 */
        		Map<String, String> orderItemAttributesMap = cartItem.getOrderItemAttributes();
        		if (UtilValidate.isNotEmpty(orderItemAttributesMap))
        		{
            		for(Entry<String, String> itemAttr : orderItemAttributesMap.entrySet()) 
            		{
            			String sAttrName = (String)itemAttr.getKey();
            			if (sAttrName.startsWith("GIFT_MSG_FROM_") || sAttrName.startsWith("GIFT_MSG_TO_") || sAttrName.startsWith("GIFT_MSG_TEXT_"))
            			{
                			cartItem.removeOrderItemAttribute(sAttrName);

            			}
            			
            		}
        		}
        		int iQuantity = cartItem.getQuantity().intValue();
        		int iSavedMsgIdx=1;
        		boolean bSavedMsg=false;
            	String sSavedMsgIdx = "";
            	String sShipGroupIdx="_1";
        		for (int i =1; i <= iQuantity;i++)
        		{
                	
                	sSavedMsgIdx=""+iSavedMsgIdx;
                	sSavedMsgIdx = sSavedMsgIdx + sShipGroupIdx;
                	bSavedMsg=false;
                	String msgFrom = StringUtils.trimToEmpty(request.getParameter("from_" + i));
                	String msgTo = StringUtils.trimToEmpty(request.getParameter("to_" + i));
                	String msgTxt = StringUtils.trimToEmpty(request.getParameter("giftMessageText_" + i));
                	String msgEnumTxt = StringUtils.trimToEmpty(request.getParameter("giftMessageEnum_" + i));
                	if (UtilValidate.isNotEmpty(msgFrom))
                	{
                		cartItem.setOrderItemAttribute("GIFT_MSG_FROM_" + sSavedMsgIdx ,msgFrom);
                    	bSavedMsg=true;
                		
                	}
                	if (UtilValidate.isNotEmpty(msgTo))
                	{
                		cartItem.setOrderItemAttribute("GIFT_MSG_TO_" + sSavedMsgIdx ,msgTo);
                    	bSavedMsg=true;
                		
                	}
                	if (UtilValidate.isNotEmpty(msgTxt))
                	{
                		cartItem.setOrderItemAttribute("GIFT_MSG_TEXT_" + sSavedMsgIdx ,msgTxt);
                    	bSavedMsg=true;
                		
                	}
                	else
                	{
                    	if (UtilValidate.isNotEmpty(msgEnumTxt))
                    	{
                    		cartItem.setOrderItemAttribute("GIFT_MSG_TEXT_" + sSavedMsgIdx ,msgEnumTxt);
                        	bSavedMsg=true;
                    		
                    	}
                		
                	}
                	if (bSavedMsg)
                	{
                		iSavedMsgIdx++;
                	}
        		}
            }
        	
        }
        		

        return "success";
    }
    
    //COMMENTING THIS METHOD, CAN BE USED IF ONLY ONE ITEM ATTRIBUTE NEED TO BE UPADTE ELSE REMOVE THIS METHOD
    /*public static String setOrderGiftMessage(HttpServletRequest request, HttpServletResponse response) 
    {
    	Map<String, Object> context = FastMap.newInstance();
    	
    	Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
    	
    	Map<String, Object> params = UtilHttp.getParameterMap(request);
        String shipGroupSeqId = (String) params.get("shipGroupSeqId");
        String countString  = (String)params.get("countString");
        String orderItemSeqId = (String)params.get("orderItemSeqId");
        String orderId = (String)params.get("orderId");
        
        String from = (String) params.get("from_"+countString);
        String to = (String) params.get("to_"+countString);
        String giftMessageText = (String) params.get("giftMessageText_"+countString);
        
        List<GenericValue> orderItemAttributes = FastList.newInstance();
		try {
			orderItemAttributes = delegator.findByAnd("OrderItemAttribute", UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItemSeqId));
		} catch (GenericEntityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        if(UtilValidate.isNotEmpty(orderItemAttributes))
        {
        	for(GenericValue orderItemAttribute : orderItemAttributes)
        	{
        		String attrName = orderItemAttribute.getString("attrName");
        		if(attrName.equals("GIFT_MSG_TEXT_"+countString+"_"+shipGroupSeqId) || attrName.equals("GIFT_MSG_FROM_"+countString+"_"+shipGroupSeqId) || attrName.equals("GIFT_MSG_TO_"+countString+"_"+shipGroupSeqId))
        		{
        			try {
						delegator.removeValue(orderItemAttribute);
					} catch (GenericEntityException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
        		}
        	}
        }
        
        Map attrMap = FastMap.newInstance();
		attrMap.put("orderId", orderId);
		attrMap.put("orderItemSeqId", orderItemSeqId);
        
        if(UtilValidate.isNotEmpty(from))
        {
			
        	attrMap.put("attrName", "GIFT_MSG_FROM_"+countString+"_"+shipGroupSeqId);
        	attrMap.put("attrValue", from);
        	
        	try {
    			
        		GenericValue orderItemAttribute = delegator.create("OrderItemAttribute", attrMap);
			} catch (GenericEntityException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        }
        
        if(UtilValidate.isNotEmpty(to))
        {
        	attrMap.put("attrName", "GIFT_MSG_TO_"+countString+"_"+shipGroupSeqId);
        	attrMap.put("attrValue", to);
        	try {
    			
        		GenericValue orderItemAttribute = delegator.create("OrderItemAttribute", attrMap);
			} catch (GenericEntityException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        }
        
        if(UtilValidate.isNotEmpty(giftMessageText))
        {
        	attrMap.put("attrName", "GIFT_MSG_TEXT_"+countString+"_"+shipGroupSeqId);
        	attrMap.put("attrValue", giftMessageText);
        	try {
    			
        		GenericValue orderItemAttribute = delegator.create("OrderItemAttribute", attrMap);
			} catch (GenericEntityException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        }
        
        return "success";
    }*/
    
    public static String setGiftMessageConfirm(HttpServletRequest request, HttpServletResponse response) 
    {
    	Map<String, Object> context = FastMap.newInstance();
    	
    	Delegator delegator = (Delegator) request.getAttribute("delegator");
    	
    	Map<String, Object> params = UtilHttp.getParameterMap(request);
        String shipGroupSeqId = (String) params.get("shipGroupSeqId");
        String orderItemSeqId = (String)params.get("orderItemSeqId");
        String orderId = (String)params.get("orderId");
        
        //REMOVE EXISTING ORDER_ITEM_ATTRIBUTES
        List<GenericValue> orderItemAttributes = FastList.newInstance();
		try {
			orderItemAttributes = delegator.findByAnd("OrderItemAttribute", UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItemSeqId));
		} catch (GenericEntityException e1) {
			e1.printStackTrace();
		}
        if(UtilValidate.isNotEmpty(orderItemAttributes))
        {
        	for(GenericValue orderItemAttribute : orderItemAttributes)
        	{
        		String attrName = orderItemAttribute.getString("attrName");
        		if(attrName.startsWith("GIFT_MSG_TEXT_") || attrName.startsWith("GIFT_MSG_FROM_") || attrName.startsWith("GIFT_MSG_TO_"))
        		{
        			int iShipId = attrName.lastIndexOf('_');
        	        if(iShipId > -1 && attrName.substring(iShipId+1).equals(shipGroupSeqId))
        	        {
        	        	try {
    						delegator.removeValue(orderItemAttribute);
    					} catch (GenericEntityException e) {
    						// TODO Auto-generated catch block
    						e.printStackTrace();
    					}
        	        }
        		}
        	}
        }
        
        GenericValue orderItemShipGroupAssoc = null;
        if(UtilValidate.isNotEmpty(orderItemSeqId) && UtilValidate.isNotEmpty(orderItemSeqId))
        {
        	try {
				orderItemShipGroupAssoc = delegator.findByPrimaryKey("OrderItemShipGroupAssoc", UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItemSeqId, "shipGroupSeqId", shipGroupSeqId));
			} catch (GenericEntityException e) {
				e.printStackTrace();
			}
        }
        
        if(UtilValidate.isNotEmpty(orderItemShipGroupAssoc))
        {
        	int quantity = orderItemShipGroupAssoc.getBigDecimal("quantity").intValue();
        	for(int i = 1; i <= quantity; i++)
        	{
        		String from = (String) params.get("from_"+i);
                String to = (String) params.get("to_"+i);
                String giftMessageText = (String) params.get("giftMessageText_"+i);
                
                Map attrMap = FastMap.newInstance();
        		attrMap.put("orderId", orderId);
        		attrMap.put("orderItemSeqId", orderItemSeqId);
        		
        		String seqIdFormatted = UtilFormatOut.formatPaddedNumber(Long.valueOf(i), 2);
        		
                if(UtilValidate.isNotEmpty(from))
                {
                	attrMap.put("attrName", "GIFT_MSG_FROM_"+seqIdFormatted+"_"+shipGroupSeqId);
                	attrMap.put("attrValue", from);
                	
                	try {
            			
                		GenericValue orderItemAttribute = delegator.create("OrderItemAttribute", attrMap);
        			} catch (GenericEntityException e1) {
        				// TODO Auto-generated catch block
        				e1.printStackTrace();
        			}
                }
                
                if(UtilValidate.isNotEmpty(to))
                {
                	attrMap.put("attrName", "GIFT_MSG_TO_"+seqIdFormatted+"_"+shipGroupSeqId);
                	attrMap.put("attrValue", to);
                	try {
            			
                		GenericValue orderItemAttribute = delegator.create("OrderItemAttribute", attrMap);
        			} catch (GenericEntityException e1) {
        				// TODO Auto-generated catch block
        				e1.printStackTrace();
        			}
                }
                
                if(UtilValidate.isNotEmpty(giftMessageText))
                {
                	attrMap.put("attrName", "GIFT_MSG_TEXT_"+seqIdFormatted+"_"+shipGroupSeqId);
                	attrMap.put("attrValue", giftMessageText);
                	try {
            			
                		GenericValue orderItemAttribute = delegator.create("OrderItemAttribute", attrMap);
        			} catch (GenericEntityException e1) {
        				// TODO Auto-generated catch block
        				e1.printStackTrace();
        			}
                }
        	}
        }
        
        return "success";
    }
    public static String addPlpItemToCart(HttpServletRequest request, HttpServletResponse response) 
    {
    	Map<String, Object> context = FastMap.newInstance(); 
    	Map<String, Object> params = UtilHttp.getParameterMap(request);
        String productId = (String) params.get("plp_add_product_id");
        String quantityStr = (String) params.get("plp_qty");
        String categoryId = (String) params.get("plp_add_category_id");
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        ShoppingCart shoppingCart = org.ofbiz.order.shoppingcart.ShoppingCartEvents.getCartObject(request);
        ShoppingCartHelper cartHelper = new ShoppingCartHelper(delegator, dispatcher, shoppingCart); 
        Locale locale = UtilHttp.getLocale(request);
        String searchText = null;
        
        //validate quantity value
        BigDecimal quantity = BigDecimal.ZERO;
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
        
        if(UtilValidate.isNotEmpty(productId) && quantity.compareTo(BigDecimal.ZERO) > 0)
        {
        	String last_page_productId = null;
            if (params.containsKey("plp_last_viewed_pdp_id")) 
            {
            	last_page_productId = (String) params.remove("plp_last_viewed_pdp_id");
            }
            String manufacturer_party_id = null;
            if (params.containsKey("manufacturer_party_id")) 
            {
            	manufacturer_party_id = (String) params.remove("manufacturer_party_id");
            }
            boolean multiSearchExists = false;
            for(Entry<String, Object> entry : params.entrySet()) 
            {
        		String parameterName = entry.getKey();
        		if (parameterName.toUpperCase().startsWith("SEARCHITEM"))
            	{
        			String searchItem = Util.stripHTML(request.getParameter(parameterName));
        			if(UtilValidate.isNotEmpty(searchItem))
        			{
        				multiSearchExists = true;
        				request.setAttribute(parameterName, searchItem);
        			}
            	}
            }
            
            if (params.containsKey("productListFormSearchText")) 
            {
            	searchText = (String) params.remove("productListFormSearchText");
            }
            
            
            request.setAttribute("searchText", searchText);
            if(UtilValidate.isEmpty(searchText) && !multiSearchExists)
            {
            	request.setAttribute("productCategoryId", categoryId);
            }
        	request.setAttribute("product_id", last_page_productId);
        	request.setAttribute("manufacturerPartyId", manufacturer_party_id);
        	
        	
        	
        	
        	/*
        	 * add item and quantity to cart using the addToCart method
        	 */
        	cartHelper.addToCart(null, null, null, productId, categoryId, null, null, null, null, quantity, null, null, null, null, null, null, null, null, null, context, null);
        	/*
        	 * add product Features
        	 */
    		setProductFeaturesOnCart(shoppingCart,productId);
    		
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
        	String urlLabel = UtilProperties.getMessage(ShoppingCartEvents.label_resource, "ShowCartLink", locale); 
        	String showCartUrl = SeoUrlHelper.makeFullUrl(request,"eCommerceShowcart");
        	messageMap.put("cartLink", "<a href='" + showCartUrl + "'>"+ urlLabel +"</a>");
        	//Set the success message
        	String successMess = UtilProperties.getMessage(ShoppingCartEvents.label_resource, "AddToCartPLPSuccess", messageMap, locale);    
            List osafeSuccessMessageList = UtilMisc.toList(successMess);
            request.setAttribute("osafeSuccessMessageList", osafeSuccessMessageList);
        }
        
        return "success";
    }
    public static String addRecurrenceItemToCart(HttpServletRequest request, HttpServletResponse response) 
    {
    	Map<String, Object> context = FastMap.newInstance(); 
    	Map<String, Object> params = UtilHttp.getParameterMap(request);
        String productId = (String) params.get("add_product_id");
        String quantityStr = (String) params.get("quantity");
        String categoryId = (String) params.get("add_category_id");
        String productName = (String) params.get("add_product_name");
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        ShoppingCart shoppingCart = org.ofbiz.order.shoppingcart.ShoppingCartEvents.getCartObject(request);
        ShoppingCartHelper cartHelper = new ShoppingCartHelper(delegator, dispatcher, shoppingCart); 
        Locale locale = UtilHttp.getLocale(request);
        ResourceBundle PARAMETERS_RECURRENCE = null;
        try
        {
        	PARAMETERS_RECURRENCE = UtilProperties.getResourceBundle("parameters_recurrence.xml", Locale.getDefault());
        }
        catch(IllegalArgumentException e)
        {
        	Debug.logWarning(e, "parameters_recurrence does not exist");
        	PARAMETERS_RECURRENCE = null;
        }
        //validate quantity value
        BigDecimal quantity = BigDecimal.ZERO;
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
        
        
        if(UtilValidate.isNotEmpty(productId) && quantity.compareTo(BigDecimal.ZERO) > 0)
        {
        	// add item and quantity to cart using the addToCart method
        	cartHelper.addToCart(null, null, null, productId, categoryId, null, null, null, null, quantity, null, null, null, null, null, null, null, null, null, context, null);
        	/*
        	 * add product Features
        	 */
    		setProductFeaturesOnCart(shoppingCart,productId);

    		for (Iterator<?> item = shoppingCart.iterator(); item.hasNext();) 
        	{
            	ShoppingCartItem sci = (ShoppingCartItem)item.next();
            	if (sci.getProductId().equals(productId)) 
            	{
            		if (sci.getRecurringDisplayPrice().compareTo(BigDecimal.ZERO) > 0)
            		{
            			if (sci.getRecurringDisplayPrice().compareTo(sci.getDisplayPrice()) == -1)
            			{
                    		sci.setBasePrice(sci.getRecurringDisplayPrice());
                    		sci.setDisplayPrice(sci.getRecurringDisplayPrice());
            			}
            			else
            			{
                    		sci.setRecurringBasePrice(sci.getBasePrice());
                    		sci.setRecurringDisplayPrice(sci.getDisplayPrice());
            				
            			}
                		sci.setIsModifiedPrice(true);
                		sci.setShoppingList("SLT_AUTO_REODR", "00001");
                		
                		
                		String sRecurrenceFreq= "90";
                        if (UtilValidate.isNotEmpty(PARAMETERS_RECURRENCE))
                        {
	                      	  String parameterRecurrenceFreqDefault = PARAMETERS_RECURRENCE.getString("RECURRENCE_FREQ_DEFAULT");
	                      	  if (UtilValidate.isNotEmpty(parameterRecurrenceFreqDefault) && Util.isNumber(parameterRecurrenceFreqDefault))
	                          {
	                      		sRecurrenceFreq = parameterRecurrenceFreqDefault;
	                          }
                        }
                        
                		sci.setOrderItemAttribute("RECURRENCE_FREQ", sRecurrenceFreq);
            		}
            		break;
            	}
            }	
        	
        }
        
        return "success";
    }
    
    public static String addLoyaltyPoints(HttpServletRequest request, HttpServletResponse response) 
    {
    	HttpSession session = request.getSession();
    	Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Locale locale = UtilHttp.getLocale(request);
        ShoppingCart cart = org.ofbiz.order.shoppingcart.ShoppingCartEvents.getCartObject(request);
        Map parameters = UtilHttp.getParameterMap(request);
        String productStoreId = ProductStoreWorker.getProductStoreId(request);
        String loyaltyPointsId = (String) parameters.get("loyaltyPointsId");
    	String checkoutLoyaltyMethod = Util.getProductStoreParm(request, "CHECKOUT_LOYALTY_METHOD");  
    	String checkoutLoyaltyConversion = Util.getProductStoreParm(request, "CHECKOUT_LOYALTY_CONVERSION"); 
    	BigDecimal checkoutLoyaltyConversionBD = BigDecimal.ZERO;
    	if (UtilValidate.isNotEmpty(checkoutLoyaltyConversion) && UtilValidate.isInteger(checkoutLoyaltyConversion)) 
        {
    		try 
            {
    			checkoutLoyaltyConversionBD = new BigDecimal(checkoutLoyaltyConversion);
            } 
            catch (NumberFormatException nfe) 
            {
            	Debug.logError(nfe, "Problems converting amount to BigDecimal", module);
            	checkoutLoyaltyConversionBD = BigDecimal.ONE;
            }
        }
    	BigDecimal totalLoyaltyPointsAmountBD = (BigDecimal) request.getAttribute("loyaltyPointsAmount");
    	
    	List orderAdjustmentAttributeList = (List) session.getAttribute("orderAdjustmentAttributeList");
    	if(UtilValidate.isEmpty(orderAdjustmentAttributeList))
    	{
    		orderAdjustmentAttributeList = FastList.newInstance();
    	}
    	else
    	{
    		for(Object orderAdjustmentAttributeInfo : orderAdjustmentAttributeList)
	    	{
	    		Map orderAdjustmentAttributeInfoMap = (Map)orderAdjustmentAttributeInfo;
	    		String adjustmentType = (String) orderAdjustmentAttributeInfoMap.get("ADJUST_TYPE");
	    		if(UtilValidate.isNotEmpty(adjustmentType) && "LOYALTY_POINTS".equalsIgnoreCase(adjustmentType))
	    		{
	    			return "success";
	    		}
	    	}
    	}
    	//retrieve loyalty points information
    	Map serviceContext = FastMap.newInstance();
    	serviceContext.put("loyaltyPointsId", loyaltyPointsId);
    	serviceContext.put("productStoreId", productStoreId);
    	Map loyaltyInfoMap = FastMap.newInstance();
    	try 
    	{            
    		loyaltyInfoMap = dispatcher.runSync("getLoyaltyPointsInfoMap", serviceContext);
        } 
    	catch (Exception e) 
    	{
            String errMsg = "Error when retriving loyalty points information :" + e.toString();
            Debug.logError(e, errMsg, module);
            return "error";
        }
    	
    	String expDate = "";
    	if(UtilValidate.isNotEmpty(loyaltyInfoMap))
    	{
    		expDate = (String) loyaltyInfoMap.get("expDate");
    	}
    	
    	//convert Total Loyalty Points Amount to Currency
    	serviceContext = FastMap.newInstance();
    	serviceContext.put("loyaltyPointsAmount", totalLoyaltyPointsAmountBD);
    	serviceContext.put("checkoutLoyaltyConversion", checkoutLoyaltyConversionBD);
    	Map loyaltyConversionMap = FastMap.newInstance();
    	try 
    	{            
    		loyaltyConversionMap = dispatcher.runSync("convertLoyaltyPoints", serviceContext);
        } 
    	catch (Exception e) 
    	{
            String errMsg = "Error when converting loyalty points to currency :" + e.toString();
            Debug.logError(e, errMsg, module);
            return "error";
        }
    	
    	BigDecimal totalLoyaltyPointsCurrencyBD = BigDecimal.ZERO;
    	if(UtilValidate.isNotEmpty(loyaltyConversionMap))
    	{
    		totalLoyaltyPointsCurrencyBD = (BigDecimal) loyaltyConversionMap.get("loyaltyPointsCurrency");
    	}
    	
    	//Add Loyalty Points Adjustment to the Cart and add orderAdjustmentAttributeList Object to session
    	if(UtilValidate.isNotEmpty(cart) && cart.size() > 0)
    	{
    		//initially set the applied values to the totals
    		BigDecimal loyaltyPointsCurrencyBD = totalLoyaltyPointsCurrencyBD;
    		BigDecimal loyaltyPointsAmountBD = totalLoyaltyPointsAmountBD;
    		//compare loyaltyPointsCurrency applied by Loyalty Points to the balance left on the order
    		BigDecimal cartGrandTotal = cart.getGrandTotal();
    		BigDecimal cartTotalTaxes = cart.getTotalSalesTax();
    		cartGrandTotal  = cartGrandTotal.subtract(cartTotalTaxes);
    		if ((loyaltyPointsCurrencyBD.compareTo(cartGrandTotal) > 0)) 
            {
    			//show warning mesage
    			session.setAttribute("showLoyaltyPointsAdjustedWarning", "Y");
    			request.setAttribute("showLoyaltyPointsAdjustedWarning", "Y");
    			loyaltyPointsCurrencyBD = cartGrandTotal;
    			
    			//convert Currency to Loyalty Points Amount
            	serviceContext = FastMap.newInstance();
            	serviceContext.put("loyaltyPointsCurrency", loyaltyPointsCurrencyBD);
            	serviceContext.put("checkoutLoyaltyConversion", checkoutLoyaltyConversionBD);
            	loyaltyConversionMap = FastMap.newInstance();
            	try 
            	{            
            		loyaltyConversionMap = dispatcher.runSync("convertCurrencyToLoyaltyPoints", serviceContext);
                } 
            	catch (Exception e) 
            	{
                    String errMsg = "Error when converting currency to loyalty points :" + e.toString();
                    Debug.logError(e, errMsg, module);
                    return "error";
                }
            	
            	if(UtilValidate.isNotEmpty(loyaltyConversionMap))
            	{
            		loyaltyPointsAmountBD = (BigDecimal) loyaltyConversionMap.get("loyaltyPointsAmount");
            	}
            }
    		else
    		{
    			//remove warning mesage if it exists
    			String showLoyaltyPointsAdjustedWarning = (String) session.getAttribute("showLoyaltyPointsAdjustedWarning");
    			if(UtilValidate.isNotEmpty(showLoyaltyPointsAdjustedWarning))
    			{
    				session.removeAttribute("showLoyaltyPointsAdjustedWarning");
    			}
    		}
    		
    		//negate the value so it can be applied to cart
    		BigDecimal loyaltyPointsCurrencyNegateBD = loyaltyPointsCurrencyBD.multiply(BigDecimal.valueOf(-1));
    		
    		//create orderAdjustment and apply to cart
    		GenericValue orderAdjustment = delegator.makeValue("OrderAdjustment");
            orderAdjustment.set("orderAdjustmentTypeId", "LOYALTY_POINTS");
            orderAdjustment.set("amount", loyaltyPointsCurrencyNegateBD);
            orderAdjustment.set("includeInTax", "Y");
            int indexOfAdjInt = cart.addAdjustment(orderAdjustment);
            String indexOfAdj = String.valueOf(indexOfAdjInt);
            
            Map<String, Object>  orderAdjustmentAttributeInfoMap = FastMap.newInstance();
            //create Order Adjustment Attributes to be saved to the order
            orderAdjustmentAttributeInfoMap.put("INDEX", indexOfAdj);
            orderAdjustmentAttributeInfoMap.put("ADJUST_TYPE", "LOYALTY_POINTS");
            orderAdjustmentAttributeInfoMap.put("ADJUST_METHOD", checkoutLoyaltyMethod);
            orderAdjustmentAttributeInfoMap.put("MEMBER_ID", loyaltyPointsId);
            orderAdjustmentAttributeInfoMap.put("ADJUST_POINTS", loyaltyPointsAmountBD);
            if (UtilValidate.isNotEmpty(expDate)) 
            {
            	orderAdjustmentAttributeInfoMap.put("EXP_DATE", expDate);
            }
            orderAdjustmentAttributeInfoMap.put("CONVERSION_FACTOR", checkoutLoyaltyConversion);
            orderAdjustmentAttributeInfoMap.put("CURRENCY_AMOUNT", loyaltyPointsCurrencyBD.setScale(2, rounding));
            //these Adjustment Attributes will not be saved to Order, but are used for processing
            orderAdjustmentAttributeInfoMap.put("TOTAL_POINTS", totalLoyaltyPointsAmountBD);
            orderAdjustmentAttributeInfoMap.put("TOTAL_AMOUNT", totalLoyaltyPointsCurrencyBD);
            orderAdjustmentAttributeInfoMap.put("INCLUDE_IN_TAX", "Y");
            
            //add to session object
            orderAdjustmentAttributeList.add(orderAdjustmentAttributeInfoMap);
            session.setAttribute("orderAdjustmentAttributeList", orderAdjustmentAttributeList);  
    	}
    	//code to calculate remaining user points
    	
    	//Send this request variable to updateCartOnChange
    	String doCartLoyalty = "N";
    	session.setAttribute("DO_CART_LOYALTY", doCartLoyalty);
    	return "success";
    }
    
    public static String removeLoyaltyPoints(HttpServletRequest request, HttpServletResponse response) 
    {
    	Map parameters = UtilHttp.getParameterMap(request);
        HttpSession session = request.getSession();
    	List orderAdjustmentAttributeList = (List) session.getAttribute("orderAdjustmentAttributeList");
    	ShoppingCart cart = org.ofbiz.order.shoppingcart.ShoppingCartEvents.getCartObject(request);
    	if(UtilValidate.isNotEmpty(cart))
        {
    		int cartAdjIndexCount = 0;
    		List cartAdjustments = cart.getAdjustments();
	    	for(Object cartAdjustmentObj : cartAdjustments)
	    	{
	    		GenericValue cartAdjustment = (GenericValue) cartAdjustmentObj;
	    		String cartAdjustmentTypeId = cartAdjustment.getString("orderAdjustmentTypeId");
	    		if(UtilValidate.isNotEmpty(cartAdjustmentTypeId) && "LOYALTY_POINTS".equalsIgnoreCase(cartAdjustmentTypeId))
	    		{
	    			cart.removeAdjustment(cartAdjIndexCount);
	    		}
	    		cartAdjIndexCount++;
	    	}
        }
    	if(UtilValidate.isNotEmpty(orderAdjustmentAttributeList))
    	{
    		int listIndexCount = 0;
	    	for(Object orderAdjustmentAttributeInfo : orderAdjustmentAttributeList)
	    	{
	    		Map orderAdjustmentAttributeInfoMap = (Map)orderAdjustmentAttributeInfo;
	    		String orderAdjType = (String) orderAdjustmentAttributeInfoMap.get("ADJUST_TYPE");
	    		if(UtilValidate.isNotEmpty(orderAdjType) && "LOYALTY_POINTS".equalsIgnoreCase(orderAdjType))
	    		{
	    			orderAdjustmentAttributeList.remove(listIndexCount);
	            }
	    		listIndexCount++;
	    	}
    	}
    	//remove all session variables that may be set
    	String showLoyaltyPointsAdjustedWarning = (String) session.getAttribute("showLoyaltyPointsAdjustedWarning");
		if(UtilValidate.isNotEmpty(showLoyaltyPointsAdjustedWarning))
		{
			session.removeAttribute("showLoyaltyPointsAdjustedWarning");
		}
    	//Send this request variable to updateCartOnChange
    	String doCartLoyalty = "N";
    	session.setAttribute("DO_CART_LOYALTY", doCartLoyalty);
    	return "success";
    } 
    public static String updateLoyaltyPoints(HttpServletRequest request, HttpServletResponse response) 
    {
    	Map parameters = UtilHttp.getParameterMap(request);
    	Locale locale = UtilHttp.getLocale(request);
        String loyaltyPointsAmountStr = (String) parameters.get("update_loyaltyPointsAmount");
        HttpSession session = request.getSession();
    	List orderAdjustmentAttributeList = (List) session.getAttribute("orderAdjustmentAttributeList");
    	ShoppingCart cart = org.ofbiz.order.shoppingcart.ShoppingCartEvents.getCartObject(request);
    	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
    	
    	BigDecimal loyaltyPointsAmountBD = new BigDecimal(loyaltyPointsAmountStr);

    	if(UtilValidate.isNotEmpty(cart))
        {
    		BigDecimal loyaltyPointsCurrencyBD = BigDecimal.ZERO;
    		for(Object orderAdjustmentAttributeInfo : orderAdjustmentAttributeList)
	    	{
	    		Map orderAdjustmentAttributeInfoMap = (Map)orderAdjustmentAttributeInfo;
	    		String orderAdjType = (String) orderAdjustmentAttributeInfoMap.get("ADJUST_TYPE");
	    		if(UtilValidate.isNotEmpty(orderAdjType) && "LOYALTY_POINTS".equalsIgnoreCase(orderAdjType))
	    		{
	    			String checkoutLoyaltyConversionStr = (String) orderAdjustmentAttributeInfoMap.get("CONVERSION_FACTOR");
	    			BigDecimal checkoutLoyaltyConversionBD = new BigDecimal(checkoutLoyaltyConversionStr);
	    			
	    			//convert Total Loyalty Points Amount to Currency
	    			Map serviceContext = FastMap.newInstance();
	    	    	serviceContext.put("loyaltyPointsAmount", loyaltyPointsAmountBD);
	    	    	serviceContext.put("checkoutLoyaltyConversion", checkoutLoyaltyConversionBD);
	    	    	Map loyaltyConversionMap = FastMap.newInstance();
	    	    	try 
	    	    	{            
	    	    		loyaltyConversionMap = dispatcher.runSync("convertLoyaltyPoints", serviceContext);
	    	        } 
	    	    	catch (Exception e) 
	    	    	{
	    	            String errMsg = "Error when converting loyalty points to currency :" + e.toString();
	    	            Debug.logError(e, errMsg, module);
	    	            return "error";
	    	        }
	    	    	
	    	    	if(UtilValidate.isNotEmpty(loyaltyConversionMap))
	    	    	{
	    	    		loyaltyPointsCurrencyBD = (BigDecimal) loyaltyConversionMap.get("loyaltyPointsCurrency");
	    	    	}
    	    		BigDecimal currentLoyaltyPointsCurrencyBD = (BigDecimal) orderAdjustmentAttributeInfoMap.get("CURRENCY_AMOUNT");
	    	    	BigDecimal cartGrandTotal = cart.getGrandTotal().add(currentLoyaltyPointsCurrencyBD);
	    	    	BigDecimal cartTotalTaxes = cart.getTotalSalesTax();
	        		cartGrandTotal  = cartGrandTotal.subtract(cartTotalTaxes);
	    	    	if(loyaltyPointsCurrencyBD.compareTo(cartGrandTotal) > 0) 
		            {
	    	    		//show warning mesage
	        			session.setAttribute("showLoyaltyPointsAdjustedWarning", "Y");
	        			request.setAttribute("showLoyaltyPointsAdjustedWarning", "Y");
		    			loyaltyPointsCurrencyBD = cartGrandTotal;
		    			
		    			//convert Currency to Loyalty Points Amount
		            	serviceContext = FastMap.newInstance();
		            	serviceContext.put("loyaltyPointsCurrency", loyaltyPointsCurrencyBD);
		            	serviceContext.put("checkoutLoyaltyConversion", checkoutLoyaltyConversionBD);
		            	loyaltyConversionMap = FastMap.newInstance();
		            	try 
		            	{            
		            		loyaltyConversionMap = dispatcher.runSync("convertCurrencyToLoyaltyPoints", serviceContext);
		                } 
		            	catch (Exception e) 
		            	{
		                    String errMsg = "Error when converting currency to loyalty points :" + e.toString();
		                    Debug.logError(e, errMsg, module);
		                    return "error";
		                }
		            	
		            	if(UtilValidate.isNotEmpty(loyaltyConversionMap))
		            	{
		            		loyaltyPointsAmountBD = (BigDecimal) loyaltyConversionMap.get("loyaltyPointsAmount");
		            	}
		            }
	    	    	else
	        		{
	        			//remove warning mesage if it exists
	        			String showLoyaltyPointsAdjustedWarning = (String) session.getAttribute("showLoyaltyPointsAdjustedWarning");
	        			if(UtilValidate.isNotEmpty(showLoyaltyPointsAdjustedWarning))
	        			{
	        				session.removeAttribute("showLoyaltyPointsAdjustedWarning");
	        			}
	        		}
	    	    	orderAdjustmentAttributeInfoMap.put("ADJUST_POINTS", loyaltyPointsAmountBD);
	    			orderAdjustmentAttributeInfoMap.put("CURRENCY_AMOUNT", loyaltyPointsCurrencyBD.setScale(2, rounding));
	    		}
	    	}
    		
    		if(UtilValidate.isNotEmpty(loyaltyPointsCurrencyBD))
    		{
    			List cartAdjustments = cart.getAdjustments();
    			BigDecimal loyaltyPointsCurrencyNegateBD = loyaltyPointsCurrencyBD.multiply(BigDecimal.valueOf(-1));
        		for(Object cartAdjustmentObj : cartAdjustments)
    	    	{
        			GenericValue cartAdjustment = (GenericValue) cartAdjustmentObj;
        			String cartAdjustmentTypeId = cartAdjustment.getString("orderAdjustmentTypeId");
        			if(UtilValidate.isNotEmpty(cartAdjustmentTypeId) && "LOYALTY_POINTS".equalsIgnoreCase(cartAdjustmentTypeId))
        			{
        				cartAdjustment.set("amount", loyaltyPointsCurrencyNegateBD);
        			}
    	    	}
    		}
        }
    	//Send this request variable to updateCartOnChange
    	String doCartLoyalty = "N";
    	session.setAttribute("DO_CART_LOYALTY", doCartLoyalty);
    	return "success";
    } 
    public static String modifyLoyaltyPoints(HttpServletRequest request, HttpServletResponse response) 
    {
    	Map parameters = UtilHttp.getParameterMap(request);
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        HttpSession session = request.getSession();
    	List orderAdjustmentAttributeList = (List) session.getAttribute("orderAdjustmentAttributeList");
    	ShoppingCart cart = org.ofbiz.order.shoppingcart.ShoppingCartEvents.getCartObject(request);
        
    	if(UtilValidate.isNotEmpty(cart) && cart.size() > 0)
        {
    		if(UtilValidate.isNotEmpty(orderAdjustmentAttributeList))
            {
	    		for(Object orderAdjustmentAttributeInfo : orderAdjustmentAttributeList)
		    	{
		    		Map orderAdjustmentAttributeInfoMap = (Map)orderAdjustmentAttributeInfo;
    	    		String adjustmentType = (String) orderAdjustmentAttributeInfoMap.get("ADJUST_TYPE");
    	    		if(UtilValidate.isNotEmpty(adjustmentType) && "LOYALTY_POINTS".equalsIgnoreCase(adjustmentType))
		    		{
    	    			String checkoutLoyaltyConversionStr = (String) orderAdjustmentAttributeInfoMap.get("CONVERSION_FACTOR");
    	    			BigDecimal checkoutLoyaltyConversionBD = new BigDecimal(checkoutLoyaltyConversionStr);
    	    			BigDecimal currentLoyaltyPointsCurrencyBD = (BigDecimal) orderAdjustmentAttributeInfoMap.get("CURRENCY_AMOUNT");
    	    	    	BigDecimal cartGrandTotal = cart.getGrandTotal().add(currentLoyaltyPointsCurrencyBD);
    	    	    	BigDecimal cartTotalTaxes = cart.getTotalSalesTax();
    	        		cartGrandTotal  = cartGrandTotal.subtract(cartTotalTaxes);
			    		if(currentLoyaltyPointsCurrencyBD.compareTo(cartGrandTotal) > 0) 
			            {
			    			//show warning mesage
			    			session.setAttribute("showLoyaltyPointsAdjustedWarning", "Y");
			    			request.setAttribute("showLoyaltyPointsAdjustedWarning", "Y");
			    			BigDecimal loyaltyPointsCurrencyBD = cartGrandTotal;
			    			String loyaltyPointsCurrencyStr = loyaltyPointsCurrencyBD.toString();
			    			
			    			//convert Currency to Loyalty Points Amount
			            	Map serviceContext = FastMap.newInstance();
			            	serviceContext.put("loyaltyPointsCurrency", loyaltyPointsCurrencyBD);
			            	serviceContext.put("checkoutLoyaltyConversion", checkoutLoyaltyConversionBD);
			            	Map loyaltyConversionMap = FastMap.newInstance();
			            	try 
			            	{            
			            		loyaltyConversionMap = dispatcher.runSync("convertCurrencyToLoyaltyPoints", serviceContext);
			                } 
			            	catch (Exception e) 
			            	{
			                    String errMsg = "Error when converting currency to loyalty points :" + e.toString();
			                    Debug.logError(e, errMsg, module);
			                    return "error";
			                }
			            	
			            	if(UtilValidate.isNotEmpty(loyaltyConversionMap))
			            	{
			            		BigDecimal loyaltyPointsAmountBD = (BigDecimal) loyaltyConversionMap.get("loyaltyPointsAmount");
			            		if (UtilValidate.isNotEmpty(loyaltyPointsAmountBD)) 
			                    {
			            			orderAdjustmentAttributeInfoMap.put("ADJUST_POINTS", loyaltyPointsAmountBD);
					            	orderAdjustmentAttributeInfoMap.put("CURRENCY_AMOUNT", loyaltyPointsCurrencyBD.setScale(2, rounding));
			            			
			            			List cartAdjustments = cart.getAdjustments();
			            			BigDecimal loyaltyPointsCurrencyNegateBD = loyaltyPointsCurrencyBD.multiply(BigDecimal.valueOf(-1));
			                		for(Object cartAdjustmentObj : cartAdjustments)
			            	    	{
			                			GenericValue cartAdjustment = (GenericValue) cartAdjustmentObj;
			                			String cartAdjustmentTypeId = cartAdjustment.getString("orderAdjustmentTypeId");
			                			if(UtilValidate.isNotEmpty(cartAdjustmentTypeId) && "LOYALTY_POINTS".equalsIgnoreCase(cartAdjustmentTypeId))
			                			{
			                				cartAdjustment.set("amount", loyaltyPointsCurrencyNegateBD.setScale(2, rounding));
			                			}
			            	    	}
			                    }
			            	}
			            }
			    		else
			    		{
			    			//remove warning mesage if it exists
			    			String showLoyaltyPointsAdjustedWarning = (String) session.getAttribute("showLoyaltyPointsAdjustedWarning");
			    			if(UtilValidate.isNotEmpty(showLoyaltyPointsAdjustedWarning))
			    			{
			    				session.removeAttribute("showLoyaltyPointsAdjustedWarning");
			    			}
			    		}
		    		}
		    	}
            }
        }
    	return "success";
    } 
    
    public static String clearShoppingCart(HttpServletRequest request, HttpServletResponse response) 
    {
    	org.ofbiz.order.shoppingcart.ShoppingCartEvents.clearCart(request, response);
        HttpSession session = request.getSession();
        //remove any attributes related to the shopping cart that is not cleared in clearCart
        removeLoyaltyPoints(request, response);
        session.removeAttribute("orderAdjustmentList");
        return "success";
    }
    public static String modifyCartRecurrence(HttpServletRequest request, HttpServletResponse response) 
    {
    	HttpSession session = request.getSession();
    	Delegator delegator = (Delegator) request.getAttribute("delegator");
    	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
    	ShoppingCart shoppingCart = org.ofbiz.order.shoppingcart.ShoppingCartEvents.getCartObject(request);
        Locale locale = UtilHttp.getLocale(request);
        Map paramMap = UtilHttp.getParameterMap(request);
        String cartItemIndex = (String) paramMap.get("cartLineIndex");
        GenericValue autoUserLogin = (GenericValue)session.getAttribute("autoUserLogin");
        Map priceMap = FastMap.newInstance();
        ResourceBundle PARAMETERS_RECURRENCE = null;
        try
        {
        	PARAMETERS_RECURRENCE = UtilProperties.getResourceBundle("parameters_recurrence.xml", Locale.getDefault());
        }
        catch(IllegalArgumentException e)
        {
        	Debug.logWarning(e, "parameters_recurrence does not exist");
        	PARAMETERS_RECURRENCE = null;
        }
        
        
        try 
        	{
        	  if (shoppingCart.items().size() == 0)
        	  {
        		  return "success";
        	  }
              int index = Integer.parseInt(cartItemIndex);
              ShoppingCartItem cartItem = shoppingCart.findCartItem(index);
              
              Map priceContext = UtilMisc.toMap("product",cartItem.getProduct(),"prodCatalogId", cartItem.getProdCatalogId(),"currencyUomId",shoppingCart.getCurrency(),"autoUserLogin",autoUserLogin);
              priceContext.put("webSiteId",shoppingCart.getWebSiteId());
              priceContext.put("productStoreId",shoppingCart.getProductStoreId());
              priceContext.put("checkIncludeVat","Y");
              priceContext.put("agreementId",shoppingCart.getAgreementId());
              priceContext.put("partyId",shoppingCart.getPartyId());  
              
              if (UtilValidate.isNotEmpty(cartItem.getShoppingListId()) && "SLT_AUTO_REODR".equals(cartItem.getShoppingListId()))
              {
                  priceContext.put("productPricePurposeId","PURCHASE");
                  priceMap = dispatcher.runSync("calculateProductPrice", priceContext);
                  cartItem.setBasePrice((BigDecimal)priceMap.get("basePrice"));
                  cartItem.setDisplayPrice((BigDecimal)priceMap.get("price"));
                  cartItem.setIsModifiedPrice(false);
                  cartItem.setShoppingList(null, null);
            	  cartItem.removeOrderItemAttribute("RECURRENCE_FREQ");
            	  
              }
              else
              {
                  priceContext.put("productPricePurposeId","RECURRING_CHARGE");
                  priceMap = dispatcher.runSync("calculateProductPrice", priceContext);
                  cartItem.setBasePrice((BigDecimal)priceMap.get("price"));
                  cartItem.setDisplayPrice((BigDecimal)priceMap.get("price"));
                  cartItem.setIsModifiedPrice(true);
                  cartItem.setShoppingList("SLT_AUTO_REODR", "00001");
                  String sIntervalNumber= "90";
                  if (UtilValidate.isNotEmpty(PARAMETERS_RECURRENCE))
                  {
                	  String parameterRecurrenceFreqDefault = PARAMETERS_RECURRENCE.getString("RECURRENCE_FREQ_DEFAULT");
                	  if (UtilValidate.isNotEmpty(parameterRecurrenceFreqDefault) && Util.isNumber(parameterRecurrenceFreqDefault))
                      {
                		  sIntervalNumber = parameterRecurrenceFreqDefault;
                      }
                  }

            	  cartItem.setOrderItemAttribute("RECURRENCE_FREQ",sIntervalNumber);
              }

        	}
        	catch (Exception e) 
        	{
        		Debug.logWarning(e, "Error validating quantity");
        	}
        return "success";
    }
    
    public static String updateRecurrenceFrequency(HttpServletRequest request, HttpServletResponse response) 
    {
    	Map<String, Object> context = FastMap.newInstance(); 
    	Map<String, Object> params = UtilHttp.getParameterMap(request);
        String sCartLineIndexStr = (String) params.get("cartLineIndex");
        String sRecurrenceFreq = (String) params.get("recurrenceFreq");
        ShoppingCart shoppingCart = org.ofbiz.order.shoppingcart.ShoppingCartEvents.getCartObject(request);
        int iCartLineIndex = -1;
        if (UtilValidate.isNotEmpty(sCartLineIndexStr)) 
        {
            try 
            {
            	iCartLineIndex = Integer.parseInt(sCartLineIndexStr);
            } 
            catch (NumberFormatException nfe) 
            {
            	String errMsg = "Error when parsing cart line index :" + nfe.toString();
	            Debug.logError(nfe, errMsg, module);
                return "error";
            }
        }
        if(UtilValidate.isNotEmpty(shoppingCart) && iCartLineIndex>-1 && UtilValidate.isNotEmpty(sRecurrenceFreq))
        {
        	ShoppingCartItem cartItem = shoppingCart.findCartItem(iCartLineIndex);
        	if(UtilValidate.isNotEmpty(cartItem)) 
            {
        		cartItem.setOrderItemAttribute("RECURRENCE_FREQ", sRecurrenceFreq);
            }
        }
        return "success";
    }
    
    /** Update the cart's UserLogin object if it isn't already set. */
    public static String keepCartUpdatedByVisitor(HttpServletRequest request, HttpServletResponse response) {
    	Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        HttpSession session = request.getSession();
        ShoppingCart cart = org.ofbiz.order.shoppingcart.ShoppingCartEvents.getCartObject(request);
        GenericValue visitor = (GenericValue) session.getAttribute("visitor");

        if (UtilValidate.isEmpty(cart.getAutoSaveListId()))
        {
            if (UtilValidate.isNotEmpty(visitor)) 
            {
                    String sVisitorId =visitor.getString("visitorId");
                    Debug.log("keepCartUpdatedByVisitor: " + sVisitorId,module);

                    try 
                    {
                        
                        Map findMap = UtilMisc.toMap("visitorId", sVisitorId, "productStoreId", cart.getProductStoreId(), "shoppingListTypeId", "SLT_SPEC_PURP", "listName", org.ofbiz.order.shoppinglist.ShoppingListEvents.PERSISTANT_LIST_NAME);
                        List existingLists = delegator.findByAndCache("ShoppingList", findMap);
                        GenericValue list = null;
                        if (existingLists != null && !existingLists.isEmpty()) 
                        {
                            list = EntityUtil.getFirst(existingLists);
                            cart.setAutoSaveListId(list.getString("shoppingListId"));
                        }
                        
                    }
                      catch (Exception e)
                      {
                          Debug.logError(e, module);
                      }
            }
            
        }
        
        return "success";
    }
    
    /** Update personalizationMap */
    public static void updatePersonalizationMap(HttpServletRequest request, HttpServletResponse response) 
    {
    	Locale locale = UtilHttp.getLocale(request);
        LocalDispatcher dispatcher = (LocalDispatcher)request.getAttribute("dispatcher");
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        HttpSession session = request.getSession();
        GenericValue userLogin = (GenericValue)session.getAttribute("userLogin");
        ShoppingCart cart = org.ofbiz.order.shoppingcart.ShoppingCartEvents.getCartObject(request);
        String productId = (String)request.getAttribute("product_id");
        
        Map<String, Object> params = UtilHttp.getParameterMap(request);
        String inputName = (String)params.get("inputName");
        
        Map<String,Object> personalizationMap = (Map<String,Object>) session.getAttribute("personalizationMap");
        if(UtilValidate.isEmpty(personalizationMap))
        {
        	personalizationMap = FastMap.newInstance();
        }
        
        String textLinesNum = (String) params.get("textLinesNum");
        String textLine = "";
        String fontSize = "";
        if(UtilValidate.isNotEmpty(textLinesNum))
        {
	    	for(int i = 0; i < new Integer(textLinesNum).intValue(); i++)
	    	{
	    		textLine = (String)params.get("textLine_" + i);
	    		if(UtilValidate.isNotEmpty(textLine))
	            {
	    			personalizationMap.put("textLine_" + i, textLine);
	            }
	    		fontSize = (String)params.get("fontSize_" + i);
	    		if(UtilValidate.isNotEmpty(fontSize))
	            {
	    			personalizationMap.put("fontSize_" + i, fontSize);
	            }
	    	}
        }
        String fontEnum = (String) params.get("fontEnum");
        if(UtilValidate.isNotEmpty(fontEnum))
        {
        	personalizationMap.put("fontEnum", fontEnum);
        }
        String textAlign = (String) params.get("textAlign");
        if(UtilValidate.isNotEmpty(textAlign))
        {
        	personalizationMap.put("textAlign", textAlign);
        }
        
        if(UtilValidate.isNotEmpty(inputName))
        {
        	String updatedInputValue = (String)params.get(inputName);
        	if(UtilValidate.isEmpty(updatedInputValue))
            {
        		personalizationMap.put(inputName, "");
            }
        }
        
        session.setAttribute("personalizationMap", personalizationMap);
    }
    
    /** Upload a file and store it in the session */
    public static String uploadFileToSession(HttpServletRequest request, HttpServletResponse response) 
    {
    	Locale locale = UtilHttp.getLocale(request);
        LocalDispatcher dispatcher = (LocalDispatcher)request.getAttribute("dispatcher");
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        HttpSession session = request.getSession();
        GenericValue userLogin = (GenericValue)session.getAttribute("userLogin");
        ShoppingCart cart = org.ofbiz.order.shoppingcart.ShoppingCartEvents.getCartObject(request);
        String productId = (String)request.getAttribute("product_id");
        
        List<MessageString> error_list = new ArrayList<MessageString>();
        MessageString tmp = null;
        ResourceBundle OSAFE_UI_LABELS = UtilProperties.getResourceBundle("OSafeUiLabels.xml", Locale.getDefault());
        
        try 
        {            
            ServletFileUpload dfu = new ServletFileUpload(new DiskFileItemFactory(10240, FileUtil.getFile("runtime/tmp")));
            java.util.List lst = null;
            try 
            {
                lst = dfu.parseRequest(request);
            } 
            catch (FileUploadException e) 
            {
                Debug.logError("uploadFileToSession ServletFileUpload" + e.getMessage(), module);
                return "error";
            }
            
            Map passedParams = FastMap.newInstance();
            FileItem fi = null;
            FileItem uploadedContent = null;
            byte[] imageBytes = {};
            String fileName = "";
            String fileContentType = "";

            for (int i = 0; i < lst.size(); i++) 
            {
                fi = (FileItem) lst.get(i);
                String fieldName = fi.getFieldName();
                if (fi.isFormField()) 
                {
                    String fieldStr = fi.getString();
                    passedParams.put(fieldName, fieldStr);
                } 
                else if (fieldName.equals("fileUpload")) 
                {
                	uploadedContent = fi;
                    imageBytes = uploadedContent.get();
                    fileName = uploadedContent.getName();
                    fileContentType = uploadedContent.getContentType();
                }
            }
            
            if(UtilValidate.isNotEmpty(passedParams))
            {
            	productId = (String)passedParams.get("fileUpload_productId");
            	if(UtilValidate.isNotEmpty(productId))
                {
            		request.setAttribute("product_id", productId);
                }
            }
            
            ByteBuffer fileUpload = null;
            if(UtilValidate.isNotEmpty(imageBytes))
            {
            	fileUpload = ByteBuffer.wrap(imageBytes);
            }
            
            Map<String,Object> fileUploadMap = (Map<String,Object>) session.getAttribute("fileUploadMap");
            if(UtilValidate.isEmpty(fileUploadMap))
            {
            	fileUploadMap = FastMap.newInstance();
            }
            
            if(UtilValidate.isNotEmpty(imageBytes))
            {
            	if(UtilValidate.isNotEmpty(fileUpload))
                {
            		fileUploadMap.put("fileUpload", fileUpload);
                }
            	if(UtilValidate.isNotEmpty(fileName))
                {
            		fileUploadMap.put("fileName", fileName);
                }
            	if(UtilValidate.isNotEmpty(fileContentType))
                {
            		fileUploadMap.put("fileContentType", fileContentType);
                }
            	
            	if(UtilValidate.isNotEmpty(fileUploadMap))
                {
                    if (UtilValidate.isNotEmpty(fileName))
                    {
            			String detailImagePathOsafe = "/osafe_theme/images/user_content/uploads/";
            			String uploadContentPath = detailImagePathOsafe + "/" + UtilDateTime.nowAsString() + "/" + productId + "/";
                        
                        Map mResult =FastMap.newInstance();
                        request.setAttribute("uploadedFile", fileUpload);
                        request.setAttribute("_uploadedFile_fileName", fileName);
                        request.setAttribute("_uploadedFile_contentType", fileContentType);
                        request.setAttribute("imageResourceType", "file");
                        request.setAttribute("imageFilePath", uploadContentPath);
                        mResult = storeFileUpload(request, response);
                        String imageUploadUrl = (String)mResult.get("imageUploadUrl");
                        if (UtilValidate.isNotEmpty(imageUploadUrl))
                        {
                        	fileUploadMap.put("imageUploadUrl", imageUploadUrl);
                        }
                    }
                    session.setAttribute("fileUploadMap", fileUploadMap);
                }
            }
            
            if(UtilValidate.isNotEmpty(productId))
            {
        		request.setAttribute("product_id", productId);
            }
            if (error_list.size() > 0)
            {
            	request.setAttribute("_ERROR_MESSAGE_LIST_", error_list);
                return "error";
            }
            else
            {
            	
            }
        } 
        catch (Exception e) 
        {
            Debug.logError(e, "uploadFileToSession " , module);
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }
        return "success";
    }
    
    /** Add customized functionality to addToCart */
    public static String addToCartCustom(HttpServletRequest request, HttpServletResponse response) 
    {
        ShoppingCart cart = org.ofbiz.order.shoppingcart.ShoppingCartEvents.getCartObject(request);
        HttpSession session = request.getSession();
        //default ofbiz add to cart
        String addToCartResult = org.ofbiz.order.shoppingcart.ShoppingCartEvents.addToCart(request, response);
        
        Integer itemIdInteger = (Integer) request.getAttribute("itemId");
        
        List<MessageString> error_list = new ArrayList<MessageString>();
        MessageString tmp = null;
        ResourceBundle OSAFE_UI_LABELS = UtilProperties.getResourceBundle("OSafeUiLabels.xml", Locale.getDefault());
        
        try 
        {
        	if(UtilValidate.isNotEmpty(itemIdInteger))
            {
        		int itemId = itemIdInteger.intValue();
            	ShoppingCartItem cartItem = cart.findCartItem(itemId);
            	if(UtilValidate.isNotEmpty(cartItem))
                {
            		String productId = cartItem.getProductId();
            		String textLinesNum = request.getParameter("textLinesNum");
            		if(UtilValidate.isNotEmpty(textLinesNum))
                    {
	            		String textLine = "";
	                	String fontSize = "";
	                	for(int i = 0; i < new Integer(textLinesNum).intValue(); i++)
	                	{
	                		textLine = (String)request.getParameter("textLine_" + i);
	                		if(UtilValidate.isNotEmpty(textLine))
	                        {
	                			cartItem.setOrderItemAttribute("TEXT_LINE_" + i, textLine);
	                        }
	                		fontSize = (String)request.getParameter("fontSize_" + i);
	                		if(UtilValidate.isNotEmpty(fontSize))
	                        {
	                			cartItem.setOrderItemAttribute("FONT_SIZE_" + i, fontSize);
	                        }
	                	}
	            		
	            		String fontEnum = request.getParameter("fontEnum");
	            		if(UtilValidate.isNotEmpty(fontEnum))
	                    {
	            			cartItem.setOrderItemAttribute("FONT_FAMILY", fontEnum);
	                    }
	            		String textAlign = request.getParameter("textAlign");
	            		if(UtilValidate.isNotEmpty(textAlign))
	                    {
	            			cartItem.setOrderItemAttribute("TEXT_ALIGN", textAlign);
	                    }
	                }
            		
            		String uploadFileName = request.getParameter("uploadFileName");
            		if(UtilValidate.isNotEmpty(uploadFileName))
                    {
            			Map<String,Object> fileUploadMap = (Map<String,Object>) session.getAttribute("fileUploadMap");
	                    if(UtilValidate.isNotEmpty(fileUploadMap))
	                    {
	                        String  imageUploadUrl = (String) fileUploadMap.get("imageUploadUrl");
	                        if (UtilValidate.isNotEmpty(imageUploadUrl))
	                        {
	                            if (UtilValidate.isNotEmpty(imageUploadUrl))
	                            {
	                            	cartItem.setOrderItemAttribute("IMAGE_UPLOAD_URL", imageUploadUrl);
	                            }
	                        }
	                    }
                    }
            		
            		//empty session maps after adding to cart
            		Map<String,Object> emptyMap = FastMap.newInstance();
                    session.setAttribute("fileUploadMap", emptyMap);
                    session.setAttribute("personalizationMap", emptyMap);            		
                }
            }
        } 
        catch (Exception e) 
        {
            Debug.logError(e, "addToCartCustom " , module);
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }
        return addToCartResult;
    }
    
    /* Stores uploaded file to the server */
    public static Map<String, Object> storeFileUpload(HttpServletRequest request, HttpServletResponse response) throws IOException, JDOMException 
    {

        Delegator delegator = (Delegator) request.getAttribute("delegator");
        ByteBuffer imageData = (ByteBuffer)request.getAttribute("uploadedFile");
        String fileName = (String)request.getAttribute("_uploadedFile_fileName");
        String contentType = (String)request.getAttribute("_uploadedFile_contentType");
        String imageResourceType = (String)request.getAttribute("imageResourceType");
        String imageFilePath = (String)request.getAttribute("imageFilePath");
        Map<String, Object> results = FastMap.newInstance();
        Map<String, Object> context = FastMap.newInstance();
        
        String imageUrl = "";
        String filenameToUse = "";
        
        if (imageResourceType.equals("file")) 
        {
            String osafeThemeServerPath = FlexibleStringExpander.expandString(OSAFE_PROPS.getString("osafeThemeServer"), context);
            int extensionIndex = fileName.lastIndexOf(".");
            if (extensionIndex == -1) {
            	filenameToUse = fileName;
            } else {
            	filenameToUse = fileName.substring(0, extensionIndex);
            }
            filenameToUse = StringUtil.replaceString(filenameToUse, " ", "_");
            
            List<GenericValue> fileExtension = FastList.newInstance();
            try {
                fileExtension = delegator.findByAnd("FileExtension", UtilMisc.toMap("mimeTypeId", contentType));
            } catch (GenericEntityException e) {
                Debug.logError(e, module);
                return ServiceUtil.returnError(e.getMessage());
            }

            GenericValue extension = EntityUtil.getFirst(fileExtension);
            if (extension != null) {
                filenameToUse += "." + extension.getString("fileExtensionId");
            }

            File file = new File(osafeThemeServerPath + imageFilePath + filenameToUse);
            
            if (!new File(osafeThemeServerPath + imageFilePath).exists()) {
            	new File(osafeThemeServerPath + imageFilePath).mkdirs();
    	    }
            
            try {
                RandomAccessFile out = new RandomAccessFile(file, "rw");
                out.write(imageData.array());
                out.close();
            } catch (FileNotFoundException e) {
                Debug.logError(e, module);
                return ServiceUtil.returnError("Unable to open file for writing: " + file.getAbsolutePath());
            } catch (IOException e) {
                Debug.logError(e, module);
                return ServiceUtil.returnError("Unable to write binary data to: " + file.getAbsolutePath());
            }

            imageUrl = imageFilePath + filenameToUse;
        }
        
        results.put("imageUploadUrl", imageUrl);
        results.put("result", "success");
        return results;
    }
    
}
