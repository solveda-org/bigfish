<#if productFeatureAndAppls?has_content>
    <input type="hidden" name="_useRowSubmit" value="Y" />
    <table class="osafe" cellspacing="0">
      <thead>
        <tr class="heading">
          <th class="idCol firstCol">${uiLabelMap.FeatureIDLabel}</th>
          <th class="idCol firstCol">${uiLabelMap.DescriptionLabel}</th>
          <th class="radioCol">${uiLabelMap.HideShowLabel}</th>
          <th class="seqCol">${uiLabelMap.SeqNumberLabel}</th>
        </tr>
      </thead>
      <tbody>
        <#assign rowClass = "1">
        <#assign rowNo = 1/>
        <input type="hidden" name="productFeatureTypeId" value="${parameters.productFeatureTypeId!}"/>
        <input type="hidden" name="productId" value="${parameters.productId!}"/>
        <#list productFeatureAndAppls as productFeatureAndAppl>
          <tr id="row_${productFeatureAndAppl.productFeatureId!}" class="<#if rowClass == "2">even<#else>odd</#if>">
            <td class="idCol firstCol">
              <input type="hidden" name="fromDate_${productFeatureAndAppl_index}" value="${productFeatureAndAppl.fromDate!}"/>
              <input type="hidden" name="productFeatureId_${productFeatureAndAppl_index}" value="${productFeatureAndAppl.productFeatureId!}"/>
              ${productFeatureAndAppl.productFeatureId!}
            </td>
            <td class="descCol">
              ${productFeatureAndAppl.description!}
            </td>
            <td class="radioCol">
              <#assign yesterday=Static["org.ofbiz.base.util.UtilDateTime"].getDayStart(nowTimestamp, -1)/>
              <span class="radiobutton">
                <#assign thruDate = request.getParameter("thruDate_${productFeatureAndAppl_index}")!productFeatureAndAppl.thruDate!''/>
                <input type="radio" name="thruDate_${productFeatureAndAppl_index}" value="${yesterday!}" <#if (thruDate?has_content)>checked="checked"</#if>/>${uiLabelMap.HideLabel}
                <input type="radio" name="thruDate_${productFeatureAndAppl_index}" value="" <#if !(thruDate?has_content)>checked="checked"</#if>/>${uiLabelMap.ShowLabel}
              </span>
            </td>
            <td class="seqCol">
               <#assign rowSeq = request.getParameter("sequenceNum_${productFeatureAndAppl_index}")!productFeatureAndAppl.sequenceNum!''/>
               <input type="text" class="small" name="sequenceNum_${productFeatureAndAppl_index}" value="${rowSeq!}" maxlength="9"/>
            </td>
            <#if rowClass == "2">
                <#assign rowClass = "1">
            <#else>
                <#assign rowClass = "2">
            </#if>
          </tr>
          <#assign rowNo = rowNo+1/>
        </#list>
      </tbody>
    </table>
<#else>
  ${screens.render("component://osafeadmin/widget/CommonScreens.xml#ListNoResult")}
</#if>