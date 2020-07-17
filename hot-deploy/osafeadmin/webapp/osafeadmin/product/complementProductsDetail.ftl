<script>
function deletTableRow(productId, productName, tableId, assocType)
{
    jQuery('#confirmDeleteTxt').html('${confirmDialogText!""} '+productId+': '+productName+'?');
    jQuery('#yesBtn').attr('onclick','').unbind('click');
    jQuery('#yesBtn').click(function() { removeProductRow(tableId,assocType)});
    displayDialogBox();
}

function removeProductRow(tableId, assocType)
{
    var table=document.getElementById(tableId);
    var inputRow = table.getElementsByTagName('tr');
    var indexPos = jQuery('#'+assocType+'RowNo').val();
    table.deleteRow(indexPos);
    hideDialog('#dialog', '#displayDialog');
    setProductIndexPos(table, assocType);
}
function addProductRow(tableId, assocType) 
{
    var table = document.getElementById(tableId);
    var rows = table.getElementsByTagName('tr');
    var indexPos = jQuery('#'+assocType+'RowNo').val();
    var row = table.insertRow(indexPos);
    
    productId =  jQuery('#'+assocType+'AddProductId').val();
    productName = jQuery('#'+assocType+'AddProductName').val(); 
    
    jQuery.get('<@ofbizUrl>addProductRow?productId='+productId+'&assocType='+assocType+'&tableId='+tableId+'&rnd='+String((new Date()).getTime()).replace(/\D/gi, "")+'</@ofbizUrl>', function(data) {
        jQuery(row).replaceWith(data);
        setProductIndexPos(table, assocType);
    });
}
function setProductIndexPos(table, assocType)
{
    var rows = table.getElementsByTagName('tr');
    // alert(rows.length);
    for (i = 1; i < rows.length; i++) {
        var columns = rows[i].getElementsByTagName('td');
        var productId;
        for (j = 0; j < columns.length; j++) {
            if(j == 5) {
                var anchors = columns[j].getElementsByTagName('a');
                if(anchors.length == 3) {
                    var deleteAnchor = anchors[0];
                    var deleteTagSecondMethodIndex = deleteAnchor.getAttribute("href").indexOf(";");
                    var deleteTagSecondMethod = deleteAnchor.getAttribute("href").substring(deleteTagSecondMethodIndex+1,deleteAnchor.getAttribute("href").length);
                    deleteAnchor.setAttribute("href", "javascript:setRowNo('"+i+"', '"+assocType+"');"+deleteTagSecondMethod);
                    
                    var insertBeforeAnchor = anchors[1];
                    var insertBeforeTagSecondMethodIndex = insertBeforeAnchor.getAttribute("href").indexOf(";");
                    var insertBeforeTagSecondMethod = insertBeforeAnchor.getAttribute("href").substring(insertBeforeTagSecondMethodIndex+1,insertBeforeAnchor.getAttribute("href").length);
                    insertBeforeAnchor.setAttribute("href", "javascript:setRowNo('"+i+"', '"+assocType+"');"+insertBeforeTagSecondMethod);
                    
                    var insertAfterAnchor = anchors[2];
                    var insertAfterTagSecondMethodIndex = insertAfterAnchor.getAttribute("href").indexOf(";");
                    var insertAfterTagSecondMethod = insertAfterAnchor.getAttribute("href").substring(insertAfterTagSecondMethodIndex+1,insertAfterAnchor.getAttribute("href").length);
                    insertAfterAnchor.setAttribute("href", "javascript:setRowNo('"+(i+1)+"', '"+assocType+"');"+insertAfterTagSecondMethod);
                }
                    
                if(anchors.length == 1) {
                    var insertBeforeAnchor = anchors[0];
                    var insertBeforeTagSecondMethodIndex = insertBeforeAnchor.getAttribute("href").indexOf(";");
                    var insertBeforeTagSecondMethod = insertBeforeAnchor.getAttribute("href").substring(insertBeforeTagSecondMethodIndex+1,insertBeforeAnchor.getAttribute("href").length);
                    insertBeforeAnchor.setAttribute("href", "javascript:setRowNo('"+i+"', '"+assocType+"');"+insertBeforeTagSecondMethod);
                }
            }
        }
        var inputs = rows[i].getElementsByTagName('input');
        for (j = 0; j < inputs.length; j++) {
            attrId = inputs[j].getAttribute("id");
            inputs[j].setAttribute("name",attrId+"_"+i)
        }
    }
    if(rows.length > 2) {
       jQuery('#'+assocType+'AddIconRow').hide();
    } else {
       jQuery('#'+assocType+'AddIconRow').show();
    }
    jQuery('#'+assocType+'TotalRows').val(rows.length-2);
}
function setRowNo(rowNo, assocType) {
    jQuery('#'+assocType+'RowNo').val(rowNo);
}
</script>

