<#assign selectedSaveCertInfo = parameters.saveCertInfo! />
<#assign selectedShipperPickupType = parameters.shipperPickupType! />
<#assign selectedCodAllowCod = parameters.codAllowCod! />
<#assign selectedCodSurchargeApplyToPackage = parameters.codSurchargeApplyToPackage! />
<#assign selectedCodFundsCode = parameters.codFundsCode! />
<#if mode?has_content>  
  <#if shipmentGatewayUps?has_content>
    <#assign shipmentGatewayConfigId= shipmentGatewayUps.shipmentGatewayConfigId! />
    <#assign connectUrl= shipmentGatewayUps.connectUrl! />   
    <#assign connectTimeout= shipmentGatewayUps.connectTimeout! />
    <#assign shipperNumber= shipmentGatewayUps.shipperNumber! />
    <#assign billShipperAccountNumber= shipmentGatewayUps.billShipperAccountNumber! />
    <#assign accessLicenseNumber= shipmentGatewayUps.accessLicenseNumber! />
    <#assign accessUserId= shipmentGatewayUps.accessUserId! />
    <#assign accessPassword= shipmentGatewayUps.accessPassword! />
    <#assign saveCertInfo= shipmentGatewayUps.saveCertInfo! />  
    <#assign saveCertPath= shipmentGatewayUps.saveCertPath! />      
    <#assign shipperPickupType= shipmentGatewayUps.shipperPickupType! />
    <#assign maxEstimateWeight= shipmentGatewayUps.maxEstimateWeight! />
    <#assign minEstimateWeight= shipmentGatewayUps.minEstimateWeight! />
    <#assign codAllowCod= shipmentGatewayUps.codAllowCod! />
    <#assign codSurchargeAmount= shipmentGatewayUps.codSurchargeAmount! />  
    <#assign codSurchargeCurrencyUomId= shipmentGatewayUps.codSurchargeCurrencyUomId! />      
    <#assign codSurchargeApplyToPackage= shipmentGatewayUps.codSurchargeApplyToPackage! />
    <#assign codFundsCode= shipmentGatewayUps.codFundsCode! />
    <#assign defaultReturnLabelMemo= shipmentGatewayUps.defaultReturnLabelMemo! />
    <#assign defaultReturnLabelSubject= shipmentGatewayUps.defaultReturnLabelSubject! />
    <#if !selectedSaveCertInfo?has_content>
        <#assign selectedSaveCertInfo = shipmentGatewayUps.saveCertInfo!""/>
    <#else>
        <#assign selectedSaveCertInfo = parameters.saveCertInfo!""/>
    </#if>
    <#if !selectedShipperPickupType?has_content>
        <#assign selectedShipperPickupType = shipmentGatewayUps.shipperPickupType!""/>
    <#else>
        <#assign selectedShipperPickupType = parameters.shipperPickupType!""/>
    </#if>
    <#if !selectedCodAllowCod?has_content>
        <#assign selectedCodAllowCod = shipmentGatewayUps.codAllowCod!""/>
    <#else>
        <#assign selectedCodAllowCod = parameters.codAllowCod!""/>
    </#if> 
    <#if !selectedCodSurchargeApplyToPackage?has_content>
        <#assign selectedCodSurchargeApplyToPackage = shipmentGatewayUps.codSurchargeApplyToPackage!""/>
    <#else>
        <#assign selectedCodSurchargeApplyToPackage = parameters.codSurchargeApplyToPackage!""/>
    </#if> 
    <#if !selectedCodFundsCode?has_content>
        <#assign selectedCodFundsCode = shipmentGatewayUps.codFundsCode!""/>
    <#else>
        <#assign selectedCodFundsCode = parameters.codFundsCode!""/>
    </#if> 
   
    <div class="infoRow">
      <div class="infoEntry">
        <div class="infoCaption">
          <label>${uiLabelMap.ConnectUrlCaption}</label>
        </div>
        <div class="infoValue">
          <#if mode?has_content>
            <input class="large" name="shipmentGatewayConfigId" type="hidden" id="shipmentGatewayConfigId" value="${parameters.shipmentGatewayConfigId!shipmentGatewayConfigId!""}"/>
            <input class="large" name="connectUrl" type="text" id="connectUrl" value="${parameters.connectUrl!connectUrl!""}"/>
          </#if>
        </div>
      </div>
    </div>
    
     <div class="infoRow">
      <div class="infoEntry">
        <div class="infoCaption">
          <label>${uiLabelMap.ConnectTimeoutCaption}</label>
        </div>
        <div class="infoValue">
          <#if mode?has_content>
            <input class="large" name="connectTimeout" type="text" id="connectTimeout" value="${parameters.connectTimeout!connectTimeout!""}"/>
          </#if>
        </div>
      </div>
    </div>
    
    <div class="infoRow">
      <div class="infoEntry">
        <div class="infoCaption">
          <label>${uiLabelMap.ShipperNumberCaption}</label>
        </div>
        <div class="infoValue">
          <#if mode?has_content>
            <input class="large" name="shipperNumber" type="text" id="shipperNumber" value="${parameters.shipperNumber!shipperNumber!""}"/>
          </#if>
        </div>
      </div>
    </div>
    
    <div class="infoRow">
      <div class="infoEntry">
        <div class="infoCaption">
          <label>${uiLabelMap.BillShipperAccountNumberCaption}</label>
        </div>
        <div class="infoValue">
          <#if mode?has_content>
            <input class="large" name="billShipperAccountNumber" type="text" id="billShipperAccountNumber" value="${parameters.billShipperAccountNumber!billShipperAccountNumber!""}"/>
          </#if>
        </div>
      </div>
    </div>
    
    <div class="infoRow">
      <div class="infoEntry">
        <div class="infoCaption">
          <label>${uiLabelMap.AccessLicenseNumberCaption}</label>
        </div>
        <div class="infoValue">
          <#if mode?has_content>
            <input class="large" name="accessLicenseNumber" type="text" id="accessLicenseNumber" value="${parameters.accessLicenseNumber!accessLicenseNumber!""}"/>
          </#if>
        </div>
      </div>
    </div>
    
    <div class="infoRow">
      <div class="infoEntry">
        <div class="infoCaption">
          <label>${uiLabelMap.AccessUserIdUpsCaption}</label>
        </div>
        <div class="infoValue">
          <#if mode?has_content>
            <input class="large" name="accessUserId" type="text" id="accessUserId" value="${parameters.accessUserId!accessUserId!""}"/>
          </#if>
        </div>
      </div>
    </div>
    
    <div class="infoRow">
      <div class="infoEntry">
        <div class="infoCaption">
          <label>${uiLabelMap.AccessPasswordUpsCaption}</label>
        </div>
        <div class="infoValue">
          <#if mode?has_content>
            <input class="large" name="accessPassword" type="text" id="accessPassword" value="${parameters.accessPassword!accessPassword!""}"/>
          </#if>
        </div>
      </div>
    </div>
    
    <div class="infoRow">
      <div class="infoEntry">
        <div class="infoCaption">
          <label>${uiLabelMap.SaveCertInfoCaption}</label>
        </div>
        <div class="infoValue">       
            <#if mode?has_content>
          		<select name="saveCertInfo" id="saveCertInfo" >
          			  <option value="">${uiLabelMap.SelectOneLabel}</option>
                      <option value="true" <#if selectedSaveCertInfo == "true" >selected=selected</#if>>True</option>
                      <option value="false" <#if selectedSaveCertInfo == "false" >selected=selected</#if>>False</option>
		        </select>
        	</#if>
        </div>        
      </div>
    </div>
    
    <div class="infoRow">
      <div class="infoEntry">
        <div class="infoCaption">
          <label>${uiLabelMap.SaveCertPathCaption}</label>
        </div>
        <div class="infoValue">
          <#if mode?has_content>
            <input class="large" name="saveCertPath" type="text" id="saveCertPath" value="${parameters.saveCertPath!saveCertPath!""}"/>
          </#if>
        </div>
      </div>
    </div>
    
    <div class="infoRow">
      <div class="infoEntry">
        <div class="infoCaption">
          <label>${uiLabelMap.ShipperPickupTypeCaption}</label>
        </div>
        <div class="infoValue">       
            <#if mode?has_content>
          		<select name="shipperPickupType" id="shipperPickupType" >
          		      <option value="">${uiLabelMap.SelectOneLabel}</option>
                      <option value="06" <#if selectedShipperPickupType == "06" >selected=selected</#if>>One Time Pickup</option>
                      <option value="01" <#if selectedShipperPickupType == "01" >selected=selected</#if>>Daily Pickup</option>
                      <option value="03" <#if selectedShipperPickupType == "03" >selected=selected</#if>>Customer Counter</option>
                      <option value="06" <#if selectedShipperPickupType == "06" >selected=selected</#if>>One Time Pickup</option>
                      <option value="07" <#if selectedShipperPickupType == "07" >selected=selected</#if>>On Call Air Pickup</option>
                      <option value="11" <#if selectedShipperPickupType == "11" >selected=selected</#if>>Suggested Retail Rates</option>
                      <option value="19" <#if selectedShipperPickupType == "19" >selected=selected</#if>>Letter Center</option>
                      <option value="20" <#if selectedShipperPickupType == "20" >selected=selected</#if>>Air Service Center</option>
		        </select>
        	</#if>
        </div>    
      </div>
    </div>
    
    <div class="infoRow">
      <div class="infoEntry">
        <div class="infoCaption">
          <label>${uiLabelMap.MaxEstimateWeightCaption}</label>
        </div>
        <div class="infoValue">
          <#if mode?has_content>
            <input class="large" name="maxEstimateWeight" type="text" id="maxEstimateWeight" value="${parameters.maxEstimateWeight!maxEstimateWeight!""}"/>
          </#if>
        </div>
      </div>
    </div>
    
    <div class="infoRow">
      <div class="infoEntry">
        <div class="infoCaption">
          <label>${uiLabelMap.MinEstimateWeightCaption}</label>
        </div>
        <div class="infoValue">
          <#if mode?has_content>
            <input class="large" name="minEstimateWeight" type="text" id="minEstimateWeight" value="${parameters.minEstimateWeight!minEstimateWeight!""}"/>
          </#if>
        </div>
      </div>
    </div>
    
    <div class="infoRow">
      <div class="infoEntry">
        <div class="infoCaption">
          <label>${uiLabelMap.CodAllowCodCaption}</label>
        </div>
        <div class="infoValue">       
            <#if mode?has_content>
          		<select name="codAllowCod" id="codAllowCod" >
                      <option value="">${uiLabelMap.SelectOneLabel}</option>
                      <option value="true" <#if selectedCodAllowCod == "true" >selected=selected</#if>>True</option>
                      <option value="false" <#if selectedCodAllowCod == "false" >selected=selected</#if>>False</option>
		        </select>
        	</#if>
        </div>    
      </div>
    </div>
    
    <div class="infoRow">
      <div class="infoEntry">
        <div class="infoCaption">
          <label>${uiLabelMap.CodSurchargeAmountCaption}</label>
        </div>
        <div class="infoValue">
          <#if mode?has_content>
            <input class="large" name="codSurchargeAmount" type="text" id="codSurchargeAmount" value="${parameters.codSurchargeAmount!codSurchargeAmount!""}"/>
          </#if>
        </div>
      </div>
    </div>
    
        <div class="infoRow">
      <div class="infoEntry">
        <div class="infoCaption">
          <label>${uiLabelMap.CodSurchargeCurrencyUomIdCaption}</label>
        </div>
        <div class="infoValue">
          <#if mode?has_content>
            <input class="large" name="codSurchargeCurrencyUomId" type="text" id="codSurchargeCurrencyUomId" value="${parameters.codSurchargeCurrencyUomId!codSurchargeCurrencyUomId!""}"/>
          </#if>
        </div>
      </div>
    </div>
    
    <div class="infoRow">
      <div class="infoEntry">
        <div class="infoCaption">
          <label>${uiLabelMap.CodSurchargeApplyToPackageCaption}</label>
        </div>
        <div class="infoValue">       
            <#if mode?has_content>
          		<select name="codSurchargeApplyToPackage" id="codSurchargeApplyToPackage" >
                      <option value="">${uiLabelMap.SelectOneLabel}</option>
                      <option value="all" <#if selectedCodSurchargeApplyToPackage == "all" >selected=selected</#if>>Surcharge amount will be applied to each shipment package</option>                      
                      <option value="first" <#if selectedCodSurchargeApplyToPackage == "first" >selected=selected</#if>>Surcharge amount will be applied to the first package in the shipment</option>
                      <option value="split" <#if selectedCodSurchargeApplyToPackage == "split" >selected=selected</#if>>Surcharge amount will be split between shipment packages</option>
                      <option value="none" <#if selectedCodSurchargeApplyToPackage == "none" >selected=selected</#if>>Surcharge will not be applied to any packages </option>
		        </select>
        	</#if>
        </div>    
      </div>
    </div>
    
    <div class="infoRow">
      <div class="infoEntry">
        <div class="infoCaption">
          <label>${uiLabelMap.CodFundsCodeCaption}</label>
        </div>
        <div class="infoValue">       
            <#if mode?has_content>
          		<select name="codFundsCode" id="codFundsCode" >
                      <option value="">${uiLabelMap.SelectOneLabel}</option>
                      <option value="0" <#if selectedCodFundsCode == "0" >selected=selected</#if>>Unsecured Funds Allowed</option>
                      <option value="8" <#if selectedCodFundsCode == "8" >selected=selected</#if>>Secured Funds Only</option>
		        </select>
        	</#if>
        </div>    
      </div>
    </div>
    
    <div class="infoRow">
      <div class="infoEntry">
        <div class="infoCaption">
          <label>${uiLabelMap.DefaultReturnLabelMemoCaption}</label>
        </div>
        <div class="infoValue">
          <#if mode?has_content>
            <input class="large" name="defaultReturnLabelMemo" type="text" id="defaultReturnLabelMemo" value="${parameters.defaultReturnLabelMemo!defaultReturnLabelMemo!""}"/>
          </#if>
        </div>
      </div>
    </div>
  
    <div class="infoRow">
      <div class="infoEntry">
        <div class="infoCaption">
          <label>${uiLabelMap.DefaultReturnLabelSubjectCaption}</label>
        </div>
        <div class="infoValue">
          <#if mode?has_content>
            <input class="large" name="defaultReturnLabelSubject" type="text" id="defaultReturnLabelSubject" value="${parameters.defaultReturnLabelSubject!defaultReturnLabelSubject!""}"/>
          </#if>
        </div>
      </div>
    </div>
    
  </#if>  
<#else>
    ${screens.render("component://osafeadmin/widget/CommonScreens.xml#ListNoDataResult")}
</#if>