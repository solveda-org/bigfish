package common;

import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.base.util.*;
import org.ofbiz.product.product.ProductWorker;
import com.osafe.control.SeoUrlHelper;
import org.ofbiz.party.party.PartyHelper;
import com.osafe.util.Util;


customerName = "Your Friend";
if (UtilValidate.isNotEmpty(userLogin))
{
	context.userLoginId = userLogin.userLoginId;
	party = userLogin.getRelatedOneCache("Party");
	person = party.getRelatedOneCache("Person");
	context.person=person;
	customerName = PartyHelper.getPartyName(delegator, party.partyId, false);
	
	partyContactMechPurpose = party.getRelatedCache("PartyContactMechPurpose");
	partyContactMechPurpose = EntityUtil.filterByDate(partyContactMechPurpose,true);
	
	partyPurposeEmails = EntityUtil.filterByAnd(partyContactMechPurpose, UtilMisc.toMap("contactMechPurposeTypeId", "PRIMARY_EMAIL"));
	partyPurposeEmails = EntityUtil.getRelatedCache("PartyContactMech", partyPurposeEmails);
	partyPurposeEmails = EntityUtil.filterByDate(partyPurposeEmails,true);
	partyPurposeEmails = EntityUtil.orderBy(partyPurposeEmails, UtilMisc.toList("fromDate DESC"));
	if (UtilValidate.isNotEmpty(partyPurposeEmails))
	{
		partyPurposeEmail = EntityUtil.getFirst(partyPurposeEmails);
		contactMech = partyPurposeEmail.getRelatedOneCache("ContactMech");
		context.userEmailContactMech = contactMech;
		context.userEmailAddress = contactMech.infoString;
		
	}

}

context.subject = UtilProperties.getMessage("OSafeUiLabels","EmailAFriendSubject",["sendFrom" : customerName, "EMAIL_CLNT_URL_DISP" : Util.getProductStoreParm(request, "EMAIL_CLNT_URL_DISP")], locale )

//set previos continue button url 
prevButtonUrl = "main";
if(UtilValidate.isNotEmpty(parameters.productId))
{
	//retrieve the product id and productCategoryId from the last visited PDP
	productId = parameters.productId;
	productCategories = ProductWorker.getCurrentProductCategories(delegator, productId);
	if(UtilValidate.isNotEmpty(productCategories))
	{
		productCategory = EntityUtil.getFirst(productCategories);
		productCategoryId = productCategory.productCategoryId;
	}
	if (UtilValidate.isNotEmpty(productId) && UtilValidate.isNotEmpty(productCategoryId))
	{
		 prevButtonUrl = SeoUrlHelper.makeSeoFriendlyUrl(request,"eCommerceProductDetail?productId="+productId+"&productCategoryId="+productCategoryId);
	}
}
 context.prevButtonUrl = prevButtonUrl;