<#if review?has_content>
    <input type="hidden" name="productRating" value="${parameters.productRating!review.productRating!}">
    <input type="hidden" name="satisfactionRating" value="${parameters.satisfactionRating!review.satisfactionRating!}">
    <input type="hidden" name="effectivenessRating" value="${parameters.effectivenessRating!review.effectivenessRating!}">
    <input type="hidden" name="qualityRating" value="${parameters.qualityRating!review.qualityRating!}">

    <#assign overallRating=parameters.productRating!review.productRating!"">
    <#assign overallRatingDir=Static["org.ofbiz.base.util.UtilFormatOut"].replaceString(""+overallRating,".","_")/>
    <#if overallRatingDir.length() == 1>
        <#assign overallRatingDir = overallRatingDir + "_0">
    </#if>
    <div class="infoRow">
       <div class="infoEntry long">
         <div class="infoCaption">
          <label>${uiLabelMap.OverallCaption}</label>
         </div>
         <div class="infoValue">
           <#if overallRating?has_content>
               <#assign ratePercentage= ((overallRating?number / 5) * 100)>
           <#else>
               <#assign ratePercentage= 0>
           </#if>
           <div class="rating_bar"><div id="productOverallRatingStars" style="width:${ratePercentage}%"></div></div>
         </div>
        <div class="starsButtons">
            <input type="button" class="standardBtn secondary" name="Star_1_0_Btn" value="1" onClick="setStars('1', 'productOverallRatingStars', 'productRating');"/>
            <input type="button" class="standardBtn secondary" name="Star_2_0_Btn" value="2" onClick="setStars('2', 'productOverallRatingStars', 'productRating');"/>
            <input type="button" class="standardBtn secondary" name="Star_3_0_Btn" value="3" onClick="setStars('3', 'productOverallRatingStars', 'productRating');"/>
            <input type="button" class="standardBtn secondary" name="Star_4_0_Btn" value="4" onClick="setStars('4', 'productOverallRatingStars', 'productRating');"/>
            <input type="button" class="standardBtn secondary" name="Star_5_0_Btn" value="5" onClick="setStars('5', 'productOverallRatingStars', 'productRating');"/>
        </div>
       </div>
    </div>

    <#assign qualityRating=parameters.qualityRating!review.qualityRating!"">
    <#assign qualityRatingDir=Static["org.ofbiz.base.util.UtilFormatOut"].replaceString(""+qualityRating,".","_")/>
    <#if qualityRatingDir.length() == 1>
        <#assign qualityRatingDir = qualityRatingDir + "_0">
    </#if>
    <div class="infoRow">
       <div class="infoEntry long">
         <div class="infoCaption">
          <label>${uiLabelMap.QualityCaption}</label>
         </div>
         <div class="infoValue">
           <#if qualityRating?has_content>
               <#assign ratePercentage= ((qualityRating?number / 5) * 100)>
           <#else>
               <#assign ratePercentage= 0>
           </#if>
           <div class="rating_bar"><div id="productQualityRatingStars" style="width:${ratePercentage}%"></div></div>
         </div>
        <div class="starsButtons">
            <input type="button" class="standardBtn secondary" name="Star_1_0_Btn" value="1" onClick="setStars('1', 'productQualityRatingStars', 'qualityRating');"/>
            <input type="button" class="standardBtn secondary" name="Star_2_0_Btn" value="2" onClick="setStars('2', 'productQualityRatingStars', 'qualityRating');"/>
            <input type="button" class="standardBtn secondary" name="Star_3_0_Btn" value="3" onClick="setStars('3', 'productQualityRatingStars', 'qualityRating');"/>
            <input type="button" class="standardBtn secondary" name="Star_4_0_Btn" value="4" onClick="setStars('4', 'productQualityRatingStars', 'qualityRating');"/>
            <input type="button" class="standardBtn secondary" name="Star_5_0_Btn" value="5" onClick="setStars('5', 'productQualityRatingStars', 'qualityRating');"/>
        </div>
       </div>
    </div>

    <#assign effectivenessRating=parameters.effectivenessRating!review.effectivenessRating!"">
    <#assign effectivenessRatingDir=Static["org.ofbiz.base.util.UtilFormatOut"].replaceString(""+effectivenessRating,".","_")/>
    <#if effectivenessRatingDir.length() == 1>
        <#assign effectivenessRatingDir = effectivenessRatingDir + "_0">
    </#if>
    <div class="infoRow">
       <div class="infoEntry long">
         <div class="infoCaption">
          <label>${uiLabelMap.EffectivenessCaption}</label>
         </div>
         <div class="infoValue">
           <#if effectivenessRating?has_content>
               <#assign ratePercentage= ((effectivenessRating?number / 5) * 100)>
           <#else>
               <#assign ratePercentage= 0>
           </#if>
           <div class="rating_bar"><div id="productEffectivenessRatingStars" style="width:${ratePercentage}%"></div></div>
         </div>
        <div class="starsButtons">
            <input type="button" class="standardBtn secondary" name="Star_1_0_Btn" value="1" onClick="setStars('1', 'productEffectivenessRatingStars', 'effectivenessRating');"/>
            <input type="button" class="standardBtn secondary" name="Star_2_0_Btn" value="2" onClick="setStars('2', 'productEffectivenessRatingStars', 'effectivenessRating');"/>
            <input type="button" class="standardBtn secondary" name="Star_3_0_Btn" value="3" onClick="setStars('3', 'productEffectivenessRatingStars', 'effectivenessRating');"/>
            <input type="button" class="standardBtn secondary" name="Star_4_0_Btn" value="4" onClick="setStars('4', 'productEffectivenessRatingStars', 'effectivenessRating');"/>
            <input type="button" class="standardBtn secondary" name="Star_5_0_Btn" value="5" onClick="setStars('5', 'productEffectivenessRatingStars', 'effectivenessRating');"/>
        </div>
       </div>
    </div>

    <#assign satisfactionRating=parameters.satisfactionRating!review.satisfactionRating!"">
    <#assign satisfactionRatingDir=Static["org.ofbiz.base.util.UtilFormatOut"].replaceString(""+satisfactionRating,".","_")/>
    <#if satisfactionRatingDir.length() == 1>
        <#assign satisfactionRatingDir = satisfactionRatingDir + "_0">
    </#if>
    <div class="infoRow">
       <div class="infoEntry long">
         <div class="infoCaption">
          <label>${uiLabelMap.SatisfactionCaption}</label>
         </div>
         <div class="infoValue">
           <#if satisfactionRating?has_content>
               <#assign ratePercentage= ((satisfactionRating?number / 5) * 100)>
           <#else>
               <#assign ratePercentage= 0>
           </#if>
           <div class="rating_bar"><div id="productSatisfactionRatingStars" style="width:${ratePercentage}%"></div></div>
         </div>
        <div class="starsButtons">
            <input type="button" class="standardBtn secondary" name="Star_1_0_Btn" value="1" onClick="setStars('1', 'productSatisfactionRatingStars', 'satisfactionRating');"/>
            <input type="button" class="standardBtn secondary" name="Star_2_0_Btn" value="2" onClick="setStars('2', 'productSatisfactionRatingStars', 'satisfactionRating');"/>
            <input type="button" class="standardBtn secondary" name="Star_3_0_Btn" value="3" onClick="setStars('3', 'productSatisfactionRatingStars', 'satisfactionRating');"/>
            <input type="button" class="standardBtn secondary" name="Star_4_0_Btn" value="4" onClick="setStars('4', 'productSatisfactionRatingStars', 'satisfactionRating');"/>
            <input type="button" class="standardBtn secondary" name="Star_5_0_Btn" value="5" onClick="setStars('5', 'productSatisfactionRatingStars', 'satisfactionRating');"/>
        </div>
       </div>
    </div>
</#if>