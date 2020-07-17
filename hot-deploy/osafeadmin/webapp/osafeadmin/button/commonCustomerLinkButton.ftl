<div class="linkButton">
  <#if !showCustomerActivityLink?has_content>
      <#assign showCustomerActivityLink = "true"/>
  </#if>
  
  <#if showCustomerActivityLink == 'true'>
    <#assign userLogins = party.getRelated("UserLogin")>
    <#if userLogins?has_content>
      <#assign userLoginId = userLogins.get(0).userLoginId>
    </#if>
    <#if userLoginId?has_content>
      <a href="<@ofbizUrl>customerActivityDetail?partyId=${parameters.partyId?if_exists}</@ofbizUrl>" onMouseover="showTooltip(event,'${uiLabelMap.CustomerWebsiteActivityTooltip}');" onMouseout="hideTooltip()"><span class="customerActivityIcon"></span></a>
    </#if>
  </#if>
</div>
