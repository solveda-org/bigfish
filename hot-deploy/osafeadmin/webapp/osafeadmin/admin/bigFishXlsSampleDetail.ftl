  <#if detailScreenName?exists && detailScreenName?has_content>
      <input type="hidden" name="detailScreen" value="${parameters.detailScreen?default(detailScreen!"")}" />
      <input type="hidden" name="sampleFile" value="Y" />
      <div class="infoRow">
          <div class="infoDetail">
              <p>${uiLabelMap.GenerateEmptyBigFishFileInfo}</p>
          </div>
      </div>
  <#else>
      ${screens.render("component://osafeadmin/widget/CommonScreens.xml#ListNoDataResult")}
  </#if>