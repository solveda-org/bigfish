<#assign cart = shoppingCart?if_exists />
<#if cart.getOrderAttribute("STORE_LOCATION")?has_content>
<div class="checkoutOrderStorePickup">
	<div id="customerStorePickup">
	<div class="displayBox storePickup">
	    <div class="displayBoxHeader">
	        <span class="displayBoxHeaderCaption">${uiLabelMap.StorePickupHeading} </span>
	    </div>
	    <input type="hidden" name="checkoutpage" value="shippingoptions"/>
	    <input type="hidden" name="shipping_method" value="${parameters.shipMethod!"NO_SHIPPING@_NA_"}">
	    <div class="entryRow">
	      <div class="entry">
	         <div class="entryLabel">
	            <label>${uiLabelMap.StoreCodeCaption}</label>
	         </div>
	         <div class="entryValue">
	            <span>${storeInfo.groupName?if_exists} (${storeInfo.groupNameLocal?if_exists})</span>
	         </div>
	      </div>
	    </div>
	    <div class="entryRow">
	      <div class="entry">
	         <div class="entryLabel">
	            <label>${uiLabelMap.StoreAddressCaption}</label>
	         </div>
	         <div class="entryValue">
	            <span>
	              ${storeAddress.address1?if_exists} ${storeAddress.address2?if_exists} ${storeAddress.address3?if_exists}<br/>
	              <#if storeAddress.city?has_content>${storeAddress.city!""},</#if>
	              <#if storeAddress.stateProvinceGeoId?has_content>${storeAddress.stateProvinceGeoId!""}</#if>
	              <#if storeAddress.postalCode?has_content>${storeAddress.postalCode!""}</#if>
	            </span>
	         </div>
	      </div>
	    </div>
	    <div class="entryRow">
	      <div class="entry">
	        <div class="entryLabel">
	          <label>${uiLabelMap.StoreTelCaption}</label>
	        </div>
	        <div class="entryValue">
	          <span>
	            <#if storePhone?has_content>
	              <#if storePhone.areaCode?has_content>${storePhone.areaCode?if_exists}-</#if>
	              <#if storePhone.contactNumber?has_content>
	                <#if storePhone.contactNumber?length &gt; 6>
	                  ${storePhone.contactNumber.substring(0, 3)}-${storePhone.contactNumber.substring(3, 7)}
	                <#else>
	                  ${storePhone.contactNumber?if_exists}
	                </#if>
	              </#if>
	            </#if>
	          </span>
	        </div>
	      </div>
	    </div>
	    <p>${uiLabelMap.PickupStoreInfo}</p>
	</div>
	</div>
 </div>
</#if>