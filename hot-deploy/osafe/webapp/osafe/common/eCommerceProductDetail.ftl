<#-- virtual product javascript -->
<#if currentProduct?exists && currentProduct?has_content>
  ${virtualJavaScript?if_exists}
  <form method="post" action="" name="${formName!'addform'}"  style="margin: 0;">
    <input type="hidden" name="plp_qty" id="plp_qty" value=""/>
    <input type="hidden" name="plp_add_product_id" id="plp_add_product_id" value=""/>
    <input type="hidden" name="plp_add_category_id" id="plp_add_category_id" value=""/> 
    <input type="hidden" name="plp_last_viewed_pdp_id" id="plp_last_viewed_pdp_id" value="${parameters.productId!productId!}"/>
    <#-- flag to display success message -->
    <input type="hidden" name="showSuccess" id="showSuccess" value="Y"/> 
    ${screens.render("component://osafe/widget/EcommerceDivScreens.xml#pdpDivSequence")}
  </form>
  ${virtualDefaultJavaScript?if_exists}
</#if>


