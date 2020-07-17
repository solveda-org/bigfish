<#if currentProduct?has_content>
    <#assign shortSalesPitch = ""/>
    <#if (SHORT_SALES_PITCH?exists && SHORT_SALES_PITCH?has_content)>
      <#assign content = SHORT_SALES_PITCH.getRelatedOne("Content")!""/>
      <#assign dataResource = content.getRelatedOne("DataResource")!""/>
      <#if dataResource?has_content>
          <#assign electronicText = dataResource.getRelatedOne("ElectronicText")!""/>
          <#assign shortSalesPitch = electronicText.textData!""/>
      </#if>
    </#if>
    <#assign longDescription = ""/>
    <#if (LONG_DESCRIPTION?exists && LONG_DESCRIPTION?has_content)>
      <#assign content = LONG_DESCRIPTION.getRelatedOne("Content")!""/>
      <#assign dataResource = content.getRelatedOne("DataResource")!""/>
      <#if dataResource?has_content>
          <#assign electronicText = dataResource.getRelatedOne("ElectronicText")!""/>
          <#assign longDescription = electronicText.textData!""/>
      </#if>
    </#if>
    <#assign specialInstruction = ""/>
    <#if (SPECIALINSTRUCTIONS?exists && SPECIALINSTRUCTIONS?has_content)>
      <#assign content = SPECIALINSTRUCTIONS.getRelatedOne("Content")!""/>
      <#assign dataResource = content.getRelatedOne("DataResource")!""/>
      <#if dataResource?has_content>
          <#assign electronicText = dataResource.getRelatedOne("ElectronicText")!""/>
          <#assign specialInstruction = electronicText.textData!""/>
      </#if>
    </#if>
    <#assign deliveryInfo = ""/>
    <#if (DELIVERY_INFO?exists && DELIVERY_INFO?has_content)>
      <#assign content = DELIVERY_INFO.getRelatedOne("Content")!""/>
      <#assign dataResource = content.getRelatedOne("DataResource")!""/>
      <#if dataResource?has_content>
          <#assign electronicText = dataResource.getRelatedOne("ElectronicText")!""/>
          <#assign deliveryInfo = electronicText.textData!""/>
      </#if>
    </#if>
    <#assign directions = ""/>
    <#if (DIRECTIONS?exists && DIRECTIONS?has_content)>
      <#assign content = DIRECTIONS.getRelatedOne("Content")!""/>
      <#assign dataResource = content.getRelatedOne("DataResource")!""/>
      <#if dataResource?has_content>
          <#assign electronicText = dataResource.getRelatedOne("ElectronicText")!""/>
          <#assign directions = electronicText.textData!""/>
      </#if>
    </#if>
    <#assign termsAndConds = ""/>
    <#if (TERMS_AND_CONDS?exists && TERMS_AND_CONDS?has_content)>
      <#assign content = TERMS_AND_CONDS.getRelatedOne("Content")!""/>
      <#assign dataResource = content.getRelatedOne("DataResource")!""/>
      <#if dataResource?has_content>
          <#assign electronicText = dataResource.getRelatedOne("ElectronicText")!""/>
          <#assign termsAndConds = electronicText.textData!""/>
      </#if>
    </#if>
    <#assign ingredients = ""/>
    <#if (INGREDIENTS?exists && INGREDIENTS?has_content)>
      <#assign content = INGREDIENTS.getRelatedOne("Content")!""/>
      <#assign dataResource = content.getRelatedOne("DataResource")!""/>
      <#if dataResource?has_content>
          <#assign electronicText = dataResource.getRelatedOne("ElectronicText")!""/>
          <#assign ingredients = electronicText.textData!""/>
      </#if>
    </#if>
    <#assign warnings = ""/>
    <#if (WARNINGS?exists && WARNINGS?has_content)>
      <#assign content = WARNINGS.getRelatedOne("Content")!""/>
      <#assign dataResource = content.getRelatedOne("DataResource")!""/>
      <#if dataResource?has_content>
          <#assign electronicText = dataResource.getRelatedOne("ElectronicText")!""/>
          <#assign warnings = electronicText.textData!""/>
      </#if>
    </#if>
    
    <#if (variantProduct?has_content && virtualProductContentList?has_content && mode=="edit")>
      <#assign virtualProductContentList = virtualProductContentList/>
    </#if>
    <#if (virtualProduct?has_content && productContentList?has_content && mode=="add")>
      <#assign virtualProductContentList = productContentList />
    </#if>
    
    <#if virtualProductContentList?has_content>
      <#assign virtualShortSalesPitch = ""/>
      <#assign virtualWarnings = ""/>
      <#assign virtualIngredients = ""/>
      <#assign virtualTermsAndConds = ""/>
      <#assign virtualDirections = ""/>
      <#assign virtualDeliveryInfo = ""/>
      <#assign virtualSpecialInstruction = ""/>
      <#assign virtualongDescription = ""/>
      
      <#list virtualProductContentList as virtualProductContent>
        <#if (virtualProductContent.productContentTypeId == 'SHORT_SALES_PITCH')>
          <#assign content = virtualProductContent.getRelatedOne("Content")!""/>
          <#assign dataResource = content.getRelatedOne("DataResource")!""/>
          <#if dataResource?has_content>
            <#assign electronicText = dataResource.getRelatedOne("ElectronicText")!""/>
            <#assign virtualShortSalesPitch = electronicText.textData!""/>
          </#if>
        </#if>
        <#if (virtualProductContent.productContentTypeId == 'LONG_DESCRIPTION')>
          <#assign content = virtualProductContent.getRelatedOne("Content")!""/>
          <#assign dataResource = content.getRelatedOne("DataResource")!""/>
          <#if dataResource?has_content>
            <#assign electronicText = dataResource.getRelatedOne("ElectronicText")!""/>
            <#assign virtualongDescription = electronicText.textData!""/>
          </#if>
        </#if>
        <#if (virtualProductContent.productContentTypeId == 'SPECIALINSTRUCTIONS')>
          <#assign content = virtualProductContent.getRelatedOne("Content")!""/>
          <#assign dataResource = content.getRelatedOne("DataResource")!""/>
          <#if dataResource?has_content>
            <#assign electronicText = dataResource.getRelatedOne("ElectronicText")!""/>
            <#assign virtualSpecialInstruction = electronicText.textData!""/>
          </#if>
        </#if>
        <#if (virtualProductContent.productContentTypeId == 'DELIVERY_INFO')>
          <#assign content = virtualProductContent.getRelatedOne("Content")!""/>
          <#assign dataResource = content.getRelatedOne("DataResource")!""/>
          <#if dataResource?has_content>
            <#assign electronicText = dataResource.getRelatedOne("ElectronicText")!""/>
            <#assign virtualDeliveryInfo = electronicText.textData!""/>
          </#if>
        </#if>
        <#if (virtualProductContent.productContentTypeId == 'DIRECTIONS')>
          <#assign content = virtualProductContent.getRelatedOne("Content")!""/>
          <#assign dataResource = content.getRelatedOne("DataResource")!""/>
          <#if dataResource?has_content>
            <#assign electronicText = dataResource.getRelatedOne("ElectronicText")!""/>
            <#assign virtualDirections = electronicText.textData!""/>
          </#if>
        </#if>
        <#if (virtualProductContent.productContentTypeId == 'TERMS_AND_CONDS')>
          <#assign content = virtualProductContent.getRelatedOne("Content")!""/>
          <#assign dataResource = content.getRelatedOne("DataResource")!""/>
          <#if dataResource?has_content>
            <#assign electronicText = dataResource.getRelatedOne("ElectronicText")!""/>
            <#assign virtualTermsAndConds = electronicText.textData!""/>
          </#if>
        </#if>
        <#if (virtualProductContent.productContentTypeId == 'INGREDIENTS')>
          <#assign content = virtualProductContent.getRelatedOne("Content")!""/>
          <#assign dataResource = content.getRelatedOne("DataResource")!""/>
          <#if dataResource?has_content>
            <#assign electronicText = dataResource.getRelatedOne("ElectronicText")!""/>
            <#assign virtualIngredients = electronicText.textData!""/>
          </#if>
        </#if>
        <#if (virtualProductContent.productContentTypeId == 'WARNINGS')>
          <#assign content = virtualProductContent.getRelatedOne("Content")!""/>
          <#assign dataResource = content.getRelatedOne("DataResource")!""/>
          <#if dataResource?has_content>
            <#assign electronicText = dataResource.getRelatedOne("ElectronicText")!""/>
            <#assign virtualWarnings = electronicText.textData!""/>
          </#if>
        </#if>
      </#list>
    </#if>
