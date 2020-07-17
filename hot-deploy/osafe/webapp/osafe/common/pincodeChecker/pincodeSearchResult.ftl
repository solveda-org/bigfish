<#if pincode?has_content && !errorMessageList?has_content>
	<#if deliveryAvailable?has_content && deliveryAvailable == 'Y'>
	    <div class="deliveryInfo">
	        <div class="successImage"></div>
	        <p class="successMessage">${uiLabelMap.DeliveryAvailableInfo!}</p>
	    </div>
	    <#if (codLimit?has_content) && (codLimit > 0)>
	        <div class="codInfo">
	            <div class="successImage"></div>
	            <#assign codLimit = Static["org.ofbiz.base.util.UtilFormatOut"].formatCurrency(codLimit?double, currencyUom, locale, currencyRounding)?if_exists>
	            <p class="successMessage">${Static["org.ofbiz.base.util.UtilProperties"].getMessage("OSafeUiLabels", "CODLimitAvailableInfo", Static["org.ofbiz.base.util.UtilMisc"].toMap("codLimit", codLimit), locale)!} </p>
	        </div>
	    <#else>
	        <div class="codInfo">
	            <div class="errorImage"></div>
	            <p class="errorMessage">${uiLabelMap.CODNotAvailableInfo!}</p>
	        </div>
	    </#if>
	</#if>
</#if>
