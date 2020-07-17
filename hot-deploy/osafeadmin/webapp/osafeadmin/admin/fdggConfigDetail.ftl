<#if paymentGatewayFdgg?has_content>

	<input type="hidden" id="paymentGatewayConfigId" name = "paymentGatewayConfigId" value="${parameters.configId!""}"/>
	<div class="infoRow">
      <div class="infoEntry">
        <div class="infoCaption">
          <label>${uiLabelMap.GatewayIdCaption}</label>
        </div>
        <div class="infoValue">
            <input class="large" name="gatewayId" type="text" id="gatewayId" value="${parameters.gatewayId!paymentGatewayFdgg.gatewayId!""}"/>
        </div>
      </div>
    </div>
    
    <div class="infoRow">
      <div class="infoEntry">
        <div class="infoCaption">
          <label>${uiLabelMap.ApiVersionCaption}</label>
        </div>
        <div class="infoValue">
            <input class="large" name="apiVersion" type="text" id="apiVersion" value="${parameters.apiVersion!paymentGatewayFdgg.apiVersion!""}"/>
        </div>
      </div>
    </div>
    
    <div class="infoRow">
      <div class="infoEntry">
        <div class="infoCaption">
          <label>${uiLabelMap.ApiVersionUrlCaption}</label>
        </div>
        <div class="infoValue">
            <input class="large" name="apiVersionUrl" type="text" id="apiVersionUrl" value="${parameters.apiVersionUrl!paymentGatewayFdgg.apiVersionUrl!""}"/>
        </div>
      </div>
    </div>
    
    <div class="infoRow">
      <div class="infoEntry">
        <div class="infoCaption">
          <label>${uiLabelMap.ApiPasswordCaption}</label>
        </div>
        <div class="infoValue">
            <input class="large" name="apiPassword" type="text" id="apiPassword" value="${parameters.apiPassword!paymentGatewayFdgg.apiPassword!""}"/>
        </div>
      </div>
    </div>
    
    <div class="infoRow">
      <div class="infoEntry">
        <div class="infoCaption">
          <label>${uiLabelMap.ApiKeyIdCaption}</label>
        </div>
        <div class="infoValue">
            <input class="large" name="apiKeyId" type="text" id="apiKeyId" value="${parameters.apiKeyId!paymentGatewayFdgg.apiKeyId!""}"/>
        </div>
      </div>
    </div>
    
    <div class="infoRow">
      <div class="infoEntry">
        <div class="infoCaption">
          <label>${uiLabelMap.ApiHmacKeyCaption}</label>
        </div>
        <div class="infoValue">
            <input class="large" name="apiHmacKey" type="text" id="apiHmacKey" value="${parameters.apiHmacKey!paymentGatewayFdgg.apiHmacKey!""}"/>
        </div>
      </div>
    </div>
    
</#if>    