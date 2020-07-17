<#if storeDetailList?exists && storeDetailList?has_content && ((displayInitialStores?has_content && displayInitialStores == "Y") || userLocation?has_content)>
 <div class="${request.getAttribute("attributeClass")!}">
  <#assign storeRowIndex=0>
  <#assign pickupStoreButtonVisible=pickupStoreButtonVisible!"">
  <#assign rowNum=0>
  <#assign rowClass = "1">
  <div class="boxList storeLocationList">
	  <#list storeDetailList as storeRow>
	    <#assign storeContentSpot = ""/>
	  	<#if storeRow.storeContentSpotContentId?has_content>
	        <#assign storeContentSpot = Static["org.ofbiz.content.content.ContentWorker"].renderContentAsText(dispatcher, delegator, storeRow.storeContentSpotContentId, Static["javolution.util.FastMap"].newInstance(), locale, "", true)/>
	    </#if>
	    
	   <#if (storeRowIndex < gmapNumDisplay)>
	    <#assign storeRowIndex=storeRowIndex + 1>
	    ${setRequestAttribute("storeRowIndex", storeRowIndex)}
	    ${setRequestAttribute("rowNum", rowNum)}
	    ${setRequestAttribute("rowClass", rowClass)}
	    ${setRequestAttribute("storeRow", storeRow)}
	    ${setRequestAttribute("storeContentSpot", storeContentSpot)}
	    ${setRequestAttribute("pickupStoreButtonVisible", pickupStoreButtonVisible)}
	    ${setRequestAttribute("userLocation", userLocation)}
	    ${screens.render("component://osafe/widget/EcommerceDivScreens.xml#storeLocatorItemsDivSequence")}
	    <#if storeContentSpot?exists && storeContentSpot?has_content && storeContentSpot != "null">
	           <div class="hiddenStoreContentSpotValue" style="display: none;"  >
	             ${Static["com.osafe.util.Util"].getFormattedText(storeContentSpot)}
	           </div>
	    </#if>
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
        <#if rowClass == "2">
            <#assign rowClass = "1">
        <#else>
            <#assign rowClass = "2">
        </#if>
	  <#assign rowNum= rowNum + 1>
	 </#if>
	 </#list>
     <div class="storeDetailBackBtn" style="display: none;">
        <a class="standardBtn negative" href="<@ofbizUrl>searchStoreLocator?address=${searchedAddress!}</@ofbizUrl>">${uiLabelMap.CommonBack}</a>
     </div>
  </div>
 </div>
</#if>