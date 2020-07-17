<li class="${request.getAttribute("attributeClass")!}">
    <div>
      <span>
    	<#if pickupStoreButtonVisible?has_content && pickupStoreButtonVisible =="Y">
    		${storeRow.storeName!""} (${storeRow.storeCode!""})
  	    <#else>
  	    	<#if storeContentSpot?exists && storeContentSpot?has_content && storeContentSpot != "null">
  	    		<a href="javascript:setDirections('${storeRow.searchAddress!""}', '${storeRow.latitude!""}, ${storeRow.longitude!""}', 'DRIVING');javascript:hideStoreList('${rowNum}', '${storeRow.storeName!}');"><span>${storeRow.storeName!""} (${storeRow.storeCode!""})</span></a>
  	    	<#else>
  	    		${storeRow.storeName!""} (${storeRow.storeCode!""})
  	    	</#if>
  	    </#if>
      </span>
    </div>
</li>