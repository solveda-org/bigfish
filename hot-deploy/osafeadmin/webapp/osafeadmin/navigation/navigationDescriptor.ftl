<#if topMenuItem?has_content>
  <#assign navDescriptorList = topMenuItem.child! /> 
  <#if navDescriptorList?has_content>
    <#list navDescriptorList as navDescriptor>
      <div class="navDescriptorBox">
        <div class="descriptorTitle">
            <a class="descriptorLink" href="<#if navDescriptor.href?has_content><@ofbizUrl>${navDescriptor.href}</@ofbizUrl></#if>">${uiLabelMap.get(navDescriptor.heading)}</a>
        </div>    
        <p class="description">${uiLabelMap.get(navDescriptor.description)}</p>
      </div>
    </#list>
  </#if>
</#if>