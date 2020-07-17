<#if mode?has_content>
  <#if carrier?has_content>
    <#assign groupName = carrier.groupName! />
    <#assign carrierPartyId = carrier.partyId! />
    <#if carrierPartyId?has_content>
      <input type="hidden" name="partyId" value="${parameters.carrierPartyId!carrierPartyId!""}"/>
    </#if>
    <#assign trackingURLPartyContents = delegator.findByAnd("PartyContent", {"partyId": carrier.partyId, "partyContentTypeId": "TRACKING_URL"})/>
          <#if trackingURLPartyContents?has_content>
              <#assign trackingURLPartyContent = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(trackingURLPartyContents)/>
              <#if trackingURLPartyContent?has_content>
                <#assign content = trackingURLPartyContent.getRelatedOne("Content")/>
                <#if content?has_content>
                 <#assign contentId=trackingUrlId/>
                 <#assign dataResource = content.getRelatedOne("DataResource")!""/>
                 <#if dataResource?has_content>
                     <#assign electronicText = dataResource.getRelatedOne("ElectronicText")!""/>
                     <#assign trackingURL = electronicText.textData!""/>
                 </#if>
                  <input type="hidden" name="contentId" value="${parameters.contentId!contentId!""}"/>
                  <input type="hidden" name="partyContentTypeId" value="TRACKING_URL"/>   
                </#if>
              </#if>
          </#if>
  </#if>
  <#if trackingUrlDataResourceId?has_content>
      <input type="hidden" name="trackingUrlDataResourceId" value="${parameters.trackingUrlDataResourceId!trackingUrlDataResourceId!""}"/>
  </#if>

  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.CarrierIdCaption}</label>
      </div>
      <div class="infoValue">
        <#if mode="add">
          <input name="carrierPartyId" type="text" id="carrierPartyId" maxlength="20" value="${parameters.carrierPartyId!carrierPartyId!""}"/>
        <#else>
          ${parameters.carrierPartyId!carrierPartyId!""}
          <input name="carrierPartyId" type="hidden" id="carrierPartyId" maxlength="20" value="${parameters.carrierPartyId!carrierPartyId!""}"/>
        </#if>
      </div>
    </div>
  </div>
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.DescriptionCaption}</label>
      </div>
      <div class="infoValue">
        <input name="groupName" type="text" id="groupName" maxlength="20" value="${parameters.groupName!groupName!""}"/>
      </div>
    </div>
  </div>

    <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.TrackingUrlCaption}</label>
      </div>
      <div class="infoValue">
       <textarea class="smallArea characterLimit" name="trackingURL" >${parameters.trackingURL!trackingURL!""}</textarea>
      </div>
    </div>
  </div>
<#else>
    ${screens.render("component://osafeadmin/widget/CommonScreens.xml#ListNoDataResult")}
</#if>
