<!-- start listBox -->
  <tr class="heading">
    <th class="idCol firstCol">${uiLabelMap.EmailTypeLabel}</th>
    <th class="idCol firstCol">${uiLabelMap.DescriptionLabel}</th>
    <th class="descCol">${uiLabelMap.SubjectLabel}</th>
    <th class="descCol">${uiLabelMap.FromLabel}</th>
    <th class="actionCol"></th>
  </tr>
  <#if resultList?exists && resultList?has_content>
    <#assign rowClass = "1"/>
     <#list resultList as email>
      <#assign hasNext = email_has_next>
      <tr class="dataRow <#if rowClass?if_exists == "2">even<#else>odd</#if>">
        <#assign emailTypeEnum = delegator.findByPrimaryKey("Enumeration", Static["org.ofbiz.base.util.UtilMisc"].toMap("enumId", email.emailType!""))?if_exists />
        <td class="idCol <#if !email_has_next?if_exists>lastRow</#if> firstCol" ><a href="<@ofbizUrl>emailConfigDetail?emailType=${email.emailType!""}</@ofbizUrl>">${email.emailType!""}</a></td>
        <td class="idCol <#if !email_has_next?if_exists>lastRow</#if>" ><a href="<@ofbizUrl>emailConfigDetail?emailType=${email.emailType!""}</@ofbizUrl>">${emailTypeEnum.description!""}</a></td>
        <td class="descCol <#if !email_has_next?if_exists>lastRow</#if>">${email.subject!""}</td>
        <td class="descCol <#if !email_has_next?if_exists>lastRow</#if>">${email.fromAddress!""}</td>
        <td class="actionCol <#if !email_has_next?if_exists>lastRow</#if>">
        	${setRequestAttribute("emailTypeId",email.emailType)}
        	${screens.render("component://osafeadmin/widget/AdminScreens.xml#emailConfigTemplateActionList")}
        </td> 
      </tr>
      <#if rowClass == "2">
        <#assign rowClass = "1">
      <#else>
        <#assign rowClass = "2">
      </#if>
    </#list>
  </#if>
<!-- end listBox -->