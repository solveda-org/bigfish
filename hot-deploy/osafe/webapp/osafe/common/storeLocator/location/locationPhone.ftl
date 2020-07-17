<li class="${request.getAttribute("attributeClass")!}">
	<div>
	  <label>${uiLabelMap.StoreLocatorPhoneCaption}</label>
	  <span>
            <#if storeRow.countryGeoId?has_content && (storeRow.countryGeoId == "USA" || storeRow.countryGeoId == "CAN")>
              ${storeRow.areaCode!""} - ${storeRow.contactNumber3!""} - ${storeRow.contactNumber4!""}
            <#else>
              ${storeRow.contactNumber!""}
            </#if>
	  </span>
	</div>
</li>
