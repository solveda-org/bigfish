<#assign productStore = Static["org.ofbiz.product.store.ProductStoreWorker"].getProductStore(request) />
<#if product?has_content>
  <#if productContentWrapper?exists>
    <#assign metaTitle = productContentWrapper.get("HTML_PAGE_TITLE")!""/>
    <#assign metaDesc = productContentWrapper.get("HTML_PAGE_META_DESC")!""/>
    <#assign metaKeyword = productContentWrapper.get("HTML_PAGE_META_KEY")!""/>
  </#if>
  <input type="hidden" name="productId" value="${product.productId?if_exists}" />
  <#assign isVariant = product.isVariant!"" />
</#if>
<#if productCategory?has_content>
  <#if currentProductCategoryContentWrapper?exists>
    <#assign metaTitle = currentProductCategoryContentWrapper.get("HTML_PAGE_TITLE")!""/>
    <#assign metaDesc = currentProductCategoryContentWrapper.get("HTML_PAGE_META_DESC")!""/>
    <#assign metaKeyword = currentProductCategoryContentWrapper.get("HTML_PAGE_META_KEY")!""/>
  </#if>
  <input type="hidden" name="productCategoryId" value="${productCategory.productCategoryId?if_exists}" />
</#if>
       <div class="infoRow row">
           <div class="infoEntry long">
               <div class="infoCaption">
                   <label>${uiLabelMap.TitleBFDefaultCaption}</label>
               </div>
               <div class="infoValue">
                   ${StringUtil.wrapString(Static["com.osafe.util.Util"].stripHTMLInLength(defaultTitle!productStore.title!"")!"")} 
               </div>
           </div>
       </div>
       <div class="infoRow row">
           <div class="infoEntry long">
               <div class="infoCaption">
                   <label>${uiLabelMap.TitleOverrideCaption}</label>
               </div>
               <div class="infoValue">
                   <textarea class="shortArea characterLimit" maxlength="250" name="metaTitle" cols="50" rows="5">${parameters.metaTitle!metaTitle!""}</textarea>
                   <span class="textCounter"></span>
               </div>
           </div>
       </div>
       <div class="infoRow row">
           <div class="infoEntry long">
               <div class="infoCaption">
                   <label>${uiLabelMap.MetaDescBFDefaultCaption}</label>
               </div>
               <div class="infoValue">
                   ${StringUtil.wrapString(Static["com.osafe.util.Util"].stripHTMLInLength(defaultMetaDescription!productStore.subtitle!"", SEO_META_DESC_LEN!"")!"")}
               </div>
           </div>
       </div>
       <div class="infoRow row">
           <div class="infoEntry long">
               <div class="infoCaption">
                   <label>${uiLabelMap.MetaDescOverrideCaption}</label>
               </div>
               <div class="infoValue">
                   <textarea name="metaDesc" maxlength="250" class="mediumArea characterLimit" cols="50" rows="5">${parameters.metaDesc!metaDesc!""}</textarea>
                   <span class="textCounter"></span>
               </div>
           </div>
       </div>
       <div class="infoRow row">
           <div class="infoEntry long">
               <div class="infoCaption">
                   <label>${uiLabelMap.MetaKeyBFDefaultCaption}</label>
               </div>
               <div class="infoValue">
                   ${StringUtil.wrapString(Static["com.osafe.util.Util"].stripHTMLInLength(defaultMetaKeywords!productStore.subtitle!"",SEO_META_KEY_LEN!"")!"")}
               </div>
           </div>
       </div>
       <div class="infoRow row">
           <div class="infoEntry medium">
               <div class="infoCaption">
                   <label>${uiLabelMap.MetaKeyOverrideCaption}</label>
               </div>
               <div class="infoValue">
                   <textarea name="metaKeyword" class="mediumArea characterLimit" maxlength="250" cols="50" rows="5">${parameters.metaKeyword!metaKeyword!""}</textarea>
                   <span class="textCounter"></span>
                </div>
           </div>
       </div>

