<#if mode?has_content>
  <#if shipmentGatewayUsps?has_content>
    <#assign shipmentGatewayConfigId= shipmentGatewayUsps.shipmentGatewayConfigId! />
    <#assign connectUrl = shipmentGatewayUsps.connectUrl!/>
    <#assign connectTimeout = shipmentGatewayUsps.connectTimeout!/>
    <#assign accessUserId = shipmentGatewayUsps.accessUserId!/>
    <#assign accessPassword = shipmentGatewayUsps.accessPassword!/>
    <#assign maxEstimateWeight = shipmentGatewayUsps.maxEstimateWeight!/>
    
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
           <input class="" name="accessPassword" type="text" id="accessPassword" value="${parameters.accessPassword!accessPassword!""}"/>
        </#if>
        </div>
      </div>
    </div>

    <div class="infoRow">
      <div class="infoEntry">
        <div class="infoCaption">
          <label>${uiLabelMap.MaxEstimateWeightUspsCaption}</label>
        </div>
        <div class="infoValue">
        <#if mode?has_content>
           <input class="" name="maxEstimateWeight" type="text" id="maxEstimateWeight" value="${parameters.maxEstimateWeight!maxEstimateWeight!""}"/>
        </#if>
        </div>
      </div>
    </div>

  </#if>
<#else>
    ${screens.render("component://osafeadmin/widget/CommonScreens.xml#ListNoDataResult")}
</#if>