<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<div class="infoRow">
  <div class="infoEntry">
    <div class="infoCaption">
        <label>${uiLabelMap.AdjustmentTypeCaption}</label>
    </div>
    <div class="infoValue">
      <select name="orderAdjustmentTypeId" id="orderAdjustmentTypeId">
          <option value="">${uiLabelMap.SelectOneLabel}</option>
          <#if orderAdjustmentTypeList?exists && orderAdjustmentTypeList?has_content>
            <#list orderAdjustmentTypeList as orderAdjustmentType>
              <option value="${orderAdjustmentType.orderAdjustmentTypeId!}" <#if parameters.orderAdjustmentTypeId?exists && parameters.orderAdjustmentTypeId == "${orderAdjustmentType.orderAdjustmentTypeId!}">selected=selected</#if>>${orderAdjustmentType.orderAdjustmentTypeId!}</option>
            </#list>
          </#if>       
      </select>
    </div>
  </div>
</div>
<div class="infoRow">
  <div class="infoEntry">
    <div class="infoCaption"><label><@required/>${uiLabelMap.DescriptionCaption}</label></div>
    <div class="infoValue">
       <input type="text" id="orderAdjustment_description" name="orderAdjustment_description" value="${parameters.orderAdjustment_description!""}" maxlength="60" autocomplete="off"/>
    </div>
  </div>
</div>
<div class="infoRow">
  <div class="infoEntry">
    <div class="infoCaption"><label><@required/>${uiLabelMap.AmountCaption}</label></div>
    <div class="infoValue">
       <input type="text" id="orderAdjustment_amount" name="orderAdjustment_amount" value="${parameters.orderAdjustment_amount!""}" maxlength="60" autocomplete="off"/>
    </div>
    <div class="infoIcon">
        <a class="helper" href="javascript:void(0);" onMouseover="showTooltip(event,'${uiLabelMap.OrderAdjustmentAmountTooltip}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
    </div>
    <a href="javascript:addOrderAdjustment();"><span class="refreshIcon"></span></a>
  </div>
</div>
<div class="infoRow">
${screens.render("component://osafeadmin/widget/AdminCheckoutScreens.xml#enteredOrderAdjustment")}
</div>