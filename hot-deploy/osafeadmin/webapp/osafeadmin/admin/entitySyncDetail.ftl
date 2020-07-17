  <#if detailScreenName?exists && detailScreenName?has_content>
      <input type="hidden" name="detailScreen" value="${parameters.detailScreen?default(detailScreen!"")}" />
      <input type="hidden" name="entitySyncId" value="9050" />
      <div class="infoRow">
          <div class="infoDetail">
              <p>${uiLabelMap.EntitySyncInfo}</p>
          </div>
      </div>
  <#else>
      ${screens.render("component://osafeadmin/widget/CommonScreens.xml#ListNoDataResult")}
  </#if>