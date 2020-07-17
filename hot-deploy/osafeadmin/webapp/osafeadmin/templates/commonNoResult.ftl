<script type="text/javascript">
  jQuery(document).ready(function () {
    jQuery('tr.noResult td').attr("colspan", jQuery('tr.heading th').size());
  });
</script>
  <tr class="noResult">
    <td colspan="0">
      <#if ((preRetrieved?exists) && (preRetrieved != "Y"))  >
        <div class="pagingLinksBox">
          <ul class="pagingLinksBody">
            <li class="noResultsText criteria">${uiLabelMap.EnterCriteriaInfo} </li>
          </ul>
        </div>
      <#else>
        <div class="pagingLinksBox">
          <ul class="pagingLinksBody">
            <li class="noResultsText">${uiLabelMap.NoMatchingDataInfo}</li>
              <input type="hidden" name="enterCriteriaInfo" value="TRUE"/>
          </ul>
        </div>
      </#if>
    </td>
  </tr>
