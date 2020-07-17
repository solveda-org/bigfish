<script type="text/javascript">

    function submitCheckoutForm(form, mode, value) {
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
        }
    }

    function updateCart() {
      var cartItemsNo = ${shoppingCartSize!"0"};
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
          updateShippingOption();
          jQuery('#SHIPPING_POSTAL_CODE').change(function () {
            updateShippingOption()
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
                updateShippingOption();
                jQuery(displayDialogId).dialog('close');
                jQuery('.shippingOptionsStorePickup').hide();
                if (jQuery('#storeCCRequired').val() == 'false') {
                    jQuery('.creditCardEntry').hide();
                }
                if (jQuery('#storeCCRequired').val() == 'true') {
                    if (jQuery('#storeCCValidate').val() == 'false') {
                        jQuery('.paymentOptions').show();
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
            jQuery.getScript(jQuery(this).attr('action')+'?'+jQuery(this).serialize(), function(data, textStatus, jqxhr) {
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
    function updateShippingOption() {
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
                        setShippingMethod(jQuery('input.shipping_method:checked').val());
                    } else {
                        setShippingMethod(jQuery('input.shipping_method').val());
                    }
                });
            } else {
                location.reload();
                jQuery('#isGoogleApi').val("");
            }
        }
    }

    // update the order item section
    function setShippingMethod(selectedShippingOption) {
        if (jQuery('.onePageCheckoutOrderItemsSeq').length) {
            jQuery('.onePageCheckoutOrderItemsSeq').load('<@ofbizUrl>${setShippingOptionRequest?if_exists}?shipMethod='+selectedShippingOption+'&rnd=' + String((new Date()).getTime()).replace(/\D/gi, "")+'</@ofbizUrl>');
        }
    }

    // remove store pick up
    function removeStorePickup(paymentMethodType) {
        jQuery('#paymentMethodTypeId').val(paymentMethodType);
        jQuery.post('<@ofbizUrl>${removeStorePickupRequest?if_exists}</@ofbizUrl>', {isGuestCheckout: "Y"}, function(data) {
            updateShippingOption();
            jQuery('.shippingOptionsStorePickup').show();
            jQuery('.creditCardEntry').show();
            jQuery('.paymentOptions').hide();
        });
    }

</script>
