<#if ((recurrencePriceMap?exists && recurrencePriceMap?has_content) && (recurrencePriceMap.price?has_content && recurrencePriceMap.price != 0)) || (recurrenceItem?has_content && recurrenceItem == "Y")>
   <li class="${request.getAttribute("attributeClass")!}<#if lineIndex == 0> firstRow</#if>">
     <div>
       <label>${uiLabelMap.CartItemRecurrenceFlagCaption}</label>
       <input type="checkbox" class="js_recurrenceFlag" name="recurrenceFlag_${cartLineIndex}" id="recurrenceFlag_${cartLineIndex}" value="Y" <#if (recurrenceItem?has_content) && recurrenceItem == "Y">checked</#if> <#if cartLine.getIsPromo()> DISABLED</#if>/>
     </div>
   </li>
</#if>
