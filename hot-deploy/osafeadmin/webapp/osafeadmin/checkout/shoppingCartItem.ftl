<#if (shoppingCart?exists && shoppingCart.size() > 0)>
 <div class="showCartItems">
  <div class="cartWrap">
    <input type="hidden" name="removeSelected" value="false"/>
    <table class="osafe">
        <thead>
            <tr class="heading">
                <th class="idCol firstCol">${uiLabelMap.ProductNoLabel}</th>
                <th class="descCol">${uiLabelMap.ItemNoLabel}</th>
                <th class="descCol">${uiLabelMap.ProductNameLabel}</th>
                <th class="qtyCol">${uiLabelMap.QtyLabel}</th>
                <th class="dollarCol">${uiLabelMap.UnitPriceLabel}</th>
                <th class="dollarCol">${uiLabelMap.ItemTotalLabel}</th>
            </tr>
        </thead>
        <tbody>
         <#assign rowClass = "1"/>
         <tr class="dataRow <#if rowClass?if_exists == "2">even<#else>odd</#if>">
          <#if (product.isVariant?if_exists == 'Y')>
          	<td class="idCol lastRowfirstCol" ><a href="<@ofbizUrl>variantProductDetail?productId=${productId?if_exists}</@ofbizUrl>">${productId?if_exists}</a></td>
          <#elseif product.productTypeId?if_exists == "FINISHED_GOOD" && (product.isVariant?if_exists == 'N') && (product.isVirtual?if_exists == 'N')>
            <td class="idCol lastRow firstCol" ><a href="<@ofbizUrl>finishedProductDetail?productId=${productId?if_exists}</@ofbizUrl>">${productId?if_exists}</a></td>
          </#if>
          <td class="descCol lastRow">${productInternalName?if_exists}</td>
          <td class="descCol lastRow"><#if productName?has_content>${productName!}</#if></td>
          <#assign quantity = cartLine.getQuantity()!/>
          <td class="qtyCol lastRow">
             ${quantity!}
          </td>
          <td class="dollarCol <astRow"><@ofbizCurrency amount=displayPrice rounding=globalContext.currencyRounding isoCode=currencyUom/></td>
          <td class="dollarCol total lastRow"><@ofbizCurrency amount=cartLine.getDisplayItemSubTotal() rounding=globalContext.currencyRounding isoCode=currencyUom/></td>
         </tr>
	      <#if rowClass == "2">
	        <#assign rowClass = "1">
	      <#else>
	        <#assign rowClass = "2">
	      </#if>
      </tbody>
    </table>
  </div>
</div>
<#else>
    <table class="osafe">
        <thead>
            <tr class="heading">
                <th class="idCol firstCol">${uiLabelMap.ProductNoLabel}</th>
                <th class="descCol">${uiLabelMap.ItemNoLabel}</th>
                <th class="descCol">${uiLabelMap.ProductNameLabel}</th>
                <th class="dollarCol">${uiLabelMap.QtyLabel}</th>
                <th class="dollarCol">${uiLabelMap.UnitPriceLabel}</th>
                <th class="dollarCol">${uiLabelMap.ItemTotalLabel}</th>
            </tr>
        </thead>
            <tbody>
                 ${screens.render("component://osafeadmin/widget/CommonScreens.xml#ListNoDataResult")}
            </tbody>
    </table>
 </#if>