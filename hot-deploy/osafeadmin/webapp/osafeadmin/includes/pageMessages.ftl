<#--
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
-->

<#if requestAttributes.errorMessageList?has_content><#assign errorMessageList=requestAttributes.errorMessageList></#if>
<#if requestAttributes.osafeSuccessMessageList?has_content><#assign osafeSuccessMessageList=requestAttributes.osafeSuccessMessageList></#if>
<#if requestAttributes.warningMessageList?has_content><#assign warningMessageList=requestAttributes.warningMessageList></#if>
<#if requestAttributes.infoMessageList?has_content><#assign infoMessageList=requestAttributes.infoMessageList></#if>
<#if requestAttributes.serviceValidationException?exists><#assign serviceValidationException = requestAttributes.serviceValidationException></#if>
<#if requestAttributes.uiLabelMap?has_content><#assign uiLabelMap = requestAttributes.uiLabelMap></#if>

<#-- If it is not set at the context level check the request attributes -->
<#if !fieldLevelErrors?has_content>
    <#if requestAttributes.fieldLevelErrors?has_content>
        <#assign fieldLevelErrors = requestAttributes.fieldLevelErrors>
    </#if>
</#if>

<#if !errorMessage?has_content>
  <#assign errorMessage = requestAttributes._ERROR_MESSAGE_?if_exists>
</#if>
<#if !errorMessageList?has_content>
  <#assign errorMessageList = requestAttributes._ERROR_MESSAGE_LIST_?if_exists>
</#if>
<#if !warningMessage?has_content>
  <#assign warningMessage = requestAttributes._WARNING_MESSAGE_?if_exists>
</#if>
<#if !warningMessageList?has_content>
  <#assign warningMessageList = requestAttributes._WARNING_MESSAGE_LIST_?if_exists>
</#if>
<#if !infoMessage?has_content>
  <#assign infoMessage = requestAttributes._INFO_MESSAGE_?if_exists>
</#if>
<#if !infoMessageList?has_content>
  <#assign infoMessageList = requestAttributes._INFO_MESSAGE_LIST_?if_exists>
</#if>
<#assign responseMessage = requestAttributes.responseMessage!"">

<#-- display the error messages -->

<#if errorMessageList?has_content>
    <#if (errorMessage?has_content || errorMessageList?has_content)>
      <div class="content-messages eCommerceErrorMessage">
        <span class="errorImageIcon errorImage"></span>
         <p class="errorMessage">${uiLabelMap.FollowingErrorsOccurredError}</p>
        <#if errorMessage?has_content>
          <p class="errorMessage">${StringUtil.wrapString(errorMessage)}</p>
        </#if>
        <#if errorMessageList?has_content>
          <#list errorMessageList as errorMsg>
            <#if errorMsg?has_content && errorMsg!="">
              <p class="errorMessage">${StringUtil.wrapString(errorMsg)}</p>
            </#if>
          </#list>
        </#if>
      </div>
    </#if>
<#else>
<#-- display the event messages -->
<#if osafeSuccessMessageList?has_content>
     <div class="content-messages eCommerceSuccessMessage">
        <span class="checkMarkIcon eventImage"></span>
 
          <#if osafeSuccessMessageList?has_content>
                <#list osafeSuccessMessageList as eventMsg>
                    <p class="eventMessage">${StringUtil.wrapString(eventMsg)}</p>
                </#list>
          </#if> 
      </div>
</#if>
</#if>
<#if responseMessage?has_content>
  <div class="content-messages eCommerceSuccessMessage">
     <span class="checkMarkIcon eventImage"></span>
        <p class="eventMessage">${StringUtil.wrapString(responseMessage)}</p>
  </div>
</#if>

<#-- display the warning messages -->
<#if (warningMessage?has_content || warningMessageList?has_content)>
  <div class="content-messages eCommerceWarningMessage">
     <span class="checkMarkIcon warningImage"></span>
    <#if warningMessage?has_content>
      <p class="warningMessage">${StringUtil.wrapString(warningMessage)}</p>
    </#if>
    <#if warningMessageList?has_content>
      <#list warningMessageList as warningMsg>
        <p class="warningMessage">${StringUtil.wrapString(warningMsg)}</p>
      </#list>
    </#if>
  </div>
</#if>

<#-- display the info messages -->
<#if (infoMessage?has_content || infoMessageList?has_content)>
  <div class="content-messages eCommerceInfoMessage">
     <span class="infoMessageIcon infoIcon"></span>
    <#if infoMessage?has_content>
      <p class="infoMessage">${StringUtil.wrapString(infoMessage)}</p>
    </#if>
    <#if infoMessageList?has_content>
      <#list infoMessageList as infoMsg>
        <p class="infoMessage">${StringUtil.wrapString(infoMsg)}</p>
      </#list>
    </#if>
  </div>
</#if>

