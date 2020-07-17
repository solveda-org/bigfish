<#if Static["com.osafe.util.Util"].isProductStoreParmTrue(REVIEW_SHOW_ON_PLP!"")>
    <#if Static["com.osafe.util.Util"].isProductStoreParmTrue(REEVOO_SHOW_ON_PLP!"")>
        <#assign reevooJsurl = REEVOO_JS_URL!"">
        <#assign reevooTrkref = REEVOO_TRKREF!"">
        <#assign reevooJsurl = reevooJsurl.concat("/").concat(reevooTrkref).concat(".js")>
        <script src="${reevooJsurl}" type="text/javascript" charset="utf-8"></script>
        <script type="text/javascript" charset="utf-8">
            ReevooMark.init_badges();
        </script>
    </#if>
</#if> 
<#if Static["com.osafe.util.Util"].isProductStoreParmTrue(REVIEW_SHOW_ON_PLP!"")>
 <div class="plpReviewStars">
    <#if Static["com.osafe.util.Util"].isProductStoreParmTrue(REEVOO_SHOW_ON_PLP!"")>
        <#assign reevooBadgeurl = REEVOO_BADGE_URL!"">
        <#assign reevooTrkref = REEVOO_TRKREF!"">
        <#assign reevooSku = "">
        <#assign skuProduct = delegator.findByPrimaryKeyCache("GoodIdentification", Static["org.ofbiz.base.util.UtilMisc"].toMap("productId", productId!"", "goodIdentificationTypeId", "SKU"))?if_exists />
        <#if skuProduct?has_content>
            <#assign reevooSku = skuProduct.idValue!"">
        <#else>
            <#assign reevooSku = productId!"">
        </#if>
        <#assign reevooBadgeurl = reevooBadgeurl.concat("/").concat(reevooTrkref!"").concat("/").concat(reevooSku!"")>
        <div class="reevoo-area">
            <a class="reevoomark" href="${reevooBadgeurl}">${uiLabelMap.ReevooProductReviewLabel}</a>
        </div>
    <#else>
        <#assign productCalculatedInfo = delegator.findByPrimaryKeyCache("ProductCalculatedInfo", Static["org.ofbiz.base.util.UtilMisc"].toMap("productId", productId!""))?if_exists>
        <#if productCalculatedInfo?has_content>
            <#assign averageCustomerRating=productCalculatedInfo.getBigDecimal("averageCustomerRating")?if_exists/>
           <#if averageCustomerRating?has_content>
               <#assign averageCustomerRating=averageCustomerRating.setScale(decimals,rounding)/>
           <#else>
               <#assign averageCustomerRating=0/>
           </#if>
            <#if averageCustomerRating?has_content && (averageCustomerRating > 0)>
                <#assign ratePercentage= ((averageCustomerRating / 5) * 100)>
                <div class="rating_bar"><div style="width:${ratePercentage}%"></div></div>
            </#if>
        </#if>
    </#if>
 </div>
</#if>