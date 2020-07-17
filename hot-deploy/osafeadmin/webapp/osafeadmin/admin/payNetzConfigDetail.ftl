<#if paymentGatewayPayNetz?has_content>
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
                       <input type="text" class="large" name="apiUrl" id="apiUrl" maxlength="255" value="${parameters.apiUrl!paymentGatewayPayNetz.apiUrl!""}"/>
                     </td>
                </tr>
                <tr class="dataRow odd">
                     <td class="descCol" >
                       <label>${uiLabelMap.ProductIdCaption!""}</label>
                     </td>
                     <td class="seqCol">
                       <input type="text" class="large" name="productId" id="productId" maxlength="255" value="${parameters.productId!paymentGatewayPayNetz.productId!""}"/>
                     </td>
                </tr>
                <tr class="dataRow odd">
                     <td class="descCol" >
                       <label>${uiLabelMap.TransactionTypeCaption!""}</label>
                     </td>
                     <td class="seqCol">
                       <input type="text" class="large" name="transactionType" id="transactionType" maxlength="255" value="${parameters.transactionType!paymentGatewayPayNetz.transactionType!""}"/>
                     </td>
                </tr>
                <tr class="dataRow even">
                     <td class="descCol" >
                       <label>${uiLabelMap.ReturnUrlCaption!""}</label>
                     </td>
                     <td class="seqCol">
                       <input type="text" class="large" name="returnUrl" id="returnUrl" maxlength="255" value="${parameters.returnUrl!paymentGatewayPayNetz.returnUrl!""}"/>
                     </td>
                </tr>
                <tr class="dataRow even">
                     <td class="descCol" >
                       <label>${uiLabelMap.RedirectUrlCaption!""}</label>
                     </td>
                     <td class="seqCol">
                       <input type="text" class="large" name="redirectUrl" id="redirectUrl" maxlength="255" value="${parameters.redirectUrl!paymentGatewayPayNetz.redirectUrl!""}"/>
                     </td>
                </tr>
                <tr class="dataRow odd">
                     <td class="descCol" >
                       <label>${uiLabelMap.LoginIdCaption!""}</label>
                     </td>
                     <td class="seqCol">
                       <input type="text" class="large" name="loginId" id="loginId" maxlength="60" value="${parameters.loginId!paymentGatewayPayNetz.loginId!""}"/>
                     </td>
                </tr>
                <tr class="dataRow even">
                     <td class="descCol" >
                       <label>${uiLabelMap.PasswordCaption!""}</label>
                     </td>
                     <td class="seqCol">
                       <input type="text" class="large" name="password" id="password" maxlength="255" value="${parameters.password!paymentGatewayPayNetz.password!""}"/>
                     </td>
                </tr>
                <tr class="dataRow odd">
                     <td class="descCol" >
                       <label>${uiLabelMap.ModeCaption!""}</label>
                     </td>
                     <td class="seqCol">
                       <select name="payNetzMode">
                           <option <#if paymentGatewayPayNetz.payNetzMode == 'TEST'> selected </#if>>${parameters.payNetzMode!"TEST"}</option>
                           <option <#if paymentGatewayPayNetz.payNetzMode == 'LIVE'> selected </#if>>${parameters.payNetzMode!"LIVE"}</option>
                       </select>
                     </td>
                </tr>
        </tbody>
      </table>
  <#else>
     ${screens.render("component://osafeadmin/widget/CommonScreens.xml#ListNoDataResult")}
</#if>
