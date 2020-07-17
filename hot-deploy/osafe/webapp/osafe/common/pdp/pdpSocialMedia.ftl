<div id="pdpSocialMedia">
<h3 id="socialMediaButtonBarTitle">${uiLabelMap.SocialMediaShareLabel}</h3>
<div id="socialMediaButtonBar">
    <#assign facebookShare=PDP_SHARE_FACEBOOK!"">
    <#if facebookShare?has_content>
        <a name="Facebook" class="facebookLink " target="Facebook" href="${facebookShare}${requestUrl}"><span>${uiLabelMap.FacebookLabel}</span></a>
    </#if>
    <#assign twitterShare=PDP_SHARE_TWITTER!"">
    <#if twitterShare?has_content>
        <a name="Twitter" class="twitterLink " target="Twitter" href="${twitterShare}${requestUrl}"><span>${uiLabelMap.TwitterLabel}</span></a>
    </#if>
</div>
<div id="socialMediaEmailToAFriend">
    <a class="emailFriendLink" href="mailto:?subject=${uiLabelMap.SocialMediaEmailToAFriendSubjectLabel}&body=${requestUrl}">${uiLabelMap.SocialMediaEmailToAFriendLabel}</a>
</div>
</div>