<div class="${request.getAttribute("attributeClass")!}">
    <#if orderItemShipGroups?exists && orderItemShipGroups?has_content>
       <#assign groupIndex=1?number/>
         <div class="displayBox">
           <#list orderItemShipGroups as cartShipInfo>
              <#if (orderItemShipGroups?size > 1) >
                <h4>${uiLabelMap.ShippingGroupHeading} ${groupIndex} of ${orderItemShipGroups.size()}</h4>
              <#else>
                <h4>${uiLabelMap.OrderDetailsHeading}</h3>
              </#if>
              ${setRequestAttribute("shipGroup", cartShipInfo)}
              ${setRequestAttribute("orderHeader", orderHeader)}
              ${setRequestAttribute("localOrderReadHelper", localOrderReadHelper)}
              ${screens.render("component://osafe/widget/EcommerceDivScreens.xml#orderDetailShippingGroupDivSequence")}
             <#assign groupIndex= groupIndex + 1/>
           </#list>
         </div>
    </#if>
</div>
