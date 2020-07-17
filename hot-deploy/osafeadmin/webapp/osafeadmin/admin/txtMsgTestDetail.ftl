  <#if detailScreenName?exists && detailScreenName?has_content>
      <input type="hidden" name="detailScreen" value="${parameters.detailScreen?default(detailScreen!"")}" />
       <div class="infoRow">
         <div class="infoEntry">
            <div class="infoCaption">
                <label>${uiLabelMap.TestModeCaption}</label>
            </div>
            <div class="entry checkbox medium">
              <input class="checkBoxEntry" type="radio" id="simpleTest" name="simpleTest" value="Y" <#if (!parameters.simpleTest?exists || (parameters.simpleTest?exists && parameters.simpleTest?string == "Y"))>checked="checked"</#if> onclick="getTestTemplateFormat('Y', 'N')"/>${uiLabelMap.SimpleTestLabel}
              <input class="checkBoxEntry" type="radio" id="simpleTest" name="simpleTest" value="N" <#if (parameters.simpleTest?exists && parameters.simpleTest?string == "N")>checked="checked"</#if> onclick="getTestTemplateFormat('N', 'N')"/>${uiLabelMap.TextMessageTemplateLabel}
            </div>
        </div>
      </div>
      <div class="infoRow templateDdDiv">
        <div class="infoEntry">
            <div class="infoCaption">
                <label>${uiLabelMap.TxtMsgTemplateCaption}</label>
            </div>
             <div class="infoValue">
                 <#assign selectedTemplate = parameters.templateId!"">
                 <#assign templateList = delegator.findByAnd("XContentXref",Static["org.ofbiz.base.util.UtilMisc"].toMap("productStoreId", globalContext.productStoreId,"contentTypeId",contentTypeId),Static["org.ofbiz.base.util.UtilMisc"].toList("contentId"))/>
                 <select id="templateId" name="templateId" class="small">
	                 <#if templateList?has_content>
	                   <#list templateList as template>
	                    <#assign contentId= template.contentId!""/>
	                    <#assign bfContentId= template.bfContentId!""/>
	                     <option value="${bfContentId}" <#if selectedTemplate?has_content && selectedTemplate.equals(bfContentId!"")>selected</#if>>${bfContentId!""}</option>
	                   </#list>
	                 </#if>
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
                <label>${uiLabelMap.TO_CELL_Caption}</label>
            </div>
             <div class="infoValue">
                 <input type="text" class="medium" name="toCell" id="toCell" maxlength="13" value="${parameters.toCell!""}"/>
             </div>
        </div>
      </div>
      <div class="infoRow">
        <div class="infoEntry textDiv">
            <div class="infoCaption">
                <label>${uiLabelMap.TextCaption!""}</label>
            </div>
            <div class="infoValue">
                <textarea class="smallArea" name="textMessage" cols="50" rows="5">${parameters.textMessage!"This is a test message"}</textarea>
            </div>
        </div>
      </div>
  <#else>
      ${screens.render("component://osafeadmin/widget/CommonScreens.xml#ListNoDataResult")}
  </#if>