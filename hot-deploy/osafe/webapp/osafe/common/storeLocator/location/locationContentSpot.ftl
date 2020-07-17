<#if storeRow.storeContentSpotContentId?has_content>
 <#assign storeContentSpot = Static["org.ofbiz.content.content.ContentWorker"].renderContentAsText(dispatcher, delegator, storeRow.storeContentSpotContentId, Static["javolution.util.FastMap"].newInstance(), locale, "", true)/>
 <#if storeContentSpot?has_content && storeContentSpot != "null">
	<li class="${request.getAttribute("attributeClass")!}">
	    <div>
	      <span>${Static["com.osafe.util.Util"].getFormattedText(storeContentSpot)}</span>
	    </div>
	</li>
 </#if>
</#if>
