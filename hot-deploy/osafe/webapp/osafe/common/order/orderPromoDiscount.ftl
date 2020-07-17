<#if orderAdjustmentsPromotion?has_content>  
 <#list orderAdjustmentsPromotion as orderAdjustment>
      <#assign adjustmentType = orderAdjustment.getRelatedOneCache("OrderAdjustmentType")>
      <#assign productPromo = orderAdjustment.getRelatedOneCache("ProductPromo")!"">
      <#assign promoCodeText = ""/>
      <#if productPromo?has_content>
         <#assign promoText = productPromo.promoText?if_exists/>
         <#assign productPromoCode = productPromo.getRelatedCache("ProductPromoCode")>
         <#assign promoCodesEntered = localOrderReadHelper.getProductPromoCodesEntered()!""/>
         <#if promoCodesEntered?has_content>
            <#list promoCodesEntered as promoCodeEntered>
              <#if productPromoCode?has_content>
                <#list productPromoCode as promoCode>
                  <#assign promoCodeEnteredId = promoCodeEntered/>
                  <#assign promoCodeId = promoCode.productPromoCodeId!""/>
                  <#if promoCodeEnteredId?has_content>
                      <#if promoCodeId == promoCodeEnteredId>
                         <#assign promoCodeText = promoCode.productPromoCodeId?if_exists/>
                      </#if>
                  </#if>
                </#list>
              </#if>
             </#list>
         </#if>
      </#if>
       <li class="${request.getAttribute("attributeClass")!}">
       <div>
	        <label><#if promoText?has_content>${promoText}<#if promoCodeText?has_content> (${promoCodeText})</#if><#else>${adjustmentType.get("description",locale)?if_exists}</#if></label>
	        <span><@ofbizCurrency amount=orderAdjustment.amount rounding=globalContext.currencyRounding isoCode=currencyUom/></span>
       </div>
      </li>
  </#list>
</#if>
