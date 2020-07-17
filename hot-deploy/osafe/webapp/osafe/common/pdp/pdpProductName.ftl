<#if pdpProductName?has_content>
  <li class="${request.getAttribute("attributeClass")!}">
    <div>
      <h1 id="js_pdpProductName">${pdpProductName!""}</h1>
    </div>
  </li>      
</#if>