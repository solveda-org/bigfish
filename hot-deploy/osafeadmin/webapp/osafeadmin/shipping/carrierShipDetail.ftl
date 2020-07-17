<#if mode?has_content>
  <#if shipCharge?has_content>

    <#assign partyId = shipCharge.partyId!"" />
    <#assign shipmentMethodTypeId = shipCharge.shipmentMethodTypeId!"" />
    <#assign carrierServiceCode = shipCharge.carrierServiceCode!"" />
    <#assign optionalMessage = shipCharge.optionalMessage!"" />
    <#assign sequenceNumber = shipCharge.sequenceNumber!"" />
       
    <#assign selectedParty = shipCharge.partyId!""/>
    
    <#else>
    	<#assign selectedParty = parameters.partyId!""/>
    	<#assign selectedShipmentMethodType = parameters.shipmentMethodTypeId!""/>
  </#if>
  
  <#assign isShipChargeDetail = false>
  <#if shipCharge?has_content>
    <#assign isShipChargeDetail = true>
  </#if>

    <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.CarrierIdCaption}</label>
      </div>
      <div class="infoValue">
	      <#if mode="add">
	        <select name="partyId" id="partyId" class="small">
	          <#if partys?has_content>
	            <#list partys as party>
	              <option value='${party.partyId!}' <#if selectedParty == party.partyId >selected=selected</#if>>${party.partyId?default(party.partyId!)}</option>
	            </#list>
	          </#if>
	        </select>
	      <#else>
	      	${parameters.partyId!partyId!""}
         	<input name="partyId" type="hidden" id="partyId" maxlength="20" value="${parameters.partyId!partyId!""}"/>
	      </#if>
      </div>
      <#if isShipChargeDetail>
      <#else>
      	<a href="addShipCarrier" target="_self" style="text-decoration:none;"><input type="button" class="standardBtn dateSelect" name="moveButton" id="moveButton" value="${uiLabelMap.AddShipChargeCarrierBtn}" /></a>
      </#if>
      
    </div>
  </div>

  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.ShippingMethCaption}</label>
      </div>
      <div class="infoValue">
      	<#if mode="add">
		    <select name="shipmentMethodTypeId" id="shipmentMethodTypeId" class="small">
	          <#if shipmentMethodTypes?has_content>
	            <#list shipmentMethodTypes as shipmentMethodType>
	              <option value='${shipmentMethodType.shipmentMethodTypeId!}' <#if selectedShipmentMethodType == shipmentMethodType.shipmentMethodTypeId >selected=selected</#if>>${shipmentMethodType.shipmentMethodTypeId?default(parameters.shipmentMethodTypeId!shipmentMethodType.shipmentMethodTypeId!)}</option>
	            </#list>
	          </#if>
	        </select>
	   <#else>
	      	${parameters.shipmentMethodTypeId!shipmentMethodTypeId!""}
         	<input name="shipmentMethodTypeId" type="hidden" id="shipmentMethodTypeId" maxlength="20" value="${parameters.shipmentMethodTypeId!shipmentMethodTypeId!""}"/>
	   </#if>  
      </div>
    </div>
  </div>
  
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.CarrierServiceCodeCaption}</label>
      </div>
      <div class="infoValue">
        <#if mode="add">
        	<input name="carrierServiceCode" type="text" id="carrierServiceCode" maxlength="20" value="${parameters.carrierServiceCode!carrierServiceCode!""}"/>
        <#else>
          	<input name="carrierServiceCode" type="text" id="carrierServiceCode" maxlength="20" value="${shipCharge.carrierServiceCode!carrierServiceCode!""}"/>
        </#if>
      </div>
    </div>
  </div>
  
    <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.MessageCaption}</label>
      </div>
      <div class="infoValue">
        <#if mode="add">
        	<input name="optionalMessage" type="text" id="optionalMessage" maxlength="20" value="${parameters.optionalMessage!optionalMessage!""}"/>
        <#else>
          	<input name="optionalMessage" type="text" id="optionalMessage" maxlength="20" value="${shipCharge.optionalMessage!optionalMessage!""}"/>
        </#if>
      </div>
    </div>
  </div>
  
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.SeqNumCaption}</label>
      </div>
      <div class="infoValue">
        <#if mode="add">
        	<input name="sequenceNumber" type="text" id="sequenceNumber" maxlength="20" value="${parameters.sequenceNumber!sequenceNumber!"1"}"/>
        <#else>
          	<input name="sequenceNumber" type="text" id="sequenceNumber" maxlength="20" value="${shipCharge.sequenceNumber!sequenceNumber!""}"/>
        </#if>
      </div>
    </div>
  </div>
  
<#else>
    ${uiLabelMap.NoDataAvailableInfo}
</#if>
