<input type="hidden" name="contentId" value="${parameters.contentId?if_exists}" />
<#assign productStore = Static["org.ofbiz.product.store.ProductStoreWorker"].getProductStore(request) />
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
      <textarea class="shortArea characterLimit" name="metaTitle" cols="50" rows="5" maxlength = "250">${parameters.metaTitle!metaTitle!""}</textarea>
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
                   <textarea  class="mediumArea characterLimit" name="metaDesc" cols="50" rows="5" maxlength = "250">${parameters.metaDesc!metaDesc!""}</textarea>
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
                   <textarea name="metaKeyword" class="mediumArea characterLimit" cols="50" rows="5" maxlength = "250">${parameters.metaKeyword!metaKeyword!""}</textarea>
                   <span class="textCounter"></span>
               </div>
           </div>
           
       </div>

