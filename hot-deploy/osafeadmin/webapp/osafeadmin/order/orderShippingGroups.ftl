
    <div class="infoRow column">
        <div class="infoEntry">
            <div class="infoCaption">
                <label>${uiLabelMap.ShipGroupCaption}</label>
            </div>
            <div class="infoValue">
                ${orderItemShipGroup.shipGroupSeqId!""}
            </div>
        </div>
    </div>
    
    <div class="infoRow column">
        <div class="infoEntry">
            <div class="infoCaption">
                <label>${uiLabelMap.ShipDateCaption}</label>
            </div>
            <div class="infoValue">
                <#if orderItemShipGroup.estimatedShipDate?has_content>
                    ${(orderItemShipGroup.estimatedShipDate?string(preferredDateFormat))!""}
                </#if>
            </div>
        </div>
    </div>
    
    <div class="infoRow column">
        <div class="infoEntry">
            <div class="infoCaption">
                <label>${uiLabelMap.ShipMethodCaption}</label>
            </div>
            <div class="infoValue">
                    <#assign shipmentMethodType = orderItemShipGroup.getRelatedOne("ShipmentMethodType")?if_exists>
                    <#if orderItemShipGroup.carrierPartyId?has_content>
		                <#assign carrier =  delegator.findByPrimaryKey("PartyGroup", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", orderItemShipGroup.carrierPartyId))?if_exists />
		                <#if carrier?has_content>${carrier.groupName?default(carrier.partyId)!}&nbsp;</#if>
		            </#if>
		            <#if shipmentMethodType?has_content>
		               ${shipmentMethodType.get("description","OSafeAdminUiLabels",locale)?default("")}
		            </#if>
            </div>
        </div>
    </div>
    
    <div class="infoRow column">
        <div class="infoEntry">
            <div class="infoCaption">
                <label>${uiLabelMap.TrackingNoCaption}</label>
            </div>
            <div class="infoValue">
                    ${orderItemShipGroup.trackingNumber!""}
            </div>
            <div class="infoIcon">
                <#if orderItemShipGroup?has_content && (orderItemShipGroup.carrierPartyId?has_content && orderItemShipGroup.carrierPartyId != "_NA_") && orderItemShipGroup.trackingNumber?has_content>
                    <#assign trackingURLPartyContents = delegator.findByAnd("PartyContent", {"partyId": orderItemShipGroup.carrierPartyId, "partyContentTypeId": "TRACKING_URL"})/>
                    <#if trackingURLPartyContents?has_content>
                        <#assign trackingURLPartyContent = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(trackingURLPartyContents)/>
                        <#if trackingURLPartyContent?has_content>
                            <#assign content = trackingURLPartyContent.getRelatedOne("Content")/>
                            <#if content?has_content>
                                <#assign dataResource = content.getRelatedOne("DataResource")!""/>
                                <#if dataResource?has_content>
                                    <#assign electronicText = dataResource.getRelatedOne("ElectronicText")!""/>
                                    <#assign trackingURL = electronicText.textData!""/>
                                    <#if trackingURL?has_content>
                                        <#assign trackingURL = Static["org.ofbiz.base.util.string.FlexibleStringExpander"].expandString(trackingURL, {"TRACKING_NUMBER":orderItemShipGroup.trackingNumber})/>
                                    </#if>
                                </#if>
                            </#if>
                        </#if>
                    </#if>
                </#if>
                <#if trackingURL?has_content>
                    <a href="JavaScript:newPopupWindow('${trackingURL!""}');" onMouseover="javascript:showTooltip(event,'${uiLabelMap.ShowTrackingInfo}');" onMouseout="hideTooltip()"><span class="shipmentDetailIcon"></span></a>
                </#if>
            </div>
        </div>
    </div>
    
    <div class="infoRow row">
        <div class="infoEntry">
            <div class="infoCaption">
                <label>${uiLabelMap.AddressCaption}</label>
            </div>
            <div class="infoValue">
	      <#assign contactMech = delegator.findByPrimaryKey("ContactMech", Static["org.ofbiz.base.util.UtilMisc"].toMap("contactMechId", orderItemShipGroup.contactMechId))?if_exists />
	      <#if contactMech?has_content>
	          <#assign postalAddress = contactMech.getRelatedOneCache("PostalAddress")?if_exists />
			    ${setRequestAttribute("PostalAddress", postalAddress)}
			    ${setRequestAttribute("DISPLAY_FORMAT", "SINGLE_LINE_FULL_ADDRESS")}
			    ${screens.render("component://osafeadmin/widget/CommonScreens.xml#displayPostalAddress")}
	      </#if>
            </div>
        </div>
    </div>
    
    <div class="infoRow row">
        <div class="infoEntry">
            <div class="infoCaption">
                <label>${uiLabelMap.ShipInstructionsCaption}</label>
            </div>
            <div class="infoValue">
                    ${orderItemShipGroup.shippingInstructions!""}
            </div>
        </div>
    </div>

