package product;

import org.ofbiz.base.util.UtilProperties;

messageMap=[:];
if(altImgNo)
{
    messageMap.put("altImgNo", altImgNo);
    if(altImgNo == '1')
    {
        context.pageTitle = UtilProperties.getMessage("OSafeAdminUiLabels","ProductAlternateImagesTitle", locale )
    }
    else
    {
        context.pageTitle = null;
    }
    context.detailInfoBoxHeading = UtilProperties.getMessage("OSafeAdminUiLabels","ProductAltNoImagesHeading",messageMap, locale )
}