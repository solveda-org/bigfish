<!-- seelct any address -->
<#if showAddressSelection?has_content && showAddressSelection == "Y">
    <#if fieldPurpose?has_content && context.get(fieldPurpose+"ContactMechList")?has_content>
      <#assign contactMechList = context.get(fieldPurpose+"ContactMechList") />
    </#if>
    <#if contactMechList?has_content>
        <div class="entry">
          <label for="${fieldPurpose?if_exists}_ADDRESSES">${uiLabelMap.SelectAddressCaption}</label>
          <#assign  selectedAddress = parameters.get(fieldPurpose+"_SELECT_ADDRESS")!postalAddressContactMechId!""/>
          <#list contactMechList as contactMech>
              <#if contactMech.contactMechTypeId?if_exists = "POSTAL_ADDRESS">
                  <#assign postalAddress=contactMech.getRelatedOneCache("PostalAddress")!"">
                  <#if postalAddress?has_content>
                      <input type="radio" class="${fieldPurpose?if_exists}_SELECT_ADDRESS" name="${fieldPurpose?if_exists}_SELECT_ADDRESS" value="${postalAddress.contactMechId!}" onchange="javascript:getPostalAddress('${postalAddress.contactMechId!}', '${fieldPurpose?if_exists}');"<#if selectedAddress == postalAddress.contactMechId >checked="checked"</#if>/>
                      <span class="radioOptionText"><#if postalAddress.attnName?has_content>${postalAddress.attnName!}<#else>${postalAddress.address1!}</#if></span>
                  </#if>
              </#if>
          </#list>
          <a href="javascript:submitCheckoutForm(document.${formName!}, 'NA', '${fieldPurpose?if_exists}_LOCATION');" class="standardBtn action">${uiLabelMap.AddAddressBtn}</a>
        </div>
    </#if>
</#if>