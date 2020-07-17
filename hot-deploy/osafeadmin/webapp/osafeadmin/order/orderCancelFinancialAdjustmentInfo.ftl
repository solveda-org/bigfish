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
			                <@ofbizCurrency amount= adjustmentAmountTotalPromo rounding=globalContext.currencyRounding isoCode=currencyUomId/>
   	                        <#assign totalAdjustmentAmount = totalAdjustmentAmount - adjustmentAmountTotalPromo>
			            </div>
			        </div>
			    </div>
	            
	            
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
	                        <input type="hidden" name="totalRefundAmount" value="${totalAdjustmentAmount!}" />
		                </div>
		            </div>
		        </div>
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
		                    <input type="text" name="orderAdjustmentDescription" id="orderAdjustmentDescription" maxlength="255" value="${orderAdjustmentDescription!""}"/>
		                </div>
		            </div>
		            <div class="infoEntry">
		                <div class="infoCaption">
		                    <label>${uiLabelMap.AmountCaption!}</label>
		                </div>
		                <div class="infoValue">
		                    <input type="hidden" name="orderAdjustmentTypeId" id="orderAdjustmentTypeId" value="FEE"/>
		                    <input type="text" name="orderAdjustmentAmount" id="orderAdjustmentAmount" maxlength="20" value="${parameters.orderAdjustmentAmount!""}"/>
		                </div>
		            </div>
	            </div>
	        </div>
	    </div>
    </#if>