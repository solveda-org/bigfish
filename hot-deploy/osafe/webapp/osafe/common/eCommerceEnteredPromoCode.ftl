<#assign shoppingCart = sessionAttributes.shoppingCart?if_exists />
<#if (shoppingCart.size() > 0)>
    <#assign productPromoCodesEntered = shoppingCart.getProductPromoCodesEntered().clone()/>
    <#assign productPromoUseInfoList = Static["javolution.util.FastList"].newInstance()/>
    <#list shoppingCart.getProductPromoUseInfoIter() as productPromoUsed>
        <#assign removed = productPromoCodesEntered.remove(productPromoUsed.productPromoCodeId)/>
        <#assign productPromoUseMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("productPromoCodeId", productPromoUsed.productPromoCodeId, "productPromoId", productPromoUsed.productPromoId, "totalDiscountAmount", productPromoUsed.totalDiscountAmount, "quantityLeftInActions", productPromoUsed.quantityLeftInActions)/>
        <#assign changed = true/>
        <#list productPromoUseInfoList as productPromoUseInfo>
          <#if productPromoUseInfo.productPromoCodeId?has_content && productPromoUsed.productPromoCodeId?has_content>
            <#if productPromoUseInfo.productPromoCodeId == productPromoUsed.productPromoCodeId && productPromoUseInfo.productPromoId == productPromoUsed.productPromoId >
                <#assign changed = false/>
            </#if>
          </#if>
        </#list>
        <#if changed>
            <#assign changed = productPromoUseInfoList.add(productPromoUseMap)/>
        </#if>
    </#list>
    <#list productPromoCodesEntered.iterator() as productPromoCodeEntered>
        <#assign productPromoCode = delegator.findByPrimaryKeyCache("ProductPromoCode", {"productPromoCodeId" : productPromoCodeEntered})/>
        <#assign productPromoUseMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("productPromoCodeId", productPromoCodeEntered, "productPromoId", productPromoCode.productPromoId!, "totalDiscountAmount", 0, "quantityLeftInActions", null)/>
        <#assign changed = productPromoUseInfoList.add(productPromoUseMap)/>
    </#list>
   <div class="boxList promoCodeList">
	    <#list productPromoUseInfoList as productPromoUseInfo>
		  <div class="boxListItemTabular actionResultItem promoCodeItem">
		    <ul class="displayList">
			 <li class="id promoCode">
			  <div>
                <label>${uiLabelMap.PromoCodeCaption}</label>
	            <#if productPromoUseInfo.productPromoCodeId?has_content>
	                <span>${productPromoUseInfo.productPromoCodeId!""}</span>
	            <#elseif productPromoUseInfo.productPromoId?has_content>
	                <#assign productPromo = delegator.findByPrimaryKeyCache("ProductPromo", {"productPromoId" : productPromoUseInfo.productPromoId})/>
	                <#if productPromo?has_content>
	                  <span>${productPromo.promoName!""}</span>
	                </#if>
	            </#if>
	          </div>
	        </li>
	        <li class="string promoDescription">
	         <div>
                <label>${uiLabelMap.PromotionCaption}</label>
	            <#if productPromoUseInfo.productPromoId?has_content>
	                <#assign productPromo = delegator.findByPrimaryKeyCache("ProductPromo", {"productPromoId" : productPromoUseInfo.productPromoId})/>
	                <#if productPromo?has_content>
	                    <span>${productPromo.promoText!""}</span>
	                </#if>
	            </#if>
	        </div>
	       </li>
	       <li class="string promoSummary">
	        <div>
                <label>${uiLabelMap.MessageCaption}</label>
		        <#if (productPromoUseInfo.quantityLeftInActions?string == null) || (productPromoUseInfo.quantityLeftInActions?double > 0)>
		             <span>${uiLabelMap.PromoCodeAddedOnlyInfo}</span>
		        <#else>
		             <span>${uiLabelMap.PromoCodeAppliedInfo}</span>
		        </#if>
		    </div>
		   </li>
		   <li class="action remove">
		    <div>
		        <#if productPromoUseInfo.productPromoCodeId?has_content>
		           <a href="javascript:removePromoCode('${productPromoUseInfo.productPromoCodeId}');"><span>${uiLabelMap.RemoveOfferLabel}</span></a>
		        </#if>
		    </div>
	       </li>
	      </ul>
		 </div>
	    </#list>
   </div>
</#if>
