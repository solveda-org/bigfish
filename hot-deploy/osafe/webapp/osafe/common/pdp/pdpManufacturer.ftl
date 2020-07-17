<#if partyContentWrapper?has_content>
 <#assign description = partyContentWrapper.get("DESCRIPTION")!"">
 <#assign profileImageUrl = partyContentWrapper.get("PROFILE_IMAGE_URL")!"">
 <#assign profileName = partyContentWrapper.get("PROFILE_NAME")!"">
 <#assign profileFbLikeUrl = partyContentWrapper.get("PROFILE_NAME")!"">
 <#assign profileTweetUrl = partyContentWrapper.get("PROFILE_TWEET_URL")!"">
 <div id="pdpManufacturer">
      <h2>${uiLabelMap.ManufacturerPDPProfileHeading}</h2>
      <p class="profileName">${profileName!""}</p>
      <img alt="${profileName}" src="${profileImageUrl!""}" class="manufacturerImage" height="${IMG_SIZE_PDP_MFG_H!""}" width="${IMG_SIZE_PDP_MFG_W!""}">
      <p class="profileDescription">${description!""}</p>
 </div>
</#if>