</#if>
<#assign descriptionTooltipMaxChar = descriptionTooltipMaxChar!"50" />
    <div class="infoRow row">
        <div class="infoEntry long">
            <div class="infoCaption">
                <label>${eCommerceUiLabel.PDPLongDescriptionHeading}</label>
            </div>
            <div class="infoValue">
              <#if (mode?has_content && mode == "add")>
                <textarea name="longDescription" cols="50" rows="5">${parameters.longDescription!""}</textarea>
              <#elseif mode?has_content && mode == "edit">
                <textarea name="longDescription" cols="50" rows="5">${parameters.longDescription!longDescription!""}</textarea>
              </#if>
            </div>
            <#if (variantProduct?has_content && mode=="edit") || (virtualProduct?has_content && mode=="add")>
                <div class="infoIcon">
                  <#if virtualongDescription?has_content>
                    <#assign virtualongDescription = Static["com.osafe.util.OsafeAdminUtil"].formatToolTipText(virtualongDescription, descriptionTooltipMaxChar)/>
                    <#assign tooltipData = Static["org.ofbiz.base.util.UtilProperties"].getMessage("OSafeAdminUiLabels", "VirtualDescriptionInfo", Static["org.ofbiz.base.util.UtilMisc"].toList("${virtualongDescription}"), locale)/>
                  <#else>
                    <#assign tooltipData = Static["org.ofbiz.base.util.UtilProperties"].getMessage("OSafeAdminUiLabels", "VirtualDescriptionBlankInfo", locale)/>
                  </#if>
                    <a class="helper" href="javascript:void(0);" onMouseover="showTooltip(event,'${tooltipData!""}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
                </div>
            </#if>
        </div>
    </div>

    <div class="infoRow row">
        <div class="infoEntry long">
            <div class="infoCaption">
                <label>${eCommerceUiLabel.PDPSalesPitchHeading}</label>
            </div>
            <div class="infoValue">
              <#if (mode?has_content && mode == "add")>
                <textarea class="shortArea" name="shortSalesPitch" id="shortSalesPitch" cols="50" rows="1">${parameters.shortSalesPitch!""}</textarea>
              <#elseif mode?has_content && mode == "edit">
                <textarea class="shortArea" name="shortSalesPitch" id="shortSalesPitch" cols="50" rows="1">${parameters.shortSalesPitch!shortSalesPitch!""}</textarea>
              </#if>
            </div>
            <#if (variantProduct?has_content && mode=="edit") || (virtualProduct?has_content && mode=="add")>
                <div class="infoIcon">
                  <#if virtualShortSalesPitch?has_content>
                    <#assign virtualShortSalesPitch = Static["com.osafe.util.OsafeAdminUtil"].formatToolTipText(virtualShortSalesPitch, descriptionTooltipMaxChar)/>
                    <#assign tooltipData = Static["org.ofbiz.base.util.UtilProperties"].getMessage("OSafeAdminUiLabels", "VirtualDescriptionInfo", Static["org.ofbiz.base.util.UtilMisc"].toList("${virtualShortSalesPitch}"), locale)/>
                  <#else>
                    <#assign tooltipData = Static["org.ofbiz.base.util.UtilProperties"].getMessage("OSafeAdminUiLabels", "VirtualDescriptionBlankInfo", locale)/>
                  </#if>
                    <a class="helper" href="javascript:void(0);" onMouseover="showTooltip(event,'${tooltipData!""}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
                </div>
            </#if>
        </div>
    </div>
   
    <div class="infoRow row">
        <div class="infoEntry long">
            <div class="infoCaption">
                <label>${eCommerceUiLabel.PDPSpecialInstructionsHeading}</label>
            </div>
            <div class="infoValue">
              <#if (mode?has_content && mode == "add")>
                <textarea class="shortArea" name="specialInstruction" cols="50" rows="5">${parameters.specialInstruction!""}</textarea>
              <#elseif mode?has_content && mode == "edit">
                <textarea class="shortArea" name="specialInstruction" cols="50" rows="5">${parameters.specialInstruction!specialInstruction!""}</textarea>
              </#if>
            </div>
            <#if (variantProduct?has_content && mode=="edit") || (virtualProduct?has_content && mode=="add")>
                <div class="infoIcon">
                  <#if virtualSpecialInstruction?has_content>
                    <#assign virtualSpecialInstruction = Static["com.osafe.util.OsafeAdminUtil"].formatToolTipText(virtualSpecialInstruction, descriptionTooltipMaxChar)/>
                    <#assign tooltipData = Static["org.ofbiz.base.util.UtilProperties"].getMessage("OSafeAdminUiLabels", "VirtualDescriptionInfo", Static["org.ofbiz.base.util.UtilMisc"].toList("${virtualSpecialInstruction}"), locale)/>
                  <#else>
                    <#assign tooltipData = Static["org.ofbiz.base.util.UtilProperties"].getMessage("OSafeAdminUiLabels", "VirtualDescriptionBlankInfo", locale)/>
                  </#if>
                    <a class="helper" href="javascript:void(0);" onMouseover="showTooltip(event,'${tooltipData!""}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
                </div>
            </#if>
        </div>
    </div>
    <div class="infoRow row">
        <div class="infoEntry long">
            <div class="infoCaption">
                <label>${eCommerceUiLabel.PDPDeliveryInfoHeading}</label>
            </div>
            <div class="infoValue">
              <#if (mode?has_content && mode == "add")>
                <textarea class="shortArea" name="deliveryInfo" cols="50" rows="5">${parameters.deliveryInfo!""}</textarea>
              <#elseif mode?has_content && mode == "edit">
                <textarea class="shortArea" name="deliveryInfo" cols="50" rows="5">${parameters.deliveryInfo!deliveryInfo!""}</textarea>
              </#if>
            </div>
            <#if (variantProduct?has_content && mode=="edit") || (virtualProduct?has_content && mode=="add")>
                <div class="infoIcon">
                  <#if virtualDeliveryInfo?has_content>
                    <#assign VirtualDescriptionInfo = Static["com.osafe.util.OsafeAdminUtil"].formatToolTipText(virtualDeliveryInfo, descriptionTooltipMaxChar)/>
                    <#assign tooltipData = Static["org.ofbiz.base.util.UtilProperties"].getMessage("OSafeAdminUiLabels", "VirtualDescriptionInfo", Static["org.ofbiz.base.util.UtilMisc"].toList("${virtualDeliveryInfo}"), locale)/>
                  <#else>
                    <#assign tooltipData = Static["org.ofbiz.base.util.UtilProperties"].getMessage("OSafeAdminUiLabels", "VirtualDescriptionBlankInfo", locale)/>
                  </#if>
                    <a class="helper" href="javascript:void(0);" onMouseover="showTooltip(event,'${tooltipData!""}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
                </div>
            </#if>
        </div>
    </div>
    <div class="infoRow row">
        <div class="infoEntry long">
            <div class="infoCaption">
                <label>${eCommerceUiLabel.PDPDirectionsHeading}</label>
            </div>
            <div class="infoValue">
              <#if (mode?has_content && mode == "add")>
                <textarea class="shortArea" name="directions" cols="50" rows="5">${parameters.directions!""}</textarea>
              <#elseif mode?has_content && mode == "edit">
                <textarea class="shortArea" name="directions" cols="50" rows="5">${parameters.directions!directions!""}</textarea>
              </#if>
            </div>
            <#if (variantProduct?has_content && mode=="edit") || (virtualProduct?has_content && mode=="add")>
                <div class="infoIcon">
                  <#if virtualDirections?has_content>
                    <#assign virtualDirections = Static["com.osafe.util.OsafeAdminUtil"].formatToolTipText(virtualDirections, descriptionTooltipMaxChar)/>
                    <#assign tooltipData = Static["org.ofbiz.base.util.UtilProperties"].getMessage("OSafeAdminUiLabels", "VirtualDescriptionInfo", Static["org.ofbiz.base.util.UtilMisc"].toList("${virtualDirections}"), locale)/>
                  <#else>
                    <#assign tooltipData = Static["org.ofbiz.base.util.UtilProperties"].getMessage("OSafeAdminUiLabels", "VirtualDescriptionBlankInfo", locale)/>
                  </#if>
                    <a class="helper" href="javascript:void(0);" onMouseover="showTooltip(event,'${tooltipData!""}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
                </div>
            </#if>
        </div>
    </div>
    <div class="infoRow row">
        <div class="infoEntry long">
            <div class="infoCaption">
                <label>${eCommerceUiLabel.PDPTermsConditionsHeading}</label>
            </div>
            <div class="infoValue">
              <#if (mode?has_content && mode == "add")>
                <textarea class="shortArea" name="termsAndConds" cols="50" rows="5">${parameters.termsAndConds!""}</textarea>
              <#elseif mode?has_content && mode == "edit">
                <textarea class="shortArea" name="termsAndConds" cols="50" rows="5">${parameters.termsAndConds!termsAndConds!""}</textarea>
              </#if>
            </div>
            <#if (variantProduct?has_content && mode=="edit") || (virtualProduct?has_content && mode=="add")>
                <div class="infoIcon">
                  <#if virtualTermsAndConds?has_content>
                    <#assign virtualTermsAndConds = Static["com.osafe.util.OsafeAdminUtil"].formatToolTipText(virtualTermsAndConds, descriptionTooltipMaxChar)/>
                    <#assign tooltipData = Static["org.ofbiz.base.util.UtilProperties"].getMessage("OSafeAdminUiLabels", "VirtualDescriptionInfo", Static["org.ofbiz.base.util.UtilMisc"].toList("${virtualTermsAndConds}"), locale)/>
                  <#else>
                    <#assign tooltipData = Static["org.ofbiz.base.util.UtilProperties"].getMessage("OSafeAdminUiLabels", "VirtualDescriptionBlankInfo", locale)/>
                  </#if>
                    <a class="helper" href="javascript:void(0);" onMouseover="showTooltip(event,'${tooltipData!""}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
                </div>
            </#if>
        </div>
    </div>
    <div class="infoRow row">
        <div class="infoEntry long">
            <div class="infoCaption">
                <label>${eCommerceUiLabel.PDPIngredientsHeading}</label>
            </div>
            <div class="infoValue">
              <#if (mode?has_content && mode == "add")>
                <textarea class="shortArea" name="ingredients" cols="50" rows="5">${parameters.ingredients!""}</textarea>
              <#elseif mode?has_content && mode == "edit">
                <textarea class="shortArea" name="ingredients" cols="50" rows="5">${parameters.ingredients!ingredients!""}</textarea>
              </#if>
            </div>
            <#if (variantProduct?has_content && mode=="edit") || (virtualProduct?has_content && mode=="add")>
                <div class="infoIcon">
                  <#if virtualIngredients?has_content>
                    <#assign virtualIngredients = Static["com.osafe.util.OsafeAdminUtil"].formatToolTipText(virtualIngredients, descriptionTooltipMaxChar)/>
                    <#assign tooltipData = Static["org.ofbiz.base.util.UtilProperties"].getMessage("OSafeAdminUiLabels", "VirtualDescriptionInfo", Static["org.ofbiz.base.util.UtilMisc"].toList("${virtualIngredients}"), locale)/>
                  <#else>
                    <#assign tooltipData = Static["org.ofbiz.base.util.UtilProperties"].getMessage("OSafeAdminUiLabels", "VirtualDescriptionBlankInfo", locale)/>
                  </#if>
                    <a class="helper" href="javascript:void(0);" onMouseover="showTooltip(event,'${tooltipData!""}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
                </div>
            </#if>
        </div>
    </div>
    <div class="infoRow row">
        <div class="infoEntry long">
            <div class="infoCaption">
                <label>${eCommerceUiLabel.PDPWarningsHeading}</label>
            </div>
            <div class="infoValue">
              <#if (mode?has_content && mode == "add")>
                <textarea class="shortArea" name="warnings" cols="50" rows="5">${parameters.warnings!""}</textarea>
              <#elseif mode?has_content && mode == "edit">
                <textarea class="shortArea" name="warnings" cols="50" rows="5">${parameters.warnings!warnings!""}</textarea>
              </#if>
            </div>
            <#if (variantProduct?has_content && mode=="edit") || (virtualProduct?has_content && mode=="add")>
                <div class="infoIcon">
                  <#if virtualWarnings?has_content>
                    <#assign virtualWarnings = Static["com.osafe.util.OsafeAdminUtil"].formatToolTipText(virtualWarnings, descriptionTooltipMaxChar)/>
                    <#assign tooltipData = Static["org.ofbiz.base.util.UtilProperties"].getMessage("OSafeAdminUiLabels", "VirtualDescriptionInfo", Static["org.ofbiz.base.util.UtilMisc"].toList("${virtualWarnings}"), locale)/>
                  <#else>
                    <#assign tooltipData = Static["org.ofbiz.base.util.UtilProperties"].getMessage("OSafeAdminUiLabels", "VirtualDescriptionBlankInfo", locale)/>
                  </#if>
                    <a class="helper" href="javascript:void(0);" onMouseover="showTooltip(event,'${tooltipData!""}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
                </div>
            </#if>
        </div>
    </div>