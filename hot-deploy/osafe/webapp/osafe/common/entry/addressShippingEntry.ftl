<#assign useShippingApplies = useShippingApplies! />
<#if !(useShippingApplies?has_content) >
  <#assign useShippingApplies = "Y"/>
</#if>
<#if useShippingApplies != "Y" || (shippingApplies?exists && shippingApplies) >
  <div id="${fieldPurpose?if_exists}_ADDRESS_ENTRY" class="displayBox">
    <#include "component://osafe/webapp/osafe/common/entry/commonAddressEntry.ftl"/>
  </div>
</#if>