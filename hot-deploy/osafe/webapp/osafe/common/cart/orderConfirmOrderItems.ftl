<#if (orderItems?has_content && orderItems.size() > 0)>
    <#assign offerPriceVisible= "N"/>
    <#list orderItems as orderItem>
        <#assign orderItemAdjustment = localOrderReadHelper.getOrderItemAdjustmentsTotal(orderItem)/>
        <#if (orderItemAdjustment < 0) >
            <#assign offerPriceVisible= "Y"/>
            <#break>
        </#if>
    </#list>
    <div id="cart_wrap">
      <input type="hidden" name="removeSelected" value="false"/>
      <#if !userLogin?has_content || userLogin.userLoginId == "anonymous">
          <input type="hidden" name="guest" value="guest"/>
      </#if>
      <#assign itemsFromList = false>
          <#assign CURRENCY_UOM_DEFAULT = Static["com.osafe.util.Util"].getProductStoreParm(request,"CURRENCY_UOM_DEFAULT")!""/>
          <#assign currencyUom = CURRENCY_UOM_DEFAULT!shoppingCart.getCurrency() />
      <#list orderItems as orderItem>
        ${setRequestAttribute("orderItem", orderItem)}
        <div class="orderConfirmOrderItem">
          ${screens.render("component://osafe/widget/EcommerceDivScreens.xml#orderConfirmOrderItemsDivSequence")}
        </div>
      </#list>
    </div>
</#if>
