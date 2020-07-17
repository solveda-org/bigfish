<#assign reviewMethod = Static["com.osafe.util.Util"].getProductStoreParm(request,"REVIEW_METHOD")!""/>
<#if reviewMethod?has_content >
	<#if (reviewMethod.toUpperCase() == "BIGFISH") || (reviewMethod.toUpperCase() == "REEVOO")>
		<#if Static["com.osafe.util.Util"].isProductStoreParmTrue(request,"REVIEW_SHOW_ON_PLP")>
		 <div class="plpReviewStars">
	    	<#if reviewMethod.toUpperCase() == "REEVOO">
	    		<#if Static["com.osafe.util.Util"].isProductStoreParmTrue(request,"REEVOO_SHOW_ON_PLP")>
			        <#assign reevooBadgeurl = Static["com.osafe.util.Util"].getProductStoreParm(request,"REEVOO_BADGE_URL")!""/>
			        <#assign reevooTrkref = Static["com.osafe.util.Util"].getProductStoreParm(request,"REEVOO_TRKREF")!""/>
			        <#assign reevooSku = "">
			        <#assign skuProduct = delegator.findByPrimaryKeyCache("GoodIdentification", Static["org.ofbiz.base.util.UtilMisc"].toMap("productId", plpProductId!"", "goodIdentificationTypeId", "SKU"))?if_exists />
			        <#if skuProduct?has_content>
			            <#assign reevooSku = skuProduct.idValue!"">
			        <#else>
			            <#assign reevooSku = plpProductId!"">
			        </#if>
			        <#assign reevooBadgeurl = reevooBadgeurl.concat("/").concat(reevooTrkref!"").concat("/").concat(reevooSku!"")>
			        <div class="reevoo-area">
			            <a class="reevoomark" href="${reevooBadgeurl}">${uiLabelMap.ReevooProductReviewLabel}</a>
			        </div>
		        </#if>
	        </#if>
	    	<#if reviewMethod.toUpperCase() == "BIGFISH">
		        <#if plpAverageStarRating?has_content>
		            <#assign ratePercentage= ((plpAverageStarRating / 5) * 100)>
		            <div class="rating_bar"><div style="width:${ratePercentage}%"></div></div>
		        </#if>
	        </#if>
		 </div>
		</#if>
	</#if>
</#if>