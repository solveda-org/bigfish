<#assign deliverToPOBox = parameters.deliverToPOBox! />
<#assign selectedIncCountryType = parameters.includeGeoId! />
<#assign selectedExcCountryType = parameters.excludeGeoId! />
<#assign selectedShipmentGatewayConfig = parameters.shipmentGatewayConfigId! />
<#if mode?has_content>
  <#if shipCharge?has_content>
    <#assign productStoreShipMethId = shipCharge.productStoreShipMethId!"" />
    <#assign partyId = shipCharge.partyId!"" />
    <#assign shipmentMethodTypeId = shipCharge.shipmentMethodTypeId!"" />
    <#assign minTotal = shipCharge.minTotal!"" />
    <#assign maxTotal = shipCharge.maxTotal!"" />
    <#assign sequenceNum = shipCharge.sequenceNumber!"" />
    <#assign includeGeoId = shipCharge.includeGeoId!"" />
    <#assign shipmentGatewayConfigId = shipCharge.shipmentGatewayConfigId!""/>
    <#assign excludeGeoId = shipCharge.excludeGeoId!"" />
    <#if !deliverToPOBox?has_content>
        <#assign deliverToPOBox = shipCharge.allowPoBoxAddr!"" />
    </#if>
    <#assign selectedParty = shipCharge.partyId!""/>
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
    	<#assign selectedParty = parameters.partyId!""/>
    	<#assign selectedShipmentMethodType = parameters.shipmentMethodTypeId!""/>
        <#assign selectedShipmentGatewayConfig = parameters.shipmentGatewayConfigId!""/> 
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
        <label>${uiLabelMap.ShipmentGatewayCaption}</label>
      </div>
      <div class="infoValue">    
          <#if (mode?has_content) >
          		<select name="shipmentGatewayConfigId" id="shipmentGatewayConfigId" class="small">
          		  <option value="">${uiLabelMap.SelectOneLabel}</option>
		          <#if shipmentGatewayConfig?has_content>		          
		            <#list shipmentGatewayConfig as config>		              
		              <option value='${config.shipmentGatewayConfigId!}' <#if selectedShipmentGatewayConfig == config.shipmentGatewayConfigId >selected=selected</#if>>${config.description}</option>
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
        	<input name="sequenceNum" type="text" id="sequenceNum" maxlength="20" value="${parameters.sequenceNum!sequenceNum!"1"}"/>
        <#else>
          	<input name="sequenceNum" type="text" id="sequenceNum" maxlength="20" value="${shipCharge.sequenceNum!sequenceNum!"1"}"/>
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
          	<input name="orderFlatPrice" type="text" id="orderFlatPrice" maxlength="20" value="${parameters.orderFlatPrice!orderFlatPrice!shipCostEst.orderFlatPrice!orderFlatPrice!""}"/>
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
		          <#assign countryDropdown = Static["com.osafe.util.OsafeAdminUtil"].getProductStoreParm(request,"COUNTRY_DROPDOWN")!"" />
	                 <#if countryDropdown?has_content>
	                    <#assign countryList = Static["org.ofbiz.base.util.StringUtil"].split(countryDropdown, ",")/>
	                    <#if countryList?has_content>
						   <#list countryList as country>
						   <#if country.toUpperCase()=="ALL">
						     <#if geoType?has_content>          
		                       <#list geoType as type>              
		                         <option value='${type.geoId!}' <#if selectedIncCountryType == type.geoId >selected=selected</#if>>${type.geoName}</option>
		                       </#list>
		                     </#if>	     
						   <#else>
						     <#assign geo = delegator.findOne("Geo",{"geoId" : country.trim()}, false)?if_exists/>						   		   
						     <option value='${country!}' <#if selectedIncCountryType == country >selected=selected</#if>>${geo.geoName}</option>
						   </#if>
	                       </#list>             
	                    </#if>
	                 <#else>
	                   <#if geoType?has_content>          
		                  <#list geoType as type>              
		                    <option value='${type.geoId!}' <#if selectedIncCountryType == type.geoId >selected=selected</#if>>${type.geoName}</option>
		                  </#list>
		               </#if>	
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
          		  <#assign countryDropdown = Static["com.osafe.util.OsafeAdminUtil"].getProductStoreParm(request,"COUNTRY_DROPDOWN")!"" />
	                 <#if countryDropdown?has_content>
	                    <#assign countryList = Static["org.ofbiz.base.util.StringUtil"].split(countryDropdown, ",")/>
	                    <#if countryList?has_content>
						   <#list countryList as country>
						   <#if country.toUpperCase()=="ALL">
						     <#if geoType?has_content>          
		                       <#list geoType as type>              
		                       <option value='${type.geoId!}' <#if selectedExcCountryType == type.geoId >selected=selected</#if>>${type.geoName}</option>
		                       </#list>
		                     </#if>	     
						   <#else>
						     <#assign geo = delegator.findOne("Geo",{"geoId" : country.trim()}, false)?if_exists/>						   		   
						     <option value='${country!}' <#if selectedExcCountryType == country >selected=selected</#if>>${geo.geoName}</option>
						   </#if>
	                       </#list>	               
	                    </#if>
	                 <#else> 
	                   <#if geoType?has_content>          
		                  <#list geoType as type>             
		                    <option value='${type.geoId!}' <#if selectedExcCountryType == type.geoId >selected=selected</#if>>${type.geoName}</option>
		                  </#list>
		               </#if>   
	                 </#if>
		        </select>
         </#if>
       </div>
     </div>
   </div>
 </#if>
<#else>
    ${uiLabelMap.NoDataAvailableInfo}
</#if>
