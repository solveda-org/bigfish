 <#assign selectedDropOffType = parameters.defaultDropoffType!/>
 <#assign selectedDefaultPackagingType = parameters.defaultPackagingType!/> 
<#if mode?has_content>  
  <#if shipmentGatewayFedex?has_content>
    <#assign shipmentGatewayConfigId= shipmentGatewayFedex.shipmentGatewayConfigId! />
    <#assign connectUrl= shipmentGatewayFedex.connectUrl! />
    <#assign connectSoapUrl= shipmentGatewayFedex.connectSoapUrl! />
    <#assign connectTimeout= shipmentGatewayFedex.connectTimeout! />
    <#assign accessAccountNbr= shipmentGatewayFedex.accessAccountNbr! />
    <#assign accessMeterNumber= shipmentGatewayFedex.accessMeterNumber! />
    <#assign accessUserKey= shipmentGatewayFedex.accessUserKey! />
    <#assign accessUserPwd= shipmentGatewayFedex.accessUserPwd! />
    <#assign labelImageType= shipmentGatewayFedex.labelImageType! />
    <#assign defaultDropoffType= shipmentGatewayFedex.defaultDropoffType! />  
    <#assign defaultPackagingType= shipmentGatewayFedex.defaultPackagingType! />      
    <#assign templateShipment= shipmentGatewayFedex.templateShipment! />
    <#assign templateSubscription= shipmentGatewayFedex.templateSubscription! />
    <#assign rateEstimateTemplate= shipmentGatewayFedex.rateEstimateTemplate! />
    <#if !selectedDropOffType?has_content>
        <#assign selectedDropOffType = shipmentGatewayFedex.defaultDropoffType!""/>
    <#else>
        <#assign selectedDropOffType = parameters.defaultDropoffType!""/>
    </#if>
    <#if !selectedDefaultPackagingType?has_content>
        <#assign selectedDefaultPackagingType = shipmentGatewayFedex.defaultPackagingType!""/>
    <#else>
        <#assign selectedDefaultPackagingType = parameters.defaultPackagingType!""/>
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
          <label>${uiLabelMap.ConnectSoapUrlCaption}</label>
        </div>
        <div class="infoValue">
          <#if mode?has_content>
            <input class="large" name="connectSoapUrl" type="text" id="connectSoapUrl" value="${parameters.connectSoapUrl!connectSoapUrl!""}"/>
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
          <label>${uiLabelMap.AccessAccountNbrFedCaption}</label>
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
          <label>${uiLabelMap.AccessMeterNumberCaption}</label>
        </div>
        <div class="infoValue">
          <#if mode?has_content>
            <input class="large" name="accessMeterNumber" type="text" id="accessMeterNumber" value="${parameters.accessMeterNumber!accessMeterNumber!""}"/>
          </#if>
        </div>
      </div>
    </div>
    
    <div class="infoRow">
      <div class="infoEntry">
        <div class="infoCaption">
          <label>${uiLabelMap.AccessUserKeyCaption}</label>
        </div>
        <div class="infoValue">
          <#if mode?has_content>
            <input class="large" name="accessUserKey" type="text" id="accessUserKey" value="${parameters.accessUserKey!accessUserKey!""}"/>
          </#if>
        </div>
      </div>
    </div>
    
    <div class="infoRow">
      <div class="infoEntry">
        <div class="infoCaption">
          <label>${uiLabelMap.AccessUserPwdCaption}</label>
        </div>
        <div class="infoValue">
          <#if mode?has_content>
            <input class="large" name="accessUserPwd" type="text" id="accessUserPwd" value="${parameters.accessUserPwd!accessUserPwd!""}"/>
          </#if>
        </div>
      </div>
    </div>
    
    <div class="infoRow">
      <div class="infoEntry">
        <div class="infoCaption">
          <label>${uiLabelMap.LabelImageTypeCaption}</label>
        </div>
        <div class="infoValue">
          <#if mode?has_content>
            <input class="large" name="labelImageType" type="text" id="labelImageType" value="${parameters.labelImageType!labelImageType!""}"/>
          </#if>
        </div>
      </div>
    </div>
    
    <div class="infoRow">
      <div class="infoEntry">
        <div class="infoCaption">
          <label>${uiLabelMap.DropoffTypeCaption}</label>
        </div>
        <div class="infoValue">       
            <#if mode?has_content>
          		<select name="defaultDropoffType" id="defaultDropoffType" class="small">
                      <option value="">${uiLabelMap.SelectOneLabel}</option>
                      <option value="REGULARPICKUP" <#if selectedDropOffType == "REGULARPICKUP" >selected=selected</#if> >Regular Pickup</option>
                      <option value="REQUESTCOURIER" <#if selectedDropOffType == "REQUESTCOURIER" >selected=selected</#if>>Request Courier</option>
                      <option value="DROPBOX" <#if selectedDropOffType == "DROPBOX" >selected=selected</#if>>Drop-Box</option>
                      <option value="BUSINESSSERVICECTR" <#if selectedDropOffType == "BUSINESSSERVICECTR" >selected=selected</#if>>Business Service Center</option>
                      <option value="STATION" <#if selectedDropOffType == "STATION" >selected=selected</#if>>Station</option>
		        </select>
        	</#if>
        </div>
      </div>
    </div>
    
    <div class="infoRow">
      <div class="infoEntry">
        <div class="infoCaption">
          <label>${uiLabelMap.DefaultPackagingTypeCaption}</label>
        </div>
        <div class="infoValue">       
            <#if mode?has_content>
          		<select name="defaultPackagingType" id="defaultPackagingType" class="small">
                      <option value="">${uiLabelMap.SelectOneLabel}</option>
                      <option value="FXENV" <#if selectedDefaultPackagingType == "FXENV" >selected="selected"</#if>>Enveloper</option>
                      <option value="FXENV_LGL" <#if selectedDefaultPackagingType == "FXENV_LGL" >selected="selected"</#if>>Enveloper Legal</option>
                      <option value="FXPAK_SM" <#if selectedDefaultPackagingType == "FXPAK_SM" >selected="selected"</#if>>Pak Small</option>
                      <option value="FXPAK_LRG" <#if selectedDefaultPackagingType == "FXPAK_LRG" >selected="selected"</#if>>Pak Large</option>
                      <option value="FXBOX_SM" <#if selectedDefaultPackagingType == "FXBOX_SM" >selected="selected"</#if>>Box Small</option>
                      <option value="FXBOX_MED" <#if selectedDefaultPackagingType == "FXBOX_MED" >selected="selected"</#if>>Box Medium</option>
                      <option value="FXBOX_LRG" <#if selectedDefaultPackagingType == "FXBOX_LRG" >selected="selected"</#if>>Box Large</option>
                      <option value="FXTUBE" <#if selectedDefaultPackagingType == "FXTUBE" >selected="selected"</#if>>Tube</option>
                      <option value="FX10KGBOX" <#if selectedDefaultPackagingType == "FX10KGBOX" >selected="selected"</#if>>10KG Box</option>
                      <option value="FX25KGBOX" <#if selectedDefaultPackagingType == "FX25KGBOX" >selected="selected"</#if>>25KG Box</option>
                      <option value="YOURPACKNG" <#if selectedDefaultPackagingType == "YOURPACKNG" >selected="selected"</#if>>Your Packaging</option>
		        </select>
        	</#if>
        </div>
      </div>
    </div>
    
    <div class="infoRow">
      <div class="infoEntry">
        <div class="infoCaption">
          <label>${uiLabelMap.TemplateShipmentCaption}</label>
        </div>
        <div class="infoValue">
          <#if mode?has_content>
            <input class="large" name="templateShipment" type="text" id="templateShipment" value="${parameters.templateShipment!templateShipment!""}"/>
          </#if>
        </div>
      </div>
    </div>
    
    <div class="infoRow">
      <div class="infoEntry">
        <div class="infoCaption">
          <label>${uiLabelMap.TemplateSubscriptionCaption}</label>
        </div>
        <div class="infoValue">
          <#if mode?has_content>
            <input class="large" name="templateSubscription" type="text" id="templateSubscription" value="${parameters.templateSubscription!templateSubscription!""}"/>
          </#if>
        </div>
      </div>
    </div>
    
    <div class="infoRow">
      <div class="infoEntry">
        <div class="infoCaption">
          <label>${uiLabelMap.RateEstimateTemplateFedCaption}</label>
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