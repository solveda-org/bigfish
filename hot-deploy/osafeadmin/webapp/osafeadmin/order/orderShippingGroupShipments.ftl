<#if orderItemShipGroup?has_content>
 <#assign orderShipments = orderItemShipGroup.getRelated("PrimaryShipment")!""/>
 <#if orderShipments?has_content>
    <#list orderShipments as shipment>
		     <#assign statusItem = shipment.getRelatedOne("StatusItem")?if_exists />
             <#assign shipmentItems =shipment.getRelated("ShipmentItem")!""/>
			    <div class="displayListBox generalInfo">
				    <div class="header"><h2>${uiLabelMap.OrderShippingDetailBoxHeading!}# ${shipment.shipmentId!}</h2></div>
					<div class="boxBody">
					    <div class="heading">${uiLabelMap.OrderShippingDetailsInfoHeading!}</div>
					    <div class="infoRow column">
					        <div class="infoEntry">
					            <div class="infoCaption">
					                <label>${uiLabelMap.ShipmentNoCaption}</label>
					            </div>
					            <div class="infoValue">
					                ${shipment.shipmentId!""}
					            </div>
					        </div>
					    </div>
					    <div class="infoRow column">
					        <div class="infoEntry">
					            <div class="infoCaption">
					                <label>${uiLabelMap.StatusCaption}</label>
					            </div>
					            <div class="infoValue">
					                <#if statusItem?has_content>${statusItem.description!""}</#if>
					            </div>
					            <div class="infoIcon">
                                    <a href="JavaScript:newPopupWindow('<@ofbizUrl>ShippingLabel.pdf?shipmentId=${shipment.shipmentId!}</@ofbizUrl>','popUpWindow','height=500,width=600,left=400,top=200,resizable=yes,scrollbars=yes,toolbar=yes,menubar=no,location=no,directories=no,status=yes');" onMouseover="javascript:showTooltip(event,'${uiLabelMap.GenerateShippingLabelInfo}');" onMouseout="hideTooltip()"><span class="shippingLabelPrintIcon"></span></a>
                                </div>
					        </div>
					    </div>
                   <#if shipmentItems?has_content>
						<div class="infoRow row">
						    <table class="osafe">
						        <tr class="heading">
						            <th class="idCol firstCol">${uiLabelMap.ItemNoLabel}</th>
						            <th class="nameCol">${uiLabelMap.ProductNameLabel}</th>
						            <th class="dollarCol">${uiLabelMap.ItemPriceLabel}</th>
						            <th class="qtyCol lastCol">${uiLabelMap.QtyLabel}</th>
						        </tr>
                              <#list shipmentItems as shipmentItem>
                                <#assign orderItem = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(delegator.findByAnd("OrderItem", {"orderId": shipment.primaryOrderId, "productId": shipmentItem.productId}))/> 
						        <#assign order =orderItem.getRelatedOne("OrderHeader")!""/>
						        <tr class="dataRow <#if rowClass?if_exists == "2">even<#else>odd</#if>">
						            <td class="idCol firstCol <#if !shipmentItem_has_next?if_exists>lastRow</#if>">${shipmentItem.productId!}</td>
						            <td class="nameCol <#if !shipmentItem_has_next?if_exists>lastRow</#if>">${orderItem.itemDescription!}</td>
						            <td class="dollarCol <#if !shipmentItem_has_next?if_exists>lastRow</#if>"><@ofbizCurrency amount=orderItem.unitPrice rounding=globalContext.currencyRounding isoCode=order.currencyUom/></td>
						            <td class="qtyCol lastCol <#if !shipmentItem_has_next?if_exists>lastRow</#if>">${shipmentItem.quantity!}</td>
						        </tr>
						      </#list>
						    </table>
						</div>
					</#if>
                    <#assign shipmentPackages = shipment.getRelated("ShipmentPackage")!""/>
                    <#list shipmentPackages as shipmentPackage>
			            <#assign dimensionUom = delegator.findByPrimaryKey('Uom', {"uomId" : shipmentPackage.dimensionUomId!})!"" />
			            <#assign weightUom = delegator.findByPrimaryKey('Uom', {"uomId" : shipmentPackage.weightUomId!})!"" />
						    <div class="heading">${uiLabelMap.OrderShippingPackageInfoHeading!}</div>
						    <div class="infoRow column">
						        <div class="infoEntry">
						            <div class="infoCaption">
						                <label>${uiLabelMap.HeightCaption}</label>
						            </div>
						            <div class="infoValue">
						                    ${shipmentPackage.boxHeight!}
						                    <#if shipmentPackage.boxHeight?has_content && dimensionUom?has_content>
						                        ${dimensionUom.abbreviation!}
						                    </#if>
						            </div>
						        </div>
						    </div>
						    
						    <div class="infoRow column">
						        <div class="infoEntry">
						            <div class="infoCaption">
						                <label>${uiLabelMap.WeightCaption}</label>
						            </div>
						            <div class="infoValue">
						                    ${shipmentPackage.weight!}
						                    <#if shipmentPackage.weight?has_content && weightUom?has_content>
						                        ${weightUom.abbreviation!}
						                    </#if>
						            </div>
						        </div>
						    </div>
						    
						    <div class="infoRow row">
						        <div class="infoEntry">
						            <div class="infoCaption">
						                <label>${uiLabelMap.WidthCaption}</label>
						            </div>
						            <div class="infoValue">
						                    ${shipmentPackage.boxWidth!}
						                    <#if shipmentPackage.boxWidth?has_content && dimensionUom?has_content>
						                        ${dimensionUom.abbreviation!}
						                    </#if>
						            </div>
						        </div>
						    </div>
						    
						    <div class="infoRow row">
						        <div class="infoEntry">
						            <div class="infoCaption">
						                <label>${uiLabelMap.DepthCaption}</label>
						            </div>
						            <div class="infoValue">
						                    ${shipmentPackage.boxLength!}
						                    <#if shipmentPackage.boxLength?has_content && dimensionUom?has_content>
						                        ${dimensionUom.abbreviation!}
						                    </#if>
						            </div>
						        </div>
						    </div>
					</#list>
                </div>
			</div>
    </#list>
 </#if>
</#if>
