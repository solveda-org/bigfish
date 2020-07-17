<script type="text/javascript">
<#assign allowCOD = Static["com.osafe.util.Util"].isProductStoreParmTrue(request,"CHECKOUT_ALLOW_COD")/>
    function submitCheckoutForm(form, mode, value) 
    {
        if (mode == "DN") {
            // done action; checkout
            form.action="<@ofbizUrl>${doneAction!""}</@ofbizUrl>";
            form.submit();
        } else if (mode == "NA") {
            // new address
            form.action="<@ofbizUrl>${addAddressAction!""}?preContactMechTypeId=POSTAL_ADDRESS&contactMechPurposeTypeId="+value+"&DONE_PAGE=${donePage!""}</@ofbizUrl>";
            form.submit();
        } else if (mode == "BK") {
            // Previous Page
            form.action="<@ofbizUrl>${backAction!""}?action=previous</@ofbizUrl>";
            form.submit();
        } else if (mode == "UC") {
            // update cart action
            if (updateCart()) {
                form.action="<@ofbizUrl>${updateCartAction!""}</@ofbizUrl>";
                form.submit();
            }
        }  else if (mode == "PA") {
            // paypal action
            document.getElementById("paymentMethodTypeId").value = value;
            form.action="<@ofbizUrl>${payPalAction!""}</@ofbizUrl>";
            form.submit();
        } else if (mode == "SO") {
            // submit order
            document.getElementById("submitOrderBtn").value = "${uiLabelMap.SubmittingOrderBtn}";
            document.getElementById("submitOrderBtn").disabled=true;
            form.action="<@ofbizUrl>${submitOrderAction!""}</@ofbizUrl>";
            form.submit();
        } else if (mode == "EB") {
            // EBS action
            document.getElementById("paymentMethodTypeId").value = value;
            form.action="<@ofbizUrl>${ebsAction!""}</@ofbizUrl>";
            form.submit();
        } else if (mode == "UWL") {
            // update wish list action
            if (updateCart()) {
                form.action="<@ofbizUrl>${updateWishListAction!""}</@ofbizUrl>";
                form.submit();
            }
        } else if (mode == "ACW") {
            // add to cart from wish list action
            if (updateCart()) {
                document.getElementById("add_item_id").value = value;
                form.action="<@ofbizUrl>${addToCartFromWishListAction!""}</@ofbizUrl>";
                form.submit();
            }
        }
    }

    function updateCart() {
      var cartItemsNo = ${shoppingCartSize!wishListSize!"0"};
      <#assign PDP_QTY_MIN = Static["com.osafe.util.Util"].getProductStoreParm(request,"PDP_QTY_MIN")!"1"/>
      <#assign PDP_QTY_MAX = Static["com.osafe.util.Util"].getProductStoreParm(request,"PDP_QTY_MAX")!"99"/>
      var lowerLimit = ${PDP_QTY_MIN!"1"};
      var upperLimit = ${PDP_QTY_MAX!"99"};
      var zeroQty = false;
      
      for (var i=0;i<cartItemsNo;i++)
      {
          var quantity = jQuery('#update_'+i).val();
          if(quantity != 0) 
          {
	          if(quantity < lowerLimit)
	          {
	              alert("${StringUtil.wrapString(StringUtil.replaceString(uiLabelMap.PDPMinQtyError,'\"','\\"'))}");
	              return false;
	          }
	          if(upperLimit!= 0 && quantity > upperLimit)
	          {
	              alert("${StringUtil.wrapString(StringUtil.replaceString(uiLabelMap.PDPMaxQtyError,'\"','\\"'))}");
	              return false;
	          }
	          if(!isWhole(quantity))
	          {
	              alert("${StringUtil.wrapString(StringUtil.replaceString(uiLabelMap.PDPQtyDecimalNumberError,'\"','\\"'))}");
	              return false;
	          }
          } 
          {
              zeroQty = true;
          }
      }
      if(zeroQty == true)
      {
          window.location='<@ofbizUrl>deleteFromCart</@ofbizUrl>';
      }
      return true;
    }
<#if formName?has_content>
    function addManualPromoCode() {
        if (jQuery('#manualOfferCode').length && jQuery('#manualOfferCode').val() != null) {
          promo = jQuery('#manualOfferCode').val().toUpperCase();
          promoCodeWithoutSpace = promo.replace(/^\s+|\s+$/g, "");
        }
        var cform = document.${formName!};
        cform.action="<@ofbizUrl>${addPromoCodeRequest!}?productPromoCodeId="+promoCodeWithoutSpace+"</@ofbizUrl>";
        cform.submit();
    }

    function removePromoCode(promoCode) {
        if (promoCode != null) {
          var cform = document.${formName!};
          cform.action="<@ofbizUrl>${removePromoCodeRequest!}?productPromoCodeId="+promoCode+"</@ofbizUrl>";
          cform.submit();
        }
    }
</#if>
   jQuery(document).ready(function () {

        // update shipping options base on shipping postal code
        if (jQuery('#SHIPPING_POSTAL_CODE').length) {
          updateShippingOption('Y');
          calcTax();
          jQuery('#SHIPPING_POSTAL_CODE').change(function () {
            updateShippingOption('N');
            calcTax();
          });
        }

        // make first shipping option as selected
        if (jQuery('input.shipping_method:checked').val() == undefined) {
          jQuery('input.shipping_method:first').attr("checked", true);
        }

        // activate pick up store event listener
        pickupStoreEventListener();
    });

    function pickupStoreEventListener() {
        //selct store and close dialouge box
        jQuery('.pickupStore').submit(function(event) {
            event.preventDefault();
            jQuery.post(jQuery(this).attr('action'), jQuery(this).serialize(), function(data) {
                updateShippingOption('N');
                jQuery(displayDialogId).dialog('close');
                jQuery('.shippingOptionsStorePickup').hide();
                <#assign storeCC = Static["com.osafe.util.Util"].isProductStoreParmTrue(request,"CHECKOUT_STORE_CC")/>
                <#assign storeCCReq = Static["com.osafe.util.Util"].isProductStoreParmTrue(request,"CHECKOUT_STORE_CC_REQ")/>
                if (${storeCC.toString()}) {
                    if (!${storeCCReq.toString()}) {
                        jQuery('.paymentOptions').show();
                        jQuery('.codOptions').hide();
                    }
                } else {
                    jQuery('.creditCardEntry').hide();
                    if(${allowCOD.toString()}) {
                        jQuery('.codOptions').show();
                    }
                }
            });
        });

        //cancel store select and close dialouge box
        jQuery('.cancelPickupStore').click(function(event) {
            event.preventDefault();
            jQuery(displayDialogId).dialog('close');
        });

        //submit store locator search form
        jQuery('.storePickup_Form').submit(function(event) {
            event.preventDefault();
            jQuery.get(jQuery(this).attr('action')+'?'+jQuery(this).serialize(), function(data) {
                jQuery('#eCommerceStoreLocatorContainer').replaceWith(data);
                pickupStoreEventListener();
                if (jQuery('#isGoogleApi').val() != "Y") {
                    loadScript();
                } else{
                    loadMap();
                }
            });
        });
    }

    // update shipping option base on postal code 
    function updateShippingOption(isOnLoad) {
        if (jQuery('#deliveryOptionBox').length) {
            if (jQuery('#SHIPPING_POSTAL_CODE').length) {
                if (jQuery('#SHIPPING_POSTAL_CODE').val() == '') {
                    postalcode = "dummy";
                }else {
                    postalcode = jQuery('#SHIPPING_POSTAL_CODE').val();
                }
                jQuery.get('<@ofbizUrl>${updateShippingOptionRequest?if_exists}?postalCode='+postalcode+'&rnd='+String((new Date()).getTime()).replace(/\D/gi, "")+'</@ofbizUrl>', function(data) {
                    jQuery('#deliveryOptionBox').replaceWith(data);
                    if(jQuery('input.shipping_method:checked').val() != null) {
                        setShippingMethod(jQuery('input.shipping_method:checked').val(), isOnLoad);
                    } else {
                        setShippingMethod(jQuery('input.shipping_method').val(), isOnLoad);
                    }
                });
            } else {
                location.reload();
                jQuery('#isGoogleApi').val("");
            }
        }
    }
    // calculate tax 
    function calcTax() {
        // get shipping address values
        var address1 = (jQuery('#SHIPPING_ADDRESS1').val()== null)?'':encodeURIComponent(jQuery('#SHIPPING_ADDRESS1').val());
        var address2 = (jQuery('#SHIPPING_ADDRESS2').val()== null)?'':encodeURIComponent(jQuery('#SHIPPING_ADDRESS2').val());
        var address3 = (jQuery('#SHIPPING_ADDRESS3').val()== null)?'':encodeURIComponent(jQuery('#SHIPPING_ADDRESS3').val());
        var city = (jQuery('#SHIPPING_CITY').val()== null)?'':encodeURIComponent(jQuery('#SHIPPING_CITY').val());
        var postalCode = (jQuery('#SHIPPING_POSTAL_CODE').val()== null)?'':encodeURIComponent(jQuery('#SHIPPING_POSTAL_CODE').val());
        var stateProvinceGeoId = (jQuery('#SHIPPING_STATE').val()== null)?'':encodeURIComponent(jQuery('#SHIPPING_STATE').val());
        var countryGeoId = (jQuery('#SHIPPING_COUNTRY').val()== null)?'':encodeURIComponent(jQuery('#SHIPPING_COUNTRY').val());

        // make ajax request parameters
        var reqParam = '?address1='+address1+'&address2='+address2+'&address3='+address3+'&city='+city;
        reqParam = reqParam+'&postalCode='+postalCode+'&stateProvinceGeoId='+stateProvinceGeoId+'&countryGeoId='+countryGeoId;

        //call ajax and update order item section
        if (jQuery('#SHIPPING_POSTAL_CODE').length) {
            if (jQuery('.onePageCheckoutOrderItemsSeq').length) {
                jQuery('.onePageCheckoutOrderItemsSeq').load('<@ofbizUrl>${calcTaxRequest?if_exists}'+reqParam+'&rnd=' + String((new Date()).getTime()).replace(/\D/gi, "")+'</@ofbizUrl>');
            }
        }
    }

    // update the order item section
    function setShippingMethod(selectedShippingOption, isOnLoad) {
        if (jQuery('.onePageCheckoutOrderItemsSeq').length) {
            jQuery('.onePageCheckoutOrderItemsSeq').load('<@ofbizUrl>${setShippingOptionRequest?if_exists}?shipMethod='+selectedShippingOption+'&rnd=' + String((new Date()).getTime()).replace(/\D/gi, "")+'</@ofbizUrl>');
        }
        if((isOnLoad != null) && (isOnLoad =='N')) {
            //call ajax and update promotion info section
            if (jQuery('.onePageCheckoutPromoCodeSeq').length) {
                jQuery('.onePageCheckoutPromoCodeSeq').load('<@ofbizUrl>${reloadPromoCodeRequest?if_exists}?rnd=' + String((new Date()).getTime()).replace(/\D/gi, "")+'</@ofbizUrl>');
            }
        }
    }
    
    // remove store pick up
    function removeStorePickup(paymentMethodType) {
        jQuery('#paymentMethodTypeId').val(paymentMethodType);
        jQuery.post('<@ofbizUrl>${removeStorePickupRequest?if_exists}</@ofbizUrl>', {isGuestCheckout: "Y"}, function(data) {
            updateShippingOption('N');
            jQuery('.shippingOptionsStorePickup').show();
            jQuery('.creditCardEntry').show();
            jQuery('.paymentOptions').hide();
            if(${allowCOD.toString()}) {
                jQuery('.codOptions').show();
            }
        });
    }

</script>
