<li class="${request.getAttribute("attributeClass")!}">
  <div class="js_pdpInStoreOnlyContent" id="js_pdpInStoreOnlyContent" <#if !(isPdpInStoreOnly?exists && isPdpInStoreOnly == "Y")>style="display:none;"</#if>>
      ${screens.render("component://osafe/widget/EcommerceContentScreens.xml#PS_IN_STORE_ONLY")}
  </div>
</li>