  <#if orderItemShipGroups?has_content>
   <div class="checkoutOrderShippingAddress">
    <#-- shipping address -->
    <#assign groupIdx = 0>
    <#list orderItemShipGroups as shipGroup>
      <#if orderHeader?has_content>
        <#assign shippingAddress = shipGroup.getRelatedOne("PostalAddress")?if_exists>
        <#assign groupNumber = shipGroup.shipGroupSeqId?if_exists>
      <#else>
        <#assign shippingAddress = cart.getShippingAddress(groupIdx)?if_exists>
        <#assign groupNumber = groupIdx + 1>
      </#if>
        <#if shippingAddress?has_content>
                   <div id="shippingAddressDisplay" class="displayBox">
                   <div class="displayBoxHeader">
                       <span class="displayBoxHeaderCaption">${uiLabelMap.ShippingAddressHeading}</span>
                    </div>
                           <div class="address">

                             <#if shippingAddress.toName?has_content><p>${shippingAddress.toName}</p></#if>
                             <#if shippingAddress.address1?has_content><p>${shippingAddress.address1}</p></#if>
                             <#if shippingAddress.address2?has_content><p>${shippingAddress.address2}</p></#if>
                             <p>
                             <#-- city and state have to stay on one line otherwise an extra space is added before the comma -->
                             <#if shippingAddress.city?has_content>${shippingAddress.city!}</#if><#if shippingAddress.stateProvinceGeoId?has_content>, ${shippingAddress.stateProvinceGeoId}</#if>
                             <#if shippingAddress.postalCode?has_content> ${shippingAddress.postalCode}</#if></p>
                             <#if shippingAddress.countryGeoId?has_content><p>${shippingAddress.countryGeoId}</p></#if>
                             <#if shippingContactMechPhoneMap?has_content>

                               <#assign shippingPhoneCont = shippingContactMechPhoneMap[(shippingAddress.contactMechId)!]!"" />
                               <#if shippingPhoneCont?has_content>
                                 <#assign shippingPhone = shippingPhoneCont["PHONE_HOME"]!""/>
                               </#if>  
                             </#if>
                             <#if shippingPhone?has_content>
                             <p>
                                    <#assign formattedPhone = Static["com.osafe.util.Util"].formatTelephone(shippingPhone.areaCode, shippingPhone.contactNumber)/>
                                    ${formattedPhone}
                            </p>
                            </#if>
                           </div>
                       </div>
        </#if>
      <#assign groupIdx = groupIdx + 1>
    </#list><#-- end list of orderItemShipGroups -->
   </div>
  </#if>
