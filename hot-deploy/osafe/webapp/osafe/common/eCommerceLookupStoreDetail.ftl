

<#if storeHoursContentId?has_content>
      <#assign storeHoursTextData = Static["org.ofbiz.content.content.ContentWorker"].renderContentAsText(dispatcher, delegator, storeHoursContentId, Static["javolution.util.FastMap"].newInstance(), locale, "", true)/>
      <#if storeHoursTextData?has_content && storeHoursTextData.equals("null")>
        <#assign storeHoursTextData = ""/>
      </#if>
    </#if>

    <#if storeNoticeContentId?has_content>
      <#assign storeNoticeTextData = Static["org.ofbiz.content.content.ContentWorker"].renderContentAsText(dispatcher, delegator, storeNoticeContentId, Static["javolution.util.FastMap"].newInstance(), locale, "", true)/>
      <#if storeNoticeTextData?has_content && storeNoticeTextData.equals("null")>
        <#assign storeNoticeTextData = ""/>
      </#if>
    </#if>
    
    <#if storeContentSpotContentId?has_content>
      <#assign storeContentSpotData = Static["org.ofbiz.content.content.ContentWorker"].renderContentAsText(dispatcher, delegator, storeContentSpotContentId, Static["javolution.util.FastMap"].newInstance(), locale, "", true)/>
      <#if storeContentSpotData?has_content && storeContentSpotData.equals("null")>
        <#assign storeContentSpotData = ""/>
      </#if>
    </#if>











<#if storeInfo?has_content>
<div class="checkoutOrderStorePickup">
	<div id="customerStorePickup">
	<div class="displayBox">


	    <div class="entryRow">
	      <div class="entry">
	         <div class="entryLabel">
	            <label>${uiLabelMap.StoreNameCaption}</label>
	         </div>
	         <div class="entryValue">
	            <span>${storeInfo.groupName?if_exists} (${storeInfo.groupNameLocal?if_exists})</span>
	         </div>
	      </div>
	    </div>


	    <div class="entryRow">
	      <div class="entry">
	         <div class="entryLabel">
	            <label>${uiLabelMap.AddressCaption}</label>
	         </div>
	         <div class="entryValue">
	            <span>
	              ${storeAddress.address1?if_exists} ${storeAddress.address2?if_exists} ${storeAddress.address3?if_exists}<br/>
	              <#if storeAddress.city?has_content>${storeAddress.city!""},</#if>
	              <#if storeAddress.stateProvinceGeoId?has_content>${storeAddress.stateProvinceGeoId!""}</#if>
	              <#if storeAddress.postalCode?has_content>${storeAddress.postalCode!""}</#if>
	            </span>
	         </div>
	      </div>
	    </div>



	    <div class="entryRow">
	      <div class="entry">
	         <div class="entryLabel">
	            <label>${uiLabelMap.StoreHoursCaption}</label>
	         </div>
	         <div class="entryValue">
	            <span>${Static["com.osafe.util.Util"].getFormattedText(storeHoursTextData)}</span>
	         </div>
	      </div>
	    </div>



	     <div class="entryRow">
	      <div class="entry">
	         <div class="entryLabel">
	            <label>${uiLabelMap.StoreNoticesCaption}</label>
	         </div>
	         <div class="entryValue">
	            <span>${Static["com.osafe.util.Util"].getFormattedText(storeNoticeTextData)}</span>
	         </div>
	      </div>
	    </div>



	    <div class="entryRow">
	      <div class="entry">
	        <div class="entryLabel">
	          <label>${uiLabelMap.StoreTelCaption}</label>
	        </div>
	        <div class="entryValue">
	          <span>
	            <#if storePhone?has_content>
	              <#if storePhone.contactNumber?has_content>
	                <#if storePhone.contactNumber?length &gt; 6>
	                <#assign contactPhoneNumber = storePhone.contactNumber!"">
	                <#assign areaCode = storePhone.areaCode!"">
	                <#assign fullPhone= Static["com.osafe.util.Util"].formatTelephone(areaCode, contactPhoneNumber?if_exists, FORMAT_TELEPHONE_NO!)/>
	                  ${fullPhone!""}
	                <#else>
	                <#if storePhone.areaCode?has_content>${storePhone.areaCode?if_exists}-</#if>
	                  ${storePhone.contactNumber?if_exists}
	                </#if>
	              </#if>
	            </#if>
	          </span>
	        </div>
	      </div>
	    </div>




	    
	</div>
	</div>
 </div>
</#if>

${Static["com.osafe.util.Util"].getFormattedText(storeContentSpotData)}

