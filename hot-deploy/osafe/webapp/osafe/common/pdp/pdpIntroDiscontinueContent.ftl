<li class="${request.getAttribute("attributeClass")!}">
  <#-- check to see if introductionDate has not passed yet -->
  <#if product.introductionDate?exists && nowTimestamp.before(product.introductionDate)>
  <div class="pdpIntroDiscontinueContent">
    ${screens.render("component://osafe/widget/EcommerceContentScreens.xml#PS_INTRODUCED")}
  </div>
  <#-- check to see if salesDiscontinuationDate has passed -->
  <#elseif product.salesDiscontinuationDate?exists && nowTimestamp.after(product.salesDiscontinuationDate)>
   <div id="pdpIntroDiscontinueContent">
    ${screens.render("component://osafe/widget/EcommerceContentScreens.xml#PS_DISCONTINUED")}
   </div>
  </#if>
</li>
  
