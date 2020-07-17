<#if goodIdentificationTypesList?exists && goodIdentificationTypesList?has_content>
  <#assign rowNo = 1/>
  <#list goodIdentificationTypesList as goodIdentificationTypeId>
    <#assign goodIdentificationType = delegator.findOne("GoodIdentificationType", {"goodIdentificationTypeId" : goodIdentificationTypeId}, false)!""/>
    <#if goodIdentificationType?exists && goodIdentificationType?has_content>
        <div class="infoRow row">
          <div class="infoEntry long">
            <div class="infoCaption">
              <label>${goodIdentificationType.get("description","OSafeAdminUiLabels",locale)}:</label>
            </div>
            <div class="infoValue">
              <#if currentProduct?has_content>
                  <#assign goodIdentification = delegator.findOne("GoodIdentification", Static["org.ofbiz.base.util.UtilMisc"].toMap("goodIdentificationTypeId" , goodIdentificationTypeId, "productId" , currentProduct.productId!),false)?if_exists/>
              </#if>
              <#if goodIdentification?exists>
                <#assign idValue = goodIdentification.idValue!""/>
              </#if>
              <#assign idValueParm = request.getParameter("idValue_${rowNo}")?if_exists/>
              <input type="hidden" name="goodIdentificationTypeId_${rowNo}" id="goodIdentificationTypeId_${rowNo}" value="${goodIdentificationTypeId!}"/>
              <#if (mode?has_content && mode == "add")>
                <input type="text" name="idValue_${rowNo}" id="idValue_${rowNo}" class="large" value="${idValueParm!""}" />
              <#elseif mode?has_content && mode == "edit">
                <#if idValueParm?has_content>
                  <input type="text" name="idValue_${rowNo}" id="idValue_${rowNo}" class="large" value="${idValueParm!""}" />
                <#elseif idValue?has_content>
                  <input type="text" name="idValue_${rowNo}" id="idValue_${rowNo}" class="large" value="${idValue!""}" />
                <#else>
                  <input type="text" name="idValue_${rowNo}" id="idValue_${rowNo}" class="large" value="" />
                </#if>
              </#if>
            </div>
            <#if (variantProduct?has_content && mode=="edit") || (virtualProduct?has_content && mode=="add")>
              <#if virtualProduct?has_content>
                  <#assign virtualGoodIdentification = delegator.findOne("GoodIdentification", Static["org.ofbiz.base.util.UtilMisc"].toMap("goodIdentificationTypeId" , goodIdentificationTypeId, "productId" , virtualProduct.productId!),false)?if_exists/>
              </#if>
              <#if virtualGoodIdentification?exists>
                <#assign virtualIdValue = virtualGoodIdentification.idValue!""/>
              </#if>
              <div class="infoIcon">
                <#if virtualIdValue?has_content>
                  <#assign tooltipData = Static["org.ofbiz.base.util.UtilProperties"].getMessage("OSafeAdminUiLabels", "VirtualIdentificationInfo", Static["org.ofbiz.base.util.UtilMisc"].toMap("idType", "${goodIdentificationType.get('description','OSafeAdminUiLabels',locale)}", "idValue","${virtualIdValue}"), locale)/>
                <#else>
                  <#assign tooltipData = Static["org.ofbiz.base.util.UtilProperties"].getMessage("OSafeAdminUiLabels", "VirtualIdentificationBlankInfo",Static["org.ofbiz.base.util.UtilMisc"].toMap("idType", "${goodIdentificationType.get('description','OSafeAdminUiLabels',locale)}"), locale)/>
                </#if>
                <a class="helper" href="javascript:void(0);" onMouseover="showTooltip(event,'${tooltipData!""}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
              </div>
            </#if>
          </div>
        </div>
        <#assign rowNo = rowNo+1/>
    </#if>
  </#list>
</#if>

<#if currentProduct?has_content>
    <#assign bfProductAllAttributes = currentProduct.getRelated("ProductAttribute") />
</#if>
<#if (mode?has_content && mode == "add") && (!virtualProduct?has_content)>
    <div class="infoRow row multiSelectVaraint">
      <div class="infoEntry long">
        <div class="infoCaption">
          <label>${uiLabelMap.MultiSelectVariantCaption}</label>
        </div>
        <div class="infoValue">
            <select id="multiSelectVariant" name="multiSelectVariant">
                <option value="NONE" <#if (parameters.multiSelectVariant!"") == "NONE">selected</#if>>${uiLabelMap.NoneLabel}</option>
                <option value="QTY" <#if (parameters.multiSelectVariant!"") == "QTY">selected</#if>>${uiLabelMap.QtyLabel}</option>
                <option value="CHECKBOX" <#if (parameters.multiSelectVariant!"") == "CHECKBOX">selected</#if>>${uiLabelMap.CheckBoxLabel}</option>
            </select>
        </div>
        <div class="infoIcon">
            <#assign tooltipData = uiLabelMap.MultiSelectVariantInfo!/>
            <a class="helper" href="javascript:void(0);" onMouseover="showTooltip(event,'${tooltipData!""}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
        </div>
      </div>
    </div>
   
