<#if shipGroups?has_content>
    <#assign shipGroup = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(shipGroups)/> 
    <#assign shipGroupSeqId = shipGroup.shipGroupSeqId!""/>
    <#assign trackingNumber = shipGroup.trackingNumber!""/>
    <#if shipGroup.shipmentMethodTypeId?has_content && shipGroup.carrierPartyId?has_content>
        <#assign orderShippingMethod = shipGroup.shipmentMethodTypeId + "@" + shipGroup.carrierPartyId />
    </#if>
    <#if currentStatus?has_content && currentStatus.statusId == "ORDER_COMPLETED">
        <#assign shipDate = shipGroup.estimatedShipDate!""/>
        <#if shipDate?has_content>
            <#assign estimatedShipDate = shipDate?string(entryDateTimeFormat)!""/>
        </#if>
    </#if>
</#if>

<input type="hidden" name="fromPartyId" value="${partyId?if_exists}"/>
<input type="hidden" name="toPartyId" value="${toPartyId?if_exists}"/>
<input type="hidden" name="needsInventoryReceive" value="${parameters.needsInventoryReceive?default("Y")}"/>
<input type="hidden" name="destinationFacilityId" value="${destinationFacilityId?if_exists}"/>
<input type="hidden" name="returnHeaderTypeId" value="${returnHeaderTypeId}"/>
<#if (orderHeader?has_content) && (orderHeader.currencyUom?has_content)>
  <input type="hidden" name="currencyUomId" value="${orderHeader.currencyUom}"/>
</#if>

<#if orderPaymentPreference?has_content>
  <input type="hidden" name="paymentMethodId" value="${orderPaymentPreference.paymentMethodId!}"/>
</#if>

<#list shippingContactMechList as shippingContactMech>
  <#assign shippingAddress = shippingContactMech.getRelatedOne("PostalAddress")>
  <input type="hidden" name="originContactMechId" value="${shippingAddress.contactMechId!}" />
  <#break>
</#list>

<input type="hidden" id="changeStatusAll" name="changeStatusAll" value="N"/>
<input type="hidden" id="statusId" name="statusId" value="${parameters.statusId!}"/>
<div class="infoRow row">
    <div class="infoEntry long">
        <div class="infoCaption">
            <label>${uiLabelMap.ActionCaption}</label>
        </div>
        <div class="infoValue">
          <div class="entry checkbox medium">
            <input class="checkBoxEntry" type="radio" <#if orderHeader.statusId == "ORDER_COMPLETED">disabled="disabled"</#if> id="actionIdCancel" name="actionId"  value="cancelOrder" checked="checked" />${uiLabelMap.CancelAnOrderLabel}<br/>
            <input class="checkBoxEntry" type="radio" disabled="disabled" id="actionIdChaneQty" name="actionId" value="changeOrderQty" <#if (parameters.actionId?exists && parameters.actionId?string == "changeOrderQty")>checked="checked"</#if> />${uiLabelMap.ChangeOrderQtyLabel}<br/>
            <input class="checkBoxEntry" type="radio" <#if orderHeader.statusId != "ORDER_APPROVED">disabled="disabled"</#if> id="actionIdComplete" name="actionId" value="completeOrder" <#if (parameters.actionId?exists && parameters.actionId?string == "completeOrder")>checked="checked"</#if> />${uiLabelMap.CompleteAnOrderLabel}<br/>
            <input class="checkBoxEntry" type="radio" id="actionIdReturn" name="actionId" value="productReturn" <#if (parameters.actionId?exists && parameters.actionId?string == "productReturn")>checked="checked"</#if> />${uiLabelMap.ProductReturnsLabel}
          </div>
        </div>
        <#if orderHeader.statusId == "ORDER_APPROVED" && allItemsApproved == true>
            <div class="infoIcon"> 
                <a href="javascript:quickShipOrder('${detailFormName!""}');" class="buttontext standardBtn action">${uiLabelMap.QuickShipBtn}</a>
            </div>
            <div class="infoIcon"> 
                <a href="javascript:quickCancelOrder('${detailFormName!""}');" class="buttontext standardBtn action">${uiLabelMap.QuickCancelBtn}</a>
            </div>
        </#if>
        <#if orderHeader.statusId == "ORDER_COMPLETED" && allItemsCompleted == true>
            <div class="infoIcon"> 
                <a href="javascript:quickReturnOrder('${detailFormName!""}');" class="buttontext standardBtn action">${uiLabelMap.QuickReturnBtn}</a>
            </div>
        </#if>
    </div>
