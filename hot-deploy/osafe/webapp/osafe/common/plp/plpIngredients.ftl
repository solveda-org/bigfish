<#if PLP_INGREDIENTS?exists && PLP_INGREDIENTS?has_content>
 <div class="plpIngredients">
   <label>${uiLabelMap.PLPIngredientsLabel}</label>
   <span><@renderContentAsText contentId="${PLP_INGREDIENTS}" ignoreTemplate="true"/></span>
 </div>
</#if>
