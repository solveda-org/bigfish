<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<#assign qualityRate = requestParameters.qualityRate!"">
<#assign legendMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("1", "Poor", "2", "Fair", "3", "Average","4","Good","5","Excellent")/>
<#if qualityRate?has_content>
    <#assign qualityLegend = legendMap["${qualityRate}"]!"">
</#if>
<div class="${request.getAttribute("attributeClass")!}">
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
                rbi.setBGWidth(${IMG_SIZE_REVIEW_STARS_W!defaultReviewStarImageWidth!});
                rbi.setBGHeight(${IMG_SIZE_REVIEW_STARS_H!defaultReviewStarImageHeight!});
                rbi.setRatingType( 'Star',
                'Stars');
                rbi.setRatingLegend(["Poor", "Fair", "Average", "Good", "Excellent"]);
                //rbi.init();
                window.parent.rateObj0 = rbi;
                </script>
            </div>
            
    </div>
    </div>
</div>