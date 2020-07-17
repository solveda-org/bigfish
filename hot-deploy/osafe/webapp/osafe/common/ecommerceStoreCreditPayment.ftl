<#if partyStoreCreditBalance?has_content && (partyStoreCreditBalance > 0) >
 <#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
 <#assign shoppingCart = sessionAttributes.shoppingCart?if_exists />
 <#if (shoppingCart.size() > 0)>
   <#assign remainingPayment = shoppingCart.getGrandTotal().subtract(shoppingCart.getPaymentTotal())! />
   <#assign CURRENCY_UOM_DEFAULT = Static["com.osafe.util.Util"].getProductStoreParm(request,"CURRENCY_UOM_DEFAULT")!""/>
   <#assign currencyUom = CURRENCY_UOM_DEFAULT!shoppingCart.getCurrency() />
   <#assign storeCreditAmount><@ofbizCurrency amount=partyStoreCreditBalance isoCode=currencyUom! rounding=globalContext.currencyRounding /></#assign>
   <#assign StoreCreditInfo = Static["org.ofbiz.base.util.UtilProperties"].getMessage("OSafeUiLabels", "StoreCreditInfo", {"amount", storeCreditAmount}, locale)!""/>
   <#if partyAppliedStoreCreditTotal?has_content && (partyAppliedStoreCreditTotal > 0) >
       <#assign appliedStoreCreditAmount = partyAppliedStoreCreditTotal />
   <#else>
       <#assign appliedStoreCreditAmount = "" />
   </#if>
   <div class="${request.getAttribute("attributeClass")!}">
    <div class="displayBox">
        <h3>${uiLabelMap.StoreCreditHeading}</h3>
        <ul class="displayActionList">
          <@fieldErrors fieldName="storeCreditAmount"/>
          <li>
           <div>
            <label>${StoreCreditInfo!}</label>
           </div>
          </li>
          <li>
           <div>
            <label>${uiLabelMap.UseStoreCreditLabel}</label>
            <input type="checkbox" id="js_useStoreCredit" name="useStoreCredit" value="Y"  <#if (parameters.useStoreCredit?has_content && parameters.useStoreCredit == "Y") || (appliedStoreCreditAmount?has_content)>checked</#if>/>
           </div>
           <div>
            <label>${uiLabelMap.AmountRedeemedLabel}</label>
            <input type="text" id="js_storeCreditAmount" name="storeCreditAmount" value="${appliedStoreCreditAmount!parameters.storeCreditAmount!}" autocomplete="off" <#if (appliedStoreCreditAmount?has_content)>readOnly="true"</#if>/>
           </div>
          </li>
        </ul>
    </div>
   </div>
 </#if>
</#if>

