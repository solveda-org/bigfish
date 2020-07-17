<li class="${request.getAttribute("attributeClass")!}<#if lineIndex == 0> firstRow</#if>">
 <div>
      <label>${uiLabelMap.TrackingNumberCaption}</label>
      <#if trackingNumber?exists && trackingNumber?has_content>
          <#if trackingURL?has_content>
            <a href="JavaScript:newPopupWindow('${trackingURL!""}');">${trackingNumber!}</a>
          <#else>
           <span>${trackingNumber!}</span>
          </#if>
      <#else>
           <span>${uiLabelMap.NotApplicableLabel}</span>
      </#if>			      
 </div>
</li>