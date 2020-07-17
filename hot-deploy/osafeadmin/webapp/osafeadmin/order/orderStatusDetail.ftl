<#if shipGroups?has_content>
    <#assign shipGroup = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(shipGroups)/> 
    <#assign shipGroupSeqId = shipGroup.shipGroupSeqId!""/>
    <#assign trackingNumber = shipGroup.trackingNumber!""/>
    <#assign orderShippingMethod = shipGroup.shipmentMethodTypeId + "@" + shipGroup.carrierPartyId>
    <#if currentStatus?has_content && currentStatus.statusId == "ORDER_COMPLETED">
        <#assign shipDate = shipGroup.estimatedShipDate!""/>
        <#if shipDate?has_content>
            <#assign estimatedShipDate = shipDate?string(preferredDateFormat)!""/>
        </#if>
    </#if>
</#if>
<div class="infoRow row">
    <div class="infoEntry long">
        <div class="infoCaption">
            <label>&nbsp;</label>
        </div>
        <div class="entryInput checkbox medium">
            <input class="checkBoxEntry" type="radio" id="changeStatusAll" name="changeStatusAll"  value="Y" checked="checked" />${uiLabelMap.ChangeStatusAllOrderItemLabel}
        </div>
    </div>
</div>

<div class="infoRow row">
    <div class="infoEntry long">
        <div class="infoCaption">
            <label>&nbsp</label>
        </div>
        <div class="entryInput checkbox medium">
            <input class="checkBoxEntry" type="radio" id="changeStatusAll" name="changeStatusAll" value="N" <#if (parameters.changeStatusAll?exists && parameters.changeStatusAll?string == "N")>checked="checked"</#if> />${uiLabelMap.ChangeStatusSelectedOrderItemLabel}
        </div>
    </div>
</div>
<input type="hidden" name="orderId" value="${parameters.orderId!orderHeader.orderId!}" />
<input type="hidden" name="internalNote" value="Y"/>
<input type="hidden" name="shipGroupSeqId" value="${shipGroupSeqId!}"/>
<div class="infoRow row">
    <div class="infoEntry long">
        <div class="infoCaption">
            <label>${uiLabelMap.OrderNewStatusCaption}</label>
        </div>
        <div class="infoValue">
            <#assign selectedStatusId = parameters.statusId!""/>
            <select name="statusId" id="statusId" class="small">
                <#if statusItems?has_content>
                    <#list statusItems as statusItem>
                        <option value='${statusItem.statusId!}' <#if selectedStatusId == statusItem.statusId>selected=selected</#if>>${statusItem.description?default(statusItem.statusId!)!}</option>
                    </#list>
                </#if>
            </select>
        </div>
    </div>
</div>

<div class="infoRow row COMPLETED">
    <div class="infoEntry long">
        <div class="infoCaption">
            <label>${uiLabelMap.OrderCarrierCaption}</label>
        </div>
        <div class="infoValue">
        	<#if isStorePickup?has_content && isStorePickup == "Y">
        		<input class="medium" disabled="disabled" type="text"  maxlength="20" value=""/>
        		<input name="shipmentMethod" type="hidden" id="shipmentMethod" maxlength="20" value="NO_SHIPPING@_NA_"/>
        	<#else>
        		<#assign productStoreId = Static["org.ofbiz.product.store.ProductStoreWorker"].getProductStoreId(request) />
	            <#assign carrierShipmentMethodList = delegator.findByAnd('ProductStoreShipmentMethView', {"productStoreId" : productStoreId})!"" />
	            <#assign selectedShippingMethod = parameters.shippingMethod!orderShippingMethod!""/>
	            <select name="shipmentMethod" id="shipmentMethod" class="small">
	                <#if carrierShipmentMethodList?has_content>
	                    <#list carrierShipmentMethodList as carrierMethod>
	                        <#assign shippingMethod = carrierMethod.shipmentMethodTypeId + "@" + carrierMethod.partyId>
	                        <#assign findCarrierShipmentMethodMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("shipmentMethodTypeId", carrierMethod.shipmentMethodTypeId, "partyId", carrierMethod.partyId,"roleTypeId" ,"CARRIER")>
	                        <#assign carrierShipmentMethod = delegator.findByPrimaryKeyCache("CarrierShipmentMethod", findCarrierShipmentMethodMap)>
	                        <#assign carrierPartyGroupName = ""/>
	                        <#if carrierMethod.partyId != "_NA_">
	                            <#assign carrierParty = carrierShipmentMethod.getRelatedOne("Party")/>
	                            <#assign carrierPartyGroup = carrierParty.getRelatedOne("PartyGroup")/>
	                            <#assign carrierPartyGroupName = carrierPartyGroup.groupName/>
	                        </#if>
	                        <option value='${shippingMethod}' <#if selectedShippingMethod == shippingMethod>selected=selected</#if>><#if carrierPartyGroupName?has_content>${carrierPartyGroupName!}, </#if>${carrierMethod.description!}</option>
	                    </#list>
	                </#if>
	            </select>
        	</#if>
            
        </div>
    </div>
</div>

<div class="infoRow row COMPLETED">
    <div class="infoEntry long">
        <div class="infoCaption">
            <label>${uiLabelMap.OrderTrackingCaption}</label>
        </div>
        <div class="infoValue">
        	<#if isStorePickup?has_content && isStorePickup == "Y">
        		<input class="medium" disabled="disabled" type="text"  maxlength="20" value="${parameters.trackingNumber!""}"/>
        		<input name="trackingNumber" type="hidden" id="trackingNumber" maxlength="20" value="${parameters.trackingNumber!""}"/>
        	<#else>
        		<input class="medium" name="trackingNumber" type="text" id="trackingNumber" maxlength="20" value="${parameters.trackingNumber!""}"/>
        	</#if>
            
        </div>
    </div>
</div>

<div class="infoRow row COMPLETED">
    <div class="infoEntry long">
        <div class="infoCaption">
        	<#if isStorePickup?has_content && isStorePickup == "Y">
        		<label>${uiLabelMap.OrderPickUpDateCaption}</label>
        	<#else>
        		<label>${uiLabelMap.OrderShipDateCaption}</label>
        	</#if>
            
        </div>
        <div class="infoValue">
            <input class="dateEntry" type="text" id="shipByDate" name="estimatedShipDate" maxlength="40" value="${parameters.estimatedShipDate!""}"/>
        </div>
    </div>
</div>

<div class="infoRow row">
    <div class="infoEntry long">
        <div class="infoCaption">
            <label>${uiLabelMap.NoteCaption}</label>
        </div>
        <div class="infoValue">
            <textarea class="smallArea" name="note" cols="50" rows="5">${parameters.note!""}</textarea>
        </div>
    </div>
</div>