<#if storeRow.openingHoursContentId?has_content>
 <#assign openingHours = Static["org.ofbiz.content.content.ContentWorker"].renderContentAsText(dispatcher, delegator, storeRow.openingHoursContentId, Static["javolution.util.FastMap"].newInstance(), locale, "", true)/>
 <#if openingHours?has_content && openingHours != "null">
	<li class="${request.getAttribute("attributeClass")!}">
	    <div>
          <label>${uiLabelMap.StoreLocatorHourCaption}</label>
	      <span>${Static["com.osafe.util.Util"].getFormattedText(openingHours)}</span>
	    </div>
	</li>
 </#if>
</#if>
