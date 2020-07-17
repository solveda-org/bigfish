<script type="text/javascript">

  lastFocusedName = null;
  function setLastFocused(formElement) {
    lastFocusedName = formElement.name;
  }

  function changeEmail() {
    jQuery('#USERNAME').val(jQuery('#CUSTOMER_EMAIL').val());
  }

  jQuery(document).ready(function () {

    if (jQuery('#BILLING_ADDRESS_ENTRY').length && jQuery('#SHIPPING_ADDRESS_ENTRY').length && jQuery('#isSameAsBilling').length && jQuery('#SHIPPING_AddressSection').length) {
      copyAddress('BILLING', jQuery('#BILLING_ADDRESS_ENTRY'), 'SHIPPING', jQuery('#SHIPPING_AddressSection'), jQuery('#isSameAsBilling'), true);
    }

    if (jQuery('#SHIPPING_POSTAL_CODE').length) {
      updateShippingOption();
      jQuery('#SHIPPING_POSTAL_CODE').change(function () {
        updateShippingOption()
      });
    }

    if (jQuery('input[name="shipping_method"]:checked').val() == undefined) {
      jQuery('input[name="shipping_method"]:first').attr("checked", true);
    }

    if(jQuery('#content').length) {
        var curLen = jQuery('#content').val().length;
        jQuery('#textCounter').html(255 - curLen+" ${uiLabelMap.CharactersLeftLabel}");
        jQuery('#content').bind('keyup', function() {
            var maxchar = 255;
            var cnt = jQuery(this).val().length;
            var remainingchar = maxchar - cnt;
            if(remainingchar < 0){
                jQuery('#textCounter').html('0 ${uiLabelMap.CharactersLeftLabel}');
                jQuery(this).val(jQuery(this).val().slice(0, maxchar));
            }else{
                jQuery('#textCounter').html(remainingchar+' ${uiLabelMap.CharactersLeftLabel}');
            }
        });
      }

    pickupStoreEventListener();
  });

  function copyAddress(fromAddr, fromAddrContainer, toAddr, toAddrSection, triggerElt, isBlindup) {
    jQuery(fromAddrContainer).find('input, select, textarea').change(function(){
      if(jQuery(triggerElt).is(":checked")){
        copyFieldvalue(fromAddr, this, toAddr);
      }
    });
    if(jQuery(triggerElt).is(":checked") && jQuery(toAddrSection).length){
      if (isBlindup) {
        jQuery(toAddrSection).hide();
      }
      jQuery(toAddrSection).find('input, select, textarea').attr('disabled','disabled');
      copyFieldsInitially(fromAddr, fromAddrContainer, toAddr, triggerElt);
    }
    jQuery(triggerElt).click(function(){
      if (isBlindup) {
        jQuery(toAddrSection).slideToggle(1000);
      }
      if(jQuery(triggerElt).is(":checked")){
        jQuery(toAddrSection).find('input, select, textarea').attr('disabled','disabled');
      } else {
        jQuery(toAddrSection).find('input, select, textarea').removeAttr('disabled');
      }
      copyFieldsInitially(fromAddr, fromAddrContainer, toAddr, triggerElt);
    });
  }

  function copyFieldsInitially(fromAddr, fromAddrContainer, toAddr, triggerElt) {
    jQuery(fromAddrContainer).find('input, select, textarea').each(function(){
      if(jQuery(triggerElt).is(":checked")){
        copyFieldvalue(fromAddr, this, toAddr);
      }
    });
  }

  function copyFieldvalue(fromAddrPurpose, fromElm, toAddrPurpose) {
    fromElmId = jQuery(fromElm).attr('id');
    var toElmId = '#'+toAddrPurpose + fromElmId.sub(fromAddrPurpose, "");
    if(jQuery(toElmId).length) {
      jQuery(toElmId).val(jQuery(fromElm).val());
      if(fromElmId == fromAddrPurpose+'_COUNTRY') {
        getAssociatedStateList(toAddrPurpose+'_COUNTRY', toAddrPurpose+'_STATE', 'advice-required-'+toAddrPurpose+'_STATE', toAddrPurpose+'_STATES', toAddrPurpose+'_STATE_TEXT');
        getAddressFormat(toAddrPurpose);
      }
      if (fromElmId == fromAddrPurpose+"_POSTAL_CODE") {
        updateShippingOption();
      }
    }
  }

  function pickupStoreEventListener() {
    jQuery('.pickupStore').submit(function(event) {
        event.preventDefault();
        jQuery.post(jQuery(this).attr('action'), jQuery(this).serialize(), function(data) {
            updateShippingOption();
            jQuery(displayDialogId).dialog('close');
        });
    });

    jQuery('.cancelPickupStore').click(function(event) {
        event.preventDefault();
        jQuery(displayDialogId).dialog('close');
    });

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

  function getAddressFormat(idPrefix) {
    var countryId = '#'+idPrefix+'_COUNTRY'
    if (jQuery(countryId).val() == "USA") {
      jQuery('.'+idPrefix+'_CAN').hide();
      jQuery('.'+idPrefix+'_OTHER').hide();
      jQuery('.'+idPrefix+'_USA').show();
    } else if (jQuery(countryId).val() == "CAN") {
      jQuery('.'+idPrefix+'_USA').hide();
      jQuery('.'+idPrefix+'_OTHER').hide();
      jQuery('.'+idPrefix+'_CAN').show();
    } else{
      jQuery('.'+idPrefix+'_USA').hide();
      jQuery('.'+idPrefix+'_CAN').hide();
      jQuery('.'+idPrefix+'_OTHER').show();
    }
  }

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
                    if(jQuery('input[name=shipping_method]:checked').val() != null) {
                        setShippingMethod(jQuery('input[name=shipping_method]:checked').val());
                    } else {
                        setShippingMethod(jQuery('input[name=shipping_method]').val());
                    }
                });
            } else {
                location.reload();
                jQuery('#isGoogleApi').val("");
            }
        }
    }

    function setShippingMethod(selectedShippingOption) {
        if (jQuery('#checkoutOrderDetailAndPromoCodeSec').length) {
            jQuery('#checkoutOrderDetailAndPromoCodeSec').load('<@ofbizUrl>${setShippingOptionRequest?if_exists}?shipMethod='+selectedShippingOption+'&rnd=' + String((new Date()).getTime()).replace(/\D/gi, "")+'</@ofbizUrl>');
        }
    }

    function setExternalCheckout(paymentMethodType) {
        jQuery('#paymentMethodTypeId').val(paymentMethodType);
        jQuery('#guestCheckout').submit();
    }

    function removeStorePickup(paymentMethodType) {
        jQuery('#paymentMethodTypeId').val(paymentMethodType);
        jQuery.post('<@ofbizUrl>${removeStorePickupRequest?if_exists}</@ofbizUrl>', {isGuestCheckout: "Y"}, function(data) {
            updateShippingOption();
        });
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

  //This method exists in geoAutoCompleter.js named 'getAssociatedStateList'. we have reused and customized.
  function getAssociatedStateList(countryId, stateId, errorId, divId, addressLine3) {
    var optionList = "";
    jQuery.ajaxSetup({async:false});
    jQuery.post("<@ofbizUrl>getAssociatedStateList</@ofbizUrl>", {countryGeoId: jQuery("#"+countryId).val()}, function(data) {
      var stateList = data.stateList;
      jQuery(stateList).each(function() {
        if (this.geoId) {
          optionList = optionList + "<option value = "+this.geoId+" >"+this.geoName+"</option>";
        } else {
          optionList = optionList + "<option value = >"+this.geoName+"</option>";
        }
      });
      jQuery("#"+stateId).html(optionList);
      if (jQuery(stateList).size() <= 1) {
        jQuery("#"+addressLine3).show();
        jQuery("#"+divId).hide();
      } else {
        jQuery("#"+addressLine3).hide();
        jQuery("#"+divId).show();
      }
    });
  }

</script>
