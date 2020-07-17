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
<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<#if requestAttributes.errorMessageList?has_content><#assign errorMessageList=requestAttributes.errorMessageList></#if>
<#if requestAttributes.osafeSuccessMessageList?has_content><#assign osafeSuccessMessageList=requestAttributes.osafeSuccessMessageList></#if>
<#if requestAttributes.warningMessageList?has_content><#assign warningMessageList=requestAttributes.warningMessageList></#if>
<#if requestAttributes.serviceValidationException?exists><#assign serviceValidationException = requestAttributes.serviceValidationException></#if>
<#if requestAttributes.uiLabelMap?has_content><#assign uiLabelMap = requestAttributes.uiLabelMap></#if>

<#assign pageMsg = "${pageMessaging?if_exists}"/>

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

<#-- display the error messages -->

<#if !(fieldLevelErrors?has_content && fieldLevelErrors ="Y")>
    <#if (errorMessage?has_content || errorMessageList?has_content)>
      <div class="content-messages eCommerceErrorMessage">
        <div class="errorImage"></div>
        <p class="errorMessage">${uiLabelMap.CommonFollowingErrorsOccurred}</p>
        <#if errorMessage?has_content>
            <ul>
	          <li class="errorMessage">${StringUtil.wrapString(errorMessage)}</li>
	        </ul>
        </#if>
        <#if errorMessageList?has_content>
            <ul>
	          <#list errorMessageList as errorMsg>
	            <li class="errorMessage">${StringUtil.wrapString(errorMsg)}</li>
	          </#list>
	        </ul>
        </#if>
      </div>
    </#if>
<#else>
    <#if errorMessageList?has_content>
        <div class="content-messages eCommerceErrorMessage">
            <div class="errorImage"></div>
            <p class="errorMessage">${uiLabelMap.CommonFollowingErrorsOccurred}</p>
            <ul>
	          <#list errorMessageList as errorMsg>
	            <li class="errorMessage">${StringUtil.wrapString(errorMsg)}</li>
	          </#list>
	        </ul>
            <@fieldErrors fieldName="GENERAL_FIELD_MESSAGE_ERROR"/>
        </div>
    </#if>
</#if>

<#if pageMsg != "N">
    <#-- display the event messages -->
    <#if osafeSuccessMessageList?has_content>
      <div class="content-messages eCommerceSuccessMessage">
         <div class="eventImage"></div>
        <#if osafeSuccessMessageList?has_content>
          <#list osafeSuccessMessageList as eventMsg>
            <p class="eventMessage">${StringUtil.wrapString(eventMsg)}</p>
          </#list>
        </#if>
      </div>
    </#if>
</#if>

<#-- display the warning messages -->
<#if (warningMessage?has_content || warningMessageList?has_content)>
  <div class="content-messages eCommerceWarningMessage">
    <div class="warningImage"></div>
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

<#-- Treating info messages the same as warnings -->
<#if (infoMessage?has_content || infoMessageList?has_content)>
  <div class="content-messages eCommerceWarningMessage">
    <div class="infoImage"></div>
    <#if infoMessage?has_content>
      <p class="warningMessage">${StringUtil.wrapString(infoMessage)}</p>
    </#if>
    <#if infoMessageList?has_content>
      <#list infoMessageList as infoMsg>
        <p class="warningMessage">${StringUtil.wrapString(infoMsg)}</p>
      </#list>
    </#if>
  </div>
</#if>