</div>

<input type="hidden" name="orderId" value="${parameters.orderId!orderHeader.orderId!}" />
<input type="hidden" name="internalNote" value="Y"/>
<input type="hidden" name="shipGroupSeqId" value="${shipGroupSeqId!}"/>

<div class="infoRow row COMPLETED">
    <div class="infoEntry long">
        <div class="infoCaption">
            <label>${uiLabelMap.OrderCarrierCaption}</label>
        </div>
        <div class="infoValue">
        	<#if (isStorePickup?has_content && isStorePickup == "Y") || (!shippingApplies)>
        		<input disabled="disabled" type="text"  maxlength="20" value=""/>
        		<input name="shipmentMethod" type="hidden" id="shipmentMethod" maxlength="20" value="NO_SHIPPING@_NA_"/>
        	<#else>
        		<#assign productStoreId = Static["org.ofbiz.product.store.ProductStoreWorker"].getProductStoreId(request) />
	            <#assign carrierShipmentMethodList = delegator.findByAnd('ProductStoreShipmentMethView', {"productStoreId" : productStoreId})!"" />
	            <#assign selectedShippingMethod = parameters.shipmentMethod!orderShippingMethod!""/>
	            <select name="shipmentMethod" id="shipmentMethod" class="small">
	                <#if carrierShipmentMethodList?has_content>
	                    <#list carrierShipmentMethodList as carrierMethod>
	                        <#if carrierMethod.shipmentMethodTypeId?has_content && carrierMethod.partyId?has_content>
	                            <#assign shippingMethod = carrierMethod.shipmentMethodTypeId + "@" + carrierMethod.partyId/>
	                        </#if>
	                        <#assign findCarrierShipmentMethodMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("shipmentMethodTypeId", carrierMethod.shipmentMethodTypeId!"", "partyId", carrierMethod.partyId!"","roleTypeId" ,"CARRIER")>
	                        <#assign carrierShipmentMethod = delegator.findByPrimaryKeyCache("CarrierShipmentMethod", findCarrierShipmentMethodMap)>
	                        <#assign carrierPartyGroupName = ""/>
	                        <#if carrierMethod.partyId != "_NA_">
	                            <#assign carrierParty = carrierShipmentMethod.getRelatedOne("Party")/>
	                            <#assign carrierPartyGroup = carrierParty.getRelatedOne("PartyGroup")/>
	                            <#assign carrierPartyGroupName = carrierPartyGroup.groupName/>
	                        </#if>
	                        <option value="${shippingMethod!}" <#if (shippingMethod?has_content) && (selectedShippingMethod == shippingMethod)>selected</#if>><#if carrierPartyGroupName?has_content>${carrierPartyGroupName!}, </#if>${carrierMethod.description!}</option>
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
        	<#if (isStorePickup?has_content && isStorePickup == "Y") || (!shippingApplies)>
        		<input disabled="disabled" type="text"  maxlength="20" value="${parameters.trackingNumber!""}"/>
        		<input name="trackingNumber" type="hidden" id="trackingNumber" maxlength="20" value="${parameters.trackingNumber!""}"/>
        	<#else>
        		<input name="trackingNumber" type="text" id="trackingNumber" maxlength="20" value="${parameters.trackingNumber!""}"/>
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
            <input class="dateEntry" type="text" id="shipByDate" name="estimatedShipDate" maxlength="40" value="${parameters.estimatedShipDate!""}" onChange="validateInput(this, '+DATE', '${uiLabelMap.OrderShipDateWarning}')"/>
        </div>
    </div>
</div>

<div class="infoRow row COMPLETED">
    <div class="infoEntry long">
        <div class="infoCaption">
            <label>${uiLabelMap.PackageWidthCaption}</label>
        </div>
        <div class="infoValue">
        	<input type="text" class="small" name="packageWidth" value="${parameters.packageWidth!packageWidth!""}" />
        	<#if lengthUom?has_content>${lengthUom.abbreviation!}</#if>
        </div>
    </div>
</div>

<div class="infoRow row COMPLETED">
    <div class="infoEntry long">
        <div class="infoCaption">
            <label>${uiLabelMap.GenerateShipLabelCaption}</label>
        </div>
        <div class="entry checkbox short">
        	<input class="checkBoxEntry" type="radio" name="generateShippingLabel" value="Y" <#if parameters.generateShippingLabel?exists && (parameters.generateShippingLabel=="Y") >checked="checked"<#elseif !(parameters.generateShippingLabel?exists)>checked="checked"</#if>/>${uiLabelMap.YesLabel}
            <input class="checkBoxEntry" type="radio" name="generateShippingLabel" value="N" <#if parameters.generateShippingLabel?exists && (parameters.generateShippingLabel=="N") >checked="checked"</#if>/>${uiLabelMap.NoLabel}
        </div>
    </div>
</div>


<div class="infoRow row COMPLETED">
    <div class="infoEntry long">
        <div class="infoCaption">
            <label>${uiLabelMap.PackageWeightCaption}</label>
        </div>
        <div class="infoValue">
        	<input type="text" class="small" name="packageWeight" value="${parameters.packageWeight!packageWeight!""}" />
        	<#assign weightUomId = Static["com.osafe.util.OsafeAdminUtil"].getProductStoreParm(request, "WEIGHT_UOM_DEFAULT")!"" />
        	<#if weightUomId?has_content>
        	  <#assign weightUomId = "WT_"+weightUomId?lower_case />
        	  <#assign weightUom = delegator.findByPrimaryKey('Uom', {"uomId" : weightUomId})!"" />
        	</#if>
        	<input type="hidden" name="weightUomId" value="${parameters.weightUomId!weightUomId!""}" />
        	<#if weightUom?has_content>
        	  ${weightUom.abbreviation!}
        	</#if>
        </div>
    </div>
</div>

<#assign lengthUomId = Static["com.osafe.util.OsafeAdminUtil"].getProductStoreParm(request, "LENGTH_UOM_DEFAULT")!"" />
<#if lengthUomId?has_content>
  <#assign lengthUomId = "LEN_"+lengthUomId?lower_case />
  <#assign lengthUom = delegator.findByPrimaryKey('Uom', {"uomId" : lengthUomId})!"" />
</#if>
<input type="hidden" name="lengthUomId" value="${parameters.lengthUomId!lengthUomId!""}" />
        	
<div class="infoRow row COMPLETED">
    <div class="infoEntry long">
        <div class="infoCaption">
            <label>${uiLabelMap.PackageHeightCaption}</label>
        </div>
        <div class="infoValue">
        	<input type="text" class="small" name="packageHeight" value="${parameters.packageHeight!packageHeight!""}" />
        	<#if lengthUom?has_content>${lengthUom.abbreviation!}</#if>
        </div>
    </div>
</div>

<div class="infoRow row COMPLETED">
    <div class="infoEntry long">
        <div class="infoCaption">
            <label>${uiLabelMap.PackageDepthCaption}</label>
        </div>
        <div class="infoValue">
        	<input type="text" class="small" name="packageDepth" value="${parameters.packageDepth!packageDepth!""}" />
        	<#if lengthUom?has_content>${lengthUom.abbreviation!}</#if>
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