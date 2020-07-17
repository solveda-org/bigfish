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
	
	var store = jQuery('.store')[rowNum];
	var storeDetailFromList = jQuery(store).children('.storeDetail')[0];

	var storeContentSpot = jQuery(storeDetailFromList).children('.hiddenStoreContentSpotValue')[0];
	
	var storeDetail = jQuery(storeDetailFromList).children('.storeDetailPageInfo')[0];
	
	var searchFormThenStoreDetail = jQuery('#${searchStoreFormName!"searchForm"}').after(jQuery(storeContentSpot));
	
	jQuery(searchFormThenStoreDetail).after(jQuery(storeDetail));
	
	
	jQuery('#${searchStoreFormName!"searchForm"}').hide();
	
	jQuery('.storeDetailPageInfo').css('display', 'inline');
	jQuery('.storeDetailPageInfo').show();
	
	jQuery('.hiddenStoreContentSpotValue').css('display', 'inline');
	jQuery('.hiddenStoreContentSpotValue').show();
	
	jQuery('.storeDetailBackBtn').css('display', 'inline');
	jQuery('.storeDetailBackBtn').show();
	
	jQuery('.storeDetail').hide();
	
	var countRows = 0;
	jQuery('.store').each(function(){
		if(countRows==rowNum)
		{
		}
		else
		{
			jQuery(this).hide();
		}
		countRows = countRows + 1;
	})

}

</script>
