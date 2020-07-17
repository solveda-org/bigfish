<script type="text/javascript">
jQuery(document).ready(function () {
  <#if Static["com.osafe.util.Util"].isProductStoreParmTrue(QUICKLOOK_ACTIVE)>
    <#if QUICKLOOK_DELAY_MS?has_content && Static["com.osafe.util.Util"].isNumber(QUICKLOOK_DELAY_MS) && QUICKLOOK_DELAY_MS != "0">
      jQuery("div.eCommerceThumbNailHolder").hover(function(){jQuery(this).find("div.plpQuicklook").fadeIn(${QUICKLOOK_DELAY_MS});},function () {jQuery(this).find("div.plpQuicklook").fadeOut(${QUICKLOOK_DELAY_MS});});
    <#else>
      jQuery("div.eCommerceThumbNailHolder div.plpQuicklook").show();
    </#if>
  </#if>

    jQuery('.facetValue.hideThem').hide();
    jQuery('.seeLessLink').hide();

    jQuery('.plpFeatureSwatchImage').click(function() {
        var swatchVariant = jQuery(this).next('.swatchVariant').clone();

        var swatchVariantOnlinePrice = jQuery(this).nextAll('.swatchVariantOnlinePrice:first').clone().show();
        swatchVariantOnlinePrice.removeClass('swatchVariantOnlinePrice').addClass('plpPriceOnline');
        jQuery(this).parents('.eCommerceListItem').find('.plpPriceOnlineSeq').find('.plpPriceOnline').replaceWith(swatchVariantOnlinePrice);

        var swatchVariantListPrice = jQuery(this).nextAll('.swatchVariantListPrice:first').clone().show();
        swatchVariantListPrice.removeClass('swatchVariantListPrice').addClass('plpPriceList');
        jQuery(this).parents('.eCommerceListItem').find('.plpPriceListSeq').find('.plpPriceList').replaceWith(swatchVariantListPrice);
        
        var swatchVariantSaveMoney = jQuery(this).nextAll('.swatchVariantSaveMoney:first').clone().show();
        swatchVariantSaveMoney.removeClass('swatchVariantSaveMoney').addClass('plpPriceSavingMoney');
        jQuery(this).parents('.eCommerceListItem').find('.plpPriceSavingMoneySeq').find('.plpPriceSavingMoney').replaceWith(swatchVariantSaveMoney);
        
        var swatchVariantSavingPercent = jQuery(this).nextAll('.swatchVariantSavingPercent:first').clone().show();
        swatchVariantSavingPercent.removeClass('swatchVariantSavingPercent').addClass('plpPriceSavingPercent');
        jQuery(this).parents('.eCommerceListItem').find('.plpPriceSavingPercentSeq').find('.plpPriceSavingPercent').replaceWith(swatchVariantSavingPercent);
        
        jQuery(this).parents('.eCommerceListItem').find('.eCommerceThumbNailHolder').find('.swatchProduct').replaceWith(swatchVariant);
        jQuery('.eCommerceThumbNailHolder').find('.swatchVariant').show().attr("class", "swatchProduct");
        jQuery(this).siblings('.plpFeatureSwatchImage').removeClass("selected");
        jQuery(this).addClass("selected");
        makePDPUrl(this);
        <#if PLP_FACET_GROUP_VARIANT_MATCH?has_content>
          var descFeatureGroup = jQuery(this).prev("input.featureGroup").val();
          if(descFeatureGroup != '') {
            jQuery.each( jQuery('.'+descFeatureGroup), function(idx, elm){
              changeSwatchImg(elm);
            });
          }
          
          var title = jQuery(this).attr("title");
          jQuery.each( jQuery('.'+title), function(idx, elm){
            changeSwatchImg(elm);
          });
        </#if>
    });

    jQuery('.seeMoreLink').click(function() {
        jQuery(this).hide().parents('li').siblings('li.hideThem').show();
        jQuery(this).siblings('.seeLessLink').show();
    });

    jQuery('.seeLessLink').click(function() {
        jQuery(this).hide().parents('li').siblings('li.hideThem').hide();
        jQuery(this).siblings('.seeMoreLink').show();
    });
    
    function changeSwatchImg(elm) {
        var swatchVariant = jQuery(elm).next('.swatchVariant').clone();
        var swatchVariantOnlinePrice = jQuery(elm).nextAll('.swatchVariantOnlinePrice:first').clone().show();
        swatchVariantOnlinePrice.removeClass('swatchVariantOnlinePrice').addClass('plpPriceOnline');
        jQuery(elm).parents('.eCommerceListItem').find('.plpPriceOnlineSeq').find('.plpPriceOnline').replaceWith(swatchVariantOnlinePrice);

        var swatchVariantListPrice = jQuery(elm).nextAll('.swatchVariantListPrice:first').clone().show();
        swatchVariantListPrice.removeClass('swatchVariantListPrice').addClass('plpPriceList');
        jQuery(elm).parents('.eCommerceListItem').find('.plpPriceListSeq').find('.plpPriceList').replaceWith(swatchVariantListPrice);
        
        var swatchVariantSaveMoney = jQuery(elm).nextAll('.swatchVariantSaveMoney:first').clone().show();
        swatchVariantSaveMoney.removeClass('swatchVariantSaveMoney').addClass('plpPriceSavingMoney');
        jQuery(elm).parents('.eCommerceListItem').find('.plpPriceSavingMoneySeq').find('.plpPriceSavingMoney').replaceWith(swatchVariantSaveMoney);
        
        var swatchVariantSavingPercent = jQuery(elm).nextAll('.swatchVariantSavingPercent:first').clone().show();
        swatchVariantSavingPercent.removeClass('swatchVariantSavingPercent').addClass('plpPriceSavingPercent');
        jQuery(elm).parents('.eCommerceListItem').find('.plpPriceSavingPercentSeq').find('.plpPriceSavingPercent').replaceWith(swatchVariantSavingPercent);
        
        jQuery(elm).parents('.eCommerceListItem').find('.eCommerceThumbNailHolder').find('.swatchProduct').replaceWith(swatchVariant);
        jQuery('.eCommerceThumbNailHolder').find('.swatchVariant').show().attr("class", "swatchProduct");
        jQuery(elm).siblings('.plpFeatureSwatchImage').removeClass("selected");
        jQuery(elm).addClass("selected");
        makePDPUrl(elm);
    }
    
    function makePDPUrl(elm) {
        var plpFeatureSwatchImageId = jQuery(elm).attr("id");
        var plpFeatureSwatchImageIdArr = plpFeatureSwatchImageId.split("|");
        var pdpUrlId = plpFeatureSwatchImageIdArr[1]+plpFeatureSwatchImageIdArr[0]; 
        var pdpUrl = document.getElementById(pdpUrlId).value;
        
        var productFeatureType = plpFeatureSwatchImageIdArr[0];
        
        jQuery('#'+plpFeatureSwatchImageIdArr[1]+'_productFeatureType').val(productFeatureType); 
        jQuery(elm).parents('.eCommerceListItem').find('a.pdpUrl').attr("href",pdpUrl);
        jQuery(elm).parents('.eCommerceListItem').find('a.pdpUrl.review').attr("href",pdpUrl+"#productReviews");
    }
});

</script>