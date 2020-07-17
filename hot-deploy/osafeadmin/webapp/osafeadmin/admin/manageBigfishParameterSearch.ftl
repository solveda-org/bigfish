   
<!-- start searchBox -->
  <input type="hidden" name="showDetail" value="true"></input>
  <div class="entryRow">
    <div class="entry medium">
      <label>${uiLabelMap.ParameterFileCaption}</label>
      <div class="entryInput">
        <select id="parameterFileName" name="parameterFileName">
        <#assign selectedParameterFileName = parameters.parameterFileName!""/>
        <#if parameterFileList?exists && parameterFileList?has_content>
            <#list parameterFileList as parameterFile>
                <option <#if selectedParameterFileName?upper_case == parameterFile.fileName?upper_case>selected=selected</#if>>${parameterFile.fileName!""}</option>
            </#list>
        </#if>
        </select>
        <a onMouseover="showTooltip(event,'${uiLabelMap.ManageBigfishParameterHelperInfo}');" onMouseout="hideTooltip()"><span class="informationIcon"></span></a>
      </div>
    </div>
  </div>
      
<!-- end searchBox -->

