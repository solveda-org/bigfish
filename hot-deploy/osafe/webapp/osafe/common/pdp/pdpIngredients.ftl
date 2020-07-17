<#if INGREDIENTS?exists &&  INGREDIENTS?has_content>
 <div id="pdpIngredients">
       <div class="displayBox">
        <h3 class="displayBoxHeaderCaption">${uiLabelMap.PDPIngredientsHeading}</h3>
        <p><@renderContentAsText contentId="${INGREDIENTS}" ignoreTemplate="true"/></p>
       </div>
 </div>
</#if>
