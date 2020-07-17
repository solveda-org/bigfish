<li class="${request.getAttribute("attributeClass")!}">
  <#if DELIVERY_INFO?exists && DELIVERY_INFO?has_content>
    <div class="pdpDeliveryInfo" id="js_pdpDeliveryInfo">
       <div class="displayBox">
         <h3>${uiLabelMap.PDPDeliveryInfoHeading}</h3>
         <p><@renderContentAsText contentId="${DELIVERY_INFO}" ignoreTemplate="true"/></p>
       </div>
    </div>
  </#if>
  <#if DELIVERY_INFO?exists && DELIVERY_INFO?has_content>
    <div class="pdpDeliveryInfo" id="js_pdpDeliveryInfo_Virtual" style="display:none">
      <div class="displayBox">
         <h3>${uiLabelMap.PDPDeliveryInfoHeading}</h3>
         <p><@renderContentAsText contentId="${DELIVERY_INFO}" ignoreTemplate="true"/></p>
      </div>
    </div>
  </#if>
  <#if productVariantMapKeys?exists && productVariantMapKeys?has_content>
    <#list productVariantMapKeys as key>
      <#assign variantContentId = ''/>
      <#assign variantContentMap = productVariantProductContentIdMap.get('${key}')!""/>
      <#if variantContentMap?has_content>
      	<#assign variantContentId = variantContentMap.get("DELIVERY_INFO")!""/>
      </#if>
      <#if variantContentId?has_content>
        <div class="pdpDeliveryInfo" id="js_pdpDeliveryInfo_${key}" style="display:none">
          <div class="displayBox">
            <h3>${uiLabelMap.PDPDeliveryInfoHeading}</h3>
            <p><@renderContentAsText contentId="${variantContentId}" ignoreTemplate="true"/></p>
          </div>
        </div>
      <#else>
        <div class="pdpDeliveryInfo" id="js_pdpDeliveryInfo_${key}" style="display:none">
         <#if DELIVERY_INFO?exists && DELIVERY_INFO?has_content>
	          <div class="displayBox">
                <h3>${uiLabelMap.PDPDeliveryInfoHeading}</h3>
                <p><@renderContentAsText contentId="${DELIVERY_INFO}" ignoreTemplate="true"/></p>
	          </div>
	     </#if>
        </div>
     </#if>
    </#list>
  </#if>
</li>
