<#-- Get the PDP Min, Max and Default Quantity of Finished Good Product -->
<#if isFinishedGood>
    <#if plpProductAtrributeMap?has_content  && plpProductAtrributeMap.get("PDP_QTY_DEFAULT")?has_content>
	    <#assign productAttrPdpQtyDefault = plpProductAtrributeMap.get("PDP_QTY_DEFAULT")!""/>
	</#if>
</#if>
<#if PDP_QTY_DEFAULT?has_content && Static["com.osafe.util.Util"].isNumber(PDP_QTY_DEFAULT) >
  <#assign PDP_QTY_DEFAULT = PDP_QTY_DEFAULT!"" />
<#else>  
  <#assign PDP_QTY_DEFAULT = 1 />
</#if>
<li class="${request.getAttribute('attributeClass')!}">
    <div id = "js_plp_div_qty_${uiSequenceScreen}_${plpProduct.productId}">
        <label>${uiLabelMap.QuantityLabel}</label>
        <#if inStock>
            <input type="text" class="quantity plpInStock" size="5" name="plp_qty_${uiSequenceScreen}_${plpProduct.productId}" id="js_plp_qty_${uiSequenceScreen}_${plpProduct.productId}" value="${productAttrPdpQtyDefault!PDP_QTY_DEFAULT!}" maxlength="5"/>
        <#else>
            <input type="text" class="quantity plpOutOfStock" size="5" name="plp_qty_${uiSequenceScreen}_${plpProduct.productId}" id="js_plp_qty_${uiSequenceScreen!}" value="${productAttrPdpQtyDefault!}" maxlength="5" disabled="disabled"/>
        </#if>
        <input type="hidden" class="plp_qty_user_modified" name="plp_qty_modified_${uiSequenceScreen}_${plpProduct.productId}" id="js_plp_qty_modified_${uiSequenceScreen}_${plpProduct.productId}" value="N"/>
    </div>
</li>
