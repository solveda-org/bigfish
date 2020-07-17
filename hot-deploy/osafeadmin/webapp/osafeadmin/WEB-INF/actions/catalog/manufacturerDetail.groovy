package catalog;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.party.party.PartyHelper;
import org.ofbiz.party.content.PartyContentWrapper;
import com.osafe.util.Util;

partyId = StringUtils.trimToEmpty(parameters.manufacturerPartyId);
context.manufacturerPartyId=partyId;
if (UtilValidate.isNotEmpty(partyId))
{
	context.generalInfoBoxHeading = UtilProperties.getMessage("OSafeAdminUiLabels","ManufacturerDetailInfoHeading",["partyId" : partyId], locale )
}
if (UtilValidate.isNotEmpty(partyId))
{
	party = delegator.findByPrimaryKey("Party", [partyId : partyId]);
	if (UtilValidate.isNotEmpty(party))
	{
        context.party=party;

		partyRoles = party.getRelated("ProductStoreRole");
        context.partyRoles=partyRoles;
        // set party product store name in context
        if (UtilValidate.isNotEmpty(partyRoles))
    	{
        	partyRoles = EntityUtil.filterByDate(partyRoles,true);
        	partyRole = EntityUtil.getFirst(partyRoles);
        	partyProductStore = partyRole.getRelatedOne("ProductStore");
            if (UtilValidate.isNotEmpty(partyProductStore))
        	{
                if (UtilValidate.isNotEmpty(partyProductStore.storeName))
            	{
                    productStoreName = partyProductStore.storeName;
            	}
                else
                {
                	productStoreName = partyProductStore.productStoreId;
                }
            	context.productStoreName = productStoreName;
        	}
    	}
        
        userLogins = party.getRelated("UserLogin");
        context.userLogins=userLogins;
		
		PartyContentWrapper partyContentWrapper = new PartyContentWrapper(party, request);
		context.partyContentWrapper = partyContentWrapper;
		
		context.description = partyContentWrapper.get("DESCRIPTION", false);
		context.longDescription = partyContentWrapper.get("LONG_DESCRIPTION", false);
		profileImageUrl = partyContentWrapper.get("PROFILE_IMAGE_URL", false);
		context.profileImageUrl = "";
		context.profileImageUrlStr = "";
		context.profileImagePath = "";
		context.profileImageName = "";
		if (UtilValidate.isNotEmpty(profileImageUrl))
		{
			context.profileImageUrl = profileImageUrl;
			context.profileImageUrlStr = profileImageUrl.toString();
			context.profileImagePath = profileImageUrl.toString().substring(0, profileImageUrl.toString().lastIndexOf("/")+1);
			context.profileImageName = profileImageUrl.toString().substring(profileImageUrl.toString().lastIndexOf("/")+1);
		}
		context.profileName = partyContentWrapper.get("PROFILE_NAME", false);
		context.IMG_SIZE_PROF_MFG_H = Util.getProductStoreParm(request,"IMG_SIZE_PROF_MFG_H");
		context.IMG_SIZE_PROF_MFG_W = Util.getProductStoreParm(request,"IMG_SIZE_PROF_MFG_W");		
	}
	
}

