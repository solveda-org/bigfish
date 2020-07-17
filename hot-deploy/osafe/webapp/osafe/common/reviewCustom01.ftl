<#if enumTypes?has_content>
    <#list enumTypes as customCategory>
        <option value="${customCategory.description!}">${customCategory.description!}</option>
    </#list>
</#if>