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