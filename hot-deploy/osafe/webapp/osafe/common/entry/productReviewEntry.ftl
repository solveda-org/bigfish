<#--
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.

-->

<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>

<#assign productName = productContentWrapper.get("PRODUCT_NAME")!currentProduct.productName!"">
<#assign internalName = currentProduct.internalName!"">

<#assign productLargeImageUrl = productContentWrapper.get("LARGE_IMAGE_URL")!"">
<#-- If the string is a literal "null" make it an "" empty string then all normal logic can stay the same -->
<#if (productLargeImageUrl?string?has_content && (productLargeImageUrl == "null"))>
    <#assign productLargeImageUrl = "">
</#if>
<#if contentPathPrefix?has_content && productLargeImageUrl?has_content>
    <#assign productLargeImageUrl = contentPathPrefix + productLargeImageUrl>
</#if>

<#assign productSmallImageUrl = productContentWrapper.get("SMALL_IMAGE_URL")!"">
<#-- If the string is a literal "null" make it an "" empty string then all normal logic can stay the same -->
<#if (productSmallImageUrl?string?has_content && (productSmallImageUrl == "null"))>
    <#assign productSmallImageUrl = "">
</#if>
<#if contentPathPrefix?has_content && productSmallImageUrl?has_content>
    <#assign productSmallImageUrl = contentPathPrefix + productSmallImageUrl>
</#if>

<#assign shortDescription = productContentWrapper.get("DESCRIPTION")?if_exists>
<#assign longDescription = productContentWrapper.get("LONG_DESCRIPTION")?if_exists>

        <input type="hidden" name="productStoreId" value="${productStore.productStoreId}">
        <input type="hidden" name="productId" value="${productId}">
        <input type="hidden" name="productCategoryId" value="${requestParameters.productCategoryId!parameters.productCategoryId!}">
        <input type="hidden" value="${requestParameters.overallRate!overallRate!""}" id="overallValue" name="overallRate">
        <input type="hidden" value="${requestParameters.qualityRate?if_exists}" id="rateObj0Value" name="qualityRate">
        <input type="hidden" value="${requestParameters.effectivenessRate?if_exists}" id="rateObj1Value" name="effectivenessRate">
        <input type="hidden" value="${requestParameters.satisfactionRate?if_exists}" id="rateObj2Value" name="satisfactionRate">
        <#assign legendMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("1", "Poor", "2", "Fair", "3", "Average","4","Good","5","Excellent")/>
        <#assign overallRate = requestParameters.overallRate!"">
        <#if overallRate?has_content>
          <#assign overallLegend = legendMap["${overallRate}"]!"">
        </#if>
        <#assign qualityRate = requestParameters.qualityRate!"">
        <#if qualityRate?has_content>
          <#assign qualityLegend = legendMap["${qualityRate}"]!"">
        </#if>
        <#assign effectivenessRate = requestParameters.effectivenessRate!"">
        <#if effectivenessRate?has_content>
          <#assign effectivenessLegend = legendMap["${effectivenessRate}"]!"">
        </#if>
        <#assign satisfactionRate = requestParameters.satisfactionRate!"">
        <#if satisfactionRate?has_content>
          <#assign satisfactionLegend = legendMap["${satisfactionRate}"]!"">
        </#if>

<div id="productReviewDetails" class="displayBox">
    <h3>${StringUtil.wrapString(productName)}</h3>
    <div class="productReviewHeaderRow" id="productReviewHeaderRow">
            <div class="productReviewHeader">
              <img border="0" alt="${StringUtil.wrapString(productName)}" class="productReviewImage" src="${productSmallImageUrl?if_exists}" height="${IMG_SIZE_REVIEW_H!""}" width="${IMG_SIZE_REVIEW_W!""}">
              <span id="reviewCopy" class="reviewCopy">
                <#if internalName?has_content>
                   <p>${uiLabelMap.InternalNameLabel}&nbsp;${internalName}</p>
                </#if>
                <p>${longDescription}</p>
               </span>
            </div>
    </div>
</div>

