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
                <#assign topProduct = delegator.findByPrimaryKey("Product", Static["org.ofbiz.base.util.UtilMisc"].toMap("productId", productId))?if_exists>
                <#assign internalName = topProduct.internalName!"">
                <#assign quantityOrdered = product.quantityOrdered!"">
                <#assign totItem = totItem + quantityOrdered />
                <#assign unitPrice = product.unitPrice!"">
                <#assign totSales = totSales + unitPrice />
                <#assign productWrapper = topProductContentWrappers[productId]!"">
                <#assign productName=productId!""/>
                <#if productWrapper?has_content>
	                <#assign productName = productWrapper.get("PRODUCT_NAME")>
                </#if>
                                
                <tr class="<#if rowClass == "2">even</#if>">
                    <td class="idCol firstCol <#if !product_has_next?if_exists>lastRow</#if> firstCol" >
                      <#if topProduct?has_content && topProduct.isVirtual == 'Y'>
                        <a href="<@ofbizUrl>virtualProductDetail?productId=${topProduct.productId!}</@ofbizUrl>">${topProduct.productId!}</a>
                      <#elseif topProduct?has_content && topProduct.isVariant == 'Y'>
                        <a href="<@ofbizUrl>variantProductDetail?productId=${topProduct.productId!}</@ofbizUrl>">${topProduct.productId!}</a>
                      <#elseif topProduct?has_content && topProduct.isVirtual == 'N' && topProduct.isVariant == 'N'>
                        <a href="<@ofbizUrl>finishedProductDetail?productId=${topProduct.productId!}</@ofbizUrl>">${topProduct.productId!}</a>
                      </#if>
                    </td>
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
                    <td class="dollarCol <#if !product_has_next>lastRow</#if> lastCol"><@ofbizCurrency amount=unitPrice!0 isoCode=globalContext.defaultCurrencyUomId rounding=globalContext.currencyRounding/></td>
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
              <th class="dollarCol footerColor" colspan="2"><@ofbizCurrency amount=totSales!0 isoCode=globalContext.defaultCurrencyUomId rounding=globalContext.currencyRounding/></th>
            </tr>
            </tfoot>
        <#else>
              ${screens.render("component://osafeadmin/widget/CommonScreens.xml#ListNoResult")}
        </#if>
        </table>
