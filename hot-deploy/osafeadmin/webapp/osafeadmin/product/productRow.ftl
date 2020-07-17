    <#if product?has_content>
      <#if productContentWrapper?exists>
        <#assign productName = productContentWrapper.get("PRODUCT_NAME")!""/>
        <#assign productLargeImageUrl = productContentWrapper.get("LARGE_IMAGE_URL")!""/>
        <#assign productLongDescription = productContentWrapper.get("LONG_DESCRIPTION")!""/>
      </#if>
         <tr class="dataRow">
         
           <input type="hidden" name="${parameters.assocType!}RelatedProductId_" id="${parameters.assocType!}RelatedProductId" value="${product.productId!}"/>
           <td class="idCol firstCol" >
             <#if product.isVirtual == 'Y'>
               <a href="<@ofbizUrl>virtualProductDetail?productId=${product.productId!}</@ofbizUrl>">${product.productId!}</a>
             <#elseif product.isVariant == 'Y'>
               <a href="<@ofbizUrl>variantProductDetail?productId=${product.productId!}</@ofbizUrl>">${product.productId!}</a>
             <#elseif product.isVirtual == 'N' && product.isVariant == 'N'>
               <a href="<@ofbizUrl>finishedProductDetail?productId=${product.productId!}</@ofbizUrl>">${product.productId!}</a>
             </#if>
           </td>
           <td class="nameCol">${(product.internalName)?if_exists}</td>
           <td class="longDescCol ">
           <input type="hidden" name="${parameters.assocType!}RelatedProductName_" id="${parameters.assocType!}RelatedProductName" value="${productName!""}"/>
           ${productName?html!""}</td>
           <td class="actionCol">
             <#if productLongDescription?has_content && productLongDescription !="">
               <#assign productLongDescription = Static["com.osafe.util.OsafeAdminUtil"].formatToolTipText(productLongDescription, ADM_TOOLTIP_MAX_CHAR!)/>
               <a href="javascript:void(0);" onMouseover="javascript:showTooltip(event,'${productLongDescription!""}');" onMouseout="hideTooltip()"><span class="descIcon"></span></a>
             </#if>
             <a href="javascript:void(0);" onMouseover="<#if productLargeImageUrl?has_content>showTooltipImage(event,'','${productLargeImageUrl}?${nowTimestamp!}');<#else>showTooltip(event,'${uiLabelMap.ProductImagesTooltip}');</#if>" onMouseout="hideTooltip()"><span class="imageIcon"></span></a>
           </td>
           <td class="seqCol">
             <input type="text" class="infoValue small textAlignCenter" name="${parameters.assocType!}SequenceNum_" id="${parameters.assocType!}SequenceNum" value="" maxlength="9"/>
           </td>
           <#assign productName = Static["com.osafe.util.OsafeAdminUtil"].formatSimpleText('${productContentWrapper.get("PRODUCT_NAME")!""}')/>
           <td class="actionCol">
             <a href="javascript:setRowNo('');javascript:deletTableRow('${product.productId?if_exists}','${productName!""}','${parameters.tableId!}','${parameters.assocType!}');" onMouseover="javascript:showTooltip(event,'${uiLabelMap.DeleteProductAssociationTooltip}');" onMouseout="hideTooltip()" ><span class="crossIcon"></span></a>
             <a href="javascript:setRowNo('');javascript:openLookup(document.${detailFormName!}.${parameters.assocType!}AddProductId,document.${detailFormName!}.${parameters.assocType!}AddProductName,'lookupProduct','500','700','center','true');" onMouseover="javascript:showTooltip(event,'${uiLabelMap.InsertBeforeNewRowTooltip}');" onMouseout="hideTooltip()"><span class="insertBeforeIcon"></span></a>
             <a href="javascript:setRowNo('');javascript:openLookup(document.${detailFormName!}.${parameters.assocType!}AddProductId,document.${detailFormName!}.${parameters.assocType!}AddProductName,'lookupProduct','500','700','center','true');" onMouseover="javascript:showTooltip(event,'${uiLabelMap.InsertAfterNewRowTooltip}');" onMouseout="hideTooltip()"><span class="insertAfterIcon"></span></a>
           </td>
         </tr>
     </#if>
