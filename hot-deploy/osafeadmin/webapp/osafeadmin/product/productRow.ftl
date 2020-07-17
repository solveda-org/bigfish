    <#if product?has_content>
      <#if productContentWrapper?exists>
        <#assign productName = productContentWrapper.get("PRODUCT_NAME")!""/>
        <#assign productLargeImageUrl = productContentWrapper.get("LARGE_IMAGE_URL")!""/>
        <#assign productLongDescription = productContentWrapper.get("LONG_DESCRIPTION")!""/>
      </#if>
         <tr class="dataRow">
         
           <input type="hidden" name="relatedProductId_" id="relatedProductId" value="${product.productId!}"/>
           <td class="idCol firstCol" ><a href="<@ofbizUrl>productDetail?productId=${product.productId?if_exists}</@ofbizUrl>">${(product.productId)?if_exists}</a></td>
           <td class="nameCol">${(product.internalName)?if_exists}</td>
           <td class="longDescCol ">
           <input type="hidden" name="relatedProductName_" id="relatedProductName" value="${productName!""}"/>
           ${productName?html!""}</td>
           <td class="actionCol">
             <#if productLongDescription?has_content && productLongDescription !="">
               <#assign productLongDescription = Static["com.osafe.util.OsafeAdminUtil"].formatToolTipText(productLongDescription, ADM_TOOLTIP_MAX_CHAR!)/>
               <a href="javascript:void(0);" onMouseover="javascript:showTooltip(event,'${productLongDescription!""}');" onMouseout="hideTooltip()"><span class="descIcon"></span></a>
             </#if>
             <a href="javascript:void(0);" onMouseover="<#if productLargeImageUrl?has_content>showTooltipImage(event,'','${productLargeImageUrl}?${nowTimestamp!}');<#else>showTooltip(event,'${uiLabelMap.ProductImagesTooltip}');</#if>" onMouseout="hideTooltip()"><span class="imageIcon"></span></a>
           </td>
           <td class="seqCol">
             <input type="text" class="infoValue small textAlignCenter" name="sequenceNum_" id="sequenceNum" value=""/>
           </td>
           <td class="actionCol">
             <a href="javascript:setRowNo('');javascript:deletTableRow('${product.productId?if_exists}','${productName!""}');" onMouseover="javascript:showTooltip(event,'${uiLabelMap.DeleteRelatedProductTooltip}');" onMouseout="hideTooltip()" ><span class="crossIcon"></span></a>
             <a href="javascript:setRowNo('');javascript:openLookup(document.${detailFormName!}.addProductId,document.${detailFormName!}.addProductName,'lookupProduct','500','700','center','true');" onMouseover="javascript:showTooltip(event,'${uiLabelMap.InsertBeforeNewRowTooltip}');" onMouseout="hideTooltip()"><span class="insertBeforeIcon"></span></a>
             <a href="javascript:setRowNo('');javascript:openLookup(document.${detailFormName!}.addProductId,document.${detailFormName!}.addProductName,'lookupProduct','500','700','center','true');" onMouseover="javascript:showTooltip(event,'${uiLabelMap.InsertAfterNewRowTooltip}');" onMouseout="hideTooltip()"><span class="insertAfterIcon"></span></a>
           </td>
         </tr>
     </#if>