<div id="productRatingEntry" class="displayBox">
<h3>${uiLabelMap.ProductRatingsHeading}</h3>
   <p class="instructions">${StringUtil.wrapString(uiLabelMap.RequiredInstructionsInfo)}</p>            
  <fieldset class="col">
    <div class="entry">
      <label for="overallRate"><@required/>${uiLabelMap.OverallRatingCaption}</label>
	        <div id="BVoverallRatingRow">
	            <table cellspacing="0" cellpadding="0" class="BVratingsTable">
	             <tbody>
	              <tr>
	                <td class="ratingWrapper">
		                <table cellspacing="0" cellpadding="0" onmousedown="overall.startSlide()" onclick="overall.setRating(event); bvrrAnalyticsWrapper(this);" class="ratingBar" onmouseup="overall.stopSlide()" onmouseout="overall.resetHover()" onmousemove="overall.doSlide(event)" id="overallRatingBar" style="width: 85px;">
		                 <tbody>
		                  <tr>
		                   <td>
		                    <table cellspacing="0" cellpadding="0" id="overallHover">
		                     <tbody>
		                      <tr>
		                       <td>
		                        <table cellspacing="0" cellpadding="0" id="overallFilled" class="rating_bar_submit_${overallRate!""}">
		                         <tbody>
		                          <tr>
		                            <td class="bottom"></td>
		                          </tr>
		                         </tbody>
		                        </table>
		                       </td>
		                      </tr>
		                     </tbody>
		                    </table>
		                   </td>
		                  </tr>
		                 </tbody>
		                </table>
	               </td>
	               <td class="ratingDisplayValue" id="overallDisplay"><#if overallRate?has_content>${overallRate!""} Stars</#if></td>
	               <td class="ratingLegendValue" id="overallLegend">${overallLegend!""}</td>
	             </tr>
	            </tbody>
	           </table>
	            <script type="text/javascript">
	            var rbi = new BvRatingBar('overall');
	            rbi.setMaxRating(5);
	            rbi.setMinRating(1);
	            rbi.setSparkleImage('/osafe_theme/images/user_content/images/starOn.gif');
	            rbi.setSpecificity(1);
	            rbi.setBGWidth( 17);
	            rbi.setBGHeight( 18);
	            rbi.setRatingType( 'Star','Stars');
	            rbi.setRatingLegend(["Poor", "Fair", "Average", "Good", "Excellent"]);
	            //rbi.init();
	            window.parent.overall = rbi;
	            </script>
	        </div>
            <@fieldErrors fieldName="overallRate"/>
	        
    </div>
    <div id="qualityRatingEntry">
    <div class="entry">
      <label for="qualityRate">${uiLabelMap.QualityLabel}</label>
	        <div id="qualityRatingRow">
	            <table cellspacing="0" cellpadding="0" class="BVratingsTable">
	             <tbody>
	              <tr>
	                <td class="ratingWrapper">
		             <table cellspacing="0" cellpadding="0" onmousedown="rateObj0.startSlide()" onclick="rateObj0.setRating(event); bvrrAnalyticsWrapper(this);" class="ratingBar" onmouseup="rateObj0.stopSlide()" onmouseout="rateObj0.resetHover()" onmousemove="rateObj0.doSlide(event)" id="rateObj0RatingBar" style="width: 85px;">
		              <tbody>
		               <tr>
		                <td>
		                 <table cellspacing="0" cellpadding="0" id="rateObj0Hover">
		                  <tbody>
		                   <tr>
		                    <td>
		                     <table cellspacing="0" cellpadding="0" id="rateObj0Filled" class="rating_bar_submit_${qualityRate!""}">
		                      <tbody>
		                       <tr>
		                        <td class="bottom"></td>
		                       </tr>
		                      </tbody>
		                     </table>
		                    </td>
		                   </tr>
		                  </tbody>
		                 </table>
		                </td>
		               </tr>
		              </tbody>
		             </table>
	               </td>
	               <td class="ratingDisplayValue" id="rateObj0Display"><#if qualityRate?has_content>${qualityRate!""} Stars</#if></td>
	               <td class="ratingLegendValue" id="rateObj0Legend">${qualityLegend!""}</td>
	             </tr>
	            </tbody>
	           </table>
	            <script type="text/javascript">
		        var rbi = new BvRatingBar('rateObj0');
		        rbi.setMaxRating(5);
		        rbi.setMinRating(1);
		        rbi.setSparkleImage('/osafe_theme/images/user_content/images/starOn.gif');
		        rbi.setSpecificity(1);
		        rbi.setBGWidth( 17);
		        rbi.setBGHeight( 18);
		        rbi.setRatingType( 'Star',
		        'Stars');
		        rbi.setRatingLegend(["Poor", "Fair", "Average", "Good", "Excellent"]);
		        //rbi.init();
		        window.parent.rateObj0 = rbi;
	            </script>
	        </div>
	        
    </div>
    </div>
    <div id="effectivenessRatingEntry">
    <div class="entry">
      <label for="effectivenessRate">${uiLabelMap.EffectivenessLabel}</label>
	        <div id="effectRatingRow">
	            <table cellspacing="0" cellpadding="0" class="BVratingsTable">
	             <tbody>
	              <tr>
	                <td class="ratingWrapper">
		             <table cellspacing="0" cellpadding="0" onmousedown="rateObj1.startSlide()" onclick="rateObj1.setRating(event); bvrrAnalyticsWrapper(this);" class="ratingBar" onmouseup="rateObj1.stopSlide()" onmouseout="rateObj1.resetHover()" onmousemove="rateObj1.doSlide(event)" id="rateObj1RatingBar" style="width: 85px;">
		              <tbody>
		               <tr>
		                <td>
		                 <table cellspacing="0" cellpadding="0" id="rateObj1Hover">
		                  <tbody>
		                   <tr>
		                    <td>
		                     <table cellspacing="0" cellpadding="0" id="rateObj1Filled" class="rating_bar_submit_${effectivenessRate!""}">
		                      <tbody>
		                       <tr>
		                        <td class="bottom"></td>
		                       </tr>
		                      </tbody>
		                     </table>
		                    </td>
		                   </tr>
		                  </tbody>
		                 </table>
		                </td>
		               </tr>
		              </tbody>
		             </table>
	               </td>
	               <td class="ratingDisplayValue" id="rateObj1Display"><#if effectivenessRate?has_content>${effectivenessRate!""} Stars</#if></td>
	               <td class="ratingLegendValue" id="rateObj1Legend">${effectivenessLegend!""}</td>
	             </tr>
	            </tbody>
	           </table>
	            <script type="text/javascript">
		        var rbi = new BvRatingBar('rateObj1');
		        rbi.setMaxRating(5);
		        rbi.setMinRating(1);
		        rbi.setSparkleImage('/osafe_theme/images/user_content/images/starOn.gif');
		        rbi.setSpecificity(1);
		        rbi.setBGWidth( 17);
		        rbi.setBGHeight( 18);
		        rbi.setRatingType( 'Star',
		        'Stars');
		        rbi.setRatingLegend(["Poor", "Fair", "Average", "Good", "Excellent"]);
		        //rbi.init();
		        window.parent.rateObj1 = rbi;
	            </script>
	        </div>
	        
    </div>
    </div>
    <div id="satisfactionRatingEntry">
    <div class="entry">
      <label for="satisfactionRate">${uiLabelMap.SatisfactionLabel}</label>
	        <div id="satisfactionRatingRow">
	            <table cellspacing="0" cellpadding="0" class="BVratingsTable">
	             <tbody>
	              <tr>
	                <td class="ratingWrapper">
		             <table cellspacing="0" cellpadding="0" onmousedown="rateObj2.startSlide()" onclick="rateObj2.setRating(event); bvrrAnalyticsWrapper(this);" class="ratingBar" onmouseup="rateObj2.stopSlide()" onmouseout="rateObj2.resetHover()" onmousemove="rateObj2.doSlide(event)" id="rateObj2RatingBar" style="width: 85px;">
		              <tbody>
		               <tr>
		                <td>
		                 <table cellspacing="0" cellpadding="0" id="rateObj2Hover">
		                  <tbody>
		                   <tr>
		                    <td>
		                     <table cellspacing="0" cellpadding="0" id="rateObj2Filled" class="rating_bar_submit_${satisfactionRate!""}">
		                      <tbody>
		                       <tr>
		                        <td class="bottom"></td>
		                       </tr>
		                      </tbody>
		                     </table>
		                    </td>
		                   </tr>
		                  </tbody>
		                 </table>
		                </td>
		               </tr>
		              </tbody>
		             </table>
	               </td>
	               <td class="ratingDisplayValue" id="rateObj2Display"><#if satisfactionRate?has_content>${satisfactionRate!""} Stars</#if></td>
	               <td class="ratingLegendValue" id="rateObj2Legend">${satisfactionLegend!""}</td>
	             </tr>
	            </tbody>
	           </table>
	            <script type="text/javascript">
		        var rbi = new BvRatingBar('rateObj2');
		        rbi.setMaxRating(5);
		        rbi.setMinRating(1);
		        rbi.setSparkleImage('/osafe_theme/images/user_content/images/starOn.gif');
		        rbi.setSpecificity(1);
		        rbi.setBGWidth( 17);
		        rbi.setBGHeight( 18);
		        rbi.setRatingType( 'Star',
		        'Stars');
		        rbi.setRatingLegend(["Poor", "Fair", "Average", "Good", "Excellent"]);
		        //rbi.init();
		        window.parent.rateObj2 = rbi;
	            </script>
	        </div>
	        
    </div>
    </div>
    <div class="entry">
      <label for="reviewNickname"><@required/>${uiLabelMap.YourNicknameCaption}</label>
      <input type="text" size="32" maxlength="100" onkeypress="return bvDisableReturn(event);" id="nickTextField" name="REVIEW_NICK_NAME" value="${requestParameters.REVIEW_NICK_NAME!prevNickName!""}"> 
      <span class="instructions">${uiLabelMap.NicknameExampleInfo}</span>
      <@fieldErrors fieldName="REVIEW_NICK_NAME"/>
    </div>
  </fieldset>
</div>

<div id="productRatingShareEntry" class="displayBox">
   <h3>${uiLabelMap.ShareOpinionHeading}</h3>
  <fieldset class="col">
    <div class="entry">
      <label for="reviewTitle"><@required/>${uiLabelMap.ReviewTitleCaption}</label>
      <input type="text" size="32" maxlength="100" onkeypress="return bvDisableReturn(event);" id="BVInputTitle" name="REVIEW_TITLE" value="${requestParameters.REVIEW_TITLE?if_exists}">
      <span class="instructions">${uiLabelMap.TitleExampleInfo}</span>
      <@fieldErrors fieldName="REVIEW_TITLE"/>
    </div>
    <div class="entry">
      <label for="reviewText"><@required/>${uiLabelMap.ReviewCaption}</label>
      <textarea rows="10" class="reviewTextField" cols="50" id="REVIEW_TEXT" name="REVIEW_TEXT">${requestParameters.REVIEW_TEXT?if_exists}</textarea>
      <div id="reviewTextHelper" class="reviewInputHelper">
	   <div class="reviewTipsHeaderDiv" id="reviewTipsHeaderDiv"><div class="tipBoxHeader" id="reviewTipsHeader">${uiLabelMap.ReviewTipInfo}</div></div>
	   <div class="tipBoxContentDiv" id="reviewTipsContentDiv"><div class="tipBoxListHeader" id="reviewTipsListHeader">${uiLabelMap.SeeReviewInfo}</div><ul class="tipBoxTips"><li>${uiLabelMap.ReviewTextInfo}</li><li>${uiLabelMap.UseProductReviewInfo}</li><li>${uiLabelMap.FocusFeaturesInfo}</li><li>${uiLabelMap.PleaseAvoidInfo}</li> <ul class="tipBoxTips_sub"> <li>${uiLabelMap.InformationChangesInfo}</li><li>${uiLabelMap.InappropriateLanguageInfo}</li><li>${uiLabelMap.InformationOtherInfo}</li><li>${uiLabelMap.DetailedPersonalInfo}</li></ul><li>${uiLabelMap.HaveSomethingToSayInfo}<a target="_blank" href="<@ofbizUrl>contactUs</@ofbizUrl>">${uiLabelMap.CustomerServiceInfo}</a>${uiLabelMap.YourPrivateCommentInfo}</li></ul></div>
      </div>
      <@fieldErrors fieldName="REVIEW_TEXT"/>
    </div>
  </fieldset>
</div>

<div id="productRatingTellOtherEntry" class="displayBox">
 <h3>${uiLabelMap.TellOthersHeading}</h3>
  <fieldset class="col">
    <div class="entry">
      <label for="reviewLocation">${uiLabelMap.LocationCaption}</label>
      <input type="text" size="32" maxlength="100" name="REVIEW_LOCATION" value="${requestParameters.REVIEW_LOCATION?if_exists}"> 
      <span class="instructions">${uiLabelMap.LocationExampleInfo}</span>
    </div>
    <div class="entry">
      <label for="reviewAge">${uiLabelMap.AgeCaption}</label>
      <input type="text" size="32" maxlength="60" name="REVIEW_AGE" value="${requestParameters.REVIEW_AGE?if_exists}"> 
    </div>
    <div class="entry">
      <label for="reviewGender">${uiLabelMap.GenderCaption}</label>
      <input type="text" size="32" maxlength="60" name="REVIEW_GENDER" value="${requestParameters.REVIEW_GENDER?if_exists}">
    </div>
  </fieldset>
</div>

