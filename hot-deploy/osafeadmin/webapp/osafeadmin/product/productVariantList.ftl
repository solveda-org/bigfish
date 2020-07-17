<#if resultList?exists && resultList?has_content>
  <tr class="heading">
    <th class="idCol firstCol">${uiLabelMap.ProductIdLabel}</th>
    <th class="nameCol">${uiLabelMap.ItemNoLabel}</th>
    <th class="dateCol">${uiLabelMap.IntroDateLabel}</th>
    <th class="dateCol">${uiLabelMap.DiscoDateLabel}</th>
    <#list resultList as variantProduct>
      <#assign variantProdDetail = variantProduct.getRelatedOneCache("AssocProduct")>
        <#assign productFeatureAndAppls = delegator.findByAnd("ProductFeatureAndAppl", {"productId" : (variantProduct.productIdTo)?if_exists, "productFeatureApplTypeId", "STANDARD_FEATURE"})/>
        <#if productFeatureAndAppls?exists && productFeatureAndAppls?has_content>
          <#list productFeatureAndAppls as productFeatureAndAppl>
            <#assign curProductFeatureType = productFeatureAndAppl.getRelatedOneCache("ProductFeatureType")>
            <#assign featureType = (curProductFeatureType.get("description",locale))?default((productFeatureAndAppl.productFeatureTypeId)?if_exists)/>
            <th class="statusCol">${featureType}</th>
          </#list>
        </#if>
      <#break>
    </#list>
  </tr>
  
    <#assign rowClass = "1"/>
    <#list resultList as variantProduct>
      <#assign variantProdDetail = variantProduct.getRelatedOneCache("AssocProduct")>
      <tr class="dataRow <#if rowClass?if_exists == "2">even<#else>odd</#if>">
        <td class="idCol firstCol" ><a href="<@ofbizUrl>productDetail?productId=${variantProduct.productIdTo?if_exists}</@ofbizUrl>">${(variantProduct.productIdTo)?if_exists}</a></td>
        <td class="nameCol">${(variantProdDetail.internalName)?if_exists}</td>
        <td class="dateCol <#if !variantProdDetail_has_next?if_exists>lastRow</#if>">${(variantProdDetail.introductionDate?string(preferredDateFormat))!""}</td>
        <td class="dateCol <#if !variantProdDetail_has_next?if_exists>lastRow</#if>">${(variantProdDetail.salesDiscontinuationDate?string(preferredDateFormat))!""}</td>
        <#assign productFeatureAndAppls = delegator.findByAnd("ProductFeatureAndAppl", {"productId" : (variantProduct.productIdTo)?if_exists, "productFeatureApplTypeId", "STANDARD_FEATURE"})/>
        <#if productFeatureAndAppls?exists && productFeatureAndAppls?has_content>
          <#list productFeatureAndAppls as productFeatureAndAppl>
            <td class="statusCol">${(productFeatureAndAppl.get("description",locale))?if_exists}</td>
          </#list>
        </#if>
      </tr>
      <#if rowClass == "2">
        <#assign rowClass = "1">
      <#else>
        <#assign rowClass = "2">
      </#if>
    </#list>
</#if>