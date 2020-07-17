<#assign chargeShipping = parameters.chargeShipping! />
<#assign returnable = parameters.returnable! />
<#if currentProduct?has_content>
    <#assign productDetailName = ""/>
    <#if (PRODUCT_NAME?exists && PRODUCT_NAME?has_content)>
      <#assign content = PRODUCT_NAME.getRelatedOne("Content")!""/>
      <#assign dataResource = content.getRelatedOne("DataResource")!""/>
      <#if dataResource?has_content>
          <#assign electronicText = dataResource.getRelatedOne("ElectronicText")!""/>
          <#assign productDetailName = electronicText.textData!""/>
      </#if>
    </#if>
    <#assign plpLabel = ""/>
    <#if (PLP_LABEL?exists && PLP_LABEL?has_content)>
      <#assign content = PLP_LABEL.getRelatedOne("Content")!""/>
      <#assign dataResource = content.getRelatedOne("DataResource")!""/>
      <#if dataResource?has_content>
          <#assign electronicText = dataResource.getRelatedOne("ElectronicText")!""/>
          <#assign plpLabel = electronicText.textData!""/>
      </#if>
    </#if>
    <#assign pdpLabel = ""/>
    <#if (PDP_LABEL?exists && PDP_LABEL?has_content)>
      <#assign content = PDP_LABEL.getRelatedOne("Content")!""/>
      <#assign dataResource = content.getRelatedOne("DataResource")!""/>
      <#if dataResource?has_content>
          <#assign electronicText = dataResource.getRelatedOne("ElectronicText")!""/>
          <#assign pdpLabel = electronicText.textData!""/>
      </#if>
    </#if>

    <#assign internalName = currentProduct.internalName!"" />
    
    <#if passedVariantProductIds?has_content || parameters.variantProductIds?has_content>
      <#assign isVariant = "Y" />
      <#assign isVirtual = "N" />
    </#if>

    <#if passedVariantProductIds?has_content>
        <#assign variantProductIds = ""/>
        <#list passedVariantProductIds as variantProductId>
            <#assign variantProductIds = variantProductIds+variantProductId/>
            <#if variantProductId_has_next?if_exists>
                <#assign variantProductIds = variantProductIds+"|"/>
            </#if>
        </#list>
    </#if>
    <#if !chargeShipping?has_content>
      <#assign chargeShipping = currentProduct.chargeShipping!"" />
    </#if>
    <#if !returnable?has_content>
      <#assign returnable = currentProduct.returnable!"" />
    </#if>
    
</#if>



    <input type="hidden" name="variantProductIds" value="${parameters.variantProductIds!variantProductIds!""}" />
    <input type="hidden" name="productTypeId" value="FINISHED_GOOD" />
    <#assign currencyUomId = CURRENCY_UOM_DEFAULT!currencyUomId />
    <input type="hidden" name="currencyUomId" value="${parameters.currencyUomId!currencyUomId!}" />
    <#if (mode?has_content && mode == "edit")>
      <input type="hidden" name="isVariant" id="isVariant" value="${isVariant!""}"/>
      <input type="hidden" name="isVirtual" id="isVirtual" value="${isVirtual!""}"/>
    </#if>
    <div class="infoRow column">
        <div class="infoEntry">
            <div class="infoCaption">
                <label>${uiLabelMap.ProductIDCaption}</label>
            </div>
            <div class="infoValue">
                <#if (mode?has_content && mode == "add") && (!virtualProduct?has_content)>
                    <input type="text" name="productId" id="productId" maxlength="20" value="${parameters.productId!""}"/>
                <#elseif (mode?has_content && mode == "add") && (virtualProduct?has_content)>
                    <input type="hidden" name="productId" id="productId" maxlength="20" value="${virtualProduct.productId!parameters.productId!""}"/>
                    <input type="text" name="productIdTo" id="productIdTo" maxlength="20" value="${parameters.productIdTo!""}"/>    
                <#elseif mode?has_content && mode == "edit">
                    <input type="hidden" name="productId" id="productId" value="${parameters.productId!currentProduct.productId?if_exists}" />${parameters.productId!currentProduct.productId?if_exists}
                </#if>
            </div>
        </div>
    </div>

    <div class="infoRow column">
       <div class="infoEntry">
           <div class="infoCaption">
               <label>${uiLabelMap.ItemNoCaption}</label>
           </div>
           <div class="infoValue">
             <#if (mode?has_content && mode == "add")>
               <input type="text" name="internalName" id="internalName" value="${parameters.internalName!""}" />
             <#elseif mode?has_content && mode == "edit">
               <input type="text" name="internalName" id="internalName" value="${parameters.internalName!internalName!""}" />
             </#if>
           </div>
       </div>
   </div>    

    <div class="infoRow column">
        <div class="infoEntry">
            <div class="infoCaption">
                <label>${uiLabelMap.TypeOfProductCaption}</label>
            </div>
            <div class="infoValue">
               <#if (mode?has_content && mode == "add") && (!virtualProduct?has_content)>
                 <div class="entry checkbox medium">
                   <input type="hidden" name="isVariant" id="isVariant" value="N"/>
                   <input class="checkBoxEntry" type="radio" name="isVirtual"  value="Y" <#if parameters.isVirtual?exists && parameters.isVirtual?string == "Y">checked="checked"<#elseif !parameters.isVirtual?exists>checked="checked"</#if> onChange="javascript:selectFinishedProduct(this)"/>${uiLabelMap.VirtualProductLabel}<br/>
                   <input class="checkBoxEntry" type="radio" name="isVirtual" value="N" <#if  parameters.isVirtual?exists && parameters.isVirtual?string == "N">checked="checked"</#if> onChange="javascript:selectFinishedProduct(this)"/>${uiLabelMap.FinishedGoodLabel}
                 </div>
               <#elseif (mode?has_content && mode == "add") && (virtualProduct?has_content)>
                 <div class="entry checkbox medium">
                   <input type="hidden" name="isVirtual" id="isVirtual" value="N"/>
                   <input class="checkBoxEntry" type="radio" name="isVariant"  value="Y" checked="checked"/>${uiLabelMap.VariantProductLabel}<br/>
                 </div>
               <#elseif mode?has_content && mode == "edit">
                 <#if isVirtual=='Y' && isVariant=='N'>
                   ${uiLabelMap.VirtualLabel}
                 <#elseif isVirtual=='N' && isVariant=='Y'>
                   ${uiLabelMap.VariantLabel}
                 <#elseif isVirtual=='N' && isVariant=='N'>
                   ${uiLabelMap.FinishedGoodLabel}
                 <#else>
                   ${uiLabelMap.UnknownLabel}
                 </#if>
               </#if>
            </div>
            <div class="infoIcon">
              <a href="javascript:void(0);" onMouseover="showTooltip(event,'${uiLabelMap.TypeOfProductInfo!""}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
          </div>
        </div>
    </div>
    
     <div class="infoRow column">
       <div class="infoEntry">
           <div class="infoCaption">
               <label>${uiLabelMap.ChargeShippingCaption}</label>
           </div>
           <div class="entry checkbox short">
             <#if (mode?has_content)>
                <input class="checkBoxEntry" type="radio" name="chargeShipping" value="Y" <#if chargeShipping?exists && (chargeShipping=="Y" ||chargeShipping=="y" || chargeShipping=="") >checked="checked"<#elseif !(chargeShipping?exists)>checked="checked"</#if>/>${uiLabelMap.YesLabel}
                <input class="checkBoxEntry" type="radio" name="chargeShipping" value="N" <#if chargeShipping=="N"||chargeShipping=="n">checked="checked"</#if>/>${uiLabelMap.NoLabel}
             </#if>
           </div>
       </div>
   </div>
   
    <#if isVirtual?exists >
      <#if isVirtual!='N' || isVariant!='N'>
        <#assign manufacturerId = virtualProduct.manufacturerPartyId!currentProduct.manufacturerPartyId! />
      <#elseif currentProduct.manufacturerPartyId?has_content >
        <#assign manufacturerId = currentProduct.manufacturerPartyId! />
      </#if>
    </#if>
    
	<#if (mode?has_content && mode == "add") && (!virtualProduct?has_content)>
	  <div class="infoRow column">
        <div class="infoEntry">
            <div class="infoCaption">
                <label></label>
            </div>            
            <div class="infoValue">
                <input type="hidden" name="blank" id="blank" value=""/>
            </div>
         </div>
       </div>
     </#if>
     
     <div class="infoRow column">
        <div class="infoEntry">
            <div class="infoCaption">
                <label>${uiLabelMap.ManufacturerCaption}</label>
            </div>
            <div class="infoValue">
            	<span id="productManufacturer" ><#if manufacturerId?has_content >${manufacturerId!}<#else>${uiLabelMap.NAInfo!}</#if></span>
            	<input type="hidden" name="manufacturerPartyId" id="manufacturerPartyId" value="${manufacturerId!}" onchange="setManufacturerIdDisplay();"/>
            	<input type="hidden" name="manufacturerPartyGroupName" id="manufacturerPartyGroupName" value=""/>
            </div>
            <#if (isVirtual?exists && ((isVirtual=='Y' && isVariant=='N') || (isVirtual=='N' && isVariant=='N'))) && (mode?has_content && mode == "edit")>
              <div class="infoIcon">
                <a href="javascript:openLookup(document.${detailFormName!}.manufacturerPartyId,document.${detailFormName!}.manufacturerPartyGroupName,'lookupManufacturer','500','700','center','true');" " onMouseover="showTooltip(event,'${uiLabelMap.ChangeManufacturerInfo!""}');" onMouseout="hideTooltip()"><span class="detailIcon"></span></a>
              </div>
            <#elseif (mode?has_content && mode == "add") && (!parameters.productId?has_content)>
              <div class="infoIcon">
                <a href="javascript:openLookup(document.${detailFormName!}.manufacturerPartyId,document.${detailFormName!}.manufacturerPartyGroupName,'lookupManufacturer','500','700','center','true');" " onMouseover="showTooltip(event,'${uiLabelMap.ChangeManufacturerInfo!""}');" onMouseout="hideTooltip()"><span class="detailIcon"></span></a>
              </div>
            </#if>
        </div>
    </div>

     <div class="infoRow column">
       <div class="infoEntry">
           <div class="infoCaption">
               <label>${uiLabelMap.ReturnableCaption}</label>
           </div>
           <div class="entry checkbox short">
             <#if (mode?has_content)>
                <input class="checkBoxEntry" type="radio" name="returnable" value="Y" <#if returnable?exists && (returnable=="Y" ||returnable=="y" || returnable=="") >checked="checked"<#elseif !(returnable?exists)>checked="checked"</#if>/>${uiLabelMap.YesLabel}
                <input class="checkBoxEntry" type="radio" name="returnable" value="N" <#if returnable=="N"||returnable=="n">checked="checked"</#if>/>${uiLabelMap.NoLabel}
             </#if>
           </div>
       </div>
   </div>
    <div class="infoRow row">
        <div class="infoEntry long">
            <div class="infoCaption">
                <label>${uiLabelMap.ProductNameCaption}</label>
            </div>
            <div class="infoValue">
                <#if (isVariant?exists && isVariant == 'Y')>
                     <textarea class="shortArea" name="productDetailName" id="productDetailName" cols="50" rows="1">${parameters.productDetailName!currentProduct.productName!""}</textarea>
                
                <#elseif (mode?has_content && mode == "add" && virtualProduct?has_content)>
	                    <#assign productVariantName = Static["org.apache.commons.lang.StringEscapeUtils"].unescapeHtml(productDetailName) >
	                    <input type="hidden" name="productDetailName" id="productDetailName" value="${productVariantName!""}"/>
	                    ${productVariantName!""}
	            <#else>
	                    <textarea class="shortArea" name="productDetailName" id="productDetailName" cols="50" rows="1">${parameters.productDetailName!productDetailName!""}</textarea>
	            </#if>
            </div>
        </div>
    </div>

    <div class="infoRow row">
        <div class="infoEntry long">
            <div class="infoCaption">
                <label>${uiLabelMap.PLPLabelCaption}</label>
            </div>
            <div class="infoValue">
                <#if (isVariant?exists && isVariant == 'Y') ||  (mode?has_content && mode == "add" && virtualProduct?has_content)>
                    ${parameters.plpLabel!plpLabel!""}
                <#else>
                    <textarea class="shortArea" name="plpLabel" id="plpLabel" cols="50" rows="1">${parameters.plpLabel!plpLabel!""}</textarea>
                </#if>
            </div>
        </div>
    </div>

   <div class="infoRow row">
       <div class="infoEntry long">
           <div class="infoCaption">
               <label>${uiLabelMap.PDPLabelCaption}</label>
           </div>
           <div class="infoValue">
               <#if (isVariant?exists && isVariant == 'Y') ||  (mode?has_content && mode == "add" && virtualProduct?has_content)>
                   ${parameters.pdpLabel!pdpLabel!""}
               <#else>
                   <textarea class="shortArea" name="pdpLabel" id="pdpLabel" cols="50" rows="1">${parameters.pdpLabel!pdpLabel!""}</textarea>
               </#if>
           </div>
       </div>
   </div>
