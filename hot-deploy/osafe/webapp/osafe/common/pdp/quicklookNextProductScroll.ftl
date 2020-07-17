<#if pdpNextProductUrl?has_content>
    <div class="pdpNextProductScroll">
        <input type="hidden" class="param" name="productId" id="productId" value="${nextProductId !}"/>
	    <input type="hidden" class="param" name="productCategoryId" value="${nextProductCategoryId !}"/>
        <a href="javaScript:void(0);" class="standardBtn productScroll" onClick="displayProductScrollActionDialogBox('${dialogPurpose!}',this);">${uiLabelMap.PdpNextProductScrollBtn}</span></a>
    </div>
</#if>