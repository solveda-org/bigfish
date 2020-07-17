  <#if detailScreenName?exists && detailScreenName?has_content>
      <input type="hidden" name="detailScreen" value="${parameters.detailScreen?default(detailScreen!"")}" />
       <div class="infoRow">
         <div class="infoEntry">
            <div class="infoCaption">
                <label>${uiLabelMap.EmailTestModeCaption}</label>
            </div>
            <div class="entry checkbox medium">
              <input class="checkBoxEntry" type="radio" id="simpleTest" name="simpleTest" value="Y" <#if (!parameters.simpleTest?exists || (parameters.simpleTest?exists && parameters.simpleTest?string == "Y"))>checked="checked"</#if> onclick="getEmailTestFormat('Y')"/>${uiLabelMap.SimpleTestLabel}
              <input class="checkBoxEntry" type="radio" id="simpleTest" name="simpleTest" value="N" <#if (parameters.simpleTest?exists && parameters.simpleTest?string == "N")>checked="checked"</#if> onclick="getEmailTestFormat('N')"/>${uiLabelMap.EmailTemplateLabel}
            </div>
        </div>
      </div>
      <div class="infoRow emailTemplateDdDiv">
        <div class="infoEntry">
            <div class="infoCaption">
                <label>${uiLabelMap.EmailTemplateCaption}</label>
            </div>
             <div class="infoValue">
                 <#assign selectedEmailTemplate = parameters.emailTemplateId!"">
                 <select id="emailTemplateId" name="emailTemplateId" class="small">
                     <!--<option value="E_ABANDON_CART" <#if selectedEmailTemplate?has_content && selectedEmailTemplate.equals("E_ABANDON_CART")>selected</#if>>E_ABANDON_CART</option>-->
                     <option value="E_CHANGE_CUSTOMER" <#if selectedEmailTemplate?has_content && selectedEmailTemplate.equals("E_CHANGE_CUSTOMER")>selected</#if>>E_CHANGE_CUSTOMER</option>
                     <option value="E_CONTACT_US" <#if selectedEmailTemplate?has_content && selectedEmailTemplate.equals("E_CONTACT_US")>selected</#if>>E_CONTACT_US</option>
                     <option value="E_FORGOT_PASSWORD" <#if selectedEmailTemplate?has_content && selectedEmailTemplate.equals("E_FORGOT_PASSWORD")>selected</#if>>E_FORGOT_PASSWORD</option>
                     <option value="E_MAILING_LIST" <#if selectedEmailTemplate?has_content && selectedEmailTemplate.equals("E_MAILING_LIST")>selected</#if>>E_MAILING_LIST</option>
                     <option value="E_NEW_CUSTOMER" <#if selectedEmailTemplate?has_content && selectedEmailTemplate.equals("E_NEW_CUSTOMER")>selected</#if>>E_NEW_CUSTOMER</option>
                     <option value="E_ORDER_CHANGE" <#if selectedEmailTemplate?has_content && selectedEmailTemplate.equals("E_ORDER_CHANGE")>selected</#if>>E_ORDER_CHANGE</option>
                     <option value="E_ORDER_CONFIRM" <#if selectedEmailTemplate?has_content && selectedEmailTemplate.equals("E_ORDER_CONFIRM")>selected</#if>>E_ORDER_CONFIRM</option>
                     <option value="E_ORDER_DETAIL" <#if selectedEmailTemplate?has_content && selectedEmailTemplate.equals("E_ORDER_DETAIL")>selected</#if>>E_ORDER_DETAIL</option>
                     <option value="E_REQUEST_CATALOG" <#if selectedEmailTemplate?has_content && selectedEmailTemplate.equals("E_REQUEST_CATALOG")>selected</#if>>E_REQUEST_CATALOG</option>
                     <!--<option value="E_SCHED_JOB_ALERT" <#if selectedEmailTemplate?has_content && selectedEmailTemplate.equals("E_SCHED_JOB_ALERT")>selected</#if>>E_SCHED_JOB_ALERT</option>-->
                     <option value="E_SHIP_REVIEW" <#if selectedEmailTemplate?has_content && selectedEmailTemplate.equals("E_SHIP_REVIEW")>selected</#if>>E_SHIP_REVIEW</option>
                 </select>
             </div>
        </div>
      </div>
      <div class="infoRow row customerIdDiv">
        <div class="infoEntry long">
          <div class="infoCaption">
            <label>${uiLabelMap.EmailCustomerCaption}</label>
          </div>
          <div class="infoValue">
              <input class="medium" name="customerId" type="text" id="customerId" maxlength="20" value="${parameters.customerId!""}"/>
          </div>
        </div>
      </div>
      <div class="infoRow row orderIdDiv">
        <div class="infoEntry long">
          <div class="infoCaption">
            <label>${uiLabelMap.EmailOrderCaption}</label>
          </div>
          <div class="infoValue">
              <input class="medium" name="orderId" type="text" id="orderId" maxlength="20" value="${parameters.orderId!""}"/>
          </div>
        </div>
      </div>
      <div class="infoRow">
        <div class="infoEntry">
        	<div class="infoCaption">
                <label>${uiLabelMap.FROM_EMAIL_ADDRESS_Caption}</label>
            </div>
             <div class="infoValue">
                 <input type="text" class="large" name="fromAddress" id="fromAddress" maxlength="255" value="${parameters.fromAddress!EMAIL_CLNT_REPLY_TO!""}"/>
             </div>
        </div>
      </div>
      <div class="infoRow">
        <div class="infoEntry">
        	<div class="infoCaption">
                <label>${uiLabelMap.TO_EMAIL_ADDRESS_Caption}</label>
            </div>
             <div class="infoValue">
                 <input type="text" class="large" name="toAddress" id="toAddress" maxlength="255" value="${parameters.toAddress!""}"/>
             </div>
        </div>
      </div>
      <div class="infoRow">
        <div class="infoEntry">
        	<div class="infoCaption">
                <label>${uiLabelMap.EMAIL_SUBJECT_Caption}</label>
            </div>
             <div class="infoValue">
                 <#assign emailSubject = "${EMAIL_CLNT_NAME!}: Email Test">
                 <input type="text" class="large" name="emailSubject" id="emailSubject" maxlength="255" value="${parameters.emailSubject!emailSubject!""}"/>
             </div>
        </div>
      </div>

      <div class="infoRow">
        <div class="infoEntry">
            <div class="infoCaption">
                <label>${uiLabelMap.TextCaption!""}</label>
            </div>
            <div class="infoValue">
                <textarea class="smallArea" name="testEmailText" cols="50" rows="5">${parameters.testEmailText!"This is a test email"}</textarea>
            </div>
        </div>
      </div>
  <#else>
      ${uiLabelMap.NoDataAvailableInfo}
  </#if>