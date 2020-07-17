    <#assign orderGrandTotal = 0?number/>
    <#assign totalPriorAdjustmnets = 0?number/>
    <div class="displayListBox detailInfo">
        <div class="header">
            <h2>${uiLabelMap.FinancialSummaryHeadeing!}</h2>
        </div>
        
        <div class="boxBody">
            <div class="heading">
                ${uiLabelMap.OriginalChargeMadeOnHeading!} ${(Static["com.osafe.util.OsafeAdminUtil"].convertDateTimeFormat(orderHeader.orderDate, preferredDateFormat).toLowerCase())!"N/A"}
            </div>
            <div class="infoRow row">
			    <div class="infoEntry">
				    <div class="infoCaption">
				        <label>${uiLabelMap.TotalForItemsCaption}</label>
				    </div>
				    <div class="infoValue">
				        <@ofbizCurrency amount = originalOrderItemSubtotal rounding=globalContext.currencyRounding isoCode=currencyUomId/>
				        <#assign orderGrandTotal = orderGrandTotal + originalOrderItemSubtotal/>
				    </div>
				</div>
			</div>
            
            <div class="infoRow row">
		        <div class="infoEntry">
		            <div class="infoCaption">
		                <label>
		                    ${uiLabelMap.PromoCaption}:
		                </label>
		            </div>
		            <div class="infoValue">
		                <@ofbizCurrency amount = originalOrderPromoAmount rounding=globalContext.currencyRounding isoCode=currencyUomId/>
		            </div>
		        </div>
		    </div>
            
            <#assign orderGrandTotal = orderGrandTotal + originalOrderPromoAmount/>       
                        
            <div class="infoRow row">
			    <div class="infoEntry">
				    <div class="infoCaption">
				        <label>${uiLabelMap.ShippingCaption}</label>
				    </div>
				    <div class="infoValue">
				        <@ofbizCurrency amount = originalOrderShippingAmount rounding=globalContext.currencyRounding isoCode=currencyUomId/>
				        <#assign orderGrandTotal = orderGrandTotal + originalOrderShippingAmount/>
				    </div>
				</div>
			</div>
			
			<div class="infoRow row">
			    <div class="infoEntry">
				    <div class="infoCaption">
				        <label>${uiLabelMap.SalesTaxCaption}</label>
				    </div>
				    <div class="infoValue">
				        <@ofbizCurrency amount= originalOrderTaxAmount rounding=globalContext.currencyRounding isoCode=currencyUomId/>
				        <#assign orderGrandTotal = orderGrandTotal + originalOrderTaxAmount/>
				    </div>
				</div>
			</div>
				        
			<div class="infoRow row">
			    <div class="infoEntry">
				    <div class="infoCaption">
				        <label>${uiLabelMap.AdjustmentsCaption}</label>
				    </div>
				    <div class="infoValue">
				        <@ofbizCurrency amount= originalOrderOtherAmount rounding=globalContext.currencyRounding isoCode=currencyUomId/>
				        <#assign orderGrandTotal = orderGrandTotal + originalOrderOtherAmount/>
				    </div>
				</div>
			</div>

			<div class="infoRow row">
			    <div class="infoEntry">
				    <div class="infoCaption">
					    <label>${uiLabelMap.TotalChargeCaption}</label>
					</div>
				    <div class="infoValue">
					    <@ofbizCurrency amount=orderGrandTotal?default(0.00) rounding=globalContext.currencyRounding isoCode=currencyUomId/>
					</div>
				</div>
			</div>

			<div class="infoRow row">
			    <div class="infoEntry">
			        <div class="infoValue">
				        <label>${uiLabelMap.TotalChargeRefundedCaption}</label>
				    </div>
				    <div class="infoValue">
				        <p><@ofbizCurrency amount = (0?number - totalChargeRefunded) rounding=globalContext.currencyRounding isoCode=currencyUomId/></p>
				    </div>
				</div>
			</div>
        </div>
        <#if statusId == "PRODUCT_RETURN">
            <div class="boxBody">
	            <div class="heading">
	                ${uiLabelMap.PriorAdjustmentsHeading!}
	            </div>
	            <div class="infoRow row">
				    <div class="infoEntry">
				        <div class="infoCaption">
					        <label>${uiLabelMap.PriorItemAdjustmentCaption}</label>
					    </div>
					    <div class="infoValue">
					        <p><@ofbizCurrency amount = (0?number - priorItemAdjustmentTotal) rounding=globalContext.currencyRounding isoCode=currencyUomId/></p>
					        <#assign totalPriorAdjustmnets = totalPriorAdjustmnets + priorItemAdjustmentTotal/>
					    </div>
					</div>
				</div>
	            
	            <div class="infoRow row">
			        <div class="infoEntry">
				        <div class="infoValue">
					        <label>${uiLabelMap.PromoAdjustCaption}</label>
					    </div>
					    <div class="infoValue">
					        <p><@ofbizCurrency amount = (0?number - priorPromoAdjustmentTotal) rounding=globalContext.currencyRounding isoCode=currencyUomId/></p>
					        <#assign totalPriorAdjustmnets = totalPriorAdjustmnets + priorPromoAdjustmentTotal/>
					    </div>
			        </div>
			    </div>
	            
	            <#assign orderGrandTotal = orderGrandTotal + originalOrderPromoAmount/>       
	                        
	            <div class="infoRow row">
				    <div class="infoEntry">
				        <div class="infoValue">
					        <label>${uiLabelMap.ShippingAdjustCaption}</label>
					    </div>
					    <div class="infoValue">
					        <p><@ofbizCurrency amount = (0?number - priorShippingAdjustmentTotal) rounding=globalContext.currencyRounding isoCode=currencyUomId/></p>
					        <#assign totalPriorAdjustmnets = totalPriorAdjustmnets + priorShippingAdjustmentTotal/>
					    </div>
					</div>
				</div>
				
				<div class="infoRow row">
				    <div class="infoEntry">
				        <div class="infoValue">
					        <label>${uiLabelMap.TaxAdjustCaption}</label>
					    </div>
					    <div class="infoValue">
					        <p><@ofbizCurrency amount = (0?number - priorTaxAdjustmentTotal) rounding=globalContext.currencyRounding isoCode=currencyUomId/></p>
					        <#assign totalPriorAdjustmnets = totalPriorAdjustmnets + priorTaxAdjustmentTotal/>
					    </div>
					</div>
				</div>
				
				<div class="infoRow row">
				    <div class="infoEntry">
				        <div class="infoValue">
					        <label>${uiLabelMap.MiscAdjustCaption}</label>
					    </div>
					    <div class="infoValue">
					        <p><@ofbizCurrency amount = (0?number - priorMiscAdjustmentTotal) rounding=globalContext.currencyRounding isoCode=currencyUomId/></p>
					        <#assign totalPriorAdjustmnets = totalPriorAdjustmnets + priorMiscAdjustmentTotal/>
					    </div>
					</div>
				</div>
					        
				<div class="infoRow row">
				    <div class="infoEntry">
				        <div class="infoValue">
					        <label>${uiLabelMap.AdjustedChargeCaption}</label>
					    </div>
					    <div class="infoValue">
					        <p><@ofbizCurrency amount = (0?number - totalPriorAdjustmnets) rounding=globalContext.currencyRounding isoCode=currencyUomId/></p>
					    </div>
					</div>
				</div>
	        </div>
        </#if>
    </div>