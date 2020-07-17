<#if mode?has_content>
  <#if shipmentGatewayDhl?has_content>
    <#assign shipmentGatewayConfigId= shipmentGatewayDhl.shipmentGatewayConfigId! />
	<#assign connectUrl= shipmentGatewayDhl.connectUrl! />
    <#assign connectTimeout= shipmentGatewayDhl.connectTimeout! />
    <#assign headVersion= shipmentGatewayDhl.headVersion! />
    <#assign headAction= shipmentGatewayDhl.headAction! />
    <#assign accessUserId= shipmentGatewayDhl.accessUserId	! />
    <#assign accessPassword= shipmentGatewayDhl.accessPassword! />
    <#assign accessAccountNbr= shipmentGatewayDhl.accessAccountNbr! />
    <#assign accessShippingKey= shipmentGatewayDhl.accessShippingKey! />
    <#assign labelImageFormat= shipmentGatewayDhl.labelImageFormat! />
    <#assign rateEstimateTemplate= shipmentGatewayDhl.rateEstimateTemplate! />
    
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
          <label>${uiLabelMap.HeadVersionCaption}</label>
        </div>
        <div class="infoValue">
          <#if mode?has_content>
            <input class="large" name="headVersion" type="text" id="headVersion" value="${parameters.headVersion!headVersion!""}"/>
          </#if>
        </div>
      </div>
    </div>
    
    <div class="infoRow">
      <div class="infoEntry">
        <div class="infoCaption">
          <label>${uiLabelMap.HeadActionCaption}</label>
        </div>
        <div class="infoValue">
          <#if mode?has_content>
            <input class="large" name="headAction" type="text" id="headAction" value="${parameters.headAction!headAction!""}"/>
          </#if>
        </div>
      </div>
    </div>
    
     <div class="infoRow">
      <div class="infoEntry">
        <div class="infoCaption">
          <label>${uiLabelMap.AccessUserIdCaption}</label>
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
          <label>${uiLabelMap.AccessPasswordCaption}</label>
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
          <label>${uiLabelMap.AccessAccountNbrCaption}</label>
        </div>
        <div class="infoValue">
          <#if mode?has_content>
            <input class="large" name="accessAccountNbr" type="text" id="accessAccountNbr" value="${parameters.accessAccountNbr!accessAccountNbr!""}"/>
          </#if>
        </div>
      </div>
    </div>
    
    <div class="infoRow">
      <div class="infoEntry">
        <div class="infoCaption">
          <label>${uiLabelMap.AccessShippingKeyCaption}</label>
        </div>
        <div class="infoValue">
          <#if mode?has_content>
            <input class="large" name="accessShippingKey" type="text" id="accessShippingKey" value="${parameters.accessShippingKey!accessShippingKey!""}"/>
          </#if>
        </div>
      </div>
    </div>
    
    <div class="infoRow">
      <div class="infoEntry">
        <div class="infoCaption">
          <label>${uiLabelMap.LabelImageFormatCaption}</label>
        </div>
        <div class="infoValue">
          <#if mode?has_content>
            <input class="large" name="labelImageFormat" type="text" id="labelImageFormat" value="${parameters.labelImageFormat!labelImageFormat!""}"/>
          </#if>
        </div>
      </div>
    </div>
    
    <div class="infoRow">
      <div class="infoEntry">
        <div class="infoCaption">
          <label>${uiLabelMap.RateEstimateTemplateCaption}</label>
        </div>
        <div class="infoValue">
          <#if mode?has_content>
            <input class="large" name="rateEstimateTemplate" type="text" id="rateEstimateTemplate" value="${parameters.rateEstimateTemplate!rateEstimateTemplate!""}"/>
          </#if>
        </div>
      </div>
    </div>
  </#if>
<#else>
   ${screens.render("component://osafeadmin/widget/CommonScreens.xml#ListNoDataResult")}
</#if>