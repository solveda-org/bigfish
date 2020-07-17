<div class="${request.getAttribute("attributeClass")!}">
  <#if orderItemShipGroups?has_content>
    <#-- shipping address -->
    <#assign groupIdx = 0>
    <#list orderItemShipGroups as shipGroup>
        <#assign shippingAddress = shipGroup.getRelatedOneCache("PostalAddress")?if_exists>
        <#assign groupNumber = shipGroup.shipGroupSeqId?if_exists>
        <#if shippingAddress?has_content>
          <div class="displayBox">
               <h3>${uiLabelMap.ShippingAddressHeading}</h3>
               ${setRequestAttribute("PostalAddress", shippingAddress)}
               ${screens.render("component://osafe/widget/CommonScreens.xml#displayPostalAddress")}
          </div> 
        </#if>
      <#assign groupIdx = groupIdx + 1>
    </#list><#-- end list of orderItemShipGroups -->
  </#if>
</div>  
