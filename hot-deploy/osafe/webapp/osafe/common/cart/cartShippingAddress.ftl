<#if shippingAddress?has_content && ((!isStorePickUp?exists) || isStorePickUp=="N")>
    <div class="${request.getAttribute("attributeClass")!}">
      <div class="displayBox">
           <h3>${uiLabelMap.ShippingAddressHeading}</h3>
           ${setRequestAttribute("PostalAddress", shippingAddress)}
           ${screens.render("component://osafe/widget/CommonScreens.xml#displayPostalAddress")}
      </div> 
    </div>
</#if>