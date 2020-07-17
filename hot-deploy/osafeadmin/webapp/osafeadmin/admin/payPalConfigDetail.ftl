  <#if paymentGatewayPayPal?has_content>
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
                       <label>${uiLabelMap.BusinessEmailCaption!""}</label>
                     </td>
                     <td class="seqCol">
                       <input type="text" class="large" name="businessEmail" id="businessEmail" maxlength="255" value="${parameters.businessEmail!paymentGatewayPayPal.businessEmail!""}"/>
                     </td>
                </tr>
                <tr class="dataRow odd">
                     <td class="descCol" >
                       <label>${uiLabelMap.ApiUserNameCaption!""}</label>
                     </td>
                     <td class="seqCol">
                       <input type="text" class="large" name="apiUserName" id="apiUserName" maxlength="255" value="${parameters.apiUserName!paymentGatewayPayPal.apiUserName!""}"/>
                     </td>
                </tr>
                <tr class="dataRow even">
                     <td class="descCol" >
                       <label>${uiLabelMap.ApiPasswordCaption!""}</label>
                     </td>
                     <td class="seqCol">
                       <input type="text" class="large" name="apiPassword" id="apiPassword" maxlength="255" value="${parameters.apiPassword!paymentGatewayPayPal.apiPassword!""}"/>
                     </td>
                </tr>
                <tr class="dataRow odd">
                     <td class="descCol" >
                       <label>${uiLabelMap.ApiSignatureCaption!""}</label>
                     </td>
                     <td class="seqCol">
                       <input type="text" class="large" name="apiSignature" id="apiSignature"maxlength="60" value="${parameters.apiSignature!paymentGatewayPayPal.apiSignature!""}"/>
                     </td>
                </tr>
                <tr class="dataRow even">
                     <td class="descCol" >
                       <label>${uiLabelMap.ApiEnvironmentCaption!""}</label>
                     </td>
                     <td class="seqCol">
                       <input type="text" class="large" name="apiEnvironment" id="apiEnvironment" maxlength="60" value="${parameters.apiEnvironment!paymentGatewayPayPal.apiEnvironment!""}"/>
                     </td>
                </tr>
                <tr class="dataRow odd">
                     <td class="descCol" >
                       <label>${uiLabelMap.NotifyUrlCaption!""}</label>
                     </td>
                     <td class="seqCol">
                       <input type="text" class="large" name="notifyUrl" id="notifyUrl" maxlength="255" value="${parameters.notifyUrl!paymentGatewayPayPal.notifyUrl!""}"/>
                     </td>
                </tr>
                <tr class="dataRow even">
                     <td class="descCol" >
                       <label>${uiLabelMap.ReturnUrlCaption!""}</label>
                     </td>
                     <td class="seqCol">
                       <input type="text" class="large" name="returnUrl" id="returnUrl" maxlength="255" value="${parameters.returnUrl!paymentGatewayPayPal.returnUrl!""}"/>
                     </td>
                </tr>
                <tr class="dataRow odd">
                     <td class="descCol" >
                       <label>${uiLabelMap.CancelReturnUrlCaption!""}</label>
                     </td>
                     <td class="seqCol">
                       <input type="text" class="large" name="cancelReturnUrl" id="cancelReturnUrl" maxlength="255"  value="${parameters.cancelReturnUrl!paymentGatewayPayPal.cancelReturnUrl!""}"/>
                     </td>
                </tr>
                <tr class="dataRow even">
                     <td class="descCol" >
                       <label>${uiLabelMap.ImageUrlCaption!""}</label>
                     </td>
                     <td class="seqCol">
                       <input type="text" class="large" name="imageUrl" id="imageUrl" maxlength="255" value="${parameters.imageUrl!paymentGatewayPayPal.imageUrl!""}"/>
                     </td>
                </tr>
                <tr class="dataRow odd">
                     <td class="descCol" >
                       <label>${uiLabelMap.ConfirmTemplateCaption!""}</label>
                     </td>
                     <td class="seqCol">
                       <input type="text" class="large" name="confirmTemplate" id="confirmTemplate" maxlength="255" value="${parameters.confirmTemplate!paymentGatewayPayPal.confirmTemplate!""}"/>
                     </td>
                </tr>
                <tr class="dataRow even">
                     <td class="descCol" >
                       <label>${uiLabelMap.RedirectUrlCaption!""}</label>
                     </td>
                     <td class="seqCol">
                       <input type="text" class="large" name="redirectUrl" id="redirectUrl"  maxlength="255" value="${parameters.redirectUrl!paymentGatewayPayPal.redirectUrl!""}"/>
                     </td>
                </tr>
                <tr class="dataRow odd">
                     <td class="descCol" >
                       <label>${uiLabelMap.ConfirmUrlCaption!""}</label>
                     </td>
                     <td class="seqCol">
                       <input type="text" class="large" name="confirmUrl" id="confirmUrl" maxlength="255" value="${parameters.confirmUrl!paymentGatewayPayPal.confirmUrl!""}"/>
                     </td>
                </tr>
                 <tr class="dataRow even">
                     <td class="descCol" >
                       <label>${uiLabelMap.ShippingCallbackUrlCaption!""}</label>
                     </td>
                     <td class="seqCol">
                       <input type="text" class="large" name="shippingCallbackUrl" id="shippingCallbackUrl" maxlength="255" value="${parameters.shippingCallbackUrl!paymentGatewayPayPal.shippingCallbackUrl!""}"/>
                     </td>
                </tr>
                <tr class="dataRow odd">
                     <td class="descCol" >
                       <label>${uiLabelMap.RequireConfirmedShippingCaption!""}</label>
                     </td>
                     <td class="seqCol">
                       <select name = "requireConfirmedShipping">
                           <option <#if paymentGatewayPayPal.requireConfirmedShipping == 'Y'> selected </#if>>${parameters.requireConfirmedShipping!"Y"}</option>
                           <option <#if paymentGatewayPayPal.requireConfirmedShipping == 'N'> selected </#if>>${parameters.requireConfirmedShipping!"N"}</option>
                       </select>
                     </td>
                </tr>
                 
        </tbody>
      </table>
  <#else>
        ${screens.render("component://osafeadmin/widget/CommonScreens.xml#ListNoDataResult")}
</#if>
