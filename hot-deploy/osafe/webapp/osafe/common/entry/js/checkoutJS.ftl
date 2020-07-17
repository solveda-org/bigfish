<script type="text/javascript">
<#assign allowCOD = Static["com.osafe.util.Util"].isProductStoreParmTrue(request,"CHECKOUT_ALLOW_COD")/>

   jQuery(document).ready(function () 
   {
   		<#-- validate the shopping cart -->
   		validateCart();
   		
        <#-- update shipping options base on shipping postal code -->
        if (jQuery('#js_SHIPPING_POSTAL_CODE').length) 
        {
          updateShippingOption('Y');
          jQuery('#js_SHIPPING_POSTAL_CODE').change(function () 
          {
            updateShippingOption('N');
          });
          jQuery('#js_SHIPPING_STATE').change(function () 
          {
            updateShippingOption('N');
          });
          jQuery('#js_SHIPPING_ADDRESS1').change(function () 
          {
            updateShippingOption('N');
          });
          jQuery('#js_SHIPPING_ADDRESS2').change(function () 
          {
            updateShippingOption('N');
          });
          jQuery('#js_SHIPPING_ADDRESS3').change(function () 
          {
            updateShippingOption('N');
          });
          jQuery('#js_SHIPPING_COUNTRY').change(function () 
          {
            updateShippingOption('N');
          });
        }

        <#-- make first shipping option as selected -->
        if (jQuery('input.js_shipping_method:checked').val() == undefined) 
        {
          jQuery('input.js_shipping_method:first').attr("checked", true);
        }

        <#-- activate pick up store event listener -->
        pickupStoreEventListener();

       <#-- display payment options based on if a user selects pay now or pay in store -->
    	if (jQuery('#js_payInStoreY').is(':checked')) 
    	{	
		    jQuery('#js_checkoutPaymentOptions').hide();     
		}
    	jQuery('#js_payInStoreN').click(function()
    	{
		    jQuery('#js_checkoutPaymentOptions').show();
		});
		
		jQuery('#js_payInStoreY').click(function()
		{
		    jQuery('#js_checkoutPaymentOptions').hide();
		});

        <#-- PAYMENT OPTIONS (RADIO BUTTON AUTO SELECTION)-->
    	<#-- select saved card option -->
    	jQuery('#js_savedCard').change(function () 
		{
			jQuery('#js_useSavedCard').prop('checked',true);
		});
		jQuery('#js_savedVerificationNo').change(function () 
		{
			jQuery('#js_useSavedCard').prop('checked',true);
		});
		
		<#-- select other card option -->
    	jQuery('#js_cardType').change(function () 
		{
			jQuery('#js_useOtherCard').prop('checked',true);
		});
		jQuery('#js_cardNumber').change(function () 
		{
			jQuery('#js_useOtherCard').prop('checked',true);
		});
		jQuery('#js_expMonth').change(function () 
		{
			jQuery('#js_useOtherCard').prop('checked',true);
		});
		jQuery('#js_expYear').change(function () 
		{
			jQuery('#js_useOtherCard').prop('checked',true);
		});
		jQuery('#js_verificationNo').change(function () 
		{
			jQuery('#js_useOtherCard').prop('checked',true);
		});

        <#-- select saved EFT option -->
        jQuery('#js_savedEftAccount').change(function () 
        {
            jQuery('#js_useSavedEftAccount').prop('checked',true);
        });
        
        <#-- select other EFT option -->
        jQuery('#js_nameOnAccount').change(function () 
        {
            jQuery('#js_useOtherEftAccount').prop('checked',true);
        });
        jQuery('#js_bankName').change(function () 
        {
            jQuery('#js_useOtherEftAccount').prop('checked',true);
        });
        jQuery('#js_routingNumber').change(function () 
        {
            jQuery('#js_useOtherEftAccount').prop('checked',true);
        });
        jQuery('#js_accountNumber').change(function () 
        {
            jQuery('#js_useOtherEftAccount').prop('checked',true);
        });
        jQuery('#js_accountType').change(function () 
        {
            jQuery('#js_useOtherEftAccount').prop('checked',true);
        });

        <#-- show payment Option div based on dropdown option selected -->
        var selectedValue = jQuery('#js_paymentOptionsDD').val();
        jQuery('#' + selectedValue ).show();

		jQuery('#js_paymentOptionsDD').change(function(){
			   jQuery(this).find("option").each(function(){
			      jQuery('#' + this.value).hide();
			    });
			    jQuery('#' + this.value).show();
			
		});

        <#-- SHOWCART event listener -->
      <#if updateCartRecurrenceFlagRequest?exists && updateCartRecurrenceFlagRequest?has_content>
		jQuery('.js_recurrenceFlag').click(function()
		{
		   var recurrenceFlagId = jQuery(this).attr('id');
           if(recurrenceFlagId != "")
           {
	           recurrenceFlagArr = recurrenceFlagId.split("_");
	           cartLineIdx = recurrenceFlagArr[1];
        	   modifyCartItemRecurrenceFlag(cartLineIdx);
           }
		});
       </#if>
       
       <#if updateRecurrenceFreqRequest?exists && updateRecurrenceFreqRequest?has_content>
          <#-- on change of recurrence frequency selection -->
          jQuery('.js_recurrenceFreq').change(function () 
          {
          	var recurrenceFreqId = jQuery(this).attr('id');
          	if(recurrenceFreqId != "")
          	{
          		recurrenceFreqIdArr = recurrenceFreqId.split("_");
    	    	cartLineIdx = recurrenceFreqIdArr[1];
    	    	recurrenceFreqValue = jQuery('#'+recurrenceFreqId).val();
          		var reqParam = '?cartLineIndex='+cartLineIdx+'&recurrenceFreq='+recurrenceFreqValue;
          		
          		modifyCartItemRecurrenceFreq(cartLineIdx,recurrenceFreqValue);
          	}
          });
	    </#if>  

        <#-- Store Credit selection -->
        jQuery('#js_useStoreCredit').click(function()
        {
            checkStoreCreditRequestFromCheckBox();
        });
        jQuery('#js_storeCreditAmount').change(function () 
        {
            checkStoreCreditRequestFromAmount();
        });

    });

    function submitCheckoutForm(form, mode, value) 
    {
        if (mode == "DN") {
            <#-- done action; checkout -->
            form.action="<@ofbizUrl>${doneAction!""}</@ofbizUrl>";
            form.submit();
        }else if (mode == "MAU") {
            <#-- multi ship address Url: validate and then done action -->
            form.action="<@ofbizUrl>${multiAddressAction!""}</@ofbizUrl>";
            form.submit();
        }else if (mode == "VDN") {
            <#-- validate and then done action -->
            if (validateCart()) {
                form.action="<@ofbizUrl>${doneAction!""}</@ofbizUrl>";
            	form.submit();
            }
        }else if (mode == "NA") {
            <#-- new address -->
            form.action="<@ofbizUrl>${addAddressAction!""}?preContactMechTypeId=POSTAL_ADDRESS&contactMechPurposeTypeId="+value+"&DONE_PAGE=${donePage!""}</@ofbizUrl>";
            form.submit();
        } else if (mode == "BK") {
            <#-- Previous Page -->
            form.action="<@ofbizUrl>${backAction!""}?action=previous</@ofbizUrl>";
            form.submit();
        }  else if (mode == "SCBK") {
            <#-- show cart back action -->
            form.action="${showCartBackAction!""}";
            form.submit();
        }  else if (mode == "CABK") {
            <#-- customer address back action -->
            form.action="<@ofbizUrl>${customerAddressBackAction!""}?action=previous</@ofbizUrl>";
            form.submit();
        }  else if (mode == "SOBK") {
            <#-- shipping options back action -->
            form.action="<@ofbizUrl>${shippingOptionsBackAction!""}?action=previous</@ofbizUrl>";
            form.submit();
        } else if (mode == "UC") {
            <#-- update cart action -->
            if (updateCart()) {
                form.action="<@ofbizUrl>${updateCartAction!""}</@ofbizUrl>";
                form.submit();
            }
        }  else if (mode == "PA") {
            <#-- paypal action -->
            document.getElementById("js_paymentMethodTypeId").value = value;
            form.action="<@ofbizUrl>${payPalAction!""}</@ofbizUrl>";
            form.submit();
        } else if (mode == "SO") {
            <#-- submit order -->
            document.getElementById("js_submitOrderBtn").value = "${uiLabelMap.SubmittingOrderBtn}";
            jQuery("#js_submitOrderBtn").attr("disabled", "disabled");
            form.action="<@ofbizUrl>${submitOrderAction!""}</@ofbizUrl>";
            form.submit();
        } else if (mode == "EB") {
            <#-- EBS action -->
            document.getElementById("js_paymentMethodTypeId").value = value;
            form.action="<@ofbizUrl>${ebsAction!""}</@ofbizUrl>";
            form.submit();
        } else if (mode == "UWL") {
            <#-- update wish list action -->
            if (updateWishlist()) {
            	form.action="<@ofbizUrl>${updateWishListAction!""}</@ofbizUrl>";
            	form.submit();
            }
        } else if (mode == "ACW") {
            <#-- add to cart from wish list action -->
            if (addItemToCartFromWishlist(value)) {
                document.getElementById("js_add_item_id").value = value;
                form.action="<@ofbizUrl>${addToCartFromWishListAction!""}</@ofbizUrl>";
                form.submit();
            }
        } else if (mode == "AMCW") {
            <#-- add multi item to cart from wish list action -->
            if (addMultiItemsToCartFromWishlist(value)) {
                form.action="<@ofbizUrl>${addMultiItemsToCartFromWishListAction!""}</@ofbizUrl>";
                form.submit();
            }
        } else if (mode == "SP") {
            <#-- store pick up action -->
            document.getElementById("js_storeId").value = value;
            <#if formName?has_content>
                document.${formName!}.action="<@ofbizUrl>${storePickupAction!""}</@ofbizUrl>";
                document.${formName!}.submit();
            </#if>
        } else if (mode == "PNZ") {
            <#-- PayNetz action -->
            document.getElementById("js_paymentMethodTypeId").value = value;
            form.action="<@ofbizUrl>${payNetzAction!""}</@ofbizUrl>";
            form.submit();
        } else if (mode == "BBK") {
            <#-- Browser Back Action -->
            window.history.back();
        } else if (mode == "GM") {
            <#-- update gift message action -->
            giftMessageConvertApostrophe();
            form.action="<@ofbizUrl>${doneAction!""}</@ofbizUrl>";
            form.submit();
        }
    }
    function setCheckoutFormAction(form, mode, value) 
    {
        if (mode == "DN")
        {
            <#-- done action; checkout -->
            form.action="<@ofbizUrl>${doneAction!""}</@ofbizUrl>";
        } 
        else if (mode == "UC")
        {
            <#-- update cart action -->
            form.action="<@ofbizUrl>${updateCartAction!""}</@ofbizUrl>";
        }
        else if (mode == "UWL")
        {
            <#-- update wish list action -->
            form.action="<@ofbizUrl>${updateWishListAction!""}</@ofbizUrl>";
        }
        else if (mode == "APC")
        {
            <#-- apply promo code -->
            if (jQuery('#js_manualOfferCode').length && jQuery('#js_manualOfferCode').val() != null)
            {
              promo = jQuery('#js_manualOfferCode').val().toUpperCase();
              promoCodeWithoutSpace = promo.replace(/^\s+|\s+$/g, "");
            }
            form.action="<@ofbizUrl>${addPromoCodeRequest!}?productPromoCodeId="+promoCodeWithoutSpace+"</@ofbizUrl>";
        }
        else if (mode == "ALP")
        {
            <#-- apply loyalty point -->
            form.action="<@ofbizUrl>${addLoyaltyPointsRequest!}</@ofbizUrl>";
        }
        else if (mode == "ULP")
        {
            <#-- update loyalty point -->
            form.action="<@ofbizUrl>${updateLoyaltyPointsRequest!}</@ofbizUrl>";
        }
    }
    
    function updateCart() 
    {
    	var cartIsValid = true;
    	<#-- get total number of lines (for either shoppingcart or wishlist) -->
    	<#if shoppingCart?has_content>
    		<#assign cartItemSize = shoppingCart.items().size()!/>
    	</#if>
    	var cartItemsNo = ${cartItemSize!0};
    	var zeroQty = false;
    	
    	<#-- validate each item in cart -->
    	for (var i=0;i<cartItemsNo;i++)
      	{
      		<#-- get ProductName, Quantity, ProductId -->
       		var productName = jQuery('#js_productName_'+i).val();
       		var productId = "";
       		var quantityInputClassAttr = jQuery('#update_'+i).attr("class");
       		if(quantityInputClassAttr != null && quantityInputClassAttr != "")
       		{
       			productId = quantityInputClassAttr.replace("qtyInCart_", "");
       		}
       		
       		var quantity = Number(getTotalQtyFromScreen('update_',i));
       		
       		<#-- check if qty is whole number -->
           	if(isQtyWhole(quantity,productName))
           	{
           		<#-- if not on showCart Page, we need to get the quantity in cart and add -->
		   		if(!(jQuery('.showCartOrderItemsItemUpdateButton').length))
		   		{
		   			quantity = quantity + getQtyInCart(productId);
		   		}
                <#-- validate qty limits -->
                if(!(validateQtyMinMax(productId,productName,quantity)))
                {
                	cartIsValid = false;
                }
           	}
           	else
           	{
           		cartIsValid = false;
           	}
           	if(quantity == 0) 
          	{
          		var zeroQty = true;
          	}
      	}
      	if(zeroQty == true)
      	{
          	//window.location='<@ofbizUrl>deleteFromCart</@ofbizUrl>';
      	}
      	return cartIsValid;
    }
    
    function updateWishlist() 
    {
    	var cartIsValid = true;
    	<#-- get total number of lines -->
    	<#if wishList?has_content>
	        <#assign cartItemSize = wishList.size()!/>
	    </#if>
    	var cartItemsNo = ${cartItemSize!0};
    	var zeroQty = false;
    	
    	<#-- validate each item in wishlist -->
    	for (var i=0;i<cartItemsNo;i++)
      	{
      		<#-- get ProductName, Quantity, ProductId -->
       		var productName = jQuery('#js_productName_'+i).val();
       		
       		var productId = "";
       		var quantityInputClassAttr = jQuery('#update_'+i).attr("class");
       		if(quantityInputClassAttr != null && quantityInputClassAttr != "")
       		{
       			productId = quantityInputClassAttr.replace("qtyInCart_", "");
       		}
            else
            {
                productId = getProductIdInWishlist(i);
            }
            var quantity = 0;
            if (jQuery('#update_'+i).length) 
            {
                quantity = Number(getTotalQtyFromScreen('update_',i));
            }
            else
            {
                quantity = getQtyInWishlist(productId);
            }
       		
       		<#-- check if qty is whole number -->
           	if(!(isQtyWhole(quantity,productName)))
           	{
				cartIsValid = false;
           	}
           	if(quantity == 0) 
          	{
          		var zeroQty = true;
          	}
      	}
      	if(zeroQty == true)
      	{
          	//window.location='<@ofbizUrl>deleteFromWishlist</@ofbizUrl>';
      	}
      	return cartIsValid;
    }
    
    function addItemToCartFromWishlist(value) 
    {
    	<#-- get total number of lines -->
    	<#if wishList?has_content>
	        <#assign cartItemSize = wishList.size()!/>
	    </#if>
    	var cartItemsNo = ${cartItemSize!0};
    	var zeroQty = false;
    	
		<#-- get ProductName, Quantity, ProductId -->
   		var productName = jQuery('#js_productName_'+value).val();
   		
   		var productId = "";
   		var quantityInputClassAttr = jQuery('#update_'+value).attr("class");
   		if(quantityInputClassAttr != null && quantityInputClassAttr != "")
   		{
   			productId = quantityInputClassAttr.replace("qtyInCart_", "");
   		}
        else
        {
            productId = getProductIdInWishlist(value);
        }
        var quantity = 0;
        if (jQuery('#update_'+value).length) 
        {
            quantity = Number(getTotalQtyFromScreen('update_',value));
        }
        else
        {
            quantity = getQtyInWishlist(productId);
        }
   		
       	if(isQtyWhole(quantity,productName))
       	{
            if(!(isQtyZero(quantity,productName,productId)))
            {
            	<#-- check how many already in cart and add to qty -->
           		quantity = Number(quantity) + Number(getQtyInCart(productId));
                <#-- validate qty limits -->
                if(validateQtyMinMax(productId,productName,quantity))
                {
	               return true;
                }
            }
       	}
       	return false;
    }
    
    function addMultiItemsToCartFromWishlist(value) 
    {
        
        var addItemsToCart = true;
        var itemSelected = false;
        var count = 0;
        jQuery('.js_add_multi_product_id').each(function () 
        {
            qtyIdArr = jQuery(this).attr("id").split("_");
            variantIsChecked = jQuery(this).is(":checked");
            if(variantIsChecked)
            {
                itemSelected = true;
                var add_productId = jQuery(this).val();
                var quantity = 0;
                if (jQuery('#update_'+qtyIdArr[5]).length) 
                {
                    quantity = jQuery('#update_'+qtyIdArr[5]).val();
                }
                else
                {
                    quantity = getQtyInWishlist(add_productId);
                }
                var productName = jQuery('#js_productName_'+qtyIdArr[5]).val();
                <#-- check if qty is whole number -->
                if(quantity != "") 
                {
                    if(isQtyWhole(quantity,productName))
                    {
                        if(!(isQtyZero(quantity,productName,add_productId)))
                        {
                            <#-- check how many already in cart and add to qty -->
                            quantity = Number(quantity) + Number(getQtyInCart(add_productId));
                            <#-- validate qty limits -->
                            if(!(validateQtyMinMax(add_productId,productName,quantity)))
                            {
                                addItemsToCart = false;
                            }
                        }
                        else
                        {
                            addItemsToCart = false;
                        }
                    }
                    else
                    {
                        addItemsToCart = false;
                    }
                }
                else
                {
                    addItemsToCart = false;
                }
            }
            count = count + 1;
        });
        if (!itemSelected)
        {
            alert("${uiLabelMap.NoWishListItemSelectedError}");
            addItemsToCart = false;
        }
        return addItemsToCart;
    }
    
