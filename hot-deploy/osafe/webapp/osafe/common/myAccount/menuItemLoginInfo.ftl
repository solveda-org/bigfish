<#assign party = userLogin.getRelatedOneCache("Party")>
<#assign fbPartyAttribute = delegator.findOne("PartyAttribute", {"partyId" : party.partyId, "attrName" : "FACEBOOK_USER"}, true)?if_exists />  
<#if fbPartyAttribute?has_content>
	<#assign fbPartyAttributeVal = fbPartyAttribute.attrValue! />
</#if>
<#if !fbPartyAttributeVal?has_content || (fbPartyAttributeVal?has_content && fbPartyAttributeVal !="TRUE")>
	<div class="${request.getAttribute("attributeClass")!}">
		<h2>${uiLabelMap.CustomerLoginHeading?if_exists}</h2>
		<ul class="displayList">
		 <li>
		  <div>
		   <p>${uiLabelMap.CustomerLoginInfo}</p>
		  </div>
		 </li>
		 <li>
		  <div>
		   <a href="<@ofbizUrl>eCommerceEditLoginInfo</@ofbizUrl>"><span>${uiLabelMap.ClickLoginDetailsInfo}</span></a>
		  </div>
		 </li>
		</ul>
	</div>
</#if>	

