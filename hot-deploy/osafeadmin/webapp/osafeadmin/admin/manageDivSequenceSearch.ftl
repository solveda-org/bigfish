   
<!-- start searchBox -->
  <div class="entryRow">
    <div class="entry medium">
      <label>${uiLabelMap.ScreenSearchCaption}</label>
      <div class="entryInput">
        <select id="screenType" name="screenType">
        <#assign selectedScreenType = parameters.screenType!""/>
        <#if resultList?exists && resultList?has_content>
            <#assign rowClass = "1">
            <#assign alreadyShownList = Static["javolution.util.FastList"].newInstance()/>
            <#list resultList as dropDownList>
                <#assign hasNext = dropDownList_has_next>
                <#if !alreadyShownList.contains(dropDownList.screen!"")>  <option <#if selectedScreenType == dropDownList.screen>selected=selected</#if>>${dropDownList.screen!""}</option></#if>
                <#assign changed = alreadyShownList.add(dropDownList.screen!"")/>
            </#list>
        </#if>
        </select>
      </div>
      <input type="hidden" name="showDetail" value="true"></input>
    </div>
  </div>
      
<!-- end searchBox -->

