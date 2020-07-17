<li class="${request.getAttribute("attributeClass")!}">
    <div>
      <span><#if userLocation?exists && userLocation?has_content>${storeRow.distance!""}</#if></span>
    </div>
</li>