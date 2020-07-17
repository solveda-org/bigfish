<#assign goodIdentificationTypes = Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue("osafe.properties", "goodIdentificationTypes")/>
<#if goodIdentificationTypes?exists && goodIdentificationTypes!=''>
  <#assign goodIdentificationTypesList = Static["org.ofbiz.base.util.StringUtil"].split(goodIdentificationTypes, ",")/>
</#if>
<#if goodIdentificationTypesList?exists && goodIdentificationTypesList?has_content>
  <div class="infoRow row">
    <div class="header"><h2>${uiLabelMap.ProductIdentificationHeading}</h2></div>
  </div>
  <#assign rowNo = 1/>
  <#list goodIdentificationTypesList as goodIdentificationTypeId>
    <#assign goodIdentificationType = delegator.findOne("GoodIdentificationType", {"goodIdentificationTypeId" : goodIdentificationTypeId}, false)/>
    <div class="infoRow row">
      <div class="infoEntry long">
        <div class="infoCaption">
          <label>${goodIdentificationType.get("description","OSafeAdminUiLabels",locale)}</label>
        </div>
        <div class="infoValue">
          <#if product?has_content>
              <#assign goodIdentification = delegator.findOne("GoodIdentification", Static["org.ofbiz.base.util.UtilMisc"].toMap("goodIdentificationTypeId" , goodIdentificationTypeId, "productId" , product.productId!),false)?if_exists/>
          </#if>
          <#if goodIdentification?exists>
            <#assign idValue = goodIdentification.idValue!""/>
          </#if>
          <#assign idValue = request.getParameter("idValue_${rowNo}")!idValue!''/>
          <input type="hidden" name="goodIdentificationTypeId_${rowNo}" id="goodIdentificationTypeId_${rowNo}" value="${goodIdentificationTypeId!}"/>
          <input type="text" name="idValue_${rowNo}" id="idValue_${rowNo}" class="large" value="${parameters.idValue!idValue!""}" />
        </div>
      </div>
    </div>
    <#assign rowNo = rowNo+1/>
  </#list>
</#if>