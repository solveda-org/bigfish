<#if storeDetailList?exists && storeDetailList?has_content && ((displayInitialStores?has_content && displayInitialStores == "Y") || userLocation?has_content)>
  <#assign storeRowIndex=0>
  <#assign rowNum=0>
  <#list storeDetailList as storeRow>
    <#assign storeContentSpot = ""/>
  	<#if storeRow.storeContentSpotContentId?has_content>
        <#assign storeContentSpot = Static["org.ofbiz.content.content.ContentWorker"].renderContentAsText(dispatcher, delegator, storeRow.storeContentSpotContentId, Static["javolution.util.FastMap"].newInstance(), locale, "", true)/>
    </#if>
   <#if (storeRowIndex < gmapNumDisplay)>
    <#assign storeRowIndex=storeRowIndex + 1>
    <div class="store">
      <div class="storeDist">
        <#if pickupStoreButtonVisible?has_content && pickupStoreButtonVisible =="Y">
          <form method="post" action="<@ofbizUrl>${storePickupFormAction!""}</@ofbizUrl>" class="pickupStore">
            <input type="hidden" name="storeId" value="${storeRow.partyId!}">
            <input type="submit" value="${uiLabelMap.SelectForPickupBtn}" class="standardBtn positive">
          </form>
        </#if>
        <#if userLocation?exists && userLocation?has_content>
          <P class="distance">${storeRow.distance!""}</P>
        </#if>
      </div>
      <div class="storeDetail">
        <ul>
        	<#if pickupStoreButtonVisible?has_content && pickupStoreButtonVisible =="Y">
        		<li class="storeName">${storeRow.storeName!""} (${storeRow.storeCode!""})</li>
	  	    <#else>
	  	    	<#if storeContentSpot?exists && storeContentSpot?has_content && storeContentSpot != "null">
	  	    		<li class="storeName"><a href="javascript:setDirections('${storeRow.searchAddress!""}', '${storeRow.latitude!""}, ${storeRow.longitude!""}', 'DRIVING');javascript:hideStoreList('${rowNum}', '${storeRow.storeName!}');">${storeRow.storeName!""} (${storeRow.storeCode!""})</a></li>
	  	    	<#else>
	  	    		<li class="storeName">${storeRow.storeName!""} (${storeRow.storeCode!""})</li>
	  	    	</#if>
	  	    </#if>
	  	    <li class="storeAddress">
            <#if storeRow.address1?has_content>
              <span class="storeAdd1">${storeRow.address1!""},</span>
            </#if>
            <#if storeRow.address2?has_content>
              <span class="storeAdd2">${storeRow.address2!""},</span>
            </#if>
            <#if storeRow.address3?has_content>
              <span class="storeAdd3">${storeRow.address3!""},</span>
            </#if>
            <#if storeRow.city?has_content>
              <span class="storeCity">${storeRow.city!""},</span>
            </#if>
            <#if storeRow.stateProvinceGeoId?has_content>
              <span class="storeState">${storeRow.stateProvinceGeoId!""}</span>
            </#if>
            <#if storeRow.postalCode?has_content>
              <span class="storeZip">${storeRow.postalCode!""}</span>
            </#if>
          </li>
          <#if storeRow.openingHoursContentId?has_content>
            <#assign openingHours = Static["org.ofbiz.content.content.ContentWorker"].renderContentAsText(dispatcher, delegator, storeRow.openingHoursContentId, Static["javolution.util.FastMap"].newInstance(), locale, "", true)/>
            <#if openingHours?has_content && openingHours != "null">
               <li class="storehours">
                 <div class="label">${uiLabelMap.StoreLocatorHourCaption}</div>
                 <div class="value" >
                 ${Static["com.osafe.util.Util"].getFormattedText(openingHours)}
                 </div>
               </li>
            </#if>
          </#if>
          <#if storeRow.storeNoticeContentId?has_content>
            <#assign storeNotice = Static["org.ofbiz.content.content.ContentWorker"].renderContentAsText(dispatcher, delegator, storeRow.storeNoticeContentId, Static["javolution.util.FastMap"].newInstance(), locale, "", true)/>
            <#if storeNotice?has_content && storeNotice != "null">
               <li class="storeNotice">
                 <div class="label">${uiLabelMap.StoreLocatorNoticeCaption}</div>
                 <div class="value" >
                 ${Static["com.osafe.util.Util"].getFormattedText(storeNotice)}
                 </div>
               </li>
            </#if>
          </#if>
          <li class="storephone">
            <div class="label">${uiLabelMap.StoreLocatorPhoneCaption}</div>
            <#if storeRow.countryGeoId?has_content && (storeRow.countryGeoId == "USA" || storeRow.countryGeoId == "CAN")>
              <div class="value">${storeRow.areaCode!""} - ${storeRow.contactNumber3!""} - ${storeRow.contactNumber4!""}</div>
            <#else>
              <div class="value">${storeRow.contactNumber!""}</div>
            </#if>
          </li>
        </ul>
 
        <div class="storeDetailPageInfo" style="display: none;" >
      	
        		<div class="entryRow">
			      <div class="entry">
			         <div class="entryLabel">
			            <label>${uiLabelMap.StoreNameCaption}</label>
			         </div>
			         <div class="entryValue">
			            <span><p>${storeRow.storeName!""} (${storeRow.storeCode!""})</p></span>
			         </div>
			      </div>
			    </div>
			    
			    
			    <div class="entryRow">
			      <div class="entry">
			         <div class="entryLabel">
			            <label>${uiLabelMap.AddressCaption}</label>
			         </div>
			         <div class="entryValue">
			            <span>
			            	<ul>
			            		<li><p><#if storeRow.address1?has_content>${storeRow.address1!""}, </#if><#if storeRow.address2?has_content>${storeRow.address2!""}, </#if><#if storeRow.address3?has_content>${storeRow.address3!""},</#if></p></li>
			            		<li><p>${storeRow.city!""}, ${storeRow.stateProvinceGeoId!""} ${storeRow.postalCode!""}</p></li>
			            	</ul>
			            </span>
			         </div>
			      </div>
			    </div>
			    
			    <#if openingHours?has_content && openingHours != "null">
				    <div class="entryRow">
				      <div class="entry">
				         <div class="entryLabel">
				            <label>${uiLabelMap.StoreLocatorHourCaption}</label>
				         </div>
				         <div class="entryValue">
				            <span><p>${Static["com.osafe.util.Util"].getFormattedText(openingHours)}</p></span>
				         </div>
				      </div>
				    </div>
			    </#if>
			    
			    <#if storeNotice?has_content && storeNotice != "null">
				    <div class="entryRow">
				      <div class="entry">
				         <div class="entryLabel">
				            <label>${uiLabelMap.StoreNoticesCaption}</label>
				         </div>
				         <div class="entryValue">
				            <span><p>${Static["com.osafe.util.Util"].getFormattedText(storeNotice)}</p></span>
				         </div>
				      </div>
				    </div>
			    </#if>
			    
			    
			    <div class="entryRow">
			      <div class="entry">
			        <div class="entryLabel">
			          <label>${uiLabelMap.StoreLocatorPhoneCaption}</label>
			        </div>
			        <div class="entryValue">
			          <span>
			            <#if storeRow.countryGeoId?has_content && (storeRow.countryGeoId == "USA" || storeRow.countryGeoId == "CAN")>
			              <div class="value"><p>${storeRow.areaCode!""} - ${storeRow.contactNumber3!""} - ${storeRow.contactNumber4!""}</p></div>
			            <#else>
			              <div class="value"><p>${storeRow.contactNumber!""}</p></div>
			            </#if>
			          </span>
			        </div>
			      </div>
			    </div>
	
        </div>

        <#if storeContentSpot?exists && storeContentSpot?has_content && storeContentSpot != "null">
               <div class="hiddenStoreContentSpotValue" style="display: none;"  >
                 ${Static["com.osafe.util.Util"].getFormattedText(storeContentSpot)}
               </div>
        </#if>
      </div>
      <div class="storeDirectionIcon">
        <#if userLocation?exists && userLocation?has_content>
        	<#if pickupStoreButtonVisible?has_content && pickupStoreButtonVisible =="Y">
	  	    <#else>
	  	    	<#if storeContentSpot?exists && storeContentSpot?has_content && storeContentSpot != "null">
	  	    		<a href="javascript:setDirections('${storeRow.searchAddress!""}', '${storeRow.latitude!""}, ${storeRow.longitude!""}', 'DRIVING');javascript:hideStoreList('${rowNum}','${storeRow.storeName!}');"><span class="storeDetailIcon"></span></a>
	  	    	</#if>
	  	    </#if>
          <a href="javascript:setDirections('${storeRow.searchAddress!""}', '${storeRow.latitude!""}, ${storeRow.longitude!""}', 'DRIVING');"><span class="drivingDirectionIcon"></span></a>
          <a href="javascript:setDirections('${storeRow.searchAddress!""}', '${storeRow.latitude!""}, ${storeRow.longitude!""}', 'WALKING');"><span class="walkingDirectionIcon"></span></a>
          <a href="javascript:setDirections('${storeRow.searchAddress!""}', '${storeRow.latitude!""}, ${storeRow.longitude!""}', 'BICYCLING');"><span class="bicyclingDirectionIcon"></span></a>
        </#if>
      </div>
  </div>
  <#assign rowNum= rowNum + 1>
 </#if>
</#list>
<div class="storeDetailBackBtn" style="display: none;">
      <a class="standardBtn negative" href="<@ofbizUrl>searchStoreLocator?address=${searchedAddress!}</@ofbizUrl>">${uiLabelMap.CommonBack}</a>
</div>
</#if>