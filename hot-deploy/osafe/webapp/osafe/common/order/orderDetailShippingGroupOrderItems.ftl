<div class="${request.getAttribute("attributeClass")!}">
   <#assign shipGroup= request.getAttribute("shipGroup")!/>
   <#if shipGroup?has_content>
       <#assign orderItemShipGroupAssoc =shipGroup.getRelated("OrderItemShipGroupAssoc")!""/>
       <#if orderItemShipGroupAssoc?has_content>
            <#assign rowClass = "1">
            <#assign lineIndex=0?number/>
            <#assign rowClass = "1">
           <#list orderItemShipGroupAssoc as shipGroupAssoc>
              <#assign orderItems =shipGroupAssoc.getRelated("OrderItem")!""/>
              <#if (orderItems?has_content && orderItems.size() > 0)>
                    <#list orderItems as orderItem>
                        ${setRequestAttribute("shipGroupAssoc", shipGroupAssoc)}
                        ${setRequestAttribute("orderItem", orderItem)}
                        ${setRequestAttribute("lineIndex", lineIndex)}
                        ${setRequestAttribute("rowClass", rowClass)}
                        ${screens.render("component://osafe/widget/EcommerceDivScreens.xml#orderDetailShippingGroupOrderItemsDivSequence")}
                        <#if rowClass == "2">
                            <#assign rowClass = "1">
                        <#else>
                            <#assign rowClass = "2">
                        </#if>
                        <#assign lineIndex= lineIndex + 1/>
                    </#list>
              </#if>
           </#list>
       </#if>
   </#if>
</div>

