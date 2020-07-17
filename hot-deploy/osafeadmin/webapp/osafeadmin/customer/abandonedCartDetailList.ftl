  <tr class="heading">
    <th class="idCol">${uiLabelMap.LineLabel}</th>
    <th class="idCol">${uiLabelMap.ProductNoLabel}</th>
    <th class="nameCol">${uiLabelMap.ItemNoLabel}</th>
    <th class="nameCol">${uiLabelMap.NameLabel}</th>
    <th class="qtyCol">${uiLabelMap.QtyLabel}</th>
  </tr>
  <#if resultList?exists && resultList?has_content>
    <#assign rowClass = "1"/>
    <#assign rowNo = 1 />
    <#list resultList as result>
      <#assign hasNext = result_has_next>
      <tr class="dataRow <#if rowClass?if_exists == "2">even<#else>odd</#if>">
        <td class="idCol <#if !result_has_next?if_exists>lastRow</#if>">${rowNo}</td>
        <#assign rowNo = rowNo + 1 />
        <#if result.productId?has_content >
         	<#assign product = delegator.findByPrimaryKey("Product", Static["org.ofbiz.base.util.UtilMisc"].toMap("productId", result.productId!))/>
         	<#assign productContentWrapper = Static["org.ofbiz.product.product.ProductContentWrapper"].makeProductContentWrapper(product,request)>
         	<#assign productName = productContentWrapper.get("PRODUCT_NAME")!itemProduct.productName!"">
        </#if>
        <td class="idCol <#if !result_has_next?if_exists>lastRow</#if>">
          <#if product?has_content && product.isVirtual == 'Y'>
            <a href="<@ofbizUrl>virtualProductDetail?productId=${product.productId!}</@ofbizUrl>">${product.productId!}</a>
          <#elseif product?has_content && product.isVariant == 'Y'>
            <a href="<@ofbizUrl>variantProductDetail?productId=${product.productId!}</@ofbizUrl>">${product.productId!}</a>
          <#elseif product?has_content && product.isVirtual == 'N' && product.isVariant == 'N'>
            <a href="<@ofbizUrl>finishedProductDetail?productId=${product.productId!}</@ofbizUrl>">${product.productId!}</a>
          </#if>
        </td>
        <td class="nameCol <#if !result_has_next?if_exists>lastRow</#if>">${(product.internalName)!""}</td>
        <td class="nameCol <#if !customerActivity_has_next?if_exists>lastRow</#if>">${productName!""}</td>
        <td class="qtyCol <#if !customerActivity_has_next?if_exists>lastRow</#if>">${result.quantity!""}</td>
      </tr>
      <#if rowClass == "2">
        <#assign rowClass = "1">
      <#else>
        <#assign rowClass = "2">
      </#if>
    </#list>
  <#else>
    ${screens.render("component://osafeadmin/widget/CommonScreens.xml#ListNoDataResult")}
  </#if>
