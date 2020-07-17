<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<#assign legendMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("1", "Poor", "2", "Fair", "3", "Average","4","Good","5","Excellent")/>
<#assign overallRate = requestParameters.overallRate!"">
<#if overallRate?has_content>
    <#assign overallLegend = legendMap["${overallRate}"]!"">
</#if>
<div class="${request.getAttribute("attributeClass")!}">
   <div class="entry">
      <label for="overallRate"><@required/>${uiLabelMap.OverallRatingCaption}</label>
            <div id="overallRatingRow">
                <table cellspacing="0" cellpadding="0" class="ratingsTable">
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
                rbi.setBGWidth(${IMG_SIZE_REVIEW_STARS_W!defaultReviewStarImageWidth!});
                rbi.setBGHeight(${IMG_SIZE_REVIEW_STARS_H!defaultReviewStarImageHeight!});
                rbi.setRatingType( 'Star','Stars');
                rbi.setRatingLegend(["Poor", "Fair", "Average", "Good", "Excellent"]);
                //rbi.init();
                window.parent.overall = rbi;
                </script>
            </div>
            <@fieldErrors fieldName="overallRate"/>
            
    </div>
 </div>