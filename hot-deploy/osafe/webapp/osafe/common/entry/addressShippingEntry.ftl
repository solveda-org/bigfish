<#if (!deliveryOption?has_content || deliveryOption != "SHIP_TO_MULTI") || (showAddressEntry?has_content && showAddressEntry == "Y")>
  <div class="${request.getAttribute("attributeClass")!}">
	<div id="js_${fieldPurpose?if_exists}_ADDRESS_ENTRY" class="displayBox">
	  <#include "component://osafe/webapp/osafe/common/entry/commonAddressEntry.ftl"/>
	</div>
  </div>
</#if>
