<!-- start shippingChargeList.ftl -->
          <thead>
            <tr class="heading">
                <th class="idCol firstCol">${uiLabelMap.ShipChargeIdLabel}</th>
                <th class="seqCol">${uiLabelMap.ShipSeqLabel}</th>
                <th class="nameCol">${uiLabelMap.ShipCarrierLabel}</th>
                <th class="nameCol">${uiLabelMap.ShipMethodTypeLabel}</th>
                <th class="nameCol">${uiLabelMap.CarrierServCodeLabel}</th>
                <th class="descCol">${uiLabelMap.CarrierShipMessageLabel}</th>
                <th class="nameCol">${uiLabelMap.ShipmentGatewayLabel}</th>
                <th class="numberCol">${uiLabelMap.ShipMinTotalLabel}</th>
                <th class="numberCol">${uiLabelMap.ShipMaxTotalLabel}</th>
                <th class="numberCol">${uiLabelMap.ShipFlatRateLabel}</th>
            </tr>
          </thead>
        <#if resultList?exists && resultList?has_content>
            <#assign rowClass = "1">
            <#list resultList as shipCharge>
              <#assign hasNext = shipCharge_has_next>
                <tr class="dataRow <#if rowClass == "2">even<#else>odd</#if>">
                <#assign shipmentCostEstimateList = delegator.findByAnd("ShipmentCostEstimate", Static["org.ofbiz.base.util.UtilMisc"].toMap("productStoreShipMethId",shipCharge.productStoreShipMethId!))/>
                <#assign shipmentGatewayList = delegator.findByAnd("ShipmentGatewayConfig", Static["org.ofbiz.base.util.UtilMisc"].toMap("shipmentGatewayConfigId",shipCharge.shipmentGatewayConfigId!))/>
                <#assign shipmentGatewayConfig = "" />
                <#if shipmentGatewayList?has_content>                    
	              	<#assign shipmentGateway = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(shipmentGatewayList)/>
	              	<#assign shipmentGatewayConfig = shipmentGateway.shipmentGatewayConfTypeId!""/>
	            </#if>
                <#if shipmentCostEstimateList?has_content>
	              	<#assign shipmentCostEstimate = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(shipmentCostEstimateList)/>
	              	<#assign flatPrice = shipmentCostEstimate.orderFlatPrice!""/>
	            </#if>    
	            
	            <!-- get the CarrierShipmentMethod Info -->
	            <#assign carrierShipmentMethod = delegator.findByPrimaryKey("CarrierShipmentMethod", Static["org.ofbiz.base.util.UtilMisc"].toMap("shipmentMethodTypeId", shipCharge.shipmentMethodTypeId!, "partyId", shipCharge.partyId!, "roleTypeId", "CARRIER"))/>             

                    <td class="idCol <#if !hasNext>lastRow</#if> firstCol" ><a href="<@ofbizUrl>shippingChargeDetail?productStoreShipMethId=${shipCharge.productStoreShipMethId}</@ofbizUrl>">${shipCharge.productStoreShipMethId}</a></td>
                    <td class="seqCol <#if !hasNext>lastRow</#if>">${shipCharge.sequenceNumber!""}</td>
                    <td class="nameCol <#if !hasNext>lastRow</#if>">${shipCharge.partyId!}</td>
                    <td class="nameCol <#if !hasNext>lastRow</#if>">${shipCharge.shipmentMethodTypeId!""}</td>
                    <td class="nameCol <#if !hasNext>lastRow</#if>">${carrierShipmentMethod.carrierServiceCode!""}</td>
                    <td class="descCol <#if !hasNext>lastRow</#if>">${carrierShipmentMethod.optionalMessage!""}</td>
                    <td class="nameCol <#if !hasNext>lastRow</#if>">${shipmentGatewayConfig!""}</td>
                    <td class="numberCol <#if !hasNext>lastRow</#if>">${shipCharge.minTotal!""}</td>
                    <td class="numberCol <#if !hasNext>lastRow</#if>">${shipCharge.maxTotal!""}</td>
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
<!-- end shippingChargeList.ftl -->