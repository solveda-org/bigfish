<tr class="heading">
  <th class="firstCol">${uiLabelMap.ConfigIDLabel}</th>
  <th class="descCol">${uiLabelMap.DescriptionLabel}</th>
</tr>
<#if resultList?exists && resultList?has_content>
  <#assign rowClass = "1"/>
    <#list resultList as paymentGateway>
        <#assign hasNext = paymentGateway_has_next>
        <tr class="dataRow <#if rowClass?if_exists == "2">even<#else>odd</#if>">
           <td class="firstCol" >
             <a href="<@ofbizUrl>${detailPage}?configId=${paymentGateway.paymentGatewayConfigId}</@ofbizUrl>">${paymentGateway.paymentGatewayConfigId!""}</a>
           </td>
           <td class="descCol <#if !hasNext>lastRow</#if>" >
              ${paymentGateway.description!"N/A"}
           </td>
        </tr>
        <#if rowClass == "2">
            <#assign rowClass = "1">
        <#else>
            <#assign rowClass = "2">
        </#if>
        </form>
    </#list>
</#if>