  <#if detailScreenName?exists && detailScreenName?has_content>
      <input type="hidden" name="detailScreen" value="${parameters.detailScreen?default(detailScreen!"")}" />

      <div class="infoRow">
        <div class="infoEntry">
            <div class="infoCaption">
                <label>${uiLabelMap.EntitytoExportCaption}</label>
            </div>
             <div class="entry checkbox medium">
                 <input type="checkbox" class="checkBoxEntry" name="exportall" id="exportall" value="Y" onclick="javascript:setCheckboxes('${detailFormName!""}','export')" <#if parameters.viewall?has_content>checked</#if> />${uiLabelMap.AllLabel}</br>
                 <#if contentTypeIds?has_content>
                     <#assign contentTypeIdList = Static["org.ofbiz.base.util.StringUtil"].split(contentTypeIds,"|")/>
                     <#list contentTypeIdList as contentTypeId>
                         <#assign contentType = delegator.findByPrimaryKeyCache("ContentType", Static["org.ofbiz.base.util.UtilMisc"].toMap("contentTypeId", contentTypeId!""))?if_exists />
                         <#if contentType?has_content>
                             <input type="checkbox" class="checkBoxEntry" name="contentTypeId" id="export-${contentTypeId_index!}" value="${contentTypeId!}"<#if passedContentTypeIds?has_content && passedContentTypeIds.contains(contentTypeId)>checked</#if>/>${contentType.get("description","OSafeAdminUiLabels",locale)}</br>
                         </#if>
                     </#list>
                 </#if>
             </div>
        </div>
      </div>

  <#else>
      ${uiLabelMap.NoDataAvailableInfo}
  </#if>