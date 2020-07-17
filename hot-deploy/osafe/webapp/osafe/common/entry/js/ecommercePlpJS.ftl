<script type="text/javascript">
jQuery(document).ready(function () {
  <#if Static["com.osafe.util.Util"].isProductStoreParmTrue(QUICKLOOK_ACTIVE)>
    <#if QUICKLOOK_DELAY_MS?has_content && Static["com.osafe.util.Util"].isNumber(QUICKLOOK_DELAY_MS) && QUICKLOOK_DELAY_MS != "0">
      jQuery("div.eCommerceThumbNailHolder").hover(function(){jQuery(this).find("div.plpQuickLook").fadeIn(${QUICKLOOK_DELAY_MS});},function () {jQuery(this).find("div.plpQuickLook").fadeOut(${QUICKLOOK_DELAY_MS});});
    <#else>
      jQuery("div.eCommerceThumbNailHolder div.plpQuickLook").show();
    </#if>
  </#if>

    jQuery('.facetValue.hideThem').hide();
    jQuery('.seeLessLink').hide();

    jQuery('.plpFeatureSwatchImage').click(function() {
        var swatchVariant = jQuery(this).next('.swatchVariant').clone();
        jQuery(this).parents('.eCommerceListItem').find('.eCommerceThumbNailHolder').find('.swatchProduct').replaceWith(swatchVariant);
        jQuery('.eCommerceThumbNailHolder').find('.swatchVariant').show().attr("class", "swatchProduct");
        jQuery(this).siblings('.plpFeatureSwatchImage').attr("class", "plpFeatureSwatchImage");
        jQuery(this).attr("class","plpFeatureSwatchImage selected");
        makePDPUrl(this);
    });

    jQuery('.seeMoreLink').click(function() {
        jQuery(this).hide().parents('li').siblings('li.hideThem').show();
        jQuery(this).siblings('.seeLessLink').show();
    });

    jQuery('.seeLessLink').click(function() {
        jQuery(this).hide().parents('li').siblings('li.hideThem').hide();
        jQuery(this).siblings('.seeMoreLink').show();
    });
    
    function makePDPUrl(elm) {
        var plpFeatureSwatchImageId = jQuery(elm).attr("id");
        var plpFeatureSwatchImageIdArr = plpFeatureSwatchImageId.split("|");
        var pdpUrl = jQuery('#pdpUrl_'+plpFeatureSwatchImageIdArr[1]).val();
        var productFeatureType = plpFeatureSwatchImageIdArr[0];
        if(pdpUrl.indexOf("?") == -1) {
            pdpUrl = pdpUrl+'?productFeatureType='+productFeatureType;
        } else {
            pdpUrl = pdpUrl+'&productFeatureType='+productFeatureType;
        }
        jQuery('#'+plpFeatureSwatchImageIdArr[1]+'_productFeatureType').val(productFeatureType);
        jQuery(elm).parents('.eCommerceListItem').find('.eCommerceThumbNailHolder').find('.swatchProduct').find('a').attr("href",pdpUrl);
        jQuery('#detailLink_'+plpFeatureSwatchImageIdArr[1]).attr("href",pdpUrl);
    }
});

</script>