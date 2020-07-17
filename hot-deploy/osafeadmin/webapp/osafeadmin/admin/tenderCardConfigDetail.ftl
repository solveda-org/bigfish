  <#if paymentGatewayTenderCard?has_content>
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
                       <input type="text" class="large" name="apiUrl" id="apiUrl" maxlength="255" value="${parameters.apiUrl!paymentGatewayTenderCard.apiUrl!""}"/>
                     </td>
                </tr>
                <tr class="dataRow odd">
                     <td class="descCol" >
                       <label>${uiLabelMap.TenderCardIdCaption!""}</label>
                     </td>
                     <td class="seqCol">
                       <input type="text" class="large" name="tenderCardId" id="tenderCardId" maxlength="60" value="${parameters.tenderCardId!paymentGatewayTenderCard.tenderCardId!""}"/>
                     </td>
                </tr>
                <tr class="dataRow even">
                     <td class="descCol" >
                       <label>${uiLabelMap.TerminalIdCaption!""}</label>
                     </td>
                     <td class="seqCol">
                       <input type="text" class="large" name="terminalId" id="terminalId" maxlength="255" value="${parameters.terminalId!paymentGatewayTenderCard.terminalId!""}"/>
                     </td>
                </tr>
        </tbody>
      </table>
  <#else>
     ${screens.render("component://osafeadmin/widget/CommonScreens.xml#ListNoDataResult")}
</#if>
