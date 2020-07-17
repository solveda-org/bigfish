<#if storeDetailList?exists && storeDetailList?has_content && ((displayInitialStores?has_content && displayInitialStores == "Y") || userLocation?has_content)>
  <#list storeDetailList as storeRow>
    <div class="store">
      <div class="storeDist">
        <#if pickupStoreButtonVisible?has_content && pickupStoreButtonVisible =="Y">
          <form method="post" action="<@ofbizUrl>${storePickupFormAction!""}</@ofbizUrl>" class="pickupStore">
            <input type="hidden" name="storeId" value="${storeRow.partyId!}">
            <input type="submit" value="${uiLabelMap.SelectForPickupBtn}" class="standardBtn positive">
          </form>
        </#if>
        <#if userLocation?exists && userLocation?has_content>
          <P class="distance">${storeRow.distance!""}</P>
        </#if>
      </div>
      <div class="storeDetail">
        <ul>
          <li class="storeName">${storeRow.storeName!""} (${storeRow.storeCode!""})</li>
          <li class="storeAddress">
            <#if storeRow.address1?has_content>
              <span class="storeAdd1">${storeRow.address1!""},</span>
            </#if>
            <#if storeRow.address2?has_content>
              <span class="storeAdd2">${storeRow.address2!""},</span>
            </#if>
            <#if storeRow.address3?has_content>
              <span class="storeAdd3">${storeRow.address3!""},</span>
            </#if>
            <#if storeRow.city?has_content>
              <span class="storeCity">${storeRow.city!""},</span>
            </#if>
            <#if storeRow.stateProvinceGeoId?has_content>
              <span class="storeState">${storeRow.stateProvinceGeoId!""}</span>
            </#if>
            <#if storeRow.postalCode?has_content>
              <span class="storeZip">${storeRow.postalCode!""}</span>
            </#if>
          </li>
          <#if storeRow.openingHoursContentId?has_content>
            <#assign openingHours = Static["org.ofbiz.content.content.ContentWorker"].renderContentAsText(dispatcher, delegator, storeRow.openingHoursContentId, Static["javolution.util.FastMap"].newInstance(), locale, "", true)/>
            <#if openingHours?has_content && openingHours != "null">
               <li class="storehours">
                 <div class="label">${uiLabelMap.StoreLocatorHourCaption}</div>
                 <div class="value" >
                 ${Static["com.osafe.util.Util"].getFormattedText(openingHours)}
                 </div>
               </li>
            </#if>
          </#if>
          <#if storeRow.storeNoticeContentId?has_content>
            <#assign storeNotice = Static["org.ofbiz.content.content.ContentWorker"].renderContentAsText(dispatcher, delegator, storeRow.storeNoticeContentId, Static["javolution.util.FastMap"].newInstance(), locale, "", true)/>
            <#if storeNotice?has_content && storeNotice != "null">
               <li class="storeNotice">
                 <div class="label">${uiLabelMap.StoreLocatorNoticeCaption}</div>
                 <div class="value" >
                 ${Static["com.osafe.util.Util"].getFormattedText(storeNotice)}
                 </div>
               </li>
            </#if>
          </#if>
          <li class="storephone">
            <div class="label">${uiLabelMap.StoreLocatorPhoneCaption}</div>
            <#if storeRow.countryGeoId?has_content && (storeRow.countryGeoId == "USA" || storeRow.countryGeoId == "CAN")>
              <div class="value">${storeRow.areaCode!""} - ${storeRow.contactNumber3!""} - ${storeRow.contactNumber4!""}</div>
            <#else>
              <div class="value">${storeRow.contactNumber!""}</div>
            </#if>
          </li>
        </ul>
      </div>
      <div class="storeDirectionIcon">
        <#if userLocation?exists && userLocation?has_content>
          <a href="javascript:setDirections('${storeRow.searchAddress!""}', '${storeRow.latitude!""}, ${storeRow.longitude!""}', 'DRIVING');"><span class="drivingDirectionIcon"></span></a>
          <a href="javascript:setDirections('${storeRow.searchAddress!""}', '${storeRow.latitude!""}, ${storeRow.longitude!""}', 'WALKING');"><span class="walkingDirectionIcon"></span></a>
          <a href="javascript:setDirections('${storeRow.searchAddress!""}', '${storeRow.latitude!""}, ${storeRow.longitude!""}', 'BICYCLING');"><span class="bicyclingDirectionIcon"></span></a>
        </#if>
      </div>
  </div>
  </#list>
</#if>