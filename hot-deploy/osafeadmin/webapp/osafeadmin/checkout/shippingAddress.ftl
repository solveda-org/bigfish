<#if shippingContactMechList?has_content>
  <#assign chosenAddress =""/>
  <#assign shippingAddress=""/>
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.NameCaption}</label>
      </div>
      <div class="entry checkbox medium">
        <#list shippingContactMechList as shippingContactMech>
          <#assign shippingAddress = shippingContactMech.getRelatedOne("PostalAddress")>
          <#assign selectShippingContactMechId= parameters.SHIPPING_SELECT_ADDRESS!""/>
          <#assign checkThisAddress = (shippingContactMech_index == 0 && !selectShippingContactMechId?has_content) || (selectShippingContactMechId?default("") == shippingAddress.contactMechId)/>
          <#if checkThisAddress>
            <#assign chosenAddress = shippingAddress/>
          </#if>
          <input class="checkBoxEntry" type="radio" id="SHIPPING_SELECT_ADDRESS" name="SHIPPING_SELECT_ADDRESS" value="${shippingAddress.contactMechId!}" <#if checkThisAddress> checked</#if> onchange="javascript:showPostalAddress('shipping_${shippingAddress.contactMechId!}','selectedShippingAddress');"/>
          ${shippingAddress.attnName?default((shippingAddress.address1)?if_exists)}
        </#list>
      </div>
    </div>
  </div>

  <#list shippingContactMechList as shippingContactMech>
    <#assign selectedAddress = shippingContactMech.getRelatedOne("PostalAddress")>
    <div id="shipping_${selectedAddress.contactMechId!}" class="infoRow selectedShippingAddress" <#if chosenAddress?has_content && chosenAddress.contactMechId?has_content && !(selectedAddress.contactMechId == chosenAddress.contactMechId)> style="display:none" </#if>>
      <div class="infoEntry">
        <div class="infoCaption">
          <label>${uiLabelMap.AddressCaption}</label>
        </div>
        <div class="infoValue address">
          <#if selectedAddress?has_content>
            <p>${selectedAddress.toName?if_exists}</p>
          </#if>
          <#if selectedAddress.address1?has_content>
            <p>${selectedAddress.address1}</p>
          </#if>
          <#if selectedAddress.address2?has_content>
            <p>${selectedAddress.address2}</p>
          </#if>
          <#if selectedAddress.address3?has_content>
            <p>${selectedAddress.address3}</p>
          </#if>
          <p>
            <#-- city and state have to stay on one line otherwise an extra space is added before the comma -->
            <#if selectedAddress.city?has_content && selectedAddress.city != '_NA_'>${selectedAddress.city}</#if><#if selectedAddress.stateProvinceGeoId?has_content && selectedAddress.stateProvinceGeoId != '_NA_'>, ${selectedAddress.stateProvinceGeoId}</#if>
            <#if selectedAddress.postalCode?has_content && selectedAddress.postalCode != '_NA_' > ${selectedAddress.postalCode}</#if>
          </p>
          <#if selectedAddress.countryGeoId?has_content>
            <p>${selectedAddress.countryGeoId}</p>
          </#if>
        </div>
      </div>
    </div>
  </#list>
</#if>