<#elseif mode?has_content && mode == "edit" && isVirtual=='Y'>
        <#if bfProductAllAttributes?has_content>
            <#assign bfMultiSelectVariantsProductAttributes = Static["org.ofbiz.entity.util.EntityUtil"].filterByAnd(bfProductAllAttributes,Static["org.ofbiz.base.util.UtilMisc"].toMap('attrName','PDP_SELECT_MULTI_VARIANT'))/> 
        </#if>
        <#if bfMultiSelectVariantsProductAttributes?has_content>
            <#assign bfMultiSelectVariantsProductAttribute = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(bfMultiSelectVariantsProductAttributes) />
            <#assign multiSelectVariant = bfMultiSelectVariantsProductAttribute.attrValue!>
        </#if>
        <#assign multiSelectVariant = parameters.multiSelectVariant!multiSelectVariant!"" />
         <div class="infoRow row multiSelectVaraint">
          <div class="infoEntry long">
            <div class="infoCaption">
              <label>${uiLabelMap.MultiSelectVariantCaption}</label>
            </div>
            <div class="infoValue">
                <select id="multiSelectVariant" name="multiSelectVariant">
                    <option value="NONE" <#if (multiSelectVariant) == "NONE">selected</#if>>${uiLabelMap.NoneLabel}</option>
                    <option value="QTY" <#if (multiSelectVariant) == "QTY">selected</#if>>${uiLabelMap.QtyLabel}</option>
                    <option value="CHECKBOX" <#if multiSelectVariant == "CHECKBOX">selected</#if>>${uiLabelMap.CheckBoxLabel}</option>
                </select>
            </div>
            <div class="infoIcon">
                <#assign tooltipData = uiLabelMap.MultiSelectVariantInfo! />
                <a class="helper" href="javascript:void(0);" onMouseover="showTooltip(event,'${tooltipData!""}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
            </div>
          </div>
    </div>
</#if>

