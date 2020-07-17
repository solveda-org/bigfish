<#if orderHeader?has_content>
<div class="${request.getAttribute("attributeClass")!}">
  ${screens.render("component://osafe/widget/EcommerceDivScreens.xml#orderDetailOrderItemsSummaryDivSequence")}
</div>  
</#if>
