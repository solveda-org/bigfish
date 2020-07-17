<#if !writerContent?exists || !writerContent?has_content>
  <#if parameters.contentId?exists && parameters.contentId?has_content>
    <#assign contentIdList = Static["org.ofbiz.base.util.UtilMisc"].toList(parameters.contentId) />
    <#list contentIdList as contentId>
      <@renderContentAsText contentId="${contentId}" ignoreTemplate="true"/>
    </#list>
  </#if>
<#else>
  ${StringUtil.wrapString(writerContent)}
</#if>