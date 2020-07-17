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

    <input type="hidden" name="productId" value="${productId}">
    <input type="hidden" name="productDescription" value="${title!}">
	<input type="hidden" name="CLIENT" value="${EMAIL_CLNT_NAME!}">
    <input type="hidden" name="productCategoryId" value="${requestParameters.productCategoryId!parameters.productCategoryId!}">
    <input type="hidden" value="${requestParameters.overallRate!overallRate!""}" id="overallValue" name="overallRate">
    <input type="hidden" value="${requestParameters.qualityRate?if_exists}" id="rateObj0Value" name="qualityRate">
    <input type="hidden" value="${requestParameters.effectivenessRate?if_exists}" id="rateObj1Value" name="effectivenessRate">
    <input type="hidden" value="${requestParameters.satisfactionRate?if_exists}" id="rateObj2Value" name="satisfactionRate">
    <#-- system params for min and max chars of product review -->
    <input type="hidden" value="${REVIEW_MIN_CHAR!}" name="REVIEW_MIN_CHAR">
    <input type="hidden" value="${REVIEW_MAX_CHAR!}" name="REVIEW_MAX_CHAR">
        
<!-- DIV for Displaying Write a review  STARTS here -->
    <div class="writeReview">
        ${screens.render("component://osafe/widget/EcommerceDivScreens.xml#writeReviewDivSequence")}
    </div>
<!-- DIV for Displaying Write a review  ENDS here -->


      

