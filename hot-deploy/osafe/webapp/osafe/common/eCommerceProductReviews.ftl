<#if Static["com.osafe.util.Util"].isProductStoreParmTrue(REVIEW_ACTIVE_FLAG!"")>
<script language="JavaScript" type="text/javascript">
<!--
    function sortReviews() {
          document.sortReviewForm.sortReviewBy.value=document.getElementById('reviewSort').value;
          document.sortReviewForm.submit();
    }
// -->
</script>

<#if productReviews?has_content>
<a name="productReviews"></a>
<form type="POST" action="<@ofbizUrl>eCommerceProductDetail#productReviews</@ofbizUrl>" name="sortReviewForm">
    <input type="hidden" name="productId" value="${productId?if_exists}">
    <input type="hidden" name="viewSize" value="${viewSize?if_exists}">
    <input type="hidden" name="viewIndex" value="${viewIndex?if_exists}">
    <input type="hidden" name="sortReviewBy" value="">
</form>

<div id="productReviewDisplay" class="displayBox">
    <div class="displayBoxHeader">
	   <span class="displayBoxHeaderCaption">${uiLabelMap.ProductReviewsHeading}</span>
		 <div id="reviewSortOptions">
		     <label>${uiLabelMap.SortByCaption}</label>
		     <select onchange="sortReviews();return false;" name="reviewSort" class="sortDropdown" id="reviewSort">
		         <option class="sortDropdown" id="default-desc" value="-productRating" <#if sortReviewBy == "-productRating"> SELECTED</#if>>
		           ${uiLabelMap.SortReviewByRatingHighLabel}
		          </option>
		          <option class="sortDropdown" id="featured-desc" value="productRating" <#if sortReviewBy == "productRating"> SELECTED</#if>>
		            ${uiLabelMap.SortReviewByRatingLowLabel}
		          </option>
		          <option class="sortDropdown" id="rank-desc" value="-postedDateTime" <#if sortReviewBy == "-postedDateTime"> SELECTED</#if>>
		            ${uiLabelMap.SortReviewByDateHighLabel}
		          </option>
		          <option class="sortDropdown" id="affiliation-desc" value="postedDateTime" <#if sortReviewBy == "postedDateTime"> SELECTED</#if>>
		            ${uiLabelMap.SortReviewByDateLowLabel}
		          </option>
		     </select>
		 </div>
	</div>
	<div class="reviewBody">
	 <#list productReviews as productReview>
	   <#assign overallRate=productReview.getBigDecimal("productRating").setScale(decimals,rounding)/>
	   <#assign quality=productReview.getBigDecimal("qualityRating")?if_exists/>
	   <#if quality?has_content>
	       <#assign qualityRate=quality.setScale(decimals,rounding)/>
	   <#else>
	       <#assign qualityRate=0/>
	   </#if>
	   <#assign effectiveness=productReview.getBigDecimal("effectivenessRating")?if_exists/>
	   <#if effectiveness?has_content>
	       <#assign effectivenessRate=effectiveness.setScale(decimals,rounding)/>
	   <#else>
	       <#assign effectivenessRate=0/>
	   </#if>
	   <#assign satisfaction=productReview.getBigDecimal("satisfactionRating")?if_exists/>
	   <#if satisfaction?has_content>
	       <#assign satisfactionRate=satisfaction.setScale(decimals,rounding)/>
	   <#else>
	       <#assign satisfactionRate=0/>
	   </#if>
	   <#if (productReview.postedDateTime)?has_content>
	       <#assign postedDate = (Static["com.osafe.util.Util"].convertDateTimeFormat(productReview.postedDateTime, FORMAT_DATE_TIME))!"N/A">
	   </#if>
	     <div class="reviewRow">
		      <div class="reviewRatingSection">
		         <#assign ratePercentage= ((overallRate / 5) * 100)>
		         <div class="rating_bar"><div style="width:${ratePercentage}%"></div></div>
		         <span class="ratingSummary">${overallRate}&nbsp;out&nbsp;of&nbsp;&nbsp;5</span>
		      </div>
		      <div class="reviewDataSection">
		         <span class="ratingNickname">${productReview.reviewNickName?if_exists}</span>
		         <br>
		         <span class="ratingTitle">${productReview.reviewTitle?if_exists}</span>
		      </div>
		      <div class="reviewPostDateSection">
		         <span class="reviewDate">${postedDate!""}</span>
		      </div>
		      <div class="reviewTextSection">
		         <span class="reviewText">${Static["com.osafe.util.Util"].getFormattedText(productReview.productReview!)}</span>
		      </div>
	     </div>	   
	 </#list>
	</div>
	<div class="pagingContainer">
	    <div class="paginationContainer">
	        <#if (viewIndex > 1)>
	             &lt;&lt;
	               <span id="prevPageLink"><a name="previousPage" href="<@ofbizUrl>eCommerceProductDetail?productId=${productId}&viewSize=${viewSize}&viewIndex=${viewIndex-1}&sortReviewBy=${sortReviewBy?if_exists}#productReviews</@ofbizUrl>">prev</a></span>
	        </#if>
	        <#if viewPages &gt; 1>
	          <#list 0 .. viewPages-1 as page>
	            <#assign pageNum=page+1/>
	            <#if pageNum == viewIndex>
	              ${viewIndex}
	            <#else>
	              <span id="pageLink"><a name="pageNumber_${pageNum}" href="<@ofbizUrl>eCommerceProductDetail?productId=${productId}&viewSize=${viewSize}&viewIndex=${pageNum}&sortReviewBy=${sortReviewBy?if_exists}#productReviews</@ofbizUrl>">${pageNum}</a></span>
	            </#if>
	          </#list>
	        </#if>
	        <#if (listSize > highIndex)>
	               <span id="nextPageLink"><a name="nextPage" href="<@ofbizUrl>eCommerceProductDetail?productId=${productId}&viewSize=${viewSize}&viewIndex=${viewIndex+1}&sortReviewBy=${sortReviewBy?if_exists}#productReviews</@ofbizUrl>">next</a></span>
	             &gt;&gt;
	        </#if>
	     </div>
	
	</div>
</div>
</#if>
</#if>