<#if formName?has_content>
    function addManualPromoCode()
    {
        if (jQuery('#js_manualOfferCode').length && jQuery('#js_manualOfferCode').val() != null)
        {
          promo = jQuery('#js_manualOfferCode').val().toUpperCase();
          promoCodeWithoutSpace = promo.replace(/^\s+|\s+$/g, "");
        }
        var cform = document.${formName!};
        cform.action="<@ofbizUrl>${addPromoCodeRequest!}?productPromoCodeId="+promoCodeWithoutSpace+"</@ofbizUrl>";
        cform.submit();
    }

    function removePromoCode(promoCode)
    {
        if (promoCode != null)
        {
          var cform = document.${formName!};
          cform.action="<@ofbizUrl>${removePromoCodeRequest!}?productPromoCodeId="+promoCode+"</@ofbizUrl>";
          cform.submit();
        }
    }
    function addGiftCardNumber() 
    {
        if (jQuery('#js_giftCardNumber').length && jQuery('#js_giftCardNumber').val() != null)
        {
          giftCardNumber = jQuery('#js_giftCardNumber').val();
          giftCardNumberWithoutSpace = giftCardNumber.replace(/^\s+|\s+$/g, "");
        }
        var cform = document.${formName!};
        cform.action="<@ofbizUrl>${addGiftCardNumberRequest!}?gcNumber="+giftCardNumberWithoutSpace+"</@ofbizUrl>";
        cform.submit();
    }

    function removeGiftCardNumber(gcPaymentMethodId)
    {
        if (gcPaymentMethodId != null)
        {
          var cform = document.${formName!};
          cform.action="<@ofbizUrl>${removeGiftCardNumberRequest!}?gcPaymentMethodId="+gcPaymentMethodId+"</@ofbizUrl>";
          cform.submit();
        }
    }
    
    function addLoyaltyPoints()
    {
    	jQuery('#js_applyLoyaltyCard').bind('click', false);
        var cform = document.${formName!};
        cform.action="<@ofbizUrl>${addLoyaltyPointsRequest!}</@ofbizUrl>";
        cform.submit();
    }
    function removeLoyaltyPoints()
    {
    	jQuery('#js_removeLoyaltyCard').bind('click', false);
	    var cform = document.${formName!};
	    cform.action="<@ofbizUrl>${removeLoyaltyPointsRequest!}</@ofbizUrl>";
	    cform.submit();
    }
    function updateLoyaltyPoints(indexOfAdj)
    {
    	jQuery('#js_updateLoyaltyPointsAmount').bind('click', false);
	    var cform = document.${formName!};
	    cform.action="<@ofbizUrl>${updateLoyaltyPointsRequest!}</@ofbizUrl>";
	    cform.submit();
    }
    function modifyCartItemRecurrenceFlag(itemIndex)
    {
        if (itemIndex != null)
        {
          var cform = document.${formName!};
          cform.action="<@ofbizUrl>${updateCartRecurrenceFlagRequest!}?cartLineIndex="+itemIndex+"</@ofbizUrl>";
          cform.submit();
        }
    }
    function modifyCartItemRecurrenceFreq(itemIndex,value)
    {
        if (itemIndex != "" && value != "")
        {
          var cform = document.${formName!};
          cform.action="<@ofbizUrl>${updateRecurrenceFreqRequest!}?cartLineIndex="+itemIndex+"&recurrenceFreq="+value+"</@ofbizUrl>";
          cform.submit();
        }
    }
</#if>
    

    function pickupStoreEventListener() 
    {
        <#-- If shipping method is select, remove related error message if one is displayed -->
        jQuery('.js_shippingMethodsContainer').click(function() 
        {
			if(jQuery(this).find('input[type="radio"]').is(':checked'))
            {
	            var selected = jQuery(".js_shipping_method:checked");
	            if(jQuery(selected).hasClass('js_shippingMethodRadioButton'))
	            {
	            	<#-- now check if there is an error message in this Div. -->
	            	var shipMethErrorMessage = jQuery(this).closest('#js_deliveryOptionBox').next('#js_deliveryOptionBoxError');
	            	<#-- check if error is displayed -->
	            	if(jQuery(shipMethErrorMessage).length)
	            	{
	            		if(jQuery.trim(jQuery(shipMethErrorMessage).html()).length)
	            		{
	            			jQuery(shipMethErrorMessage).children().hide();
	            		}
	            	}
            	}
            }
		});
		
        <#-- cancel store select and close dialouge box -->
        jQuery('.js_cancelPickupStore').click(function(event) 
        {
            event.preventDefault();
            jQuery(displayDialogId).dialog('close');
        });
        
        <#-- submit store locator search form -->
        jQuery('.storePickup_Form').submit(function(event) 
        {
            event.preventDefault();
            jQuery.get(jQuery(this).attr('action')+'?'+jQuery(this).serialize(), function(data) 
            {
                jQuery('#eCommerceStoreLocatorContainer').replaceWith(data);
                pickupStoreEventListener();
                if (jQuery('#isGoogleApi').val() != "Y") 
                {
                    loadScript();
                } 
                else
                {
                    hideDirection();
                }
            });
        });
    }

    <#-- update shipping option base on postal code -->
    function updateShippingOption(isOnLoad) 
    {
        if (jQuery('#js_deliveryOptionBox').length) 
        {
            if (jQuery('#js_SHIPPING_POSTAL_CODE').length) 
            {
            	var useShipping = true;
            	if (jQuery('#js_isSameAsBilling').length)
	  			{
	  				if(jQuery('#js_isSameAsBilling').is(":checked"))
	    			{
	    				useShipping = false;
	    			}
	  			}
	  			
	  			if(useShipping)
	  			{
	                <#-- get shipping address values -->
	                var address1 = (jQuery('#js_SHIPPING_ADDRESS1').val()== null)?'':encodeURIComponent(jQuery('#js_SHIPPING_ADDRESS1').val());
	                var address2 = (jQuery('#js_SHIPPING_ADDRESS2').val()== null)?'':encodeURIComponent(jQuery('#js_SHIPPING_ADDRESS2').val());
	                var address3 = (jQuery('#js_SHIPPING_ADDRESS3').val()== null)?'':encodeURIComponent(jQuery('#js_SHIPPING_ADDRESS3').val());
	                var city = (jQuery('#js_SHIPPING_CITY').val()== null)?'':encodeURIComponent(jQuery('#js_SHIPPING_CITY').val());
	                var postalCode = (jQuery('#js_SHIPPING_POSTAL_CODE').val()== null)?'':encodeURIComponent(jQuery('#js_SHIPPING_POSTAL_CODE').val());
	                var stateProvinceGeoId = (jQuery('#js_SHIPPING_STATE').val()== null)?'':encodeURIComponent(jQuery('#js_SHIPPING_STATE').val());
	                var countryGeoId = (jQuery('#js_SHIPPING_COUNTRY').val()== null)?'':encodeURIComponent(jQuery('#js_SHIPPING_COUNTRY').val());
                }
                else
                {
                	if(jQuery('#js_BILLING_STATE').length)
                	{
	                	<#-- get billing address values -->
		                var address1 = (jQuery('#js_BILLING_ADDRESS1').val()== null)?'':encodeURIComponent(jQuery('#js_BILLING_ADDRESS1').val());
		                var address2 = (jQuery('#js_BILLING_ADDRESS2').val()== null)?'':encodeURIComponent(jQuery('#js_BILLING_ADDRESS2').val());
		                var address3 = (jQuery('#js_BILLING_ADDRESS3').val()== null)?'':encodeURIComponent(jQuery('#js_BILLING_ADDRESS3').val());
		                var city = (jQuery('#js_BILLING_CITY').val()== null)?'':encodeURIComponent(jQuery('#js_BILLING_CITY').val());
		                var postalCode = (jQuery('#js_BILLING_POSTAL_CODE').val()== null)?'':encodeURIComponent(jQuery('#js_BILLING_POSTAL_CODE').val());
		                var stateProvinceGeoId = (jQuery('#js_BILLING_STATE').val()== null)?'':encodeURIComponent(jQuery('#js_BILLING_STATE').val());
		                var countryGeoId = (jQuery('#js_BILLING_COUNTRY').val()== null)?'':encodeURIComponent(jQuery('#js_BILLING_COUNTRY').val());
	                }
	                else if(jQuery('#js_PERSONAL_STATE').length)
	                {
	                	<#-- get billing address values from Personal Info section -->
		                var address1 = (jQuery('#js_BILLING_ADDRESS1').val()== null)?'':encodeURIComponent(jQuery('#js_BILLING_ADDRESS1').val());
		                var address2 = (jQuery('#js_BILLING_ADDRESS2').val()== null)?'':encodeURIComponent(jQuery('#js_BILLING_ADDRESS2').val());
		                var address3 = (jQuery('#js_BILLING_ADDRESS3').val()== null)?'':encodeURIComponent(jQuery('#js_BILLING_ADDRESS3').val());
		                var city = (jQuery('#js_BILLING_CITY').val()== null)?'':encodeURIComponent(jQuery('#js_BILLING_CITY').val());
		                var postalCode = (jQuery('#js_BILLING_POSTAL_CODE').val()== null)?'':encodeURIComponent(jQuery('#js_BILLING_POSTAL_CODE').val());
		                var stateProvinceGeoId = (jQuery('#js_BILLING_STATE').val()== null)?'':encodeURIComponent(jQuery('#js_BILLING_STATE').val());
		                var countryGeoId = (jQuery('#js_BILLING_COUNTRY').val()== null)?'':encodeURIComponent(jQuery('#js_BILLING_COUNTRY').val());
	                }
                }
        
                <#-- make ajax request parameters -->
                var reqParam = '?address1='+address1+'&address2='+address2+'&address3='+address3+'&city='+city;
                reqParam = reqParam+'&postalCode='+postalCode+'&stateProvinceGeoId='+stateProvinceGeoId+'&countryGeoId='+countryGeoId;
                
                jQuery.get('<@ofbizUrl>${updateShippingOptionRequest?if_exists}'+reqParam+'&callback=Y&rnd='+String((new Date()).getTime()).replace(/\D/gi, "")+'</@ofbizUrl>', function(data) {
                    var shippingOptions = jQuery(data).find('#js_deliveryOptionBox');
                    jQuery('#js_deliveryOptionBox').replaceWith(shippingOptions);
                    <#-- if a shipping option is already selected, then set this shipping option to the cart -->
                    if(jQuery('input.js_shipping_method:checked').val() != null) 
                    {
                        setShippingMethod(jQuery('input.js_shipping_method:checked').val(), isOnLoad);
                    } 
                    <#-- if there are shipping options available but none are selected, then select the first one and set to cart -->
                    else 
                    {
                        jQuery('input.js_shipping_method:first').attr("checked", true);
                        setShippingMethod(jQuery('input.js_shipping_method').val(), isOnLoad);
                    }
                });
            } else {
                location.reload();
                jQuery('#isGoogleApi').val("");
            }
        }
    }

    <#-- update the order item section -->
    function setShippingMethod(selectedShippingOption, isOnLoad) 
    {
    	var selectedStoreId = "";
        if (jQuery('.js_onePageCheckoutOrderItemsSummary').length) 
        {
            <#-- if store pick up show/hide COD payment option -->
            if (selectedShippingOption == "NO_SHIPPING@_NA_")
            {
                <#assign storeCC = Static["com.osafe.util.Util"].isProductStoreParmTrue(request,"CHECKOUT_STORE_CC")/>
                <#assign storeCCReq = Static["com.osafe.util.Util"].isProductStoreParmTrue(request,"CHECKOUT_STORE_CC_REQ")/>
                if(${storeCC.toString()} && ${storeCCReq.toString()})
                {
                    jQuery('.js_paymentOptions').hide();
                    jQuery('#js_checkoutPaymentOptions').show(); 
                }
                else if (${storeCC.toString()} && !${storeCCReq.toString()})
                {
                    jQuery('.js_paymentOptions').show();
                    jQuery('#js_checkoutPaymentOptions').show(); 
                }
                else if (!${storeCC.toString()})
                {
                    jQuery('.js_paymentOptions').hide();
                    jQuery('#js_checkoutPaymentOptions').hide(); 
                }
                if(${allowCOD.toString()})
                {
                    jQuery('.js_codOptions').hide();
                }
                if (jQuery('#js_payInStoreY').is(':checked')) 
                {   
                    jQuery('#js_checkoutPaymentOptions').hide();     
                }
                
                <#-- get Store Id -->
                selectedStoreId = jQuery('#js_storeId').val();
            }
            else
            {
                if(${allowCOD.toString()})
                {
                    jQuery('.js_codOptions').show();
                }
                jQuery('.js_paymentOptions').hide();
                jQuery('#js_checkoutPaymentOptions').show();
            }
            
            jQuery.ajaxSetup({async:false});
            jQuery.get('<@ofbizUrl>${setShippingOptionRequest?if_exists}?shipMethod='+selectedShippingOption+'&storeId='+selectedStoreId+'&rnd=' + String((new Date()).getTime()).replace(/\D/gi, "")+'</@ofbizUrl>', function(data)
            {
            	if (jQuery('.js_onePageCheckoutOrderItemsSummary').length) 
        		{
	            	jQuery('.js_onePageCheckoutOrderItemsSummary').replaceWith(data);
	            }
	             
	            <#-- if error is displayed and then shipping option gets selected when new shipping option is selected -->
	            var selected = jQuery(".js_shipping_method:checked");
			    if(jQuery(selected).length)
			    {
				    if(jQuery('.js_shippingMethodsContainer').find('input[type="radio"]').is(':checked'))
		            {
			            var selected = jQuery(".js_shipping_method:checked");
			            if(jQuery(selected).hasClass('js_shippingMethodRadioButton'))
			            {
			            	<#-- now check if there is an error message in this Div. -->
			            	var shipMethErrorMessage = jQuery('.js_shippingMethodsContainer').closest('#js_deliveryOptionBox').next('#js_deliveryOptionBoxError');
			            	<#-- check if error is displayed -->
			            	if(jQuery(shipMethErrorMessage).length)
			            	{
			            		if(jQuery.trim(jQuery(shipMethErrorMessage).html()).length)
			            		{
			            			jQuery(shipMethErrorMessage).children().hide();
			            		}
			            	}
		            	}
		             }	
				 }
			});
        }
        
        if((isOnLoad != null) && (isOnLoad =='N')) 
        {
        	if (jQuery('.js_onePageCheckoutLoyaltyPoints').length) 
             {
                jQuery.get('<@ofbizUrl>${refreshLoyaltyPointsRequest?if_exists}?rnd=' + String((new Date()).getTime()).replace(/\D/gi, "")+'</@ofbizUrl>', function(lpData)
             	{
             		jQuery('.js_onePageCheckoutLoyaltyPoints').replaceWith(lpData);	
	            });
             }
            if (jQuery('.js_onePageCheckoutPromoCode').length) 
            {
	            jQuery.get('<@ofbizUrl>${reloadPromoCodeRequest?if_exists}?rnd=' + String((new Date()).getTime()).replace(/\D/gi, "")+'</@ofbizUrl>', function(promoData)
	         	{
	         		jQuery('.js_onePageCheckoutPromoCode').replaceWith(promoData);	
	            });
            }
        }
        if (jQuery('.js_onePageCheckoutGiftCard').length) 
	    {
	        <#-- capture warning message since it will dissapear when gift card section is reloaded -->
	        var gcWarningMessTest;
	        if((isOnLoad == null) || (isOnLoad !='N')) 
	        {
	        	gcWarningMessTest = jQuery("#js_gcWarningMessTest");
	        }
	     	jQuery.get('<@ofbizUrl>${refreshGiftCardRequest?if_exists}?rnd=' + String((new Date()).getTime()).replace(/\D/gi, "")+'</@ofbizUrl>', function(gcData)
	     	{
	     		jQuery('.js_onePageCheckoutGiftCard').find('.js_eCommerceEnteredGiftCardPayment').replaceWith(jQuery(gcData).find('.js_eCommerceEnteredGiftCardPayment'));	
	     		if((isOnLoad == null) || (isOnLoad !='N')) 
				{
					if(jQuery(gcWarningMessTest).length)
					{
						<#-- re-apply error message -->
						jQuery(".giftCardSummary").append(gcWarningMessTest);
					}
				}
	        });
	    }
	    
	     if (jQuery('#js_remainingPayment').length) 
	     {
	     	jQuery.get('<@ofbizUrl>${reloadBalanceRequest?if_exists}?rnd=' + String((new Date()).getTime()).replace(/\D/gi, "")+'</@ofbizUrl>', function(data) 
	        {
	           var balanceSection = jQuery(data).find("#js_remainingPayment");
	           jQuery('#js_remainingPayment').replaceWith(balanceSection);
	           
	           var remainingBalance = Number(jQuery("#js_remainingPaymentValue").val());
               if(Number(remainingBalance) <= 0)
               {
               		jQuery(".js_paymentOptions").hide();
               		jQuery("#js_checkoutPaymentOptions").hide();
               }
	        });
	     }
    }
    
    
    function getQtyInWishlist(productId) 
    {
        <#-- check how many items are already in the wish list -->
        var qtyInWishlist = Number(0);
        <#if wishList?has_content>
            <#list wishList as wishListItem>
                cartItemProductId = '${wishListItem.productId}';
                if(cartItemProductId == productId)
                {
                    wishlistItemQty = Number(${wishListItem.quantity});
                    qtyInWishlist = Number(qtyInWishlist) + Number(wishlistItemQty);
                }
            </#list>
        </#if>    
        return qtyInWishlist;
    }
    
    function getProductIdInWishlist(index) 
    {
        <#-- check how many items are already in the wish list -->
        var cartItemProductId = "";
        <#if wishList?has_content>
            <#list wishList as wishListItem>
                if(index == '${wishListItem.shoppingListItemSeqId}')
                {
                    cartItemProductId = '${wishListItem.productId}';
                }
            </#list>
        </#if>    
        return cartItemProductId;
    }

    function checkStoreCreditRequestFromCheckBox() 
    {
        if (jQuery('#js_useStoreCredit').is(':checked') && isNotEmpty(jQuery('#js_storeCreditAmount').val())) 
        {   
            addStoreCredit();
        }
        else if (isNotEmpty(jQuery('#js_storeCreditAmount').val()))
        {
            removeStoreCredit();
        }
    }

    function checkStoreCreditRequestFromAmount() 
    {
        if (jQuery('#js_useStoreCredit').is(':checked') && isNotEmpty(jQuery('#js_storeCreditAmount').val())) 
        {   
            addStoreCredit();
        }
    }

    function addStoreCredit() 
    {
        var cform = document.${formName!};
        cform.action="<@ofbizUrl>${addStoreCreditRequest!}</@ofbizUrl>";
        cform.submit();
    }

    function removeStoreCredit()
    {
          var cform = document.${formName!};
          cform.action="<@ofbizUrl>${removeStoreCreditRequest!}</@ofbizUrl>";
          cform.submit();
    }

    function isNotEmpty(value) 
    {
        return !isEmpty(value);
    }

    function isEmpty(value) 
    {
        valueWithoutSpace = value.replace(/^\s+|\s+$/g, "");
        if (valueWithoutSpace == null || valueWithoutSpace == "") 
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    function giftMessageConvertApostrophe()
    {
    	jQuery('.js_giftMessageFrom').each(function() 
        {
            var giftMessageFromInputId = jQuery(this).attr('id');
            convertApostrophe(giftMessageFromInputId);
        });
        
        jQuery('.js_giftMessageTo').each(function() 
        {
            var giftMessageToInputId = jQuery(this).attr('id');
            convertApostrophe(giftMessageToInputId);
        });
        
    	jQuery('.js_giftMessageText').each(function() 
        {
            var giftMessageInputId = jQuery(this).attr('id');
            convertApostrophe(giftMessageInputId);
        });
    }
    
</script>
