<div class ="${request.getAttribute("attributeClass")!} orderConfirmEmailAlert">
  <#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
  <input type="hidden" name="orderId" maxlength="255" value="${parameters.orderId}"/>
  <#assign CHECKOUT_EMAIL_ALERT = Static["com.osafe.util.Util"].getProductStoreParm(request,"CHECKOUT_EMAIL_ALERT")!""/>
  <#if CHECKOUT_EMAIL_ALERT?has_content && Static["org.ofbiz.base.util.UtilValidate"].isInteger(CHECKOUT_EMAIL_ALERT)>
    <#assign sendEmailToCount = Static["java.lang.Integer"].valueOf(CHECKOUT_EMAIL_ALERT)>
      <#if sendEmailToCount?has_content && sendEmailToCount gt 0 && sendEmailToCount lt 11>
          <span>${uiLabelMap.OrderAdditionalEmailInfo}</span>
          <#list 1..sendEmailToCount as index>
            <div class="entry">
                <label for="orderAdditionalEmail">${uiLabelMap.EmailIdLabel} #${index}:</label>
                <#assign orderAdditionalEmailId = parameters.get("orderAdditionalEmail_${index}")!''/>
                   <div class="entryField">
		                <input type="text" class="large" name="orderAdditionalEmail_${index}" id="orderAdditionalEmail_${index}" maxlength="255" value="${orderAdditionalEmailId!""}"/>
		                <@fieldErrors fieldName="orderAdditionalEmail_${index}"/>
		           </div>
            </div>
          </#list>
          <div class="orderAdditionalEmailButtons"> 
            <a href="javascript:document.orderCompleteForm.action='<@ofbizUrl>eCommerceOrderAdditionalEmail</@ofbizUrl>';document.orderCompleteForm.submit()" class="standardBtn action">${uiLabelMap.SendEmailBtn}</a>
          </div>
      </#if>
  </#if>
</div>
