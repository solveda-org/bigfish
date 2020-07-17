<#if resultList?exists && resultList?has_content>
  <table class="osafe">
    <input type="hidden" name="_useRowSubmit" value="Y" />
      <thead>
        <tr class="heading">
          <th class="idCol firstCol">${uiLabelMap.ProductNoLabel}</th>
          <th class="nameCol">${uiLabelMap.ItemNoLabel}</th>
          <th class="descCol">${uiLabelMap.NameLabel}</th>
          <th class="actionCol"></th>
          <th class="seqCol">${uiLabelMap.SeqNumberLabel}</th>
        </tr>
      </thead>
      <tbody>
        <#assign rowClass = "1">
        <input type="hidden" name="productCategoryId" value="${parameters.productCategoryId!}"/>
        <#list resultList as productCategoryMember>
          <#assign hasNext = productCategoryMember_has_next>
          <#assign product = delegator.findOne("Product", Static["org.ofbiz.base.util.UtilMisc"].toMap("productId", productCategoryMember.productId), false) />
          <#assign productContentWrapper = Static["org.ofbiz.product.product.ProductContentWrapper"].makeProductContentWrapper(product, request)!""/>
          <#if productContentWrapper?exists>
            <#assign productName = productContentWrapper.get("PRODUCT_NAME")!""/>
            <#assign productLargeImageUrl = productContentWrapper.get("LARGE_IMAGE_URL")!""/>
            <#assign productLongDescription = productContentWrapper.get("LONG_DESCRIPTION")!""/>
          </#if>
          <input type="hidden" name="fromDate_${productCategoryMember_index}" value="${productCategoryMember.fromDate!}"/>
          <input type="hidden" name="productId_${productCategoryMember_index}" value="${product.productId!}"/>
          <tr id="row_${productCategoryMember_index}" class="dataRow <#if rowClass == "2">even<#else>odd</#if>">
            <td class="idCol firstCol">${product.productId!}</td>
            <td class="nameCol <#if !hasNext>lastRow</#if>">${product.internalName?if_exists}</td>
            <td class="descCol <#if !hasNext>lastRow</#if>">${productName?if_exists}</td>
            <td class="actionCol <#if !hasNext>lastRow</#if>">
              <#if productLongDescription?has_content && productLongDescription !="">
                <#assign productLongDescription = Static["com.osafe.util.OsafeAdminUtil"].formatToolTipText(productLongDescription, ADM_TOOLTIP_MAX_CHAR!)/>
                <a href="javascript:void(0);" onMouseover="javascript:showTooltip(event,'${productLongDescription?html}');" onMouseout="hideTooltip()"><span class="descIcon"></span></a>
                <#assign productContentWrapper = Static["org.ofbiz.product.product.ProductContentWrapper"].makeProductContentWrapper(product, request)!""/>
              </#if>
              <a href="javascript:void(0);" onMouseover="<#if productLargeImageUrl?has_content>showTooltipImage(event,'${uiLabelMap.ProductImagesTooltip}','${productLargeImageUrl}');<#else>showTooltip(event,'${uiLabelMap.ProductImagesTooltip}');</#if>" onMouseout="hideTooltip()"><span class="imageIcon"></span></a>
            </td>
            <#assign rowSeq = request.getParameter("sequenceNum_${productCategoryMember_index}")!productCategoryMember.sequenceNum!''/>
            <td class="seqCol lastCol <#if !hasNext>lastRow</#if>"><input type="text" class="infoValue small" name="sequenceNum_${productCategoryMember_index}" value="${rowSeq!}" maxlength="9"/>
            </td>
          </tr>
          <#if rowClass == "2">
              <#assign rowClass = "1">
          <#else>
              <#assign rowClass = "2">
          </#if>
        </#list>
      </tbody>
    </table>
<#else>
  ${screens.render("component://osafeadmin/widget/CommonScreens.xml#ListNoResult")}
</#if>
<!-- end listBox -->