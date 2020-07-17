<#if mode?has_content>
  <#if shipCharge?has_content>

    <#assign productStoreShipMethId = shipCharge.productStoreShipMethId!"" />
    <#assign partyId = shipCharge.partyId!"" />
    <#assign shipmentMethodTypeId = shipCharge.shipmentMethodTypeId!"" />
    <#assign minTotal = shipCharge.minTotal!"" />
    <#assign maxTotal = shipCharge.maxTotal!"" />
    <#assign sequenceNumber = shipCharge.sequenceNumber!"" />
    
    
    <#assign selectedParty = shipCharge.partyId!""/>
    <#assign selectedShipmentMethodType = shipCharge.shipmentMethodTypeId!""/>
    
    <#else>
    	<#assign selectedParty = parameters.partyId!""/>
    	<#assign selectedShipmentMethodType = parameters.shipmentMethodTypeId!""/>

  </#if>
  <#if shipCostEst?has_content>

    <#assign flatRate = shipCostEst.orderFlatPrice!"" />
    <#assign shipmentCostEstimateId = shipCostEst.shipmentCostEstimateId!"" />

  </#if>
  
  <#assign isShipChargeDetail = false>
  <#if shipCharge?has_content>
    <#assign isShipChargeDetail = true>
  </#if>
  
  <#if isShipChargeDetail>
  		<input class="small" name="shipmentCostEstimateId" type="hidden" id="shipmentCostEstimateId" maxlength="20" value="${shipCostEst.shipmentCostEstimateId!shipmentCostEstimateId!""}"/>
  <#else>
  		<input class="small" name="shipmentCostEstimateId" type="hidden" id="shipmentCostEstimateId" maxlength="20" value="${parameters.shipmentCostEstimateId!shipmentCostEstimateId!""}"/>
  </#if>

  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.IdCaption}</label>
      </div>
      <div class="infoValue">
        <#if mode="add">
        	<input name="productStoreShipMethId" type="text" id="productStoreShipMethId" maxlength="20" value="${parameters.productStoreShipMethId!productStoreShipMethId!""}"/>	
        <#else>
        	${parameters.productStoreShipMethId!productStoreShipMethId!""}
         	<input name="productStoreShipMethId" type="hidden" id="productStoreShipMethId" maxlength="20" value="${parameters.productStoreShipMethId!productStoreShipMethId!""}"/>
        </#if>
      </div>
    </div>
  </div>

  
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.CarrierCaption}</label>
      </div>
      <div class="infoValue">
        <select name="partyId" id="partyId" class="small">
          <#if partys?has_content>
            <#list partys as party>
              <option value='${party.partyId!}' <#if selectedParty == party.partyId >selected=selected</#if>>${party.partyId?default(party.partyId!)}</option>
            </#list>
          </#if>
        </select>
      </div>
    </div>
  </div>
  
  
  <div class="infoRow row">
    <div class="infoEntry long">
      <div class="infoCaption">
        <label>${uiLabelMap.ShippingMethCaption}</label>
      </div>
      <div class="infoValue">
        <select name="shipmentMethodTypeId" id="shipmentMethodTypeId" class="small">
          <#if shipmentMethodTypes?has_content>
            <#list shipmentMethodTypes as shipmentMethodType>
              <option value='${shipmentMethodType.shipmentMethodTypeId!}' <#if selectedShipmentMethodType == shipmentMethodType.shipmentMethodTypeId >selected=selected</#if>>${shipmentMethodType.shipmentMethodTypeId?default(parameters.shipmentMethodTypeId!shipmentMethodType.shipmentMethodTypeId!)}</option>
            </#list>
          </#if>
        </select>
      </div>
    </div>
  </div>
  
  
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.MinTotalCaption}</label>
      </div>
      <div class="infoValue">
        <#if mode="add">
        	<input name="minTotal" type="text" id="minTotal" maxlength="20" value="${parameters.minTotal!minTotal!""}"/>
        <#else>
          	<input name="minTotal" type="text" id="minTotal" maxlength="20" value="${shipCharge.minTotal!minTotal!""}"/>
        </#if>
      </div>
    </div>
  </div>
  
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.MaxTotalCaption}</label>
      </div>
      <div class="infoValue">
        <#if mode="add">
        	<input name="maxTotal" type="text" id="maxTotal" maxlength="20" value="${parameters.maxTotal!maxTotal!""}"/>
        <#else>
          	<input name="maxTotal" type="text" id="maxTotal" maxlength="20" value="${shipCharge.maxTotal!maxTotal!""}"/>
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
  
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.FlatRateCaption}</label>
      </div>
      <div class="infoValue">
        <#if mode="add">
        	<input name="orderFlatPrice" type="text" id="orderFlatPrice" maxlength="20" value="${parameters.orderFlatPrice!orderFlatPrice!""}"/>
        <#else>
          	<input name="orderFlatPrice" type="text" id="orderFlatPrice" maxlength="20" value="${shipCostEst.orderFlatPrice!orderFlatPrice!""}"/>
        </#if>
      </div>
    </div>
  </div>
  
    

<#else>
    ${uiLabelMap.NoDataAvailableInfo}
</#if>
