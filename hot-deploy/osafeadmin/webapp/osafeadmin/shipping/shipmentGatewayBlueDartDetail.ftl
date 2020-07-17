<#assign selectedFormat = parameters.format! />
<#if mode?has_content>
  <#if shipmentGatewayBlueDart?has_content>
    <#assign shipmentGatewayConfigId= shipmentGatewayBlueDart.shipmentGatewayConfigId! />
    <#assign connectUrl = shipmentGatewayBlueDart.connectUrl!/>
    <#assign customerId = shipmentGatewayBlueDart.customerId!/>
    <#assign customerLisenceKey = shipmentGatewayBlueDart.customerLisenceKey!/>
    <#assign versionNum = shipmentGatewayBlueDart.versionNum!/>
    <#assign scanNum = shipmentGatewayBlueDart.scanNum!/>
    <#assign format = shipmentGatewayBlueDart.format!/>
    <#if !selectedFormat?has_content>
        <#assign selectedFormat = shipmentGatewayBlueDart.format!""/>
    <#else>
        <#assign selectedFormat = parameters.format!""/>
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
          <label>${uiLabelMap.CustomerIdCaption}</label>
        </div>
        <div class="infoValue">
        <#if mode?has_content>
           <input class="large" name="customerId" type="text" id="customerId" value="${parameters.customerId!customerId!""}"/>
        </#if>
        </div>
      </div>
    </div>

    <div class="infoRow">
      <div class="infoEntry">
        <div class="infoCaption">
          <label>${uiLabelMap.CustomerLisenceKeyCaption}</label>
        </div>
        <div class="infoValue">
        <#if mode?has_content>
           <input class="large" name="customerLisenceKey" type="text" id="customerLisenceKey" value="${parameters.customerLisenceKey!customerLisenceKey!""}"/>
        </#if>
        </div>
      </div>
    </div>

    <div class="infoRow">
      <div class="infoEntry">
        <div class="infoCaption">
          <label>${uiLabelMap.VersionNumCaption}</label>
        </div>
        <div class="infoValue">
        <#if mode?has_content>
           <input class="" name="versionNum" type="text" id="versionNum" value="${parameters.versionNum!versionNum!""}"/>
        </#if>
        </div>
      </div>
    </div>

    <div class="infoRow">
      <div class="infoEntry">
        <div class="infoCaption">
          <label>${uiLabelMap.ScanNumCaption}</label>
        </div>
        <div class="infoValue">
        <#if mode?has_content>
           <input class="" name="scanNum" type="text" id="scanNum" value="${parameters.scanNum!scanNum!""}"/>
        </#if>
        </div>
      </div>
    </div>

   <div class="infoRow">
      <div class="infoEntry">
        <div class="infoCaption">
          <label>${uiLabelMap.FormatCaption}</label>
        </div>
        <div class="infoValue">       
            <#if mode?has_content>
          		<select name="format" id="format" class="small">
          		      <option value="">${uiLabelMap.SelectOneLabel}</option>
                      <option value="xml" <#if selectedFormat == "xml" >selected=selected</#if>>XML</option>
                      <option value="html" <#if selectedFormat == "html" >selected=selected</#if>>HTML</option>
		        </select>
        	</#if>
        </div>    
      </div>
    </div>
  </#if>
<#else>
   ${screens.render("component://osafeadmin/widget/CommonScreens.xml#ListNoDataResult")}
</#if>