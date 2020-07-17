<div class="${request.getAttribute("attributeClass")!}">
  <#if shoppingCart?exists && shoppingCart?has_content >
    <div class="boxList giftMessageList">
      ${screens.render("component://osafe/widget/EcommerceDivScreens.xml#giftMessageConfirmOrderItemsDivSequence")}
   </div>
  </#if>
</div>