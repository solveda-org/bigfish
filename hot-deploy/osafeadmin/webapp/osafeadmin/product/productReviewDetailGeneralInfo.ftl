<#if review?has_content>
    <#assign product = review.getRelatedOne("Product")>
    <#assign productContentWrapper = Static["org.ofbiz.product.product.ProductContentWrapper"].makeProductContentWrapper(product,request)>
    <#assign productName = productContentWrapper.get("PRODUCT_NAME")!product.productName!"">
    <div class="infoRow column">
        <div class="infoEntry">
            <div class="infoCaption">
                <label>${uiLabelMap.ReviewIdCaption}</label>
            </div>
            <div class="infoValue">
                ${review.productReviewId!""}
            </div>
        </div>
    </div>
    
    <div class="infoRow column">
        <div class="infoEntry">
            <div class="infoCaption">
                <label>${uiLabelMap.ProductNoCaption}</label>
            </div>
            <div class="infoValue">
                ${review.productId!""}
            </div>
        </div>
    </div>
    
    <div class="infoRow column">
        <div class="infoEntry">
            <div class="infoCaption">
                <label>${uiLabelMap.ReviewNickNameCaption}</label>
            </div>
            <div class="infoValue">
                ${review.reviewNickName!""}
            </div>
        </div>
    </div>
    
    <div class="infoRow column">
        <div class="infoEntry">
            <div class="infoCaption">
                <label>${uiLabelMap.ProductNameCaption}</label>
            </div>
            <div class="infoValue">
                ${productName!""}
            </div>
        </div>
    </div>
    
    <div class="infoRow column">
        <div class="infoEntry">
            <div class="infoCaption">
                <label>${uiLabelMap.DatePostedCaption}</label>
            </div>
            <div class="infoValue">
                ${review.postedDateTime?string(preferredDateFormat)}
            </div>
        </div>
    </div>
    <div class="infoRow column">
        <div class="infoEntry">
            <div class="infoCaption">
                <label>${uiLabelMap.DaysSincePostedCaption}</label>
            </div>
            <div class="infoValue">
                ${postedInterval!""}
            </div>
        </div>
    </div>
</#if>