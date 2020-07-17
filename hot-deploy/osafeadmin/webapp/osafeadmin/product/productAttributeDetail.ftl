<#if product?has_content>
	<table class="osafe" id="productAttributes">
	  <thead>
	    <tr class="heading">
	      <th class="nameCol firstCol">${uiLabelMap.NameLabel}</th>
	      <th class="nameCol">${uiLabelMap.ValueLabel}</th>
	    </tr>
	  </thead>
	  <tbody>
	    <#assign rowClass = "1"/>
	    <#assign rowNo = 1/>
	    <input type="hidden" name="productId" value="${product.productId?if_exists}" />
	    <#if productAttrList?has_content>
	    	<#list productAttrList as productAttr>
		      	<#assign attrName = productAttr.attrName!"">
				<#assign attrValue = productAttr.attrValue!"">
	        	<tr class="dataRow <#if rowClass?if_exists == "2">even<#else>odd</#if>">
		        	<td class="nameCol">${attrName}</td>
		        	<td class="nameCol">
	            		<input name="productAttrValue_${attrName}" type="text" id="productAttrValue_${attrName}" value="${attrValue!""}"/>
	           		</td>
	        	</tr>
	        	<#if rowClass == "2">
	          		<#assign rowClass = "1">
	        	<#else>
	          		<#assign rowClass = "2">
	        	</#if>
	        	<#assign rowNo = rowNo+1/>
	      	</#list>
	    </#if>
	  </tbody>
	</table>
<#else>
	<p>${uiLabelMap.NoDataAvailableInfo}</p>
</#if>