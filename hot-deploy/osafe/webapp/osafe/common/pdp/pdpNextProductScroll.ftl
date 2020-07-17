<li class="${request.getAttribute("attributeClass")!}">
  <#if pdpNextProductUrl?has_content>
    <div>
        <a href="${pdpNextProductUrl}" class="standardBtn productScroll"><span>${uiLabelMap.PdpNextProductScrollBtn}</span></a>
    </div>
  </#if>
</li>