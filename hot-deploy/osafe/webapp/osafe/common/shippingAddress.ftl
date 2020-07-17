  <#if orderItemShipGroups?has_content>
   <div class="checkoutOrderShippingAddress">
    <#-- shipping address -->
    <#assign groupIdx = 0>
    <#list orderItemShipGroups as shipGroup>
      <#if orderHeader?has_content>
        <#assign shippingAddress = shipGroup.getRelatedOneCache("PostalAddress")?if_exists>
        <#assign groupNumber = shipGroup.shipGroupSeqId?if_exists>
      <#else>
        <#assign shippingAddress = cart.getShippingAddress(groupIdx)?if_exists>
        <#assign groupNumber = groupIdx + 1>
      </#if>
        <#if shippingAddress?has_content>
                   <div class="displayBox">
                       <h3>${uiLabelMap.ShippingAddressHeading}</h3>
                           <div class="address">

                             <#if shippingAddress.toName?has_content><p>${shippingAddress.toName}</p></#if>
                             <#if shippingAddress.address1?has_content><p>${shippingAddress.address1}</p></#if>
                             <#if shippingAddress.address2?has_content><p>${shippingAddress.address2}</p></#if>
                             <p>
                             <#-- city and state have to stay on one line otherwise an extra space is added before the comma -->
                             <#if shippingAddress.city?has_content  && shippingAddress.city != '_NA_'>${shippingAddress.city!}</#if><#if shippingAddress.stateProvinceGeoId?has_content && shippingAddress.stateProvinceGeoId != '_NA_'>, ${shippingAddress.stateProvinceGeoId}</#if>
                             <#if shippingAddress.postalCode?has_content && shippingAddress.postalCode != '_NA_'> ${shippingAddress.postalCode}</#if></p>
                             <#if shippingAddress.countryGeoId?has_content><p>${shippingAddress.countryGeoId}</p></#if>

                           </div>
                       </div>
        </#if>
      <#assign groupIdx = groupIdx + 1>
    </#list><#-- end list of orderItemShipGroups -->
   </div>
  </#if>
