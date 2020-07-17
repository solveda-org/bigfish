<div class="container button">
     <input type="submit" value="${uiLabelMap.SubmitBtn}" class="standardBtn action"/>
     <a href="<@ofbizUrl>eCommerceProductDetail?productId=${productId?if_exists}</@ofbizUrl>" class="standardBtn negative">${uiLabelMap.CancelBtn}</a>
	 <span class="productReviewBarLinks">
		 <span class="previewBarLink">
		    <a name="termAndCondLink" style="display: none;" id="termAndCondLink" href="<@ofbizUrl>ratingsTermsAndConditions</@ofbizUrl>"></a>
		    <a name="previewTermsAndConds" href="javascript:void(window.open(document.getElementById('termAndCondLink').href,null,'left=50,top=50,width=500,height=500,toolbar=1,location=0,resizable=1,scrollbars=1'))">${uiLabelMap.TermsConditionsLabel}</a>
		 </span>
		 <span class="previewBarLink">
		    <a name="guidelinesLink" style="display: none;" id="guidelinesLink" href="<@ofbizUrl>ratingsGuideLines</@ofbizUrl>"></a>
		    <a name="previewGuidelines" href="javascript:void(window.open(document.getElementById('guidelinesLink').href,null,'left=50,top=50,width=500,height=500,toolbar=1,location=0,resizable=1,scrollbars=1'))">${uiLabelMap.ReviewGuidelinesLabel}</a>
		 </span>
     </span>
</div>

