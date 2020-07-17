 <div id="js_${reviewScreenPrefix!}reviewPagingList" class="${request.getAttribute("attributeClass")!}">
  <div class="paginationContainer">
   <#if viewIndex?exists && viewIndex?has_content >
    <#if (viewIndex > 1)>
      &lt;&lt;
      <span id="prevPageLink"><a name="previousPage" href="<@ofbizUrl>eCommerceProductDetail?productId=${productId}&viewSize=${viewSize}&viewIndex=${viewIndex-1}&sortReviewBy=${sortReviewBy?if_exists}&tab=productReviews</@ofbizUrl>"><span>${uiLabelMap.PreviousLabel}</span></a></span>
    </#if>
    <#if viewPages &gt; 1>
      <#list 0 .. viewPages-1 as page>
        <#assign pageNum=page+1/>
        <#if pageNum == viewIndex>
          ${viewIndex}
        <#else>
          <span id="pageLink"><a name="pageNumber_${pageNum}" href="<@ofbizUrl>eCommerceProductDetail?productId=${productId}&viewSize=${viewSize}&viewIndex=${pageNum}&sortReviewBy=${sortReviewBy?if_exists}&tab=productReviews</@ofbizUrl>"><span>${pageNum}</span></a></span>
        </#if>
      </#list>
    </#if>
    <#if (listSize > highIndex)>
      <span id="nextPageLink"><a name="nextPage" href="<@ofbizUrl>eCommerceProductDetail?productId=${productId}&viewSize=${viewSize}&viewIndex=${viewIndex+1}&sortReviewBy=${sortReviewBy?if_exists}&tab=productReviews</@ofbizUrl>"><span>${uiLabelMap.NextLabel}</span></a></span>
      &gt;&gt;
    </#if>
   </#if>
  </div>
</div>
