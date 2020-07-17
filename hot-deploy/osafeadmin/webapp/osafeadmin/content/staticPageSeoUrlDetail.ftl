<#if contentList?has_content>
    <input type="hidden" name="_useRowSubmit" value="Y" />
    <table class="osafe" cellspacing="0">
      <thead>
        <tr class="heading">
          <th class="idCol firstCol">${uiLabelMap.BigFishContentIdLabel}</th>
          <th class="nameCol">${uiLabelMap.SeoFriendlyPrefixLabel}</th>
          <th class="nameCol">${uiLabelMap.SeoFriendlyNameLabel}</th>
        </tr>
      </thead>
      <tbody>
        <#assign rowClass = "1">
        <#assign rowNo = 1/>
        <#list contentList as content>
          <#assign thisContent = content.getRelatedOne("Content")?if_exists />
          <tr class="<#if rowClass == "2">even<#else>odd</#if>">
            <td class="idCol firstCol">
              <input type="hidden" name="contentId_${content_index}" value="${content.contentId!}"/>
              ${content.contentId!}
            </td>
            <td class="nameCol">
              ${seoFriendlyPrefix!}
            </td>
            <td class="nameCol">
              <#assign seoUrlValue = thisContent.contentName!"" />
              <#assign contentAttribute = delegator.findOne("ContentAttribute", {"contentId" : content.contentId, "attrName" : "SEO_FRIENDLY_URL"}, false)?if_exists>
              <#if contentAttribute?has_content>
                <#assign seoUrlValue = contentAttribute.attrValue!"" />
              </#if>
              <#assign seoUrlValue = seoUrlValue?trim?lower_case?replace(" ","-") />
              <input type="text" name="attrValue_${content_index}" value="${request.getParameter("attrValue_${content_index}")!seoUrlValue!}" class="medium"/>
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