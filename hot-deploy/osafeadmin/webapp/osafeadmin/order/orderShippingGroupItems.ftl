<#if orderItemShipGroupAssoc?has_content>
<div class="infoRow row">
    <table class="osafe">
        <tr class="heading">
            <th class="idCol firstCol">${uiLabelMap.ItemNoLabel}</th>
            <th class="nameCol">${uiLabelMap.ProductNameLabel}</th>
            <th class="dollarCol">${uiLabelMap.ItemPriceLabel}</th>
            <th class="qtyCol lastCol">${uiLabelMap.QtyLabel}</th>
        </tr>
      <#list orderItemShipGroupAssoc as shipGroupAssoc>
        <#assign orderItem =shipGroupAssoc.getRelatedOne("OrderItem")!""/>
        <#assign order =orderItem.getRelatedOne("OrderHeader")!""/>
        <tr class="dataRow <#if rowClass?if_exists == "2">even<#else>odd</#if>">
            <td class="idCol firstCol <#if !shipGroupAssoc_has_next?if_exists>lastRow</#if>">${orderItem.productId!}</td>
            <td class="nameCol <#if !shipGroupAssoc_has_next?if_exists>lastRow</#if>">${orderItem.itemDescription!}</td>
            <td class="dollarCol <#if !shipGroupAssoc_has_next?if_exists>lastRow</#if>"><@ofbizCurrency amount=orderItem.unitPrice rounding=globalContext.currencyRounding isoCode=order.currencyUom/></td>
            <td class="qtyCol lastCol <#if !shipGroupAssoc_has_next?if_exists>lastRow</#if>">${shipGroupAssoc.quantity!}</td>
        </tr>
      </#list>
    </table>
</div>
</#if>




