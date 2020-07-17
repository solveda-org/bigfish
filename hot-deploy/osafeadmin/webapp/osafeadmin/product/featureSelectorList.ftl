      <div class="infoRow row">
           <div class="infoEntry">
               <div class="infoCaption">
                   <label>${uiLabelMap.FeatureCaption}</label>
               </div>
               <div class="infoValue">
                   ${featureTypeDescription!}
               </div>
           </div>
       </div>
       <div class="infoRow row">
           <div class="infoEntry">
               <div class="infoCaption">
                   <label>${uiLabelMap.PickFromCaption}</label>
               </div>
               <div class="infoValue">
                   <#if productFeatureList?has_content>
                       <select name="distinguishProductFeatureMulti" id="distinguishProductFeatureMulti" multiple>
                           <#list productFeatureList as productFeature>
                               <option value="${productFeature.productFeatureId}@DISTINGUISHING_FEAT">${productFeature.description!}</option>
                           </#list>
                       </select>
                   </#if>
               </div>
               <div class="infoValue">
                  <#assign featurePickerHelperText = Static["org.ofbiz.base.util.UtilProperties"].getMessage("OSafeAdminUiLabels", "FeaturePickerHelperIconInfo", locale)/>
                  <a class="helper" href="javascript:void(0);" onMouseover="showTooltip(event,'${featurePickerHelperText!""}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
               </div>
           </div>
       </div>
       <div class="entryButton">
           <div class="entry">
               <div class="searchButton">
                   <a href="javascript:hideDialog('#lookUpDialog', '#displayLookUpDialog')"  class="buttontext standardBtn action ">${uiLabelMap.BackBtn}</a>
                   <a href="javascript:setFeatureId(this);" id="donePickedFeature"  class="buttontext standardBtn action">${uiLabelMap.DoneBtn}</a>
               </div>
           </div>
       </div>