<!-- start listBox -->
      <tr class="heading">
        <th class="idCol firstCol">${uiLabelMap.ProductNoLabel}</th>
        <th class="descCol">${uiLabelMap.ItemNoLabel}</th>
        <th class="descCol">${uiLabelMap.ProductNameLabel}</th>
      </tr>
      <#if resultList?exists && resultList?has_content>
        <#assign rowClass = "1"/>
        <#list resultList as product>
          <#assign hasNext = product_has_next>
          <#assign productContentWrapper = Static["org.ofbiz.product.product.ProductContentWrapper"].makeProductContentWrapper(product, request)!""/>
          <#assign productLargeImageUrl = productContentWrapper.get("LARGE_IMAGE_URL")!"">
          <tr class="dataRow <#if rowClass?if_exists == "2">even<#else>odd</#if>">
            <#assign productName = Static["com.osafe.util.OsafeAdminUtil"].formatSimpleText('${productContentWrapper.get("PRODUCT_NAME")!""}')/>
            <td class="idCol <#if !product_has_next?if_exists>lastRow</#if> firstCol" ><a href="javascript:set_values('${product.productId?if_exists}','${productName!}')">${product.productId?if_exists}</a></td>
            <td class="descCol <#if !product_has_next?if_exists>lastRow</#if>">${product.internalName?if_exists}</td>
            <td class="descCol">
              ${productContentWrapper.get("PRODUCT_NAME")?html!""}
            </td>
          </tr>
          <#if rowClass == "2">
            <#assign rowClass = "1">
          <#else>
            <#assign rowClass = "2">
          </#if>
        </#list>
      </#if>
<!-- end listBox -->