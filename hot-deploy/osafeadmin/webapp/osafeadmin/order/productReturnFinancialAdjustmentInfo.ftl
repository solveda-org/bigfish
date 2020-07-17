    <input type="hidden" name="returnAdjustmentTypeId" id="returnAdjustmentTypeId" value="RET_MAN_ADJ" />
    <#if (returnOrderItemSubtotal > 0)>
	    <div class="displayListBox detailInfo">
	        <div class="header">
                <h2>${uiLabelMap.FinancialAdjustmentsHeading!}</h2>
            </div>
            <div class="boxBody">
	            <table class="osafe">
				    <tr class="heading">
				        <th class="idCol">${uiLabelMap.ItemSeqNoLabel}</th>
				        <th class="idCol">${uiLabelMap.ProductNoLabel}</th>
				        <th class="dollarCol">${uiLabelMap.ItemPriceLabel}</th>
				        <th class="qtyCol">${uiLabelMap.QtyAdjLabel}</th>
				        <th class="dollarCol">${uiLabelMap.PriceAdjustLabel}</th>
				    </tr>
    				<#if returnItems?exists && returnItems?has_content>
				        <#assign rowClass = "1">
				        <#assign rowNo = 0/>
				        <#list returnItems as returnItem>
				            <#assign productId = returnItem.productId?if_exists>
				            <#if returnItemSeqIdQuantityMap?has_content>
				                <#assign returnQty = returnItemSeqIdQuantityMap.get("${returnItem.orderItemSeqId}")/>
				                <#assign returnQtyAdj = (0?number - returnQty)/>
				            </#if>
				            
				            <tr class="dataRow <#if rowClass == "2">even<#else>odd></#if>">
				                <td class="idCol <#if !returnItem_has_next>lastRow</#if>">${(returnItem.orderItemSeqId)!""}</td>
				                <td class="idCol <#if !returnItem_has_next>lastRow</#if>">${productId!}</td>
				                <td class="dollarCol <#if !returnItem_has_next>lastRow</#if>"><@ofbizCurrency amount = returnItem.unitPrice rounding=globalContext.currencyRounding isoCode=currencyUomId/></td>
				                <td class="qtyCol <#if !returnItem_has_next>lastRow</#if>">${returnQtyAdj}</td>
				                <td class="dollarCol <#if !returnItem_has_next>lastRow</#if>"><@ofbizCurrency amount = returnItem.unitPrice*returnQtyAdj rounding=globalContext.currencyRounding isoCode=currencyUomId/></td>
				            </tr>
				            <#-- toggle the row color -->
				            <#if rowClass == "2">
				                <#assign rowClass = "1">
				            <#else>
				                <#assign rowClass = "2">
				            </#if>
				            <#assign rowNo = rowNo+1/>
				        </#list>
				    </#if>
	            </table>
            </div>  
	        <div class="boxBody">    
	            <div class="heading">
	                <h2>${uiLabelMap.CalculatedFinancialAdjustmentsHeading!}</h2>
	            </div>
	            
	            <#assign rowNo = 1/>
	            <#list orderAdjustments as orderAdjustment>
	                <#assign returnAdjustments = orderAdjustment.getRelated("ReturnAdjustment")/>
	                <#if !returnAdjustments?has_content>
		                <#assign returnAdjustmentType = returnItemTypeMap.get(orderAdjustment.get("orderAdjustmentTypeId"))/>
		                <#assign adjustmentType = orderAdjustment.getRelatedOne("OrderAdjustmentType")/>
	                    <#assign description = orderAdjustment.description?default(adjustmentType.get("description",locale))/>
	                    
		                <#if orderAdjustment.orderAdjustmentTypeId == "SHIPPING_CHARGES">
		                    <#assign adjustmentCaption = uiLabelMap.ShippingCaption/>
		                <#elseif orderAdjustment.orderAdjustmentTypeId == "SALES_TAX">
		                    <#assign adjustmentCaption = uiLabelMap.SalesTaxCaption/>
		                <#elseif orderAdjustment.orderAdjustmentTypeId == "PROMOTION_ADJUSTMENT">
		                    <#assign adjustmentCaption = uiLabelMap.PromoCaption+":"/>
		                <#else>
		                    <#assign adjustmentCaption = description/>
		                </#if>
		                <input type="hidden" name="returnAdjustmentTypeId_${rowNo}" value="${returnAdjustmentType}"/>
	                    <input type="hidden" name="orderAdjustmentId_${rowNo}" value="${orderAdjustment.orderAdjustmentId}"/>
	                    <input type="hidden" name="returnItemSeqId_${rowNo}" value='${orderAdjustment.orderItemSeqId!"N/A"}'/>
	                    <input type="hidden" name="description_${rowNo}" value="${description}"/>
	                    
		                <div class="infoRow row">
			                <div class="infoEntry">
			                    <div class="infoValue medium">
			                        <label>${adjustmentCaption!}</label>
			                    </div>
			                    <div class="infoValue medium">
			                        <p><input type="checkbox" name="includeReturnAdjustment_${rowNo}" value="Y"/>${uiLabelMap.IncludeLabel}</p>
			                    </div>
			                    <div class="infoValue">
         			                <@ofbizCurrency amount= (0?number - orderAdjustment.amount) rounding=globalContext.currencyRounding isoCode=currencyUomId/>
   	                                <input type="hidden" name="adjustmentAmount_${rowNo}" value="${orderAdjustment.amount}"/>
			                    </div>
			                </div>
		                </div>
		                <#assign rowNo = rowNo + 1/>
		            </#if>    
	            </#list>
	        </div>
	      </div>
	        
         <div class="displayListBox detailInfo">
	        <div class="boxBody">
		        <div class="heading">
	                <h2>${uiLabelMap.MiscFinancialAdjustmentsHeading!}</h2>
	            </div>
	            
	            <div class="infoRow row">
		            <div class="infoEntry">
		                <div class="infoCaption">
		                    <label>${uiLabelMap.DescriptionCaption!}</label>
		                </div>
		                <div class="infoValue">
		                    <input type="text" name="orderAdjustmentDescription" id="orderAdjustmentDescription" maxlength="255" value="${parameters.orderAdjustmentDescription!""}"/>
		                </div>
		            </div>
		            <div class="infoEntry">
		                <div class="infoCaption">
		                    <label>${uiLabelMap.AmountCaption!}</label>
		                </div>
		                <div class="infoValue">
		                    <input type="hidden" name="orderAdjustmentTypeId" id="orderAdjustmentTypeId" value="RTN_REFUND"/>
		                    <input type="text" name="orderAdjustmentAmount" id="orderAdjustmentAmount" maxlength="20" value="${parameters.orderAdjustmentAmount!""}"/>
		                </div>
		            </div>
		        </div>
	        </div>
	        
	     </div>
    </#if>