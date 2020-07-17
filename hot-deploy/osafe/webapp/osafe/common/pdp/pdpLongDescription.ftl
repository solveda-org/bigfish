<#if (LONG_DESCRIPTION?exists && LONG_DESCRIPTION?has_content) || categoryPdpDescription?has_content>
 <div id="pdpLongDescription">
       <div class="displayBox">
         <h3 class="displayBoxHeaderCaption">${uiLabelMap.PDPLongDescriptionHeading}</h3>
        <#-- Distinguishing Features -->
        <#if categoryPdpDescription?has_content>
          <p class="categoryDescription">${categoryPdpDescription!""}</p>
        </#if>
        <#if LONG_DESCRIPTION?exists && LONG_DESCRIPTION?has_content>
          <p><@renderContentAsText contentId="${LONG_DESCRIPTION}" ignoreTemplate="true"/></p>
        </#if>
       </div>
 </div>
</#if>
