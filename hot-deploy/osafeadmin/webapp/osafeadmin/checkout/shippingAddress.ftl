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
            <#if shippingAddress?has_content>
                  ${setRequestAttribute("PostalAddress",shippingAddress)}
                  ${setRequestAttribute("DISPLAY_FORMAT", "SINGLE_LINE_NICKNAME")}
                  ${screens.render("component://osafeadmin/widget/CommonScreens.xml#displayPostalAddress")}
            </#if>
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
                  ${setRequestAttribute("PostalAddress",selectedAddress)}
                  ${screens.render("component://osafeadmin/widget/CommonScreens.xml#displayPostalAddress")}
            </#if>
        </div>
      </div>
    </div>
  </#list>
</#if>
