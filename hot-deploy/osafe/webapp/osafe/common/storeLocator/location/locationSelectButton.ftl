<#if pickupStoreButtonVisible?has_content && pickupStoreButtonVisible =="Y">
	<li class="${request.getAttribute("attributeClass")!}">
		<div>
	           <a href="javascript:submitCheckoutForm(null, 'SP', '${storeRow.partyId!}');"  class="standardBtn positive"><span>${uiLabelMap.SelectForPickupBtn}</span></a>
		</div>
	</li>
</#if>
