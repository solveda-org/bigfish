<li class="${request.getAttribute("attributeClass")!}">
    <div>
      <span>
          <#if storeRow.storeAddress?has_content>
              ${setRequestAttribute("PostalAddress", storeRow.storeAddress)}
              ${screens.render("component://osafe/widget/CommonScreens.xml#displayPostalAddress")}
          </#if>
       </span>
    </div>
</li>