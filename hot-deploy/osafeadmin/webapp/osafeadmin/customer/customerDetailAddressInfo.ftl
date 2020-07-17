<!-- start customerDetailAddressInfo.ftl -->
<table class="osafe">
  <tr class="heading">
    <th class="nameCol">${uiLabelMap.NameLabel}</th>
    <th class="descCol">${uiLabelMap.AddressLabel}</th>
  </tr>
  <#if shippingContactMechList?exists && shippingContactMechList?has_content>
    <#assign rowClass = "1"/>
    <#list shippingContactMechList as shippingContactMech>
      <#assign postalAddress = shippingContactMech.getRelatedOne("PostalAddress")?if_exists>
      <tr class="dataRow <#if rowClass?if_exists == "2">even<#else>odd</#if>">
        <td class="nameCol <#if !note_has_next?if_exists>lastRow</#if>">
          <a href="<@ofbizUrl>${customerAddressDetailAction!}?contactMechId=${postalAddress.contactMechId?if_exists}&partyId=${parameters.partyId!}</@ofbizUrl>">${postalAddress?if_exists.attnName?default((postalAddress?if_exists.address1)?if_exists)}</a>
        </td>
        <td class="descCol <#if !note_has_next?if_exists>lastRow</#if>">
          <#if postalAddress?has_content>
            <#if postalAddress.address1?has_content>${postalAddress.address1},</#if>
            <#if postalAddress.address2?has_content> ${postalAddress.address2},</#if>
            <#if postalAddress.address3?has_content> ${postalAddress.address3},</#if>
            <#-- city and state have to stay on one line otherwise an extra space is added before the comma -->
            <#if postalAddress.city?has_content && postalAddress.city != '_NA_'> ${postalAddress.city},</#if>
            <#if postalAddress.stateProvinceGeoId?has_content && postalAddress.stateProvinceGeoId != '_NA_'> ${postalAddress.stateProvinceGeoId}</#if>
            <#if postalAddress.postalCode?has_content && postalAddress.postalCode != '_NA_' > ${postalAddress.postalCode}</#if>
            <#if postalAddress.countryGeoId?has_content> ${postalAddress.countryGeoId}</#if>
          </#if>
        </td>
      </tr>
      <#if rowClass == "2">
        <#assign rowClass = "1">
      <#else>
        <#assign rowClass = "2">
      </#if>
    </#list>
  <#else>
    <tr><td class="boxNumber" colspan="2">${uiLabelMap.NoDataAvailableInfo}</td></tr>
  </#if>
</table>
<!-- end customerDetailAddressInfo.ftl -->