<#assign shippingInstructions = "" />
<#if orderItemShipGroups?has_content>
    <#list orderItemShipGroups as shipGroup>
          <#assign shipInstructions = shipGroup.shippingInstructions!"">
          <#if shipInstructions?has_content>
              <#assign shippingInstructions = shipInstructions/>
          </#if>
    </#list>
</#if>

<#if shippingInstructions?has_content>
	<li class="${request.getAttribute("attributeClass")!}">
		<div>
		  <label>${uiLabelMap.CartShippingInstructionsLabel}</label>
		  <span>${shippingInstructions!}</span>
		</div>
	</li>
</#if>
