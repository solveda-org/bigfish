<!-- start displayBox -->
<div class="displayBox topProducts">
    <div class="header"><h2>${uiLabelMap.TopProductsOnlineSaleAnalysisHeading}
      <#if topProductsList?has_content>
        <a href="<@ofbizUrl>topProductsDetail?srchShipTo=Y</@ofbizUrl>" class="adminMore">${uiLabelMap.MoreLinkLabel}</a>
      </#if>
    </h2></div>
    <div class="boxBody">
        <table class="osafe">
        <tr class="heading">
            <th class="boxCaption firstCol">${uiLabelMap.ProductLabel}</th>
            <th class="boxNumber">${uiLabelMap.NoItemsLabel}</th>
            <th class="boxDollar lastCol">${uiLabelMap.SalesLabel}${globalContext.currencySymbol!}</th>
        </tr>
        <#if topProductsList?has_content>
          <#assign topProductsList = Static["org.ofbiz.entity.util.EntityUtil"].orderBy(topProductsList,Static["org.ofbiz.base.util.UtilMisc"].toList("unitPrice DESC")) />
            <#assign rowClass = "1">
            <#list topProductsList as product>

                <#assign productId = product.productId!"">
                <#assign topProduct = delegator.findByPrimaryKey("Product", Static["org.ofbiz.base.util.UtilMisc"].toMap("productId", productId))?if_exists>
                <#assign internalName = topProduct.internalName!"">
                <#assign quantityOrdered = product.quantityOrdered!"">
                <#assign unitPrice = product.unitPrice!"">
                <#assign productWrapper = topProductContentWrappers[productId]!"">
                <#assign productName=productId!""/>
                <#if productWrapper?has_content>
	                <#assign productName = productWrapper.get("PRODUCT_NAME")>
                </#if>
                <#if (productName.toString().length() > 35)>
                   <#assign productName = productName.toString().substring(0,34)>
                   <#assign productName = productName + " ...">
                </#if>
                <tr class="<#if rowClass == "2">even</#if>">
                    <td class="boxCaption <#if !product_has_next>lastRow</#if> firstCol"><#if internalName?has_content>${internalName}:</#if>${productName}</td>
                    <td class="boxNumber <#if !product_has_next>lastRow</#if>">${quantityOrdered}</td>
                    <td class="boxDollar <#if !product_has_next>lastRow</#if> lastCol"><@ofbizCurrency amount=unitPrice!0 isoCode=globalContext.defaultCurrencyUomId rounding=globalContext.currencyRounding /></td>
                </tr>

                <#-- toggle the row color -->
                <#if rowClass == "2">
                    <#assign rowClass = "1">
                <#else>
                    <#assign rowClass = "2">
                </#if>
            </#list>
        <#else>
            ${screens.render("component://osafeadmin/widget/CommonScreens.xml#ListNoDataResult")}        
        </#if>
        </table>
    </div>
</div>
<!-- end displayBox -->