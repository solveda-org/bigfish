<!-- start shipChargeList.ftl -->
          <thead>
            <tr class="heading">
                <th class="idCol firstCol">${uiLabelMap.ShipChargeIdLabel}</th>
                <th class="nameCol">${uiLabelMap.ShipCarrierLabel}</th>
                <th class="typeCol">${uiLabelMap.ShipMethodTypeLabel}</th>
                <th class="numberCol">${uiLabelMap.ShipMinTotalLabel}</th>
                <th class="numberCol">${uiLabelMap.ShipMaxTotalLabel}</th>
                <th class="seqCol">${uiLabelMap.ShipSeqLabel}</th>
                <th class="numberCol">${uiLabelMap.ShipFlatRateLabel}</th>
            </tr>
          </thead>
        <#if resultList?exists && resultList?has_content>
            <#assign rowClass = "1">
            <#list resultList as shipCharge>
              <#assign hasNext = shipCharge_has_next>
                <tr class="dataRow <#if rowClass == "2">even<#else>odd</#if>">
                <#assign shipmentCostEstimateList = delegator.findByAnd("ShipmentCostEstimate", Static["org.ofbiz.base.util.UtilMisc"].toMap("productStoreShipMethId",shipCharge.productStoreShipMethId!))/>
                <#if shipmentCostEstimateList?has_content>
	              	<#assign shipmentCostEstimate = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(shipmentCostEstimateList)/>
	              	<#assign flatPrice = shipmentCostEstimate.orderFlatPrice!""/>
	            </#if>                 

                    <td class="idCol <#if !hasNext>lastRow</#if> firstCol" ><a href="<@ofbizUrl>shipChargeDetail?productStoreShipMethId=${shipCharge.productStoreShipMethId}</@ofbizUrl>">${shipCharge.productStoreShipMethId}</a></td>
                    <td class="nameCol <#if !hasNext>lastRow</#if>">${shipCharge.partyId!}</td>
                    <td class="typeCol <#if !hasNext>lastRow</#if>">${shipCharge.shipmentMethodTypeId!""}</td>
                    <td class="numberCol <#if !hasNext>lastRow</#if>">${shipCharge.minTotal!""}</td>
                    <td class="numberCol <#if !hasNext>lastRow</#if>">${shipCharge.maxTotal!""}</td>
                    <td class="seqCol <#if !hasNext>lastRow</#if>">${shipCharge.sequenceNumber!""}</td>
                    <td class="numberCol <#if !hasNext>lastRow</#if>">${flatPrice!""}</td>
                </tr>

                <#-- toggle the row color -->
                <#if rowClass == "2">
                    <#assign rowClass = "1">
                <#else>
                    <#assign rowClass = "2">
                </#if>
            </#list>
        </#if>
<!-- end shipChargeList.ftl -->