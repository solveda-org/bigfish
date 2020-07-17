<li class="${request.getAttribute("attributeClass")!}">
  <#if pdpPrevProductUrl?has_content>
    <div>
        <a href="${pdpPrevProductUrl}" class="standardBtn productScroll"><span>${uiLabelMap.PdpPrevProductScrollBtn}</span></a>
    </div>
  </#if>
</li>