<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<#assign legendMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("1", "Poor", "2", "Fair", "3", "Average","4","Good","5","Excellent")/>
<#assign satisfactionRate = requestParameters.satisfactionRate!"">
<#if satisfactionRate?has_content>
    <#assign satisfactionLegend = legendMap["${satisfactionRate}"]!"">
</#if>
<div class="${request.getAttribute("attributeClass")!}">
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
                rbi.setBGWidth(${IMG_SIZE_REVIEW_STARS_W!defaultReviewStarImageWidth!});
                rbi.setBGHeight(${IMG_SIZE_REVIEW_STARS_H!defaultReviewStarImageHeight!});
                rbi.setRatingType( 'Star',
                'Stars');
                rbi.setRatingLegend(["Poor", "Fair", "Average", "Good", "Excellent"]);
                //rbi.init();
                window.parent.rateObj2 = rbi;
                </script>
            </div>
            
    </div>
    </div>
</div>