<#if pdpPrevProductUrl?has_content>
    <div class="pdpPrevProductScroll">
        <input type="hidden" class="param" name="productId" id="productId" value="${prevProductId !}"/>
	    <input type="hidden" class="param" name="productCategoryId" value="${prevProductCategoryId !}"/>
        <a href="javaScript:void(0);" class="standardBtn productScroll" onClick="displayProductScrollActionDialogBox('${dialogPurpose!}',this);"><span>${uiLabelMap.PdpPrevProductScrollBtn}</span></a>
    </div>
</#if>