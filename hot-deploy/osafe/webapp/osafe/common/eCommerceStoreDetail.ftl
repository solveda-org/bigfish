<#if storeInfo?has_content && shoppingCartStoreId?exists && shoppingCartStoreId?has_content>
<div class="checkoutOrderStorePickup">
  <div class="displayBox">
      <h3>${uiLabelMap.StorePickupHeading} </h3>
      <input type="hidden" name="shipping_method" class="js_shipping_method" value="${parameters.shipMethod!"NO_SHIPPING@_NA_"}">
      <input type="hidden" name="shipMethod" value="${parameters.shipMethod!"NO_SHIPPING@_NA_"}">
      <input type="hidden" id="js_storeId" name="storeId" value="">
      <ul class="displayDetail">
       <li>
        <div>
         <label>${uiLabelMap.StoreCodeCaption}</label>
         <span>${storeInfo.groupName?if_exists} (${storeInfo.groupNameLocal?if_exists})</span>
         <#assign cart = session.getAttribute("shoppingCart")/>
         <#if (!oneStoreOpenStoreId?exists || !oneStoreOpenStoreId?has_content) && (!showChangeStoreOption?exists || !showChangeStoreOption?has_content)>
          <#if cart?has_content && cart.getOrderAttribute("STORE_LOCATION")?has_content>
           <span class="StorePickupBtn"><a href="javaScript:void(0);" onClick="displayActionDialogBox('${dialogPurpose!}',this);" class="standardBtn positive"><span>${uiLabelMap.ChangeStoreBtn!}</span></a></span>
          </#if>
         </#if>
        </div>
       </li>
       <li>
        <div>
         <label>${uiLabelMap.StoreAddressCaption}</label>
              ${setRequestAttribute("PostalAddress", storeAddress)}
              ${screens.render("component://osafe/widget/CommonScreens.xml#displayPostalAddress")}
        </div>
       </li>
       <li>
        <div>
         <label>${uiLabelMap.StoreTelCaption}</label>
         <span>
         <#if storePhone?has_content>
           <#if storePhone.contactNumber?has_content>
            <#if storePhone.contactNumber?length &gt; 6>
              <#assign contactPhoneNumber = storePhone.contactNumber!"">
              <#assign areaCode = storePhone.areaCode!"">
              <#assign fullPhone= Static["com.osafe.util.Util"].formatTelephone(areaCode, contactPhoneNumber?if_exists, FORMAT_TELEPHONE_NO!)/>
               ${fullPhone!""}
            <#else>
             <#if storePhone.areaCode?has_content>${storePhone.areaCode?if_exists}-</#if>
               ${storePhone.contactNumber?if_exists}
            </#if>
           </#if>
         </#if>
         </span>
        </div>
       </li>
       <li>
        <div>
         <span>${uiLabelMap.PickupStoreInfo}</span>
        </div>
       </li>
      </ul>
  </div>
</div>
</#if>