<script>
function hideStoreList(rowNum,storeName)
{
    jQuery(document).attr('title', storeName);
    
    jQuery('#pesStoreLocator').hide();
    jQuery('#ptsStoreLocator').hide();
    jQuery('#drivingDirectionIcon').hide();
    jQuery('.storeName').hide();
    
    jQuery('.storeNameNoLink').css('display', 'inline');
    jQuery('.storeNameNoLink').show();
    
    var parent = jQuery('#eCommerceMainPanel');
    var title = jQuery(parent).children('h1')[0];
    jQuery(title).text(storeName);
    
    var store = jQuery('.location')[rowNum];

    var storeContentSpot = jQuery(store).next('.hiddenStoreContentSpotValue');
    
    var storeDetail = jQuery(storeContentSpot).next('.storeDetailPageInfo');
    
    var searchFormThenStoreDetail = jQuery('#${searchStoreFormName!"searchForm"}').after(jQuery(storeContentSpot));
    
    jQuery(searchFormThenStoreDetail).after(jQuery(storeDetail));
    
    
    jQuery('#${searchStoreFormName!"searchForm"}').hide();
    
    jQuery('.storeDetailPageInfo').css('display', 'inline');
    jQuery('.storeDetailPageInfo').show();
    
    jQuery('.hiddenStoreContentSpotValue').css('display', 'inline');
    jQuery('.hiddenStoreContentSpotValue').show();
    
    jQuery('.storeDetailBackBtn').css('display', 'inline');
    jQuery('.storeDetailBackBtn').show();

    jQuery('.content-messages').each(function(){
    jQuery(this).hide();
    })
    
    var countRows = 0;
    jQuery('.location').each(function(){
        if(countRows==rowNum)
        {
        }
        else
        {
            jQuery(this).hide();
            if (jQuery(this).next('.hiddenStoreContentSpotValue').length)
            {
                jQuery(this).next('.hiddenStoreContentSpotValue').hide();
                if (jQuery(this).next('.hiddenStoreContentSpotValue').next('.storeDetailPageInfo').length)
                {
                    jQuery(this).next('.hiddenStoreContentSpotValue').next('.storeDetailPageInfo').hide();
                }
            }
            else
            {
                if (jQuery(this).next('.storeDetailPageInfo').length)
                {
                    jQuery(this).next('.storeDetailPageInfo').hide();
                }
            }
        }
        countRows = countRows + 1;
    })

}

</script>
