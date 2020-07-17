<#if pickupStoreButtonVisible?has_content && pickupStoreButtonVisible =="Y">
 <#else>
   <#if storeContentSpot?exists && storeContentSpot?has_content && storeContentSpot != "null">
		<li class="${request.getAttribute("attributeClass")!}">
		    <div>
 	    		<a href="javascript:setDirections('${storeRow.searchAddress!""}', '${storeRow.latitude!""}, ${storeRow.longitude!""}', 'DRIVING');javascript:hideStoreList('${rowNum}','${storeRow.storeName!}');"><span class="storeDetailIcon"></span></a>
		    </div>
		</li>
   </#if>
</#if>
		