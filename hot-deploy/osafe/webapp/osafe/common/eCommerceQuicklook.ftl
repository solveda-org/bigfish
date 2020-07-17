<#-- virtual product javascript -->
<#if currentProduct?exists && currentProduct?has_content>
  ${virtualJavaScript?if_exists}
  <form method="post" action="" name="addform"  style="margin: 0;">
    <input type="hidden" name="plp_qty" id="plp_qty" value=""/>
    <input type="hidden" name="plp_add_product_id" value=""/>
    <input type="hidden" name="plp_add_category_id" value=""/> 
    <input type="hidden" name="plp_add_product_name" value=""/> 
    <#-- flag to display success message -->
    <input type="hidden" name="showSuccess" id="showSuccess" value="Y"/> 
    ${screens.render("component://osafe/widget/EcommerceDivScreens.xml#quicklookDivSequence")}
  </form>
  ${virtualDefaultJavaScript?if_exists}
</#if>


