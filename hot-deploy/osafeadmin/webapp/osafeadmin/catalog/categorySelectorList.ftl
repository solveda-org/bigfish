<#if resultList?exists && resultList?has_content><#assign topLevelList = resultList></#if>
<tr class="heading">
  <th class="idCol firstCol">${uiLabelMap.CategoryLabel}</th>
</tr>
  
<#if topLevelList?exists && topLevelList?has_content>
  <#list topLevelList as category>
    <#if catContentWrappers?exists>
      <tr class="dataRow">
        <td class="idCol firstCol" ><a class="leftMargin10" href="javascript:set_values('${category.productCategoryId!?if_exists}','${catContentWrappers[category.productCategoryId].get("CATEGORY_NAME")?if_exists}')">${catContentWrappers[category.productCategoryId].get("CATEGORY_NAME")?if_exists}</a></td>
      </tr>
      <#if showSubNav?exists && showSubNav =='Y'>
        <#assign subCatList = Static["org.ofbiz.product.category.CategoryWorker"].getRelatedCategoriesRet(request, "subCatList", category.getString("productCategoryId"), true)>
          <#if subCatList?exists && subCatList?has_content>
          <#list subCatList as subCategory>
            <tr class="dataRow">
              <td class="idCol spacer"><a href="javascript:set_values('${subCategory.productCategoryId!?if_exists}','${catContentWrappers[subCategory.productCategoryId!].get("CATEGORY_NAME")?if_exists}')">${catContentWrappers[subCategory.productCategoryId!].get("CATEGORY_NAME")?if_exists}</a></td>
            </tr>
          </#list>
        </#if>
      </#if>
    </#if>
  </#list>
</#if>
  