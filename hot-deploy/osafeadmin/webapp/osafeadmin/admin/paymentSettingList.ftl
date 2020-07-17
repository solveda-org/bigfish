<tr class="heading">
  <th class="nameCol firstCol">${uiLabelMap.ServiceTypeLabel}</th>
  <th class="typeCol">${uiLabelMap.PaymentMethodTypeLabel}</th>
  <th class="nameCol">${uiLabelMap.ServiceNameLabel}</th>
  <th class="descCol">${uiLabelMap.CustomMethodLabel}</th>
  <th class="nameCol">${uiLabelMap.PaymentGatwayConfigIdLabel}</th>
  <th class="nameCol">${uiLabelMap.PaymentPropertiesLabel}</th>
  <th class="actionColSmall lastCol">${uiLabelMap.ApplyToAllLabel}</th>
</tr>
<#if resultList?exists && resultList?has_content>
  <#assign rowClass = "1"/>
    <#list resultList as paymentSetting>
        <#assign hasNext = paymentSetting_has_next>
        <tr class="dataRow <#if rowClass?if_exists == "2">even<#else>odd</#if>">
           <td class="nameCol firstCol" >
               <#assign paymentServiceTypeEnum = paymentSetting.getRelatedOne("Enumeration")?if_exists>
               <a href="<@ofbizUrl>${detailPage}?paymentMethodTypeId=${paymentSetting.paymentMethodTypeId?if_exists}&paymentServiceTypeEnumId=${paymentSetting.paymentServiceTypeEnumId?if_exists}</@ofbizUrl>">
                   ${(paymentServiceTypeEnum.get("description",locale))?default(paymentSetting.paymentServiceTypeEnumId?if_exists)}
               </a>
           </td>
           <td class="typeCol" >
               <#assign paymentMethodType = paymentSetting.getRelatedOne("PaymentMethodType")?if_exists/>
               ${(paymentMethodType.get("description",locale))?default(paymentSetting.paymentMethodTypeId?if_exists)}
           </td>
           <td class="nameCol" >${paymentSetting.paymentService?if_exists}</td>
           <td class="descCol" >
               <#assign customMethod = paymentSetting.getRelatedOne("CustomMethod")?if_exists/>
               <#if customMethod?has_content>
                   ${customMethod.description?if_exists} (${customMethod.customMethodName?if_exists})
               <#else>
                   ${paymentSetting.paymentCustomMethodId?if_exists}
               </#if>
           </td>
           <td class="nameCol" >
               <#assign paymentGatewayConfig = paymentSetting.getRelatedOne("PaymentGatewayConfig")?if_exists/>
               <#if paymentGatewayConfig?has_content>
                   ${paymentGatewayConfig.description?if_exists}
               <#else>
                   ${paymentSetting.paymentGatewayConfigId?if_exists}
               </#if>
           </td>
           <td class="nameCol" >${paymentSetting.paymentPropertiesPath?if_exists}</td>
           <td class="actionColSmall lastCol" >${paymentSetting.applyToAllProducts?if_exists}</td>
        </tr>
        <#if rowClass == "2">
            <#assign rowClass = "1">
        <#else>
            <#assign rowClass = "2">
        </#if>
        </form>
    </#list>
</#if>