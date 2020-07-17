<script>
function deletTableRow(productId, productName)
{
    jQuery('#confirmDeleteTxt').html('${confirmDialogText!""} '+productId+': '+productName+'?');
    displayDialogBox();
}

function removeProductRow(tableId){
    var table=document.getElementById(tableId);
    var inputRow = table.getElementsByTagName('tr');
    var indexPos = jQuery('#rowNo').val();
    table.deleteRow(indexPos);
    hideDialog('#dialog', '#displayDialog');
    setProductIndexPos(table);
}
function addProductRow(tableId) 
{
    var table = document.getElementById(tableId);
    var rows = table.getElementsByTagName('tr');
    var indexPos = jQuery('#rowNo').val();
    var row = table.insertRow(indexPos);
    
    productId =  jQuery('#addProductId').val();
    productName = jQuery('#addProductName').val(); 
    
    jQuery.get('<@ofbizUrl>addProductRow?productId='+productId+'&rnd='+String((new Date()).getTime()).replace(/\D/gi, "")+'</@ofbizUrl>', function(data) {
        jQuery(row).replaceWith(data);
        setProductIndexPos(table);
    });
}
function setProductIndexPos(table)
{
    var rows = table.getElementsByTagName('tr');
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
                    deleteAnchor.setAttribute("href", "javascript:setRowNo('"+i+"');"+deleteTagSecondMethod);
                    
                    var insertBeforeAnchor = anchors[1];
                    var insertBeforeTagSecondMethodIndex = insertBeforeAnchor.getAttribute("href").indexOf(";");
                    var insertBeforeTagSecondMethod = insertBeforeAnchor.getAttribute("href").substring(insertBeforeTagSecondMethodIndex+1,insertBeforeAnchor.getAttribute("href").length);
                    insertBeforeAnchor.setAttribute("href", "javascript:setRowNo('"+i+"');"+insertBeforeTagSecondMethod);
                    
                    var insertAfterAnchor = anchors[2];
                    var insertAfterTagSecondMethodIndex = insertAfterAnchor.getAttribute("href").indexOf(";");
                    var insertAfterTagSecondMethod = insertAfterAnchor.getAttribute("href").substring(insertAfterTagSecondMethodIndex+1,insertAfterAnchor.getAttribute("href").length);
                    insertAfterAnchor.setAttribute("href", "javascript:setRowNo('"+(i+1)+"');"+insertAfterTagSecondMethod);
                }
                    
                if(anchors.length == 1) {
                    var insertBeforeAnchor = anchors[0];
                    var insertBeforeTagSecondMethodIndex = insertBeforeAnchor.getAttribute("href").indexOf(";");
                    var insertBeforeTagSecondMethod = insertBeforeAnchor.getAttribute("href").substring(insertBeforeTagSecondMethodIndex+1,insertBeforeAnchor.getAttribute("href").length);
                    insertBeforeAnchor.setAttribute("href", "javascript:setRowNo('"+i+"');"+insertBeforeTagSecondMethod);
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
       jQuery('#addIconRow').hide();
    } else {
       jQuery('#addIconRow').show();
    }
    $('totalRows').value = rows.length-2;
}
function setRowNo(rowNo) {
    jQuery('#rowNo').val(rowNo);
}
</script>

<table class="osafe" id="ralatedProducts">
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
    <input type="hidden" name="rowNo" id="rowNo"/>
    <#if resultList?exists && resultList?has_content>
        <input type="hidden" name="totalRows" id="totalRows" value="${parameters.totalRows!resultList?size}"/>
    <#else>
        <input type="hidden" name="totalRows" id="totalRows" value="${parameters.totalRows!}"/>
    </#if>
    <input type="hidden" name="addProductId" id="addProductId"/>
    <input type="hidden" name="addProductName" id="addProductName" onchange="addProductRow('ralatedProducts');"/>
    <#if resultList?exists && resultList?has_content && !parameters.totalRows?exists>
      <#list resultList as relatedProduct>
        <#assign rowSeq = rowNo * 10>
        <#assign relatedProdDetail = relatedProduct.getRelatedOne("MainProduct")>
        <#assign productContentWrapper = Static["org.ofbiz.product.product.ProductContentWrapper"].makeProductContentWrapper(relatedProdDetail, request)!""/>
         <#assign productLargeImageUrl = productContentWrapper.get("LARGE_IMAGE_URL")!"">
         <tr class="dataRow <#if rowClass?if_exists == "2">even<#else>odd</#if>">
         
           <input type="hidden" name="relatedProductId_${rowNo}" id="relatedProductId" value="${relatedProduct.productId!}"/>
           <td class="idCol firstCol" ><a href="<@ofbizUrl>productDetail?productId=${relatedProduct.productId?if_exists}</@ofbizUrl>">${(relatedProduct.productId)?if_exists}</a></td>
           <td class="nameCol">${(relatedProdDetail.internalName)?if_exists}</td>
           <td class="longDescCol ">
           <input type="hidden" name="relatedProductName_${rowNo}" id="relatedProductName" value="${productContentWrapper.get("PRODUCT_NAME")!""}"/>
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
             <input type="text" class="infoValue small textAlignCenter" name="sequenceNum_${rowNo}" id="sequenceNum" value="${rowSeq!}"/>
           </td>
           <#assign productName = Static["com.osafe.util.OsafeAdminUtil"].formatSimpleText('${productContentWrapper.get("PRODUCT_NAME")!""}')/>
           <td class="actionCol">
             <a href="javascript:setRowNo('${rowNo}');javascript:deletTableRow('${relatedProduct.productId?if_exists}','${productName!""}');" onMouseover="javascript:showTooltip(event,'${uiLabelMap.DeleteRelatedProductTooltip}');" onMouseout="hideTooltip()"><span class="crossIcon"></span></a>
             <a href="javascript:setRowNo('${rowNo}');javascript:openLookup(document.${detailFormName!}.addProductId,document.${detailFormName!}.addProductName,'lookupProduct','500','700','center','true');" onMouseover="javascript:showTooltip(event,'${uiLabelMap.InsertBeforeNewRowTooltip}');" onMouseout="hideTooltip()"><span class="insertBeforeIcon"></span></a>
             <a href="javascript:setRowNo('${rowNo+1}');javascript:openLookup(document.${detailFormName!}.addProductId,document.${detailFormName!}.addProductName,'lookupProduct','500','700','center','true');" onMouseover="javascript:showTooltip(event,'${uiLabelMap.InsertAfterNewRowTooltip}');" onMouseout="hideTooltip()"><span class="insertAfterIcon"></span></a>           </td>
        </tr>
        <#if rowClass == "2">
          <#assign rowClass = "1">
        <#else>
          <#assign rowClass = "2">
        </#if>
        <#assign rowNo = rowNo+1/>
      </#list>

    <#elseif parameters.totalRows?exists>
      <#assign minRow = parameters.totalRows?number/>
      <#list 1..minRow as x>
        <tr class="dataRow <#if rowClass?if_exists == "2">even<#else>odd</#if>">
           <#assign relatedProductId = request.getParameter("relatedProductId_${x}")!/>
           <input type="hidden" name="relatedProductId_${x}" id="relatedProductId" value="${relatedProductId!}"/>
           <#assign relatedProdDetail = delegator.findOne("Product", {"productId" : relatedProductId}, false) />
           <#assign productContentWrapper = Static["org.ofbiz.product.product.ProductContentWrapper"].makeProductContentWrapper(relatedProdDetail, request)!""/>
           <#assign productLargeImageUrl = productContentWrapper.get("LARGE_IMAGE_URL")!"">
           <td class="idCol firstCol" ><a href="<@ofbizUrl>productDetail?productId=${relatedProductId?if_exists}</@ofbizUrl>">${(relatedProductId)?if_exists}</a></td>
           <td class="nameCol">${(relatedProdDetail.internalName)?if_exists}</td>
           <td class="longDescCol ">${productContentWrapper.get("PRODUCT_NAME")?html!""}
           <#assign relatedProductName = request.getParameter("relatedProductName_${x}")!productContentWrapper.get("PRODUCT_NAME")!""/>
           <input type="hidden" name="relatedProductName_${x}" id="relatedProductName" value="${relatedProductName!""}"/>
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
             <#assign sequenceNum = request.getParameter("sequenceNum_${x}")!/>
             <input type="text" class="infoValue small textAlignCenter" name="sequenceNum_${x}" id="sequenceNum" value="${sequenceNum!}"/>
           </td>
           <#assign productName = Static["com.osafe.util.OsafeAdminUtil"].formatSimpleText('${productContentWrapper.get("PRODUCT_NAME")!""}')/>
           <td class="actionCol">
             <a href="javascript:setRowNo('${x}');javascript:deletTableRow('${relatedProdDetail.productId?if_exists}','${productName!}');" onMouseover="javascript:showTooltip(event,'${uiLabelMap.DeleteRelatedProductTooltip}');" onMouseout="hideTooltip()"><span class="crossIcon"></span></a>
             <a href="javascript:setRowNo('${x}');javascript:openLookup(document.${detailFormName!}.addProductId,document.${detailFormName!}.addProductName,'lookupProduct','500','700','center','true');" onMouseover="javascript:showTooltip(event,'${uiLabelMap.InsertBeforeNewRowTooltip}');" onMouseout="hideTooltip()"><span class="insertBeforeIcon"></span></a>
             <a href="javascript:setRowNo('${x+1}');javascript:openLookup(document.${detailFormName!}.addProductId,document.${detailFormName!}.addProductName,'lookupProduct','500','700','center','true');" onMouseover="javascript:showTooltip(event,'${uiLabelMap.InsertAfterNewRowTooltip}');" onMouseout="hideTooltip()"><span class="insertAfterIcon"></span></a>           </td>
        </tr>
        <#if rowClass == "2">
          <#assign rowClass = "1">
        <#else>
          <#assign rowClass = "2">
        </#if>
      </#list>
    </#if>
    
    <tr class="dataRow" id="addIconRow" <#if (resultList?exists && resultList?has_content) || (minRow?exists && minRow &gt; 0)> style="display:none"</#if>>
      <td colspan="5">&nbsp;</td>
      <td class="actionCol">
        <span class="noAction"></span>
        <a href="javascript:setRowNo('1');javascript:openLookup(document.${detailFormName!}.addProductId,document.${detailFormName!}.addProductName,'lookupProduct','500','700','center','true');" onMouseover="javascript:showTooltip(event,'${uiLabelMap.InsertBeforeNewRowTooltip}');" onMouseout="hideTooltip()"><span class="insertBeforeIcon"></span></a>
        <span class="noAction"></span>
      </td>
    </tr>
  </tbody>
</table>