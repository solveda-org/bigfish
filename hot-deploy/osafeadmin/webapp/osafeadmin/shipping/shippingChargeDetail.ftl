<#assign deliverToPOBox = parameters.deliverToPOBox! />
<#assign selectedIncCountryType = parameters.includeGeoId! />
<#assign selectedExcCountryType = parameters.excludeGeoId! />
<#assign selectedIncFeatureGrp = parameters.includeFeatureGroupId! />
<#assign selectedExcFeatureGrp = parameters.excludeFeatureGroupId! />
<#assign selectedShipmentGatewayConfig = parameters.shipmentGatewayConfigId! />

<#if mode?has_content>
  <#if shipCharge?has_content>
    <#assign productStoreShipMethId = shipCharge.productStoreShipMethId!"" />
    <#assign partyId = shipCharge.partyId!"" />
    <#assign shipmentMethodTypeId = shipCharge.shipmentMethodTypeId!"" />
    <#assign minTotal = shipCharge.minTotal!"" />
    <#assign maxTotal = shipCharge.maxTotal!"" />
    <#assign minWeight = shipCharge.minWeight!"" />
    <#assign maxWeight = shipCharge.maxWeight!"" />
    <#assign sequenceNum = shipCharge.sequenceNumber!"" />
    <#assign includeFeatureGroupId = shipCharge.includeFeatureGroup!"" />
    <#assign excludeFeatureGroupId = shipCharge.excludeFeatureGroup!"" />
    <#assign includeGeoId = shipCharge.includeGeoId!"" />
    <#assign shipmentGatewayConfigId = shipCharge.shipmentGatewayConfigId!""/>
    <#assign excludeGeoId = shipCharge.excludeGeoId!"" />
    <#assign selectedShipmentCustomMethodId = shipCharge.shipmentCustomMethodId!"" />
    <#if !deliverToPOBox?has_content>
        <#assign deliverToPOBox = shipCharge.allowPoBoxAddr!"" />
    </#if>
    <#assign selectedParty = shipCharge.partyId!""/>
    <#if !selectedIncFeatureGrp?has_content>
        <#assign selectedIncFeatureGrp = shipCharge.includeFeatureGroup!""/>
    </#if>
    <#if !selectedExcFeatureGrp?has_content>
        <#assign selectedExcFeatureGrp = shipCharge.excludeFeatureGroup!""/>
    </#if>
    <#if !selectedIncCountryType?has_content>
        <#assign selectedIncCountryType = shipCharge.includeGeoId!""/>
    </#if>
    <#if !selectedShipmentGatewayConfig?has_content>
        <#assign selectedShipmentGatewayConfig = shipCharge.shipmentGatewayConfigId!""/>
    </#if>
    <#if !selectedExcCountryType?has_content>
        <#assign selectedExcCountryType = shipCharge.excludeGeoId!""/>
    </#if>  
    <#assign selectedShipmentMethodType = shipCharge.shipmentMethodTypeId!""/>
    
  <#else>
  		<#assign selectedIncCountryType = parameters.includeGeoId!""/>
  		<#assign selectedExcCountryType = parameters.excludeGeoId!""/>
  		<#assign selectedIncFeatureGrp = parameters.includeFeatureGroupId!"" />
        <#assign selectedExcFeatureGrp = parameters.excludeFeatureGroupId!"" />
    	<#assign selectedParty = parameters.partyId!""/>
    	<#assign selectedShipmentMethodType = parameters.shipmentMethodTypeId!""/>
        <#assign selectedShipmentGatewayConfig = parameters.shipmentGatewayConfigId!""/> 
        <#assign selectedShipmentCustomMethodId = parameters.shipmentCustomMethodId!"" />
  </#if>
  <#if shipCostEst?has_content>
    <#assign orderFlatPrice = shipCostEst.orderFlatPrice!"" />
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
    </div>
  </div>
  
  
  <div class="infoRow row">
    <div class="infoEntry long">
      <div class="infoCaption">
        <label>${uiLabelMap.ShippingMethodCaption}</label>
      </div>
      <div class="infoValue">
        
          	<#if mode="add">
          		<select name="shipmentMethodTypeId" id="shipmentMethodTypeId" class="small">
	          		<#if shipmentMethodTypes?has_content>
			            <#list shipmentMethodTypes as shipmentMethodType>
			              <option value='${shipmentMethodType.shipmentMethodTypeId!}' <#if selectedShipmentMethodType == shipmentMethodType.shipmentMethodTypeId!"" >selected=selected</#if>>${shipmentMethodType.shipmentMethodTypeId?default(parameters.shipmentMethodTypeId!shipmentMethodType.shipmentMethodTypeId!)}</option>
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
        <label>${uiLabelMap.ShipmentGatewayCaption}</label>
      </div>
      <div class="infoValue">    
          <#if (mode?has_content) >
          		<select name="shipmentGatewayConfigId" id="shipmentGatewayConfigId" class="small">
          		  <option value="">${uiLabelMap.SelectOneLabel}</option>
		          <#if shipmentGatewayConfig?has_content>		          
		            <#list shipmentGatewayConfig as config>		              
		              <option value='${config.shipmentGatewayConfigId!}' <#if selectedShipmentGatewayConfig == config.shipmentGatewayConfigId >selected=selected</#if>>${config.description!}</option>
		            </#list>
		          </#if>
		        </select>
          </#if>
      </div>
    </div>
  </div>
  
  
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.ShipmentCustomMethodCaption}</label>
      </div>
      <div class="infoValue">    
          <#if (mode?has_content) >
          		<select name="shipmentCustomMethodId" id="shipmentCustomMethodId" class="small">
          		  <option value="">${uiLabelMap.SelectOneLabel}</option>
		          <#if shipmentCustomMethods?has_content>		          
		            <#list shipmentCustomMethods as customMethod >		              
		              <option value='${customMethod.customMethodId!}' <#if selectedShipmentCustomMethodId == customMethod.customMethodId >selected=selected</#if>>${customMethod.customMethodId!}</option>
		            </#list>
		          </#if>
		        </select>
          </#if>
      </div>
    </div>
  </div>
  
  
  <#if mode="edit">
  		<!-- get the CarrierShipmentMethod Info -->
	    <#assign carrierShipmentMethod = delegator.findByPrimaryKey("CarrierShipmentMethod", Static["org.ofbiz.base.util.UtilMisc"].toMap("shipmentMethodTypeId", shipCharge.shipmentMethodTypeId!, "partyId", shipCharge.partyId!, "roleTypeId", "CARRIER"))/> 
  </#if>
  
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.CarrierServiceCodeCaption}</label>
      </div>
      <div class="infoValue">
        <#if mode="add">
        	<input name="carrierServiceCode" type="text" id="carrierServiceCode" maxlength="20" value="${parameters.carrierServiceCode!carrierServiceCode!""}"/>
        <#else>
        	${parameters.carrierServiceCode!carrierShipmentMethod.carrierServiceCode!""}
          	<input name="carrierServiceCode" type="hidden" id="carrierServiceCode" maxlength="20" value="${carrierShipmentMethod.carrierServiceCode!carrierServiceCode!""}"/>
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
          	<input name="optionalMessage" type="text" id="optionalMessage" maxlength="20" value="${carrierShipmentMethod.optionalMessage!optionalMessage!""}"/>
        </#if>
      </div>
    </div>
  </div>
  
  
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.MinTotalCaption}</label>
      </div>
      <div class="infoValue">
        <input name="minTotal" type="text" id="minTotal" maxlength="20" value="${parameters.minTotal!minTotal!""}"/>
      </div>
    </div>
  </div>
  
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.MaxTotalCaption}</label>
      </div>
      <div class="infoValue">
        	<input name="maxTotal" type="text" id="maxTotal" maxlength="20" value="${parameters.maxTotal!maxTotal!""}"/>
      </div>
    </div>
  </div>
  
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.MinWeightCaption}</label>
      </div>
      <div class="infoValue">
        	<input name="minWeight" type="text" id="minWeight" maxlength="20" value="${parameters.minWeight!minWeight!""}"/>
      </div>
      <div class="infoIcon">
              <a href="javascript:void(0);" onMouseover="showTooltip(event,'${uiLabelMap.MinWeightInfo!""}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
      </div>
    </div>
  </div>
  
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.MaxWeightCaption}</label>
      </div>
      <div class="infoValue">
        	<input name="maxWeight" type="text" id="maxWeight" maxlength="20" value="${parameters.maxWeight!maxWeight!""}"/>
      </div>
      <div class="infoIcon">
              <a href="javascript:void(0);" onMouseover="showTooltip(event,'${uiLabelMap.MaxWeightInfo!""}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
      </div>
    </div>
  </div>
  
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.SeqNumCaption}</label>
      </div>
      <div class="infoValue">
        	<input name="sequenceNum" type="text" id="sequenceNum" maxlength="9" value="${parameters.sequenceNum!sequenceNum!"1"}"/>
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
          	<input name="orderFlatPrice" type="text" id="orderFlatPrice" maxlength="20" value="${parameters.orderFlatPrice!shipCostEst.orderFlatPrice!orderFlatPrice!""}"/>
        </#if>
      </div>
    </div>
  </div>
  
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.DeliverPOBoxCaption}</label>
      </div>
      <div class="entry checkbox short">
        <#if (mode?has_content)>
        	<input class="checkBoxEntry" type="radio" name="deliverToPOBox" value="Y" <#if deliverToPOBox?exists && (deliverToPOBox=="Y" || deliverToPOBox=="") >checked="checked"<#elseif !(deliverToPOBox?exists)>checked="checked"</#if>/>${uiLabelMap.YesLabel}
            <input class="checkBoxEntry" type="radio" name="deliverToPOBox" value="N" <#if deliverToPOBox=="N">checked="checked"</#if>/>${uiLabelMap.NoLabel}
        </#if>
      </div>
    </div>
  </div>
 <#if Static["com.osafe.util.OsafeAdminUtil"].isProductStoreParmTrue(COUNTRY_MULTI!"")> 
   <div class="infoRow">
     <div class="infoEntry">
       <div class="infoCaption">
         <label>${uiLabelMap.IncludeCountryCaption}</label>
       </div>
       <div class="infoValue">    
          <#if (mode?has_content) >
          		<select name="includeGeoId" id="includeGeoId" class="small">          		
          		  <option value="">${uiLabelMap.SelectOneLabel}</option>
		             <#assign countryList = Static["com.osafe.util.OsafeAdminUtil"].getCountryList(request)/>		           
	                    <#if countryList?has_content>
                            <#list countryList as country>
                            <option value='${country.geoId}' <#if selectedIncCountryType = country.geoId >selected=selected</#if>>${country.get("geoName")}</option>
                            </#list>
                        </#if>	                   
		        </select>
          </#if>
       </div>
     </div>
   </div>
  
   <div class="infoRow">
     <div class="infoEntry">
       <div class="infoCaption">
         <label>${uiLabelMap.ExcludeCountryCaption}</label>
       </div>
       <div class="infoValue">    
          <#if (mode?has_content) >
          		<select name="excludeGeoId" id="excludeGeoId" class="small">
          		  <option value="">${uiLabelMap.SelectOneLabel}</option>
          		  <#assign countryList = Static["com.osafe.util.OsafeAdminUtil"].getCountryList(request)/>		           
	                    <#if countryList?has_content>
                            <#list countryList as country>
                            <option value='${country.geoId}' <#if selectedExcCountryType = country.geoId >selected=selected</#if>>${country.get("geoName")}</option>
                            </#list>
                        </#if>
		        </select>
         </#if>
       </div>
     </div>
   </div>
</#if>   
   <div class="infoRow">
     <div class="infoEntry">
       <div class="infoCaption">
         <label>${uiLabelMap.IncludeFeatureCaption}</label>
       </div>
       <div class="infoValue">    
          <#if (mode?has_content) >
                <select name="includeFeatureGroupId" id="includeFeatureGroupId" class="small">
                  <option value="">${uiLabelMap.SelectOneLabel}</option>
                        <#if productFeatureGroup?has_content>
                            <#list productFeatureGroup as feature>
                                <option value='${feature.productFeatureGroupId}' <#if selectedIncFeatureGrp = feature.productFeatureGroupId >selected=selected</#if>>${feature.description}</option>
                            </#list>
                        </#if>
                </select>
         </#if>
       </div>
     </div>
   </div>
   
   <div class="infoRow">
     <div class="infoEntry">
       <div class="infoCaption">
         <label>${uiLabelMap.ExcludeFeatureCaption}</label>
       </div>
       <div class="infoValue">    
          <#if (mode?has_content) >
                <select name="excludeFeatureGroupId" id="excludeFeatureGroupId" class="small">
                  <option value="">${uiLabelMap.SelectOneLabel}</option>
                        <#if productFeatureGroup?has_content>
                            <#list productFeatureGroup as feature>
                            <option value='${feature.productFeatureGroupId}' <#if selectedExcFeatureGrp = feature.productFeatureGroupId >selected=selected</#if>>${feature.description}</option>
                            </#list>
                        </#if>
                </select>
         </#if>
       </div>
     </div>
   </div>
   
<#else>
     ${screens.render("component://osafeadmin/widget/CommonScreens.xml#ListNoDataResult")}
</#if>
