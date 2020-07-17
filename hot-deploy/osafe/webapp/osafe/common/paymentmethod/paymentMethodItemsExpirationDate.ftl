<li class="${request.getAttribute("attributeClass")!}">
  <div>
    <label>${uiLabelMap.ExpiresOnLabel}</label>
    <#assign cardExpireDate=savedCreditCard.expireDate?if_exists/>
    <#assign showExpired="true"/> 
    <#if (cardExpireDate?has_content) && (Static["org.ofbiz.base.util.UtilValidate"].isDateAfterToday(cardExpireDate))>
        <#assign showExpired="false"/>
    </#if>
    <span>${cardExpireDate!""}<#if showExpired == "true"><label>${uiLabelMap.ExpiredLabel}</label></#if></span>
  </div>
</li>