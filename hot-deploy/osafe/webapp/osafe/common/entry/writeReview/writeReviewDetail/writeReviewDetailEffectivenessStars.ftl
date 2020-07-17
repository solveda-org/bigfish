<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<#assign effectivenessRate = requestParameters.effectivenessRate!"">
<#assign legendMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("1", "Poor", "2", "Fair", "3", "Average","4","Good","5","Excellent")/>
<#if effectivenessRate?has_content>
    <#assign effectivenessLegend = legendMap["${effectivenessRate}"]!"">
</#if>
<div class="${request.getAttribute("attributeClass")!}">
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
                rbi.setBGWidth(${IMG_SIZE_REVIEW_STARS_W!defaultReviewStarImageWidth!});
                rbi.setBGHeight(${IMG_SIZE_REVIEW_STARS_H!defaultReviewStarImageHeight!});
                rbi.setRatingType( 'Star',
                'Stars');
                rbi.setRatingLegend(["Poor", "Fair", "Average", "Good", "Excellent"]);
                //rbi.init();
                window.parent.rateObj1 = rbi;
                </script>
            </div>
            
    </div>
    </div>
</div>