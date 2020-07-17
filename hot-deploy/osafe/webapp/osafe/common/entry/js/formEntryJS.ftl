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
    jQuery('.characterLimit').each(function(){
            restrictTextLength(this);
        });
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
      copyFieldsInitially(fromAddr, fromAddrContainer, toAddr, triggerElt);
    }
    jQuery(triggerElt).click(function(){
      if (isBlindup) {
        jQuery(toAddrSection).slideToggle(1000);
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
      if(fromElmId == fromAddrPurpose+'AddressContactMechId') {
          return;
      }
      jQuery(toElmId).val(jQuery(fromElm).val());
      jQuery(toElmId).change();
    }
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

  function getPostalAddress(contactMechId, purpose) {
    jQuery.ajaxSetup({async:false});
    jQuery.post("<@ofbizUrl>getPostalAddress</@ofbizUrl>", {contactMechId: contactMechId}, function(data) {
    <#if Static["com.osafe.util.Util"].isProductStoreParmTrue(request,"COUNTRY_MULTI")>
        jQuery("#"+purpose+"_COUNTRY > option").each(function() {
            if (this.value == data.countryGeoId) {
               jQuery(this).attr('selected', 'selected');
               jQuery(this).change();
            }
        });
    <#else>
        jQuery("#"+purpose+"_COUNTRY").val(data.countryGeoId);
    </#if>
       jQuery("#"+purpose+"_STATE > option").each(function() {
        if (this.value ==data.stateProvinceGeoId) {
           jQuery(this).attr('selected', 'selected');
        }
    });
    jQuery("#"+purpose+"AddressContactMechId").val(data.contactMechId);
    jQuery("#"+purpose+"_ADDRESS1").val(data.address1);
    jQuery("#"+purpose+"_ADDRESS2").val(data.address2);
    jQuery("#"+purpose+"_ADDRESS3").val(data.address3);
    jQuery("#"+purpose+"_CITY").val(data.city);
    jQuery("#"+purpose+"_POSTAL_CODE").val(data.postalCode);
    jQuery("#"+purpose+"_POSTAL_CODE").change();
    getAddressFormat(purpose);
    });
  }
 function restrictTextLength(textArea){
    var maxchar = jQuery(textArea).attr('maxlength');
    var curLen = jQuery(textArea).val().length;
    var regCharLen = lineBreakCount(jQuery(textArea).val());
    jQuery(textArea).next('.textCounter').html("* "+(maxchar - (curLen+regCharLen))+" ${uiLabelMap.CharactersLeftLabel}");
    jQuery(textArea).keyup(function() {
        var cnt = jQuery(this).val().length;
        var regCharLen = lineBreakCount(jQuery(this).val());
        var remainingchar = maxchar - (cnt + regCharLen);
        if(remainingchar < 0){
            jQuery(this).next('.textCounter').html("* "+'0 ${uiLabelMap.CharactersLeftLabel}');
            jQuery(this).val(jQuery(this).val().slice(0, (maxchar-regCharLen)));
        } else{
            jQuery(this).next('.textCounter').html("* "+remainingchar+' ${uiLabelMap.CharactersLeftLabel}');
        }
    });
 }
  function lineBreakCount(str){
        /* counts \n */
        try {
            return((str.match(/[^\n]*\n[^\n]*/gi).length));
        } catch(e) {
            return 0;
        }
    }
    
    
    //set cell phone number to required based on user text messaging options
    jQuery(document).ready(function () {
        //when page first loads
        txtPreferenceSelected = jQuery("input[name='PARTY_TEXT_PREFERENCE']:checked").val();
        if(txtPreferenceSelected == "Y")
        {
            jQuery("#PHONE_MOBILE_REQUIRED").val("true");
        }
        else
        {
            jQuery("#PHONE_MOBILE_REQUIRED").val("false");
        }
        //when user changes preference
        jQuery("input[name='PARTY_TEXT_PREFERENCE']").change(function(){
            txtPreferenceSelected = jQuery(this).val();
            if(txtPreferenceSelected == "Y")
            {
                jQuery("#PHONE_MOBILE_REQUIRED").val("true");
            }
            else
            {
                jQuery("#PHONE_MOBILE_REQUIRED").val("false");
            }
        });
    });
</script>
