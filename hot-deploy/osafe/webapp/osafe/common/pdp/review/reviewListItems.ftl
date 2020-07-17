<#if productReviews?has_content>
  <div class="boxList reviewList" id="js_${reviewScreenPrefix!}reviewList">
	  <#list productReviews as productReview>
	    ${setRequestAttribute("productReview", productReview)}
	    ${screens.render("component://osafe/widget/EcommerceDivScreens.xml#${reviewScreenPrefix!}ReviewListItemsDivSequence")}
	  </#list>
  </div>
</#if>
