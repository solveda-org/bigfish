  <#if paymentGatewaySagePayToken?has_content>
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
                       <label>${uiLabelMap.VendorCaption!""}</label>
                     </td>
                     <td class="seqCol">
                       <input type="text" class="large" name="vendor" id="vendor" maxlength="60" value="${parameters.vendor!paymentGatewaySagePayToken.vendor!""}"/>
                     </td>
                </tr>
                <tr class="dataRow odd">
                     <td class="descCol" >
                       <label>${uiLabelMap.ProductionHostCaption!""}</label>
                     </td>
                     <td class="seqCol">
                       <input type="text" class="large" name="productionHost" id="productionHost" maxlength="60" value ="${parameters.productionHost!paymentGatewaySagePayToken.productionHost!""}"/>
                     </td>
                </tr>
                <tr class="dataRow even">
                     <td class="descCol" >
                       <label>${uiLabelMap.TestingHostCaption!""}</label>
                     </td>
                     <td class="seqCol">
                       <input type="text" class="large" name="testingHost" id="testingHost" maxlength="60" value ="${parameters.testingHost!paymentGatewaySagePayToken.testingHost!""}"/>
                     </td>
                </tr>
                <tr class="dataRow odd">
                     <td class="descCol" >
                       <label>${uiLabelMap.SagePayModeCaption!""}</label>
                     </td>
                     <td class="seqCol">
                        <select name="sagePayMode">
                         <option <#if paymentGatewaySagePayToken.sagePayMode == 'TEST'> selected </#if>>${parameters.sagePayMode!uiLabelMap.TestCaption}</option>
                         <option <#if paymentGatewaySagePayToken.sagePayMode == 'PRODUCTION'> selected </#if>>${parameters.sagePayMode!uiLabelMap.ProductionCaption}</option>
                       </select>
                     </td>
                </tr>
                <tr class="dataRow even">
                     <td class="descCol" >
                       <label>${uiLabelMap.ProtocolVersionCaption!""}</label>
                     </td>
                     <td class="seqCol">
                       <input type="text" class="large" name="protocolVersion" id="protocolVersion" maxlength="10" value ="${parameters.protocolVersion!paymentGatewaySagePayToken.protocolVersion!""}"/>
                     </td>
                </tr>
                <tr class="dataRow odd">
                     <td class="descCol" >
                       <label>${uiLabelMap.AuthenticationTransTypeCaption!""}</label>
                     </td>
                     <td class="seqCol">
                     <select name="authenticationTransType">
                         <option <#if paymentGatewaySagePayToken.authenticationTransType == 'PAYMENT'> selected </#if>>${parameters.authenticationTransType!uiLabelMap.PaymentCaption}</option>
                         <option <#if paymentGatewaySagePayToken.authenticationTransType == 'DEFFERED'> selected </#if>>${parameters.authenticationTransType!uiLabelMap.DefferedCaption}</option>
                      </select>
                     </td>
                </tr>
                <tr class="dataRow even">
                     <td class="descCol" >
                       <label>${uiLabelMap.AuthenticationUrlCaption!""}</label>
                     </td>
                     <td class="seqCol">
                       <input type="text" class="large" name="authenticationUrl" id="authenticationUrl" maxlength="255" value ="${parameters.authenticationUrl!paymentGatewaySagePayToken.authenticationUrl!""}"/>
                     </td>
                </tr>
                <tr class="dataRow odd">
                     <td class="descCol" >
                       <label>${uiLabelMap.AuthoriseTransTypeCaption!""}</label>
                     </td>
                     <td class="seqCol">
                       <input type="text" class="large" name="authoriseTransType" id="authoriseTransType" maxlength="60" value ="${parameters.authoriseTransType!paymentGatewaySagePayToken.authoriseTransType!""}"/>
                     </td>
                </tr>
                <tr class="dataRow even">
                     <td class="descCol" >
                       <label>${uiLabelMap.AuthoriseUrlCaption!""}</label>
                     </td>
                     <td class="seqCol">
                       <input type="text" class="large" name="authoriseUrl" id="authoriseUrl" maxlength="255" value ="${parameters.authoriseUrl!paymentGatewaySagePayToken.authoriseUrl!""}"/>
                     </td>
                </tr>
                <tr class="dataRow odd">
                     <td class="descCol" >
                       <label>${uiLabelMap.ReleaseTransTypeCaption!""}</label>
                     </td>
                     <td class="seqCol">
                       <input type="text" class="large" name="releaseTransType" id="releaseTransType" maxlength="60" value ="${parameters.releaseTransType!paymentGatewaySagePayToken.releaseTransType!""}"/>
                     </td>
                </tr>
                <tr class="dataRow even">
                     <td class="descCol" >
                       <label>${uiLabelMap.ReleaseTransUrlCaption!""}</label>
                     </td>
                     <td class="seqCol">
                       <input type="text" class="large" name="releaseUrl" id="releaseUrl" maxlength="255" value ="${parameters.releaseUrl!paymentGatewaySagePayToken.releaseUrl!""}"/>
                     </td>
                </tr>
                <tr class="dataRow odd">
                     <td class="descCol" >
                       <label>${uiLabelMap.VoidTransTypeCaption!""}</label>
                     </td>
                     <td class="seqCol">
                       <input type="text" class="large" name="voidTransType" id="voidTransType" maxlength="60" value ="${parameters.voidTransType!paymentGatewaySagePayToken.voidTransType!""}"/>
                     </td>
                </tr>
                 <tr class="dataRow even">
                     <td class="descCol" >
                       <label>${uiLabelMap.VoidTransUrlCaption!""}</label>
                     </td>
                     <td class="seqCol">
                       <input type="text" class="large" name="voidUrl" id="voidUrl" maxlength="255" value ="${parameters.voidUrl!paymentGatewaySagePayToken.voidUrl!""}"/>
                     </td>
                </tr>
                <tr class="dataRow odd">
                     <td class="descCol" >
                       <label>${uiLabelMap.RefundTransTypeCaption!""}</label>
                     </td>
                     <td class="seqCol">
                       <input type="text" class="large" name="refundTransType" id="refundTransType" maxlength="60" value ="${parameters.refundTransType!paymentGatewaySagePayToken.refundTransType!""}"/>
                     </td>
                </tr>
                <tr class="dataRow even">
                     <td class="descCol" >
                       <label>${uiLabelMap.RefundTransUrlCaption!""}</label>
                     </td>
                     <td class="seqCol">
                       <input type="text" class="large" name="refundUrl" id="refundUrl" maxlength="255" value ="${parameters.refundUrl!paymentGatewaySagePayToken.refundUrl!""}"/>
                     </td>
                </tr>
                <tr class="dataRow odd">
                     <td class="descCol" >
                       <label>${uiLabelMap.RegistrationTransTypeCaption!""}</label>
                     </td>
                     <td class="seqCol">
                       <input type="text" class="large" name="registrationTransType" id="registrationTransType" maxlength="60" value ="${parameters.registrationTransType!paymentGatewaySagePayToken.registrationTransType!""}"/>
                     </td>
                </tr>
                <tr class="dataRow even">
                     <td class="descCol" >
                       <label>${uiLabelMap.RegistrationTransUrlCaption!""}</label>
                     </td>
                     <td class="seqCol">
                       <input type="text" class="large" name="registrationUrl" id="registrationUrl" maxlength="255" value ="${parameters.registrationUrl!paymentGatewaySagePayToken.registrationUrl!""}"/>
                     </td>
                </tr>
                <tr class="dataRow odd">
                     <td class="descCol" >
                       <label>${uiLabelMap.StoreTokenCaption!""}</label>
                     </td>
                     <td class="seqCol">
                       <select name="storeToken">
                         <option <#if paymentGatewaySagePayToken.storeToken == '0'> selected </#if>>${parameters.storeToken!'0'}</option>
                         <option <#if paymentGatewaySagePayToken.storeToken == '1'> selected </#if>>${parameters.storeToken!'1'}</option>
                       </select>
                     </td>
                </tr>
                 
        </tbody>
      </table>
  <#else>
        ${screens.render("component://osafeadmin/widget/CommonScreens.xml#ListNoDataResult")}
</#if>
