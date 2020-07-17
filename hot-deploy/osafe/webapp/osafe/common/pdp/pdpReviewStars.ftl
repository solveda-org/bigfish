<#assign reviewMethod = Static["com.osafe.util.Util"].getProductStoreParm(request,"REVIEW_METHOD")!""/>
<#if reviewMethod?has_content >
	<#if reviewMethod.toUpperCase() == "REEVOO" >
	        <#assign reevooBadgeurl = Static["com.osafe.util.Util"].getProductStoreParm(request,"REEVOO_BADGE_URL")!""/>
	        <#assign reevooTrkref = Static["com.osafe.util.Util"].getProductStoreParm(request,"REEVOO_TRKREF")!""/>
	        <#assign reevooSku = "">
	        <#if currentProduct?has_content>
	            <#assign productId = currentProduct.productId!"">
	        </#if>
	        <#assign reevooSku = "">
	        <#assign skuProduct = delegator.findByPrimaryKeyCache("GoodIdentification", Static["org.ofbiz.base.util.UtilMisc"].toMap("productId", productId!"", "goodIdentificationTypeId", "SKU"))?if_exists />
	        <#if skuProduct?has_content>
	            <#assign reevooSku = skuProduct.idValue!"">
	        <#else>
	            <#assign reevooSku = productId!"">
	        </#if>
	        <#assign reevooBadgeurl = reevooBadgeurl.concat("/").concat(reevooTrkref).concat("/").concat(reevooSku)>
	
	        <div class="pdpReviewStars">
		        <div class="reevoo-area">
		            <a class="reevoomark" href="${reevooBadgeurl}">${uiLabelMap.ReevooProductReviewLabel}</a>
		        </div>
	        </div> 
	</#if>
	<#if reviewMethod.toUpperCase() == "BIGFISH" >
	      <div class="pdpReviewStars">
	        <div id="productReviewCustomerRating">
	          <#if averageStarRating?has_content>
	            <label>${uiLabelMap.CustomerRatingLabel}</label>
	            <div class="reviewCustomerRating">
	                <#assign ratePercentage= ((averageStarRating?number / 5) * 100)>
	                <div class="customerRating">
	                    <div class="rating_bar"><div style="width:${ratePercentage}%"></div></div>
	                    <span class="ratingSummaryFinal">${averageStarRating}&nbsp;out&nbsp;of&nbsp;5</span>
	                </div>
	            </div>
	          </#if>
	        </div>
	      </div>
	</#if>
</#if>