<div class="${request.getAttribute("attributeClass")!}">
    <h3>${uiLabelMap.SeeReviewInfo}</h3>
    <ul class="displayList tipBoxTips">
	    <li><span>${uiLabelMap.ReviewTextInfo}</span></li>
	    <li><span>${uiLabelMap.UseProductReviewInfo}</span></li>
	    <li><span>${uiLabelMap.FocusFeaturesInfo}</span></li>
	    <li><span>${uiLabelMap.PleaseAvoidInfo}</span></li>
	</ul> 
    <ul class="displayList tipBoxTipsSubItem"> 
		    <li><span>${uiLabelMap.InformationChangesInfo}</span></li>
		    <li><span>${uiLabelMap.InappropriateLanguageInfo}</span></li>
		    <li><span>${uiLabelMap.InformationOtherInfo}</span></li>
		    <li><span>${uiLabelMap.DetailedPersonalInfo}</span></li>
		    <li>
		     <span>${uiLabelMap.HaveSomethingToSayInfo}</span>
		     <a target="_blank" href="<@ofbizUrl>contactUs</@ofbizUrl>"><span>${uiLabelMap.CustomerServiceInfo}</span></a>
		     <span>${uiLabelMap.YourPrivateCommentInfo}</span>
		    </li>
    </ul>
</div>