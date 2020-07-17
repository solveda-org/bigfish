<#if Static["com.osafe.util.Util"].isProductStoreParmTrue(request,"REVIEW_ACTIVE_FLAG")>
    <#assign reviewMethod = Static["com.osafe.util.Util"].getProductStoreParm(request,"REVIEW_METHOD")!""/>
	<#if reviewMethod?has_content >
		<#if reviewMethod = "REEVOO">
		        <#assign reevooJsurl = Static["com.osafe.util.Util"].getProductStoreParm(request,"REEVOO_JS_URL")!"">
		        <#assign reevooBadgeurl = Static["com.osafe.util.Util"].getProductStoreParm(request,"REEVOO_BADGE_URL")!"">
		        <#assign reevooTrkref = Static["com.osafe.util.Util"].getProductStoreParm(request,"REEVOO_TRKREF")!"">
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
		        <#assign reevooJsurl = reevooJsurl.concat("/").concat(reevooTrkref).concat(".js")>
		        <#assign reevooBadgeurl = reevooBadgeurl.concat("/").concat(reevooTrkref).concat("/").concat(reevooSku)>
		
		        <script src="${reevooJsurl}" type="text/javascript" charset="utf-8"></script>
		        <script type="text/javascript" charset="utf-8">
		            ReevooMark.init_badges();
		        </script>
		        <div id="pdpCustomerRating">
			        <div class="reevoo-area">
			            <a class="reevoomark" href="${reevooBadgeurl}">${uiLabelMap.ReevooProductReviewLabel}</a>
			        </div>
		        </div>
		</#if>	    
		<#if reviewMethod = "BIGFISH">	  
		      <div id="pdpCustomerRating">
		        <div id="productReviewCustomerRating">
		            <label>${uiLabelMap.CustomerRatingLabel}</label>
		            <div class="reviewCustomerRating">
		            <#if customerRating?exists>
		                <#assign ratePercentage= ((customerRating / 5) * 100)>
		                <div class="customerRating">
		                    <div class="rating_bar"><div style="width:${ratePercentage}%"></div></div>
		                    <span class="ratingSummaryFinal">${customerRating}&nbsp;out&nbsp;of&nbsp;5</span>
		                </div>
		                <div class="customerRatingLinks">
		                    <a href="#productReviews" title="Read all reviews" id="BVSummaryReadReviewsLink">Read ${reviewSize} Review(s)</a>
		                    <#if Static["com.osafe.util.Util"].isProductStoreParmTrue(request,"REVIEW_WRITE_REVIEW")>
		                        &nbsp;&#124;&nbsp;<a href="<@ofbizUrl>eCommerceProductReviewSubmit?productId=${product_id?if_exists}&productCategoryId=${productCategoryId?if_exists}<#if !userLogin?has_content>&amp;review=review</#if></@ofbizUrl>" title="${uiLabelMap.WriteReviewLabel}" id="submitPageReview">${uiLabelMap.WriteReviewLabel}</a>
		                    </#if>
		                </div>
		            <#elseif Static["com.osafe.util.Util"].isProductStoreParmTrue(request,"REVIEW_WRITE_REVIEW")>
		                <div class="customerRatingLinks"><a href="<@ofbizUrl>eCommerceProductReviewSubmit?productId=${product_id?if_exists}&productCategoryId=${productCategoryId?if_exists}<#if !userLogin?has_content>&amp;review=review</#if></@ofbizUrl>" title="${uiLabelMap.FirstToReviewLabel}" id="submitPageReview">${uiLabelMap.FirstToReviewLabel}</a></div>    
		            </#if>
		            </div>
		        </div>
		      </div>  
		</#if>
    </#if>
</#if>