<#assign yearStart= request.getAttribute("yearStart")!/>
<#assign yearEnd= request.getAttribute("yearEnd")!/>

<#if yearStart?has_content>
    <#assign startingYear = yearStart?string("yyyy")>
    <#assign changed = request.removeAttribute("yearStart")!/>
<#else>
    <#assign startingYear = default?string("yyyy")>
</#if>
<#assign thisYear = startingYear?number>

<#if yearEnd?has_content>
    <#assign endingYear = yearEnd?string("yyyy")>
    <#assign changed = request.removeAttribute("yearEnd")!/>
    <#assign yearEnd = startingYear?number - endingYear?number />
<#else>
    <#assign yearEnd = 70 />
</#if>

<#list 0..yearEnd as i>
    <#assign expireYear = thisYear - i>
    <option value="${expireYear}">${expireYear}</option>
</#list>