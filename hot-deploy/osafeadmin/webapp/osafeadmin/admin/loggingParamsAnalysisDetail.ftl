<div class="infoRow row">
    <div class="header sub"><h2>${uiLabelMap.AnalysisResultBoxHeading!}</h2></div>
</div>
<p>${uiLabelMap.ActiveSettingsLabel}</p> 
      <div class="infoRow">
          <div class="infoDetail">
              <p><#if printVerboseValue?has_content>${uiLabelMap.printVerbosePropLabel}  <#if printVerboseValue != 'false'><em>${printVerboseValue!}</em><#else>${printVerboseValue!} </#if> </#if></p>
              <p><#if printTimingValue?has_content>${uiLabelMap.printTimingPropLabel}  <#if printTimingValue != 'false'><em>${printTimingValue!}</em><#else>${printTimingValue!} </#if> </#if></p>
              <p><#if printInfoValue?has_content>${uiLabelMap.printInfoPropLabel}  <#if printInfoValue != 'false'><em>${printInfoValue!}</em><#else>${printInfoValue!} </#if> </#if></p>
              <p><#if printImportantValue?has_content>${uiLabelMap.printImportantPropLabel}  <#if printImportantValue != 'true'><em>${printImportantValue!}</em><#else>${printImportantValue!} </#if> </#if></p>
              <p><#if printWarningValue?has_content>${uiLabelMap.printWarningPropLabel}  <#if printWarningValue != 'true'><em>${printWarningValue!}</em><#else>${printWarningValue!} </#if> </#if></p>
              <p><#if printErrorValue?has_content>${uiLabelMap.printErrorPropLabel}  <#if printErrorValue != 'true'><em>${printErrorValue!}</em><#else>${printErrorValue!} </#if> </#if></p>
              <p><#if printFatalValue?has_content>${uiLabelMap.printFatalPropLabel}  <#if printFatalValue != 'true'><em>${printFatalValue!}</em><#else>${printFatalValue!} </#if> </#if></p>
          </div>
      </div>
<p>${uiLabelMap.LoggingParamsChangeInfo}</p>
