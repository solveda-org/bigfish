<!-- start customerDetailAddressInfo.ftl -->
<table class="osafe">
  <tr class="heading">
    <th class="nameCol">${uiLabelMap.NicknameLabel}</th>
    <th class="descCol">${uiLabelMap.AddressLabel}</th>
  </tr>
  <#if resultList?exists && resultList?has_content>
    <#assign rowClass = "1"/>
    <#list resultList as shippingContactMech>
      <#assign postalAddress = shippingContactMech.getRelatedOne("PostalAddress")?if_exists>
      <tr class="dataRow <#if rowClass?if_exists == "2">even<#else>odd</#if>">
        <td class="nameCol <#if !note_has_next?if_exists>lastRow</#if>">
          <a href="<@ofbizUrl>${customerAddressDetailAction!}?contactMechId=${postalAddress.contactMechId?if_exists}&partyId=${parameters.partyId!}</@ofbizUrl>">${postalAddress?if_exists.attnName?default((postalAddress?if_exists.address1)?if_exists)}</a>
        </td>
        <td class="descCol <#if !note_has_next?if_exists>lastRow</#if>">
          ${setRequestAttribute("PostalAddress",postalAddress)}
          ${setRequestAttribute("DISPLAY_FORMAT", "SINGLE_LINE_FULL_ADDRESS")}
          ${screens.render("component://osafeadmin/widget/CommonScreens.xml#displayPostalAddress")}
        </td>
      </tr>
      <#if rowClass == "2">
        <#assign rowClass = "1">
      <#else>
        <#assign rowClass = "2">
      </#if>
    </#list>
  <#else>
        ${screens.render("component://osafeadmin/widget/CommonScreens.xml#ListNoDataResult")}
  </#if>
</table>
<!-- end customerDetailAddressInfo.ftl -->