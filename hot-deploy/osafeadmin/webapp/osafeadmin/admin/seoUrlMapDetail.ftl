  <#if detailScreenName?exists && detailScreenName?has_content>
      <input type="hidden" name="detailScreen" value="${parameters.detailScreen?default(detailScreen!"")}" />
      <div class="infoRow">
          <div class="infoDetail">
              <p>${uiLabelMap.SeoUrlMapInfo}</p>
          </div>
      </div>
  <#else>
      ${screens.render("component://osafeadmin/widget/CommonScreens.xml#ListNoDataResult")}
  </#if>