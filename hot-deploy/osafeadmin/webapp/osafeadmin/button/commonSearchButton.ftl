<!-- Common search button  -->
<div class="entryButton">
  <div class="entry">
    <div class="searchButton">
      <input type="submit" class="standardBtn action" name="searchOrderBtn" value="${searchBtn!uiLabelMap.SearchBtn!}"/>
    </div>
  </div>
</div>
<div class="infoDetailIcon">
 <div class="linkButton">
    <#if showSearchBoxXMLLink?has_content && showSearchBoxXMLLink == 'true'>
        <a href="<@ofbizUrl>downloadFile?fileToExport=${fileToExport!}</@ofbizUrl>" target="blank" class="buttontext action" onMouseover="showTooltip(event,'${ExportToXMLTooltipText}');" onMouseout="hideTooltip()"><span class="exportToXmlIcon"></span></a>
    </#if>
 </div>
</div>