<table class="osafe" id="complementProducts">
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
    <input type="hidden" name="productId" id="productId" value="${parameters.productId!product.productId!}"/>
    <input type="hidden" name="compProductAssocTypeId" id="compProductAssocTypeId" value="PRODUCT_COMPLEMENT"/>
    <input type="hidden" name="compRowNo" id="compRowNo"/>
    <#if compProductAssoc?exists && compProductAssoc?has_content>
        <input type="hidden" name="compTotalRows" id="compTotalRows" value="${parameters.compTotalRows!compProductAssoc?size}"/>
    <#else>
        <input type="hidden" name="compTotalRows" id="compTotalRows" value="${parameters.compTotalRows!}"/>
    </#if>
    <input type="hidden" name="compAddProductId" id="compAddProductId"/>
    <input type="hidden" name="compAddProductName" id="compAddProductName" onchange="addProductRow('complementProducts', 'comp');"/>
    <#if compProductAssoc?exists && compProductAssoc?has_content && !parameters.compTotalRows?exists>
      <#list compProductAssoc as relatedProduct>
        <#assign relatedProdDetail = relatedProduct.getRelatedOne("AssocProduct")>
        <#assign productContentWrapper = Static["org.ofbiz.product.product.ProductContentWrapper"].makeProductContentWrapper(relatedProdDetail, request)!""/>
         <#assign productLargeImageUrl = productContentWrapper.get("LARGE_IMAGE_URL")!"">
         <tr class="dataRow <#if rowClass?if_exists == "2">even<#else>odd</#if>">
         
           <input type="hidden" name="compRelatedProductId_${rowNo}" id="compRelatedProductId" value="${relatedProdDetail.productId!}"/>
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
           <input type="hidden" name="compRelatedProductName_${rowNo}" id="compRelatedProductName" value="${productContentWrapper.get("PRODUCT_NAME")!""}"/>
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
             <input type="text" class="infoValue small textAlignCenter" name="compSequenceNum_${rowNo}" id="compSequenceNum" value="${relatedProduct.sequenceNum!}" maxlength="9"/>
           </td>
           <#assign productName = Static["com.osafe.util.OsafeAdminUtil"].formatSimpleText('${productContentWrapper.get("PRODUCT_NAME")!""}')/>
           <td class="actionCol">
             <a href="javascript:setRowNo('${rowNo}', 'comp');javascript:deletTableRow('${relatedProduct.productIdTo?if_exists}','${productName!}', 'complementProducts', 'comp');" onMouseover="javascript:showTooltip(event,'${uiLabelMap.DeleteProductAssociationTooltip}');" onMouseout="hideTooltip()"><span class="crossIcon"></span></a>
             <a href="javascript:setRowNo('${rowNo}', 'comp');javascript:openLookup(document.${detailFormName!}.compAddProductId,document.${detailFormName!}.compAddProductName,'lookupProduct','500','700','center','true');" onMouseover="javascript:showTooltip(event,'${uiLabelMap.InsertBeforeNewRowTooltip}');" onMouseout="hideTooltip()"><span class="insertBeforeIcon"></span></a>
             <a href="javascript:setRowNo('${rowNo+1}', 'comp');javascript:openLookup(document.${detailFormName!}.compAddProductId,document.${detailFormName!}.compAddProductName,'lookupProduct','500','700','center','true');" onMouseover="javascript:showTooltip(event,'${uiLabelMap.InsertAfterNewRowTooltip}');" onMouseout="hideTooltip()"><span class="insertAfterIcon"></span></a>
           </td>

        </tr>
        <#if rowClass == "2">
          <#assign rowClass = "1">
        <#else>
          <#assign rowClass = "2">
        </#if>
        <#assign rowNo = rowNo+1/>
      </#list>

    <#elseif parameters.compTotalRows?has_content>
      <#assign minRow = parameters.compTotalRows?number/>
      <#list 1..minRow as x>
        <tr class="dataRow <#if rowClass?if_exists == "2">even<#else>odd</#if>">
           <#assign relatedProductId = request.getParameter("compRelatedProductId_${x}")!/>
           <input type="hidden" name="compRelatedProductId_${x}" id="compRelatedProductId" value="${relatedProductId!}"/>
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
           <#assign relatedProductName = request.getParameter("compRelatedProductName_${x}")!productContentWrapper.get("PRODUCT_NAME")!""/>
           <input type="hidden" name="compRelatedProductName_${x}" id="compRelatedProductName" value="${relatedProductName!""}"/>
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
             <#assign sequenceNum = request.getParameter("compSequenceNum_${x}")!/>
             <input type="text" class="infoValue small textAlignCenter" name="compSequenceNum_${x}" id="compSequenceNum" value="${sequenceNum!}" maxlength="9"/>
           </td>
           <#assign productName = Static["com.osafe.util.OsafeAdminUtil"].formatSimpleText('${productContentWrapper.get("PRODUCT_NAME")!""}')/>
           <td class="actionCol">
             <a href="javascript:setRowNo('${x}','comp');javascript:deletTableRow('${relatedProdDetail.productIdTo?if_exists}','${productName!}', 'complementProducts', 'comp');" onMouseover="javascript:showTooltip(event,'${uiLabelMap.DeleteProductAssociationTooltip}');" onMouseout="hideTooltip()"><span class="crossIcon"></span></a>
             <a href="javascript:setRowNo('${x}','comp');javascript:openLookup(document.${detailFormName!}.compAddProductId,document.${detailFormName!}.compAddProductName,'lookupProduct','500','700','center','true');" onMouseover="javascript:showTooltip(event,'${uiLabelMap.InsertBeforeNewRowTooltip}');" onMouseout="hideTooltip()"><span class="insertBeforeIcon"></span></a>
             <a href="javascript:setRowNo('${x+1}','comp');javascript:openLookup(document.${detailFormName!}.compAddProductId,document.${detailFormName!}.compAddProductName,'lookupProduct','500','700','center','true');" onMouseover="javascript:showTooltip(event,'${uiLabelMap.InsertAfterNewRowTooltip}');" onMouseout="hideTooltip()"><span class="insertAfterIcon"></span></a>
           </td>
        </tr>
        <#if rowClass == "2">
          <#assign rowClass = "1">
        <#else>
          <#assign rowClass = "2">
        </#if>
      </#list>
    </#if>
    
    <tr class="dataRow" id="compAddIconRow" <#if (!parameters.compTotalRows?has_content && compProductAssoc?exists && compProductAssoc?has_content) || (minRow?exists && minRow &gt; 0)> style="display:none"</#if>>
      <td colspan="5">&nbsp;</td>
      <td class="actionCol">
        <span class="noAction"></span>
        <a href="javascript:setRowNo('1','comp');javascript:openLookup(document.${detailFormName!}.compAddProductId,document.${detailFormName!}.compAddProductName,'lookupProduct','500','700','center','true');" onMouseover="javascript:showTooltip(event,'${uiLabelMap.InsertBeforeNewRowTooltip}');" onMouseout="hideTooltip()"><span class="insertBeforeIcon"></span></a>
        <span class="noAction"></span>
      </td>
    </tr>
  </tbody>
</table>