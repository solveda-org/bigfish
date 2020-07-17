<#if paymentGatewayAuthorizeNet?has_content>
	<#if !selectedDelimitedData?has_content>
        <#assign selectedDelimitedData = paymentGatewayAuthorizeNet.delimitedData!""/>
    <#else>
        <#assign selectedDelimitedData = parameters.delimitedData!""/>
    </#if>
    
    <#if !selectedMethod?has_content>
        <#assign selectedMethod = paymentGatewayAuthorizeNet.method!""/>
    <#else>
        <#assign selectedMethod = parameters.method!""/>
    </#if>
    
    <#if !selectedEmailCustomer?has_content>
        <#assign selectedEmailCustomer = paymentGatewayAuthorizeNet.emailCustomer!""/>
    <#else>
        <#assign selectedEmailCustomer = parameters.emailCustomer!""/>
    </#if>
    
    <#if !selectedEmailMerchant?has_content>
        <#assign selectedEmailMerchant = paymentGatewayAuthorizeNet.emailMerchant!""/>
    <#else>
        <#assign selectedEmailMerchant = parameters.emailMerchant!""/>
    </#if>
    
    <#if !selectedTestMode?has_content>
        <#assign selectedTestMode = paymentGatewayAuthorizeNet.testMode!""/>
    <#else>
        <#assign selectedTestMode = parameters.testMode!""/>
    </#if>
    
    <#if !selectedRelayResponse?has_content>
        <#assign selectedRelayResponse = paymentGatewayAuthorizeNet.relayResponse!""/>
    <#else>
        <#assign selectedRelayResponse = parameters.relayResponse!""/>
    </#if>
    
	<input type="hidden" id="paymentGatewayConfigId" name = "paymentGatewayConfigId" value="${parameters.configId!""}"/>
	<div class="infoRow">
      <div class="infoEntry">
        <div class="infoCaption">
          <label>${uiLabelMap.TranscationUrlCaption}</label>
        </div>
        <div class="infoValue">
            <input class="large" name="transactionUrl" type="text" id="transactionUrl" value="${parameters.transactionUrl!paymentGatewayAuthorizeNet.transactionUrl!""}"/>
        </div>
      </div>
    </div>
    
    <div class="infoRow">
      <div class="infoEntry">
        <div class="infoCaption">
          <label>${uiLabelMap.CertificateAliasCaption}</label>
        </div>
        <div class="infoValue">
            <input class="large" name="certificateAlias" type="text" id="certificateAlias" value="${parameters.certificateAlias!paymentGatewayAuthorizeNet.certificateAlias!""}"/>
        </div>
      </div>
    </div>
    
    <div class="infoRow">
      <div class="infoEntry">
        <div class="infoCaption">
          <label>${uiLabelMap.ApiVersionCaption}</label>
        </div>
        <div class="infoValue">
            <input class="large" name="apiVersion" type="text" id="apiVersion" value="${parameters.apiVersion!paymentGatewayAuthorizeNet.apiVersion!""}"/>
        </div>
      </div>
    </div>
    
    <div class="infoRow">
      <div class="infoEntry">
        <div class="infoCaption">
          <label>${uiLabelMap.DelimitedDataCaption}</label>
        </div>        
        <div class="infoValue">
        	<select name="delimitedData" id="delimitedData" >
                      <option value="true" <#if selectedDelimitedData == "true" >selected=selected</#if>>True</option>
                      <option value="false" <#if selectedDelimitedData == "false" >selected=selected</#if>>False</option>
		    </select>
        </div>
      </div>
    </div>
    
    <div class="infoRow">
      <div class="infoEntry">
        <div class="infoCaption">
          <label>${uiLabelMap.DelimiterCharCaption}</label>
        </div>
        <div class="infoValue">
            <input class="large" name="delimiterChar" type="text" id="delimiterChar" value="${parameters.delimiterChar!paymentGatewayAuthorizeNet.delimiterChar!""}"/>
        </div>
      </div>
    </div>
    
    <div class="infoRow">
      <div class="infoEntry">
        <div class="infoCaption">
          <label>${uiLabelMap.MethodCaption}</label>
        </div>
        <div class="infoValue">
        	<select name="method" id="method" >
                      <option value="CC" <#if selectedMethod == "CC" >selected=selected</#if>>CC</option>
		    </select>
        </div>
      </div>
    </div>
    
    <div class="infoRow">
      <div class="infoEntry">
        <div class="infoCaption">
          <label>${uiLabelMap.EmailToCustomerCaption}</label>
        </div>
        <div class="infoValue">
            <select name="emailCustomer" id="emailCustomer" >
                      <option value="true" <#if selectedEmailCustomer == "true" >selected=selected</#if>>True</option>
                      <option value="false" <#if selectedEmailCustomer == "false" >selected=selected</#if>>False</option>
		    </select>
        </div>
      </div>
    </div>
    
    <div class="infoRow">
      <div class="infoEntry">
        <div class="infoCaption">
          <label>${uiLabelMap.EmailToMerchantCaption}</label>
        </div>
        <div class="infoValue">
        	<select name="emailMerchant" id="emailMerchant" >
                      <option value="true" <#if selectedEmailMerchant == "true" >selected=selected</#if>>True</option>
                      <option value="false" <#if selectedEmailMerchant == "false" >selected=selected</#if>>False</option>
		    </select>
        </div>
      </div>
    </div>
    
    <div class="infoRow">
      <div class="infoEntry">
        <div class="infoCaption">
          <label>${uiLabelMap.TestModeCaption}</label>
        </div>
        <div class="infoValue">
        	<select name="testMode" id="testMode" >
                      <option value="true" <#if selectedTestMode == "true" >selected=selected</#if>>True</option>
                      <option value="false" <#if selectedTestMode == "false" >selected=selected</#if>>False</option>
		    </select>
        </div>
      </div>
    </div>
    
    <div class="infoRow">
      <div class="infoEntry">
        <div class="infoCaption">
          <label>${uiLabelMap.RelayResponseCaption}</label>
        </div>
        <div class="infoValue">
        	<select name="relayResponse" id="relayResponse" >
                      <option value="true" <#if selectedRelayResponse == "true" >selected=selected</#if>>True</option>
                      <option value="false" <#if selectedRelayResponse == "false" >selected=selected</#if>>False</option>
		    </select>
        </div>
      </div>
    </div>
    
    <div class="infoRow">
      <div class="infoEntry">
        <div class="infoCaption">
          <label>${uiLabelMap.TransactionKeyCaption}</label>
        </div>
        <div class="infoValue">
            <input class="large" name="tranKey" type="text" id="tranKey" value="${parameters.tranKey!paymentGatewayAuthorizeNet.tranKey!""}"/>
        </div>
      </div>
    </div>
    
    <div class="infoRow">
      <div class="infoEntry">
        <div class="infoCaption">
          <label>${uiLabelMap.UserIdCaption}</label>
        </div>
        <div class="infoValue">
            <input class="large" name="userId" type="text" id="userId" value="${parameters.userId!paymentGatewayAuthorizeNet.userId!""}"/>
        </div>
      </div>
    </div>
    
    <div class="infoRow">
      <div class="infoEntry">
        <div class="infoCaption">
          <label>${uiLabelMap.PasswordCaption}</label>
        </div>
        <div class="infoValue">
            <input class="large" name="pwd" type="text" id="pwd" value="${parameters.pwd!paymentGatewayAuthorizeNet.pwd!""}"/>
        </div>
      </div>
    </div>
    
    <div class="infoRow">
      <div class="infoEntry">
        <div class="infoCaption">
          <label>${uiLabelMap.TransDescriptionCaption}</label>
        </div>
        <div class="infoValue">
            <input class="large" name="transDescription" type="text" id="transDescription" value="${parameters.transDescription!paymentGatewayAuthorizeNet.transDescription!""}"/>
        </div>
      </div>
    </div>
    
    <div class="infoRow">
      <div class="infoEntry">
        <div class="infoCaption">
          <label>${uiLabelMap.DuplicateWindowCaption}</label>
        </div>
        <div class="infoValue">
            <input class="large" name="duplicateWindow" type="text" id="duplicateWindow" value="${parameters.duplicateWindow!paymentGatewayAuthorizeNet.duplicateWindow!""}"/>
        </div>
      </div>
    </div>
    
</#if>    