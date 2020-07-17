<#assign stringYear = thisDate?string("yyyy")>
<#assign thisYear = stringYear?number>

<#list 0..10 as i>
    <#assign expireYear = thisYear + i>
    <option value="${expireYear}">${expireYear}</option>
</#list>