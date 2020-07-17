<#if mode?has_content>
  <#if productStoreParm?has_content>
    <#assign parmKey = productStoreParm.parmKey?if_exists />
    <#assign description = productStoreParm.description!"" />
    <#assign parmCategory = productStoreParm.parmCategory!"" />
    <#assign parmValue = productStoreParm.parmValue!"" />
  </#if>
        <div class="infoRow">
        <#-- ==== Spot Name === -->
            <div class="infoEntry">
                <div class="infoCaption"><label>${uiLabelMap.ParameterNameCaption}</label></div>
                <#-- ===== Spot Name ==== -->
               <div class="infoValue">
                  <#if mode?has_content && mode == "add">
                    <input type="text" class="characterLimit" name="parmKey" value="${parameters.parmKey!parmKey!""}" maxlength = "60" />
                    <span class="textCounter"></span>
                  <#elseif mode?has_content && mode == "edit">
                    <input type="hidden" name="parmKey" value="${parmKey!""}" />${parmKey!""}
                  </#if>
               </div>
            </div>
        </div>
        <div class="infoRow">
        <#-- ==== Category === -->
            <div class="infoEntry">
                <div class="infoCaption"><label>${uiLabelMap.CategoryCaption}</label></div>
               <div class="infoValue">
                  <#if mode?has_content && mode == "add">
                    <input type="text" name="parmCategory" value="${parameters.parmCategory!parmCategory!""}" />
                  <#elseif mode?has_content && mode == "edit">
                    <input type="hidden" name="parmCategory" value="${parmCategory!""}" />${parmCategory!""}
                  </#if>
               </div>
            </div>
        </div>
        <div class="infoRow">
        <#-- ==== Description === -->
            <div class="infoEntry">
                <div class="infoCaption"><label>${uiLabelMap.DescriptionCaption}</label></div>
                <#-- ===== Spot Name ==== -->
               <div class="infoValue">
                 <textarea class="smallArea characterLimit" maxlength = "255" name="description" cols="50" rows="1">${parameters.description!description!""}</textarea>
                 <span class="textCounter"></span>
               </div>
            </div>
        </div>
        <#-- ====== Value ==== -->
        <div class="infoRow">
            <div class="infoEntry long">
                <div class="infoCaption"><label>${uiLabelMap.ValueCaption}</label></div>
                <div class="infoValue">
                    <textarea class="smallArea characterLimit" maxlength = "255" name="parmValue" cols="50" rows="5">${parameters.newValue!parameters.parmValue!parmValue!""}</textarea>
                    <span class="textCounter"></span>
                </div>
            </div>
        </div>
 <#else>
    ${screens.render("component://osafeadmin/widget/CommonScreens.xml#ListNoDataResult")}
</#if>
