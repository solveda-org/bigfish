<#if billingContactMechList?has_content>
  <#assign chosenAddress =""/>
  <#assign billingAddress=""/>
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.NameCaption}</label>
      </div>
      <div class="entry checkbox medium">
        <#list billingContactMechList as billingContactMech>
          <#assign billingAddress = billingContactMech.getRelatedOne("PostalAddress")>
          <#assign selectBillingContactMechId= parameters.BILLING_SELECT_ADDRESS!""/>
          <#assign checkThisAddress = (billingContactMech_index == 0 && !selectBillingContactMechId?has_content) || (selectBillingContactMechId?default("") == billingAddress.contactMechId)/>
          <#if checkThisAddress>
            <#assign chosenAddress = billingAddress/>
          </#if>
          <div>
              <input class="checkBoxEntry" type="radio" id="BILLING_SELECT_ADDRESS" name="BILLING_SELECT_ADDRESS" value="${billingAddress.contactMechId!}" <#if checkThisAddress> checked</#if> onchange="javascript:showPostalAddress('billing_${billingAddress.contactMechId!}','selectedBillingAddress');"/>
              <#if billingAddress?has_content>
                  ${setRequestAttribute("PostalAddress",billingAddress)}
                  ${setRequestAttribute("DISPLAY_FORMAT", "SINGLE_LINE_NICKNAME")}
                  ${screens.render("component://osafeadmin/widget/CommonScreens.xml#displayPostalAddress")}
              </#if>
           </div>
        </#list>
      </div>
    </div>
  </div>

  <#list billingContactMechList as billingContactMech>
    <#assign selectedAddress = billingContactMech.getRelatedOne("PostalAddress") />
    <div id="billing_${selectedAddress.contactMechId!}" class="infoRow selectedBillingAddress" <#if chosenAddress?has_content && chosenAddress.contactMechId?has_content && !(selectedAddress.contactMechId == chosenAddress.contactMechId)> style="display:none" </#if>>
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
