<#if mode?has_content>
	<#if shipmentMethod?has_content>
    <#assign description = shipmentMethod.description!"" />
    <#assign sequenceNum = shipmentMethod.sequenceNum!"" />
  	</#if>

  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.ShippingMethodCaption}</label>
      </div>
      <div class="infoValue">
	      <#if mode="add">
	          <input name="shipmentMethodTypeId" type="text" id="shipmentMethodTypeId" maxlength="20" value="${parameters.shipmentMethodTypeId!shipmentMethodTypeId!""}"/>
	      <#else>
	      		${parameters.shipmentMethodTypeId!shipmentMethodTypeId!""}
	      		<input name="shipmentMethodTypeId" type="hidden" id="shipmentMethodTypeId" maxlength="20" value="${parameters.shipmentMethodTypeId!shipmentMethodTypeId!""}"/>
	      </#if>
      </div>
    </div>
  </div>
  
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.DescriptionCaption}</label>
      </div>
      <div class="infoValue">
          <input name="description" type="text" id="description" maxlength="60" value="${parameters.description!description!""}"/>
      </div>
    </div>
  </div>

  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.SequenceCaption}</label>
      </div>
      <div class="infoValue">
          <input name="sequenceNum" type="text" id="sequenceNum" maxlength="9" value="${parameters.sequenceNum!sequenceNum!""}"/>
      </div>
    </div>
  </div>

<#else>
     ${screens.render("component://osafeadmin/widget/CommonScreens.xml#ListNoDataResult")}
</#if>
