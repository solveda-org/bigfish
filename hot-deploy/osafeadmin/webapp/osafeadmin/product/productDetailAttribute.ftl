<#if currentProduct?has_content>
  <#if productListPrice?has_content>
    <#assign listPrice = productListPrice.price!"" />
  </#if>
  <#if productDefaultPrice?has_content>
    <#assign defaultPrice = productDefaultPrice.price!"" />
  </#if>
  
  <#if productVariantListPrice?has_content>
    <#assign variantListPrice = productVariantListPrice.price!"" />
  </#if>
  <#if productVariantSalePrice?has_content>
    <#assign variantSalePrice = productVariantSalePrice.price!"" />
  </#if>
  
  <#if currentProduct.introductionDate?has_content>
    <#assign introductionDate = (currentProduct.introductionDate)?string(preferredDateFormat)>
  </#if>
  <#if currentProduct.salesDiscontinuationDate?has_content>
    <#assign salesDiscontinuationDate = (currentProduct.salesDiscontinuationDate)?string(preferredDateFormat)>
  </#if>
</#if>
   <#if (mode?has_content && mode =='edit' && isVariant != 'Y') || (mode?has_content && mode =='add' && !virtualProduct?has_content)>
       <div class="infoRow column">
           <div class="infoEntry">
               <div class="infoCaption">
                   <label>${uiLabelMap.ListPriceCaption}</label>
               </div>
               <div class="infoValue">
                   <#if (mode?has_content && mode == "add")>
                     <input type="text" class="textEntry textAlignRight" name="listPrice" id="listPrice" value="${parameters.listPrice!listPrice!}"/>
                   <#elseif mode?has_content && mode == "edit">
                     <input type="text" class="textEntry textAlignRight" name="listPrice" id="listPrice" value="<#if parameters.listPrice?has_content || listPrice?has_content>${parameters.listPrice!listPrice?string("0.00")!}</#if>"/>
                   </#if>
               </div>
           </div>
       </div>
       <div class="infoRow column">
           <div class="infoEntry">
               <div class="infoCaption">
                   <label>${uiLabelMap.SalePriceCaption}</label>
               </div>
               <div class="infoValue">
                   <#if (mode?has_content && mode == "add")>
                     <input type="text"  class="textEntry textAlignRight" name="defaultPrice" id="defaultPrice" value="${parameters.defaultPrice!defaultPrice!}"/>
                   <#elseif mode?has_content && mode == "edit">
                     <input type="text"  class="textEntry textAlignRight" name="defaultPrice" id="defaultPrice" value="<#if parameters.defaultPrice?has_content || defaultPrice?has_content>${parameters.defaultPrice!defaultPrice?string("0.00")!}</#if>"/>
                     <#if productPriceCondList?has_content><span class="pricingInfo">${uiLabelMap.PricingRulesApplyInfo}</span></#if>
                   </#if>
               </div>
            </div>
        </div>
    <#else>
      <div class="infoRow column">
        <div class="infoEntry">
          <div class="infoCaption">
            <label>${uiLabelMap.ListPriceCaption}</label>
          </div>
          <div class="infoValue">
            <input type="text" class="textEntry textAlignRight" name="variantListPrice" id="variantListPrice" value="<#if parameters.variantListPrice?has_content || variantListPrice?has_content>${parameters.variantListPrice!variantListPrice?string("0.00")!}</#if>"/>
          </div>
          <div class="infoIcon">
              <#assign tooltipData = Static["org.ofbiz.base.util.UtilProperties"].getMessage("OSafeAdminUiLabels", "VariantListPriceInfo", Static["org.ofbiz.base.util.UtilMisc"].toList("${globalContext.currencySymbol!}${listPrice!}"), locale)/>
              <a href="javascript:void(0);" onMouseover="showTooltip(event,'${tooltipData!""}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
          </div>
        </div>
      </div>
      
      <div class="infoRow column">
        <div class="infoEntry">
          <div class="infoCaption">
            <label>${uiLabelMap.SalePriceCaption}</label>
          </div>
          <div class="infoValue">
            <input type="text"  class="textEntry textAlignRight" name="variantSalePrice" id="variantSalePrice" value="<#if parameters.variantSalePrice?has_content || variantSalePrice?has_content>${parameters.variantSalePrice!variantSalePrice?string("0.00")!}</#if>"/>
          </div>
          <div class="infoIcon">
              <#assign tooltipData = Static["org.ofbiz.base.util.UtilProperties"].getMessage("OSafeAdminUiLabels", "VariantSalePriceInfo", Static["org.ofbiz.base.util.UtilMisc"].toList("${globalContext.currencySymbol!}${defaultPrice!}"), locale)/>
              <a href="javascript:void(0);" onMouseover="showTooltip(event,'${tooltipData!""}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
          </div>
        </div>
        <#if productPriceCondList?has_content><span class="pricingInfo">${uiLabelMap.PricingRulesApplyInfo}</span></#if>
      </div>
    </#if>
    
    <div class="infoRow column">
        <div class="infoEntry">
            <div class="infoCaption">
                <label>${uiLabelMap.IntroducedDateCaption}</label>
            </div>
            <div class="infoValue">
                <div class="entryInput from">
                  <#if (mode?has_content && mode == "add")>
                    <input class="dateEntry datePicker" type="text" id="introductionDate" name="introductionDate" maxlength="40" value="${parameters.introductionDate!""}"/>
                  <#elseif mode?has_content && mode == "edit">
                    <input class="dateEntry datePicker" type="text" id="introductionDate" name="introductionDate" maxlength="40" value="${parameters.introductionDate!introductionDate!""}"/>
                  </#if>
                </div>
            </div>
        </div>
    </div>
    <div class="infoRow column">
        <div class="infoEntry">
            <div class="infoCaption">
                <label>${uiLabelMap.DiscontinuedDateCaption}</label>
            </div>
            <div class="infoValue">
                <div class="entryInput from">
                  <#if (mode?has_content && mode == "add")>
                    <input class="dateEntry datePicker" type="text" id="salesDiscontinuationDate" name="salesDiscontinuationDate" maxlength="40" value="${parameters.salesDiscontinuationDate!""}"/>
                  <#elseif mode?has_content && mode == "edit">
                    <input class="dateEntry datePicker" type="text" id="salesDiscontinuationDate" name="salesDiscontinuationDate" maxlength="40" value="${parameters.salesDiscontinuationDate!salesDiscontinuationDate!""}"/>
                  </#if>
                 </div>
            </div>
         </div>
    </div>
    <#if (currentProduct?exists) && (mode?has_content)>
      <#assign bfProductAllAttributes = currentProduct.getRelated("ProductAttribute") />
      <#if bfProductAllAttributes?has_content>
        <#assign bfTotalInventoryProductAttributes = Static["org.ofbiz.entity.util.EntityUtil"].filterByAnd(bfProductAllAttributes,Static["org.ofbiz.base.util.UtilMisc"].toMap('attrName','BF_INVENTORY_TOT'))/> 
        <#assign bfWHInventoryProductAttributes = Static["org.ofbiz.entity.util.EntityUtil"].filterByAnd(bfProductAllAttributes,Static["org.ofbiz.base.util.UtilMisc"].toMap('attrName','BF_INVENTORY_WHS'))/>
      </#if>
      <#if bfTotalInventoryProductAttributes?has_content>
        <#assign bfTotalInventoryProductAttribute = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(bfTotalInventoryProductAttributes) />
        <#assign bfTotalInventory = bfTotalInventoryProductAttribute.attrValue!>
      </#if>
      
      <#if bfWHInventoryProductAttributes?has_content>
        <#assign bfWHInventoryProductAttribute = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(bfWHInventoryProductAttributes) />
        <#assign bfWHInventory = bfWHInventoryProductAttribute.attrValue!>
      </#if>
    </#if>
    <div class="infoRow column">
      <div class="infoEntry long">
        <div class="infoCaption">
          <label>${uiLabelMap.BFTotalInventoryCaption}</label>
        </div>
        <div class="infoValue">
          <#if (mode?has_content && mode == "add")>
            <input type="text" class="textEntry" name="bfTotalInventory" id="bfTotalInventory" value="${parameters.bfTotalInventory!""}" />
          <#elseif mode?has_content && mode == "edit">
            <input type="text" class="textEntry" name="bfTotalInventory" id="bfTotalInventory" value="${parameters.bfTotalInventory!bfTotalInventory!""}" />
          </#if>
        </div>
      </div>
    </div>
    
    <div class="infoRow column">
       <div class="infoEntry">
           <div class="infoCaption">
               <label>${uiLabelMap.BFWareHouseInventoryCaption}</label>
           </div>
           <div class="infoValue">
             <#if (mode?has_content && mode == "add")>
               <input type="text" class="textEntry" name="bfWHInventory" id="bfWHInventory" value="${parameters.bfWHInventory!""}" />
             <#elseif mode?has_content && mode == "edit">
               <input type="text" class="textEntry" name="bfWHInventory" id="bfWHInventory" value="${parameters.bfWHInventory!bfWHInventory!""}" />
             </#if>
           </div>
       </div>
   </div>