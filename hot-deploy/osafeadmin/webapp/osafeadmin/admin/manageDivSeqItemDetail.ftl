<#if mode?has_content>
  <#if divSeqItemEntry?has_content>
    <#assign key = divSeqItemEntry.key?if_exists />
    <#assign description = divSeqItemEntry.description!"" />
    <#assign screen = divSeqItemEntry.screen!"" />
    <#assign div = divSeqItemEntry.div!"" />
    <#assign value = divSeqItemEntry.value!"" />
  </#if>
  
    
     <div class="infoRow">
        <#-- ==== screen === -->
            <div class="infoEntry">
                <div class="infoCaption"><label>${uiLabelMap.ScreenCaption}</label></div>
                <div class="infoValue">
                 <#if mode?has_content && mode == "add">
                <select id="screen" name="screen">
                <#assign selectedScreenType = parameters.screen!parameters.ScreenType!""/>
                <#if allScreens?exists && allScreens?has_content>
                    <#assign rowClass = "1">
                    <#assign alreadyShownList = Static["javolution.util.FastList"].newInstance()/>
                    <#list allScreens as dropDownList>
                        <#assign hasNext = dropDownList_has_next>
                        <#if !alreadyShownList.contains(dropDownList.screen!"")>  <option <#if selectedScreenType == dropDownList.screen>selected=selected</#if>>${dropDownList.screen!""}</option></#if>
                        <#assign changed = alreadyShownList.add(dropDownList.screen!"")/>
                    </#list>
                </#if>
                </select>
                  <#elseif mode?has_content && mode == "edit">
                    <input type="hidden" name="screen" value="${screen!""}" />${screen!""}
                  </#if>
               </div>
            </div>
        </div>
        <div class="infoRow">
        <#-- ==== Spot Name === -->
            <div class="infoEntry">
                <div class="infoCaption"><label>${uiLabelMap.KeyCaption}</label></div>
                <#-- ===== Spot Name ==== -->
                <div class="infoValue">
                  <#if mode?has_content && mode == "add">
                    <input type="text" name="key" value="${parameters.key!key!""}" />
                  <#elseif mode?has_content && mode == "edit">
                    <input type="hidden" name="key" value="${key!""}" />${key!""}
                  </#if>
               </div>
            </div>
        </div>
       
        <div class="infoRow">
        <#-- ==== div === -->
            <div class="infoEntry">
                <div class="infoCaption"><label>${uiLabelMap.DivCaption}</label></div>
                <div class="infoValue">
                  <#if mode?has_content && mode == "add">
                    <input type="text" name="uiDiv" value="${parameters.uiDiv!div!""}" /> 
                  <#elseif mode?has_content && mode == "edit">
                    <input type="hidden" name="uiDiv" value="${div!""}" />${div!""}
                  </#if>
               </div>
               <a href="javascript:void(0);" onMouseover="showTooltip(event,'${uiLabelMap.DivTagPurposeHelperInfo}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
            </div>
        </div>
        <div class="infoRow">
        <#-- ==== Description === -->
            <div class="infoEntry long">
                <div class="infoCaption"><label>${uiLabelMap.DescriptionCaption}</label></div>
                <#-- ===== Spot Name ==== -->
               <div class="infoValue">
               <#if mode?has_content && mode == "add">
                 <textarea class="smallArea" name="description" cols="50" rows="1">${parameters.description!description!""}</textarea>
               <#elseif mode?has_content && mode == "edit">
                  <textarea class="smallArea" name="description" cols="50" rows="1" readonly="yes">${parameters.description!description!""}</textarea>
               </#if>
               </div>
            </div>
        </div>
       <#-- ====== Value ==== -->
        <div class="infoRow">
            <div class="infoEntry">
                <div class="infoCaption"><label>${uiLabelMap.SeqIdCaption}</label></div>
                <div class="infoValue">
                    <input type="text" name="value" value="${parameters.value!value!""}" />
                </div>
            </div>
        </div>
<#else>
    ${uiLabelMap.NoDataAvailableInfo}
</#if>
