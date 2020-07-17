    <#if (cancelOrderItemSubtotal > 0)>
	    <div class="displayListBox detailInfo">  
	        <div class="boxBody">    
	            <div class="heading">
	                <h2>${uiLabelMap.FinancialAdjustmentsHeading!}</h2>
	            </div>
	            <#assign totalAdjustmentAmount = 0/>
	            <div class="infoRow row">
	                <div class="infoEntry">
	                    <div class="infoCaption">
	                        <label>${uiLabelMap.TotalItemAdjustCaption}</label>
	                    </div>
	                    <div class="infoValue">
	                        <@ofbizCurrency amount= (0?number - cancelOrderItemSubtotal) rounding=globalContext.currencyRounding isoCode=currencyUomId/>
	                        <#assign totalAdjustmentAmount = totalAdjustmentAmount + cancelOrderItemSubtotal>
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
			                <@ofbizCurrency amount= (0?number - adjustmentAmountTotalPromo) rounding=globalContext.currencyRounding isoCode=currencyUomId/>
			            </div>
			        </div>
			    </div>
	            <#assign totalAdjustmentAmount = totalAdjustmentAmount + adjustmentAmountTotalPromo>
	            
	            
	            <div class="infoRow row">
				    <div class="infoEntry">
					    <div class="infoCaption">
					        <label>${uiLabelMap.ShippingCaption}</label>
					    </div>
					    <div class="infoValue">
					        <@ofbizCurrency amount = adjustmentAmountTotalShipping rounding=globalContext.currencyRounding isoCode=currencyUomId/>
					        <#assign totalAdjustmentAmount = totalAdjustmentAmount - adjustmentAmountTotalShipping>
					    </div>
					</div>
				</div>
				
				<div class="infoRow row">
				    <div class="infoEntry">
					    <div class="infoCaption">
					        <label>${uiLabelMap.SalesTaxCaption}</label>
					    </div>
					    <div class="infoValue">
					        <@ofbizCurrency amount = adjustmentAmountTotalTax rounding=globalContext.currencyRounding isoCode=currencyUomId/>
					        <#assign totalAdjustmentAmount = totalAdjustmentAmount - adjustmentAmountTotalTax>
					    </div>
					</div>
				</div>
	                        
		        <div class="infoRow row">
		            <div class="infoEntry">
		                <div class="infoCaption">
		                    <label>${uiLabelMap.TotalAdjustmentCaption}</label>
		                </div>
		                <div class="infoValue">
		                    <@ofbizCurrency amount= (0?number - totalAdjustmentAmount) rounding=globalContext.currencyRounding isoCode=currencyUomId/>
		                </div>
		            </div>
		        </div>
	            <input type="hidden" name="totalRefundAmount" value="${parameters.totalRefundAmount!totalAdjustmentAmount!}" />
	        </div>
	    </div>
    </#if>