<script language="javascript">
    jQuery(document).ready(function() { 
        jQuery("#topProductsTable").tablesorter(); 
    } 
); 
</script>
        <table class="osafe tablesorter" id="topProductsTable">
        <#if topProductsList?has_content>
        <#assign topProductsList = Static["org.ofbiz.entity.util.EntityUtil"].orderBy(topProductsList,Static["org.ofbiz.base.util.UtilMisc"].toList("unitPrice DESC")) />
        <#assign totItem = 0/>
        <#assign totSales = 0/>
          <thead>
          <tr class="heading">
            	<th class="idCol firstCol">${uiLabelMap.ProductIdLabel}</th>
            	<th class="nameCol">${uiLabelMap.ItemNoLabel}</th>
                <th class="descCol">${uiLabelMap.DescriptionLabel}</th>
                <th class="nameCol">${uiLabelMap.CategoryLabel}</th>
                <th class="nameCol">${uiLabelMap.DeliveryLabel}</th>
                <th class="statusCol">${uiLabelMap.NoItemsLabel}</th>
                <th class="statusCol">${uiLabelMap.NoOrdersLabel}</th>
                <th class="dollarCol">${uiLabelMap.SalesLabel} ${globalContext.currencySymbol!}</th>
            </tr>
         </thead>
            <#assign rowClass = "1">
            <#list topProductsList as product>
                <#assign productId = product.productId!"">
                <#assign totOrder = allTopProductsOrderMap.get(productId) />
                <#if productId.indexOf("^")!= -1>
                    <#assign deliveryOption = productId.substring(productId.indexOf("^")+1)>
                    <#assign productId = productId.substring(0,productId.indexOf("^"))>
                </#if>
                <#assign topProduct = delegator.findByPrimaryKeyCache("Product", Static["org.ofbiz.base.util.UtilMisc"].toMap("productId", productId))?if_exists>
                <#assign internalName = topProduct.internalName!"">
                <#assign quantityOrdered = product.quantityOrdered!"">
                <#assign totItem = totItem + quantityOrdered />
                <#assign unitPrice = product.unitPrice!"">
                <#assign totSales = totSales + unitPrice />
                <#assign productName = topProductContentWrappers[productId].get("PRODUCT_NAME")>
                <#if !productName?string?has_content>
                    <#assign productName = productId>
                </#if>
                                
                <tr class="<#if rowClass == "2">even</#if>">
                    <td class="idCol firstCol <#if !product_has_next?if_exists>lastRow</#if> firstCol" ><a href="<@ofbizUrl>productDetail?productId=${productId?if_exists}</@ofbizUrl>">${productId?if_exists}</a></td>
                    <td class="nameCol <#if !product_has_next>lastRow</#if>">${internalName!""}</td>
                    <td class="descCol <#if !product_has_next>lastRow</#if>">${productName?if_exists} </td>
                    <td class="nameCol <#if !product_has_next>lastRow</#if>">
                      <#assign productCategoryMembers = Static["org.ofbiz.entity.util.EntityUtil"].filterByDate(delegator.findByAnd("ProductCategoryMember",Static["org.ofbiz.base.util.UtilMisc"].toMap("productId",productId))?if_exists) />
                      <#if productCategoryMembers?has_content>
                        <#assign productCategoryMember = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(productCategoryMembers) />
                        <#assign productCategory = productCategoryMember.getRelatedOne("ProductCategory") />
                        ${productCategory.categoryName!}
                      </#if>
                    </td>
                    <td class="nameCol <#if !product_has_next>lastRow</#if>"><#if deliveryOption! == "SHIP_TO">${uiLabelMap.DeliveryShipToInfo}<#elseif deliveryOption! == "STORE_PICKUP">${uiLabelMap.DeliveryStorePickupInfo}</#if></td>
                    <td class="statusCol <#if !product_has_next>lastRow</#if>">${quantityOrdered?if_exists} </td>
                    <td class="statusCol <#if !product_has_next>lastRow</#if>">${totOrder?if_exists} </td>
                    <td class="dollarCol <#if !product_has_next>lastRow</#if> lastCol"><@ofbizCurrency amount=unitPrice!0 isoCode=globalContext.defaultCurrencyUomId /></td>
                </tr>

                <#-- toggle the row color -->
                <#if rowClass == "2">
                    <#assign rowClass = "1">
                <#else>
                    <#assign rowClass = "2">
                </#if>
            </#list>
            <tfoot>
            <tr>
              <th class="idCol firstCol footerColor" colspan="5">Total</th>
              <th class="statusCol footerColor">${totItem?if_exists}</th>
              <th class="dollarCol footerColor" colspan="2"><@ofbizCurrency amount=totSales!0 isoCode=globalContext.defaultCurrencyUomId /></th>
            </tr>
            </tfoot>
        <#else>
                <tr>
                    <td colspan="6" class="boxNumber">${uiLabelMap.NoMatchingDataInfo}</td>
                </tr>
        </#if>
        </table>
