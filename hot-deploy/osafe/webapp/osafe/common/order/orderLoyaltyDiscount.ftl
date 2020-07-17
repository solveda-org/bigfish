<#if orderAdjustmentsLoyalty?has_content>  
 <#list orderAdjustmentsLoyalty as orderAdjustment>
      <#assign adjustmentType = orderAdjustment.getRelatedOneCache("OrderAdjustmentType")>
      <#assign adjustmentTypeDesc = adjustmentType.get("description",locale)!"">
       <li class="${request.getAttribute("attributeClass")!}">
       <div>
        <label>${adjustmentTypeDesc!}</label>
        <span><@ofbizCurrency amount=orderAdjustment.amount rounding=globalContext.currencyRounding isoCode=currencyUom/></span>
       </div>
      </li>
  </#list>
</#if>
