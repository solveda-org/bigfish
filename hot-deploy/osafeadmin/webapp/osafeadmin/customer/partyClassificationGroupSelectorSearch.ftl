<!-- start searchBox -->
  <input type="hidden" id="lookupProduct_noConditionFind" value="Y" name="noConditionFind">
  <div class="entryRow">
    <div class="entry">
      <label class="extraLargeLabel">${uiLabelMap.PartyClassificationCaption}</label>
      <div class="entryInput">
        <input class="textEntry" type="text" id="partyClassificationGroupId" name="partyClassificationGroupId" maxlength="40" value="${parameters.partyClassificationGroupId!""}"/>
      </div>
    </div>
  </div>
  <#assign partyClassificationTypes = delegator.findByAnd("PartyClassificationType", {}, ["description"]) />
  <div class="entryRow">
    <div class="entry medium">
      <label class="extraLargeLabel">${uiLabelMap.PartyClassificationTypeCaption}</label>
      <div class="entryInput">
        <select id="partyClassificationTypeId" name="partyClassificationTypeId" class="short">
            <option value="">${uiLabelMap.SelectOneLabel}</option>
            <#if partyClassificationTypes?exists && partyClassificationTypes?has_content>
              <#list partyClassificationTypes as partyClassificationType>
                  <option value="${partyClassificationType.partyClassificationTypeId?if_exists}" <#if (parameters.partyClassificationTypeId!"") == "${partyClassificationType.partyClassificationTypeId?if_exists}">selected</#if>>${partyClassificationType.description!partyClassificationType.partyClassificationTypeId!""}</option>
              </#list>
            </#if>
          </select>
      </div>
    </div>
  </div>
  <div class="entryRow">
    <div class="entry medium">
      <label class="extraLargeLabel">${uiLabelMap.DescriptionCaption}</label>
      <div class="entryInput">
        <input class="largeTextEntry" type="text" id="description" name="description" value="${parameters.description!""}"/>
      </div>
    </div>
  </div>
  <!-- end searchBox -->