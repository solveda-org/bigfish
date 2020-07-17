<!-- start customerDetailPersonalInfo.ftl -->
<#if title?has_content>
    <#assign title = title.attrValue!"" >
</#if>

<#if gender?has_content>
      <#assign gender = gender.attrValue!"" >
</#if>

<#if dob_MMDD?has_content>
      <#assign dob_MMDD = dob_MMDD.attrValue!"" >
</#if>

<#if dob_MMDDYYYY?has_content>
      <#assign dob_MMDDYYYY = dob_MMDDYYYY.attrValue!"" >
</#if>

<#if dob_DDMM?has_content>
      <#assign dob_DDMM = dob_DDMM.attrValue!"" >
</#if>

<#if dob_DDMMYYYY?has_content>
      <#assign dob_DDMMYYYY = dob_DDMMYYYY.attrValue!"" >
</#if>

    <div class="displayBox personalInfo">
      <div class="header"><h2>${personalInfoBoxHeading!} <span class="headingHelperText">${uiLabelMap.CustomerDetailPersonalInfoHeadingHelperInfo}</span></h2></div>
      <div class="boxBody">

        <#if title?has_content>
            <div class="infoRow">
               <div class="infoEntry">
                 <div class="infoCaption">
                  <label>${uiLabelMap.TitleCaption}</label>
                 </div>
                 <div class="infoValue medium">
                   ${title!""}
                 </div>
               </div>
            </div>
        </#if>

        <#if gender?has_content>
            <div class="infoRow">
               <div class="infoEntry">
                 <div class="infoCaption">
                  <label>${uiLabelMap.GenderCaption}</label>
                 </div>
                 <div class="infoValue medium">
                   <#if gender == 'M'>
                       ${uiLabelMap.genderMaleLabel!""}
                   <#elseif gender == 'F'>
                       ${uiLabelMap.genderFemaleLabel!""}
                   <#else>
                       ${gender!""}
                   </#if>
                 </div>
               </div>
            </div>
        </#if>

        <#if dob_MMDD?has_content>
            <div class="infoRow">
               <div class="infoEntry">
                 <div class="infoCaption">
                  <label>${uiLabelMap.DOBCaption}</label>
                 </div>
                 <div class="infoValue medium">
                   ${dob_MMDD!""}
                 </div>
               </div>
            </div>
        </#if>

        <#if dob_MMDDYYYY?has_content>
            <div class="infoRow">
               <div class="infoEntry">
                 <div class="infoCaption">
                  <label>${uiLabelMap.DOBCaption}</label>
                 </div>
                 <div class="infoValue medium">
                   ${dob_MMDDYYYY!""}
                 </div>
               </div>
            </div>
        </#if>

        <#if dob_DDMM?has_content>
            <div class="infoRow">
               <div class="infoEntry">
                 <div class="infoCaption">
                  <label>${uiLabelMap.DOBCaption}</label>
                 </div>
                 <div class="infoValue medium">
                   ${dob_DDMM!""}
                 </div>
               </div>
            </div>
        </#if>

        <#if dob_DDMMYYYY?has_content>
            <div class="infoRow">
               <div class="infoEntry">
                 <div class="infoCaption">
                  <label>${uiLabelMap.DOBCaption}</label>
                 </div>
                 <div class="infoValue medium">
                   ${dob_DDMMYYYY!""}
                 </div>
               </div>
            </div>
        </#if>

        </div>
    </div>
    
    

<!-- end customerDetailPersonalInfo.ftl -->
