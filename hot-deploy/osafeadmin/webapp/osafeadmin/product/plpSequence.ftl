<#if resultList?exists && resultList?has_content>
  <table class="osafe">
    <input type="hidden" name="_useRowSubmit" value="Y" />
      <thead>
        <tr class="heading">
          <th class="idCol firstCol">${uiLabelMap.ProductIdLabel}</th>
          <th class="nameCol">${uiLabelMap.ItemNoLabel}</th>
          <th class="longDescCol">${uiLabelMap.NameLabel}</th>
          <th class="actionCol"></th>
          <th class="seqCol">${uiLabelMap.SeqNumberLabel}</th>
        </tr>
      </thead>
      <tbody>
        <#assign alt_row = false>
        <#assign rowIdx = 0>
        <#list resultList as productCategoryMember>
          <#assign hasNext = productCategoryMember_has_next>
          <#assign product = delegator.findOne("Product", Static["org.ofbiz.base.util.UtilMisc"].toMap("productId", productCategoryMember.productId), true) />
          <#assign productContentWrapper = Static["org.ofbiz.product.product.ProductContentWrapper"].makeProductContentWrapper(product, request)!""/>
          <#assign rowIdx = rowIdx + 1>
          <#assign rowSeq = rowIdx * 10>
          <tr id="row_${productCategoryMember_index}" class="dataRow <#if alt_row>even<#else>odd</#if>">
            <td class="idCol firstCol">
                  <input type="hidden" name="productCategoryId_o_${productCategoryMember_index}" value="${productCategoryMember.productCategoryId!}"/>
                  <input name="_rowSubmit_o_${productCategoryMember_index}" type="hidden" value="Y"/>
                  <input type="hidden" name="fromDate_o_${productCategoryMember_index}" value="${productCategoryMember.fromDate!}"/>
                  <input type="hidden" name="productId_o_${productCategoryMember_index}" value="${product.productId!}"/>
                  ${productCategoryMember.productId!}                  
            </td>
            <td class="nameCol <#if !hasNext>lastRow</#if>">${product.internalName?if_exists}
            </td>
            <td class="longDescCol <#if !hasNext>lastRow</#if>">${productContentWrapper.get("PRODUCT_NAME")!""}</td>
            <td class="actionCol <#if !hasNext>lastRow</#if>">
           <#assign productLongDescription = productContentWrapper.get("LONG_DESCRIPTION")!""/>
           <#if productLongDescription?has_content && productLongDescription !="">
                    <#assign productLongDescription = productLongDescription?replace("\'", "\\'") />
                    <#assign productLongDescription = productLongDescription?replace("\r\n", "\\n") />
                    <a href="javascript:void(0);" onMouseover="javascript:showTooltip(event,'${productLongDescription?html}');" onMouseout="hideTooltip()"><span class="descIcon"></span></a>
                    <#assign productContentWrapper = Static["org.ofbiz.product.product.ProductContentWrapper"].makeProductContentWrapper(product, request)!""/>
                    <#if productContentWrapper?exists>
                      <#assign productLargeImageUrl = productContentWrapper.get("LARGE_IMAGE_URL")!"">
                    </#if>
                    <a href="javascript:void(0);" onMouseover="<#if productLargeImageUrl?has_content>showTooltipImage(event,'${uiLabelMap.ProductImagesTooltip}','${productLargeImageUrl}');<#else>showTooltip(event,'${uiLabelMap.ProductImagesTooltip}');</#if>" onMouseout="hideTooltip()"><span class="imageIcon"></span></a>
           </#if>
            </td>
            <td class="seqCol lastCol <#if !hasNext>lastRow</#if>"><input type="text" class="infoValue small" name="sequenceNum_o_${productCategoryMember_index}" value="${rowSeq}"/>
            </td>
          </tr>
          <#assign alt_row = !alt_row>
        </#list>
      </tbody>
    </table>
<#else>
  ${screens.render("component://osafeadmin/widget/CommonScreens.xml#ListNoResult")}
</#if>
<!-- end listBox -->