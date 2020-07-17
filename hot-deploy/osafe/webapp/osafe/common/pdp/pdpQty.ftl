<#if !(pdpSelectMultiVariant?has_content) || (pdpSelectMultiVariant.toUpperCase() != "QTY" && pdpSelectMultiVariant.toUpperCase() != "CHECKBOX") >
  <li class="${request.getAttribute("attributeClass")!}">
    <div id="js_quantity_div">
      <#if currentProduct.isVirtual?if_exists?upper_case != "Y">
        <#if productAtrributeMap?exists && productAtrributeMap.get("PDP_QTY_DEFAULT")?has_content>
      	  <#assign productAttrPdpQtyDefault = productAtrributeMap.get("PDP_QTY_DEFAULT")!""/>
      	</#if>
      </#if>
      
      <#if PDP_QTY_DEFAULT?has_content && Static["com.osafe.util.Util"].isNumber(PDP_QTY_DEFAULT) >
        <#assign PDP_QTY_DEFAULT = PDP_QTY_DEFAULT!"" />
      <#else>  
        <#assign PDP_QTY_DEFAULT = 1 />
      </#if>
      
      <#if inStock>
        <label>${uiLabelMap.QuantityLabel}:</label>
        <input type="text" class="quantity" size="5" name="quantity" id="js_quantity" value="${parameters.quantity!productAttrPdpQtyDefault!PDP_QTY_DEFAULT!}" maxlength="5"/>
      <#elseif !isSellable>
        <label>${uiLabelMap.QuantityLabel}:</label>
        <input type="text" class="quantity" size="5" name="quantity" id="js_quantity" value="${parameters.quantity!productAttrPdpQtyDefault!PDP_QTY_DEFAULT!}" maxlength="5" disabled="disabled"/>
      </#if>
    </div>
  </li>
</#if>