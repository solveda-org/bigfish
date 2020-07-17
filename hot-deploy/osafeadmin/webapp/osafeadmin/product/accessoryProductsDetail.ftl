<table class="osafe" id="accessoryProducts">
  <thead>
    <tr class="heading">
      <th class="idCol firstCol">${uiLabelMap.ProductNoLabel}</th>
      <th class="nameCol">${uiLabelMap.ItemNoLabel}</th>
      <th class="longDescCol ">${uiLabelMap.ProductNameLabel}</th>
      <th class="actionCol"></th>
      <th class="seqCol">${uiLabelMap.SeqNumberLabel}</th>
      <th class="actionCol"></th>
    </tr>
  </thead>
  <tbody>
    <#assign rowClass = "1"/>
    <#assign rowNo = 1/>
    <input type="hidden" name="accessProductAssocTypeId" id="accessProductAssocTypeId" value="PRODUCT_ACCESSORY"/>
    <input type="hidden" name="accessRowNo" id="accessRowNo"/>
    <#if accessProductAssoc?exists && accessProductAssoc?has_content>
        <input type="hidden" name="accessTotalRows" id="accessTotalRows" value="${parameters.accessTotalRows!accessProductAssoc?size}"/>
    <#else>
        <input type="hidden" name="accessTotalRows" id="accessTotalRows" value="${parameters.accessTotalRows!}"/>
    </#if>
    <input type="hidden" name="accessAddProductId" id="accessAddProductId"/>
    <input type="hidden" name="accessAddProductName" id="accessAddProductName" onchange="addProductRow('accessoryProducts', 'access');"/>
    <#if accessProductAssoc?exists && accessProductAssoc?has_content && !parameters.accessTotalRows?exists>
      <#list accessProductAssoc as relatedProduct>
        <#assign relatedProdDetail = relatedProduct.getRelatedOne("AssocProduct")>
        <#assign productContentWrapper = Static["org.ofbiz.product.product.ProductContentWrapper"].makeProductContentWrapper(relatedProdDetail, request)!""/>
         <#assign productLargeImageUrl = productContentWrapper.get("LARGE_IMAGE_URL")!"">
         <tr class="dataRow <#if rowClass?if_exists == "2">even<#else>odd</#if>">
         
           <input type="hidden" name="accessRelatedProductId_${rowNo}" id="accessRelatedProductId" value="${relatedProdDetail.productId!}"/>
           <td class="idCol firstCol" >
             <#if relatedProdDetail?has_content && relatedProdDetail.isVirtual == 'Y'>
               <a href="<@ofbizUrl>virtualProductDetail?productId=${relatedProdDetail.productId!}</@ofbizUrl>">${relatedProdDetail.productId!}</a>
             <#elseif relatedProdDetail?has_content && relatedProdDetail.isVariant == 'Y'>
               <a href="<@ofbizUrl>variantProductDetail?productId=${relatedProdDetail.productId!}</@ofbizUrl>">${relatedProdDetail.productId!}</a>
             <#elseif relatedProdDetail?has_content && relatedProdDetail.isVirtual == 'N' && relatedProdDetail.isVariant == 'N'>
               <a href="<@ofbizUrl>finishedProductDetail?productId=${relatedProdDetail.productId!}</@ofbizUrl>">${relatedProdDetail.productId!}</a>
             </#if>
           </td>
           <td class="nameCol">${(relatedProdDetail.internalName)?if_exists}</td>
           <td class="longDescCol ">
           <input type="hidden" name="accessRelatedProductName_${rowNo}" id="accessRelatedProductName" value="${productContentWrapper.get("PRODUCT_NAME")!""}"/>
           ${productContentWrapper.get("PRODUCT_NAME")!""}
           </td>
           <td class="actionCol">
             <#assign productLongDescription = productContentWrapper.get("LONG_DESCRIPTION")!""/>
             <#if productLongDescription?has_content && productLongDescription !="">
               <#assign productLongDescription = Static["com.osafe.util.OsafeAdminUtil"].formatToolTipText(productLongDescription, ADM_TOOLTIP_MAX_CHAR!)/>
               <a href="javascript:void(0);" onMouseover="javascript:showTooltip(event,'${productLongDescription!""}');" onMouseout="hideTooltip()"><span class="descIcon"></span></a>
             </#if>
             <a href="javascript:void(0);" onMouseover="<#if productLargeImageUrl?has_content>showTooltipImage(event,'','${productLargeImageUrl}?${nowTimestamp!}');<#else>showTooltip(event,'${uiLabelMap.ProductImagesTooltip}');</#if>" onMouseout="hideTooltip()"><span class="imageIcon"></span></a>
           </td>
           <td class="seqCol">
             <input type="text" class="infoValue small textAlignCenter" name="accessSequenceNum_${rowNo}" id="accessSequenceNum" value="${relatedProduct.sequenceNum!}" maxlength="9"/>
           </td>
           <#assign productName = Static["com.osafe.util.OsafeAdminUtil"].formatSimpleText('${productContentWrapper.get("PRODUCT_NAME")!""}')/>
           <td class="actionCol">
             <a href="javascript:setRowNo('${rowNo}','access');javascript:deletTableRow('${relatedProduct.productIdTo?if_exists}','${productName!""}', 'accessoryProducts', 'access');" onMouseover="javascript:showTooltip(event,'${uiLabelMap.DeleteProductAssociationTooltip}');" onMouseout="hideTooltip()"><span class="crossIcon"></span></a>
             <a href="javascript:setRowNo('${rowNo}','access');javascript:openLookup(document.${detailFormName!}.accessAddProductId,document.${detailFormName!}.accessAddProductName,'lookupProduct','500','700','center','true');" onMouseover="javascript:showTooltip(event,'${uiLabelMap.InsertBeforeNewRowTooltip}');" onMouseout="hideTooltip()"><span class="insertBeforeIcon"></span></a>
             <a href="javascript:setRowNo('${rowNo+1}','access');javascript:openLookup(document.${detailFormName!}.accessAddProductId,document.${detailFormName!}.accessAddProductName,'lookupProduct','500','700','center','true');" onMouseover="javascript:showTooltip(event,'${uiLabelMap.InsertAfterNewRowTooltip}');" onMouseout="hideTooltip()"><span class="insertAfterIcon"></span></a>           </td>
        </tr>
        <#if rowClass == "2">
          <#assign rowClass = "1">
        <#else>
          <#assign rowClass = "2">
        </#if>
        <#assign rowNo = rowNo+1/>
      </#list>

    <#elseif parameters.accessTotalRows?has_content && parameters.accessTotalRows!= '0'>
      <#assign minRow = parameters.accessTotalRows?number/>
      <#list 1..minRow as x>
        <tr class="dataRow <#if rowClass?if_exists == "2">even<#else>odd</#if>">
           <#assign relatedProductId = request.getParameter("accessRelatedProductId_${x}")!/>
           <input type="hidden" name="accessRelatedProductId_${x}" id="accessRelatedProductId" value="${relatedProductId!}"/>
           <#assign relatedProdDetail = delegator.findOne("Product", {"productId" : relatedProductId}, false) />
           <#assign productContentWrapper = Static["org.ofbiz.product.product.ProductContentWrapper"].makeProductContentWrapper(relatedProdDetail, request)!""/>
           <#assign productLargeImageUrl = productContentWrapper.get("LARGE_IMAGE_URL")!"">
           <td class="idCol firstCol" >
             <#if relatedProdDetail?has_content && relatedProdDetail.isVirtual == 'Y'>
               <a href="<@ofbizUrl>virtualProductDetail?productId=${relatedProdDetail.productId!}</@ofbizUrl>">${relatedProdDetail.productId!}</a>
             <#elseif relatedProdDetail?has_content && relatedProdDetail.isVariant == 'Y'>
               <a href="<@ofbizUrl>variantProductDetail?productId=${relatedProdDetail.productId!}</@ofbizUrl>">${relatedProdDetail.productId!}</a>
             <#elseif relatedProdDetail?has_content && relatedProdDetail.isVirtual == 'N' && relatedProdDetail.isVariant == 'N'>
               <a href="<@ofbizUrl>finishedProductDetail?productId=${relatedProdDetail.productId!}</@ofbizUrl>">${relatedProdDetail.productId!}</a>
             </#if>
           </td>
           <td class="nameCol">${(relatedProdDetail.internalName)?if_exists}</td>
           <td class="longDescCol ">${productContentWrapper.get("PRODUCT_NAME")?html!""}
           <#assign relatedProductName = request.getParameter("accessRelatedProductName_${x}")!productContentWrapper.get("PRODUCT_NAME")!""/>
           <input type="hidden" name="accessRelatedProductName_${x}" id="accessRelatedProductName" value="${relatedProductName!""}"/>
           </td>
           <td class="actionCol">
             <#assign productLongDescription = productContentWrapper.get("LONG_DESCRIPTION")!""/>
             <#if productLongDescription?has_content && productLongDescription !="">
               <#assign productLongDescription = Static["com.osafe.util.OsafeAdminUtil"].formatToolTipText(productLongDescription, ADM_TOOLTIP_MAX_CHAR!)/>
               <a href="javascript:void(0);" onMouseover="javascript:showTooltip(event,'${productLongDescription!""}');" onMouseout="hideTooltip()"><span class="descIcon"></span></a>
             </#if>
             <a href="javascript:void(0);" onMouseover="<#if productLargeImageUrl?has_content>showTooltipImage(event,'','${productLargeImageUrl}?${nowTimestamp!}');<#else>showTooltip(event,'${uiLabelMap.ProductImagesTooltip}');</#if>" onMouseout="hideTooltip()"><span class="imageIcon"></span></a>
           </td>
           <td class="seqCol">
             <#assign sequenceNum = request.getParameter("accessSequenceNum_${x}")!/>
             <input type="text" class="infoValue small textAlignCenter" name="accessSequenceNum_${x}" id="accessSequenceNum" value="${sequenceNum!}" maxlength="9"/>
           </td>
           <#assign productName = Static["com.osafe.util.OsafeAdminUtil"].formatSimpleText('${productContentWrapper.get("PRODUCT_NAME")!""}')/>
           <td class="actionCol">
             <a href="javascript:setRowNo('${x}','access');javascript:deletTableRow('${relatedProdDetail.productIdTo?if_exists}','${productName!}', 'accessoryProducts', 'access');" onMouseover="javascript:showTooltip(event,'${uiLabelMap.DeleteProductAssociationTooltip}');" onMouseout="hideTooltip()"><span class="crossIcon"></span></a>
             <a href="javascript:setRowNo('${x}','access');javascript:openLookup(document.${detailFormName!}.accessAddProductId,document.${detailFormName!}.accessAddProductName,'lookupProduct','500','700','center','true');" onMouseover="javascript:showTooltip(event,'${uiLabelMap.InsertBeforeNewRowTooltip}');" onMouseout="hideTooltip()"><span class="insertBeforeIcon"></span></a>
             <a href="javascript:setRowNo('${x+1}','access');javascript:openLookup(document.${detailFormName!}.accessAddProductId,document.${detailFormName!}.accessAddProductName,'lookupProduct','500','700','center','true');" onMouseover="javascript:showTooltip(event,'${uiLabelMap.InsertAfterNewRowTooltip}');" onMouseout="hideTooltip()"><span class="insertAfterIcon"></span></a>           </td>
        </tr>
        <#if rowClass == "2">
          <#assign rowClass = "1">
        <#else>
          <#assign rowClass = "2">
        </#if>
      </#list>
    </#if>
    
    <tr class="dataRow" id="accessAddIconRow" <#if (!parameters.accessTotalRows?has_content && accessProductAssoc?exists && accessProductAssoc?has_content) || (minRow?exists && minRow &gt; 0)> style="display:none"</#if>>
      <td colspan="5">&nbsp;</td>
      <td class="actionCol">
        <span class="noAction"></span>
        <a href="javascript:setRowNo('1', 'access');javascript:openLookup(document.${detailFormName!}.accessAddProductId,document.${detailFormName!}.accessAddProductName,'lookupProduct','500','700','center','true');" onMouseover="javascript:showTooltip(event,'${uiLabelMap.InsertBeforeNewRowTooltip}');" onMouseout="hideTooltip()"><span class="insertBeforeIcon"></span></a>
        <span class="noAction"></span>
      </td>
    </tr>
  </tbody>
</table>