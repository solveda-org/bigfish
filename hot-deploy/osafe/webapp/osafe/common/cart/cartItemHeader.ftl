<#assign showHeaders = true >
<#if shoppingCartTotalQuantity?exists && shoppingCartTotalQuantity == 0>
  <#assign showHeaders = false >
</#if>

<#if showHeaders == true>
  <div class="cartItemHeader">
    <#if divSequenceList?has_content>
      <#list divSequenceList as divSequenceItem>
        <#assign sequenceNum = divSequenceItem.value!/>
        <#if sequenceNum?has_content && sequenceNum?number !=0>
          ${screens.render("component://osafe/widget/EcommerceDivScreens.xml#cartHeader${divSequenceItem.div}")}
        </#if>
      </#list>
    </#if>
  </div>
</#if>





