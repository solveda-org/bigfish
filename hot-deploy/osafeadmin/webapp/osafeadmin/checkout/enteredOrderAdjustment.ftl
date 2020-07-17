<table class="osafe">
  <thead>
    <tr class="heading">
      <th class="idCol firstCol">${uiLabelMap.AdjustmentTypeLabel}</th>
      <th class="actionCol"></th>
      <th class="descCol">${uiLabelMap.DescriptionLabel}</th>
      <th class="currencyCol">${uiLabelMap.AmountLabel}</th>
      <th class="actionCol"></th>
    </tr>
  </thead>
  <#assign shoppingCart = sessionAttributes.shoppingCart?if_exists />
  <#if (shoppingCart.size() > 0)>
      <tbody>
      <#assign orderAdjustmentList = sessionAttributes.orderAdjustmentList!/>
      <#list orderAdjustmentList as orderAdjustmentInfoMap>
        <#-- get OrderAdjustment info if there is one set in session -->
        <#assign orderAdjustmentId = orderAdjustmentInfoMap.ADJUST_ID!""/>
	    <#assign orderAdjustmentTypeId = orderAdjustmentInfoMap.ADJUST_TYPE!""/>
	    <#assign orderAdjustment_description = orderAdjustmentInfoMap.ADJUST_DESCRIPTION!""/>
	    <#assign orderAdjustment_amount = orderAdjustmentInfoMap.ADJUST_AMOUNT!""/>
	    <#assign orderAdjustment_includeInTax = ""/>
	    <#assign orderAdjustment_includeInShipping = ""/>
	    <#-- Get currency -->
	    <#assign CURRENCY_UOM_DEFAULT = Static["com.osafe.util.Util"].getProductStoreParm(request,"CURRENCY_UOM_DEFAULT")!""/>  
	    <#assign currencyUom = shoppingCart.getCurrency()!""/>
	    <#if !currencyUom?has_content>
	      <#assign currencyUom = CURRENCY_UOM_DEFAULT!""/>
	    </#if>
        <tr>
          <td class="idCol firstCol">${orderAdjustmentTypeId!}</td>
          <#assign adjustmentInfoMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("includeInTax", orderAdjustment_includeInTax)>
          <#assign adjustmentInfoMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("includeInShipping", orderAdjustment_includeInShipping)>
          <#assign adjustmentInfo = "<p>"+Static["org.ofbiz.base.util.UtilProperties"].getMessage("OSafeAdminUiLabels","AdjustmentIncludeInTaxInfo",adjustmentInfoMap, locale )+"</p>">
    	  <#assign adjustmentInfo = adjustmentInfo + "<p>"+Static["org.ofbiz.base.util.UtilProperties"].getMessage("OSafeAdminUiLabels","AdjustmentIncludeInShippingInfo",adjustmentInfoMap, locale )+"</p>">
          <td class="actionCol">
        	<p onMouseover="showTooltip(event,'${adjustmentInfo!""}');" onMouseout="hideTooltip()"><span class="informationIcon"></span></p>
          </td> 
          <td class="descCol">
            ${orderAdjustment_description!}
          </td>
          <td class="currencyCol">
              <@ofbizCurrency amount=orderAdjustment_amount!0 isoCode=currencyUom rounding=globalContext.currencyRounding/>
          </td>
          <td class="actionCol">
            <a href="javascript:removeOrderAdjustment('${orderAdjustmentId!""}');"><span class="crossIcon"></span></a>
          </td>
        </tr>
      </#list>
    </tbody>
</#if>
</table>

