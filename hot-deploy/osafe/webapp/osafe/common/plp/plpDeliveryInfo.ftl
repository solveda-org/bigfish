<#if PLP_DELIVERY_INFO?exists &&  PLP_DELIVERY_INFO?has_content>
  <div class="plpDeliveryInfo">
     <label>${uiLabelMap.PLPDeliveryInfoLabel}</label>
     <span><@renderContentAsText contentId="${PLP_DELIVERY_INFO}" ignoreTemplate="true"/></span>
  </div>
</#if>
