<#if storeRow.storeNoticeContentId?has_content>
 <#assign storeNotice = Static["org.ofbiz.content.content.ContentWorker"].renderContentAsText(dispatcher, delegator, storeRow.storeNoticeContentId, Static["javolution.util.FastMap"].newInstance(), locale, "", true)/>
 <#if storeNotice?has_content && storeNotice != "null">
	<li class="${request.getAttribute("attributeClass")!}">
	    <div>
          <label>${uiLabelMap.StoreLocatorNoticeCaption}</label>
	      <span>${Static["com.osafe.util.Util"].getFormattedText(storeNotice)}</span>
	    </div>
	</li>
 </#if>
</#if>
