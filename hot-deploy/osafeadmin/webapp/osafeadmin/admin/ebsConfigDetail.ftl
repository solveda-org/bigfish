  <#if paymentGatewayEBS?has_content>
   <input type="hidden" id="paymentGatewayConfigId" name = "paymentGatewayConfigId" value="${parameters.configId!""}"/> 
   <table class="osafe" cellspacing="0">
       <thead>
         <tr class="heading">
           <th class="idCol firstCol">${uiLabelMap.ParameterNameLabel!""}</th>
           <th class="descCol">${uiLabelMap.CurrentValueLabel!""}</th>
         </tr>
       </thead>
       <tbody>
            <#assign rowClass = "1">
                <tr class="dataRow even">
                     <td class="descCol" >
                       <label>${uiLabelMap.ApiUrlCaption!""}</label>
                     </td>
                     <td class="seqCol">
                       <input type="text" class="large" name="apiUrl" id="apiUrl" maxlength="255" value="${parameters.apiUrl!paymentGatewayEBS.apiUrl!""}"/>
                     </td>
                </tr>
                <tr class="dataRow odd">
                     <td class="descCol" >
                       <label>${uiLabelMap.MerchantIdCaption!""}</label>
                     </td>
                     <td class="seqCol">
                       <input type="text" class="large" name="merchantId" id="merchantId" maxlength="60" value="${parameters.merchantId!paymentGatewayEBS.merchantId!""}"/>
                     </td>
                </tr>
                <tr class="dataRow even">
                     <td class="descCol" >
                       <label>${uiLabelMap.SecretKeyCaption!""}</label>
                     </td>
                     <td class="seqCol">
                       <input type="text" class="large" name="secretKey" id="secretKey" maxlength="255" value="${parameters.secretKey!paymentGatewayEBS.secretKey!""}"/>
                     </td>
                </tr>
                <tr class="dataRow odd">
                     <td class="descCol" >
                       <label>${uiLabelMap.ReturnUrlCaption!""}</label>
                     </td>
                     <td class="seqCol">
                       <input type="text" class="large" name="returnUrl" id="returnUrl" maxlength="255" value="${parameters.returnUrl!paymentGatewayEBS.returnUrl!""}"/>
                     </td>
                </tr>
                <tr class="dataRow even">
                     <td class="descCol" >
                       <label>${uiLabelMap.RedirectUrlCaption!""}</label>
                     </td>
                     <td class="seqCol">
                       <input type="text" class="large" name="redirectUrl" id="redirectUrl"maxlength="255" value="${parameters.redirectUrl!paymentGatewayEBS.redirectUrl!""}"/>
                     </td>
                </tr>
                <tr class="dataRow odd">
                     <td class="descCol" >
                       <label>${uiLabelMap.ModeCaption!""}</label>
                     </td>
                     <td class="seqCol">
                       <select name="ebsMode">
                           <option <#if paymentGatewayEBS.ebsMode == 'TEST'> selected </#if>>${parameters.ebsMode!"TEST"}</option>
                           <option <#if paymentGatewayEBS.ebsMode == 'LIVE'> selected </#if>>${parameters.ebsMode!"LIVE"}</option>
                       </select>
                     </td>
                </tr>
        </tbody>
      </table>
  <#else>
  	 ${screens.render("component://osafeadmin/widget/CommonScreens.xml#ListNoDataResult")}
</#if>
