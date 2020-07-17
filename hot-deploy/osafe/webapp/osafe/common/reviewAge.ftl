<#if enumTypes?has_content>
    <#list enumTypes as ageCategory>
        <option value="${ageCategory.description!}">${ageCategory.description!}</option>
    </#list>
</#if>