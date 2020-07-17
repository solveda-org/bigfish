<#if INGREDIENTS?exists &&  INGREDIENTS?has_content>
 <div class="pdpIngredients">
       <div class="displayBox">
        <h3>${uiLabelMap.PDPIngredientsHeading}</h3>
        <p><@renderContentAsText contentId="${INGREDIENTS}" ignoreTemplate="true"/></p>
       </div>
 </div>
</#if>