<#if (mode?has_content) && !(mode == "edit" && isVirtual=='Y' && isVariant=='N')>
    <#if bfProductAllAttributes?has_content>
        <#assign productGiftMessageAttributes = Static["org.ofbiz.entity.util.EntityUtil"].filterByAnd(bfProductAllAttributes,Static["org.ofbiz.base.util.UtilMisc"].toMap('attrName','CHECKOUT_GIFT_MESSAGE'))/> 
    </#if>
    <#if productGiftMessageAttributes?has_content>
        <#assign productGiftMessageAttribute = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(productGiftMessageAttributes) />
        <#assign checkoutGiftMessage = productGiftMessageAttribute.attrValue!>
    </#if>
    <#assign checkoutGiftMessage = parameters.checkoutGiftMessage!checkoutGiftMessage!"" />
    <div class="infoRow row buyableProductAttribute">
        <div class="infoEntry">
            <div class="infoCaption">
                <label>${uiLabelMap.GiftMessageCaption}</label>
            </div>
            <div class="entry checkbox short">
                <#if (mode?has_content)>
                    <input class="checkBoxEntry" type="radio" name="checkoutGiftMessage" value="TRUE" <#if checkoutGiftMessage=="TRUE">checked="checked"</#if>/>${uiLabelMap.YesLabel}
                    <input class="checkBoxEntry" type="radio" name="checkoutGiftMessage" value="FALSE" <#if checkoutGiftMessage=="FALSE">checked="checked"</#if>/>${uiLabelMap.NoLabel}
                </#if>
            </div>
            <div class="infoIcon">
                <#assign tooltipData = Static["org.ofbiz.base.util.UtilProperties"].getMessage("OSafeAdminUiLabels", "ProductCheckoutGiftMessageInfo", [CHECKOUT_GIFT_MESSAGE!], locale)/>
                <a class="helper" href="javascript:void(0);" onMouseover="showTooltip(event,'${tooltipData!""}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
            </div>
        </div>
    </div>
    
	<#if bfProductAllAttributes?has_content>
        <#assign productPdpQuantityMinAttributes = Static["org.ofbiz.entity.util.EntityUtil"].filterByAnd(bfProductAllAttributes,Static["org.ofbiz.base.util.UtilMisc"].toMap('attrName','PDP_QTY_MIN'))/> 
        <#if productPdpQuantityMinAttributes?has_content>
	        <#assign productPdpQuantityMinAttribute = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(productPdpQuantityMinAttributes) />
	        <#assign pdpQuantityMin = productPdpQuantityMinAttribute.attrValue!>
    	</#if>
    </#if>
    <#assign pdpQuantityMin = parameters.pdpQuantityMin!pdpQuantityMin!"" />
    <#assign PDP_QTY_MIN = Static["com.osafe.util.OsafeAdminUtil"].getProductStoreParm(request,"PDP_QTY_MIN")!"1"/>  
    <div class="infoRow row buyableProductAttribute">
       <div class="infoEntry">
           <div class="infoCaption">
               <label>${uiLabelMap.pdpQuantityMinCaption}</label>
           </div>
           <div class="infoValue">
            <input type="text" name="pdpQuantityMin" class="textEntry" id="pdpQuantityMin" value="<#if parameters.pdpQuantityMin?has_content || pdpQuantityMin?has_content>${parameters.pdpQuantityMin!pdpQuantityMin!}</#if>"/>
          </div>
           <div class="infoIcon">
               <#assign tooltipData = Static["org.ofbiz.base.util.UtilProperties"].getMessage("OSafeAdminUiLabels", "PdpQtyMinTooltip", [PDP_QTY_MIN!], locale)/>
               <a class="helper" href="javascript:void(0);" onMouseover="showTooltip(event,'${tooltipData!""}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
           </div>
       </div>
    </div>
    
    <#if bfProductAllAttributes?has_content>
        <#assign productPdpQuantityMaxAttributes = Static["org.ofbiz.entity.util.EntityUtil"].filterByAnd(bfProductAllAttributes,Static["org.ofbiz.base.util.UtilMisc"].toMap('attrName','PDP_QTY_MAX'))/> 
        <#if productPdpQuantityMaxAttributes?has_content>
	        <#assign productPdpQuantityMaxAttribute = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(productPdpQuantityMaxAttributes) />
	        <#assign pdpQuantityMax = productPdpQuantityMaxAttribute.attrValue!>
    	</#if>
    </#if>
    <#assign pdpQuantityMax = parameters.pdpQuantityMax!pdpQuantityMax!"" />
    <#assign PDP_QTY_MAX = Static["com.osafe.util.OsafeAdminUtil"].getProductStoreParm(request,"PDP_QTY_MAX")!"99"/>  
    <div class="infoRow row buyableProductAttribute">
       <div class="infoEntry">
           <div class="infoCaption">
               <label>${uiLabelMap.pdpQuantityMaxCaption}</label>
           </div>
           <div class="infoValue">
            <input type="text" name="pdpQuantityMax" class="textEntry" id="pdpQuantityMax" value="<#if parameters.pdpQuantityMax?has_content || pdpQuantityMax?has_content>${parameters.pdpQuantityMax!pdpQuantityMax!}</#if>"/>
          </div>
           <div class="infoIcon">
               <#assign tooltipData = Static["org.ofbiz.base.util.UtilProperties"].getMessage("OSafeAdminUiLabels", "PdpQtyMaxTooltip", [PDP_QTY_MAX!], locale)/>
               <a class="helper" href="javascript:void(0);" onMouseover="showTooltip(event,'${tooltipData!""}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
           </div>
       </div>
    </div>
    
    <#if bfProductAllAttributes?has_content>
        <#assign productPdpQuantityDefaultAttributes = Static["org.ofbiz.entity.util.EntityUtil"].filterByAnd(bfProductAllAttributes,Static["org.ofbiz.base.util.UtilMisc"].toMap('attrName','PDP_QTY_DEFAULT'))/> 
        <#if productPdpQuantityDefaultAttributes?has_content>
	        <#assign productPdpQuantityDefaultAttribute = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(productPdpQuantityDefaultAttributes) />
	        <#assign pdpQuantityDefault = productPdpQuantityDefaultAttribute.attrValue!>
    	</#if>
    </#if>
    <#assign pdpQuantityDefault = parameters.pdpQuantityDefault!pdpQuantityDefault!"" />
    <#assign PDP_QTY_DEFAULT = Static["com.osafe.util.OsafeAdminUtil"].getProductStoreParm(request,"PDP_QTY_DEFAULT")!"1"/> 
    <#if Static["com.osafe.util.OsafeAdminUtil"].isNumber(PDP_QTY_DEFAULT) >
        <#assign PDP_QTY_DEFAULT = PDP_QTY_DEFAULT!"" />
    <#else>  
        <#assign PDP_QTY_DEFAULT = 1 />
    </#if> 
    <div class="infoRow row buyableProductAttribute">
        <div class="infoEntry">
            <div class="infoCaption">
                <label>${uiLabelMap.pdpQuantityDefaultCaption}</label>
            </div>
            <div class="infoValue">
                <input type="text" name="pdpQuantityDefault" class="textEntry" id="pdpQuantityDefault" value="<#if parameters.pdpQuantityDefault?has_content || pdpQuantityDefault?has_content>${parameters.pdpQuantityDefault!pdpQuantityDefault!}</#if>"/>
            </div>
            <div class="infoIcon">
                <#assign tooltipData = Static["org.ofbiz.base.util.UtilProperties"].getMessage("OSafeAdminUiLabels", "PdpQtyDefaultTooltip", [PDP_QTY_DEFAULT!], locale)/>
                <a class="helper" href="javascript:void(0);" onMouseover="showTooltip(event,'${tooltipData!""}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
            </div>
       </div>
    </div>
</#if>