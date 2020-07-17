package customer;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.UtilValidate;
import org.apache.commons.lang.StringUtils;

import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.EntityUtil;

userLogin = session.getAttribute("userLogin");
partyId = StringUtils.trimToEmpty(parameters.partyId);

context.partyId=partyId;

party = delegator.findByPrimaryKey("Party", [partyId : partyId]);

messageMap=[:];
messageMap.put("partyId", partyId);

context.detailInfoBoxHeading = UtilProperties.getMessage("OSafeAdminUiLabels","CustomerDetailInfoHeading",messageMap, locale )

activityList = FastList.newInstance();

userLogins = party.getRelated("UserLogin");
if(UtilValidate.isNotEmpty(userLogins))
{
    userLogin = EntityUtil.getFirst(userLogins);
    userLoginId = userLogin.userLoginId;
    if((userLogin.enabled).equals("N"))
    {
        isEnabled = 'DISABLED';
    }
    else
    {
        isEnabled = 'ENABLED';
    }
    if((userLogin.hasLoggedOut).equals("Y"))
    {
       isLoggedIn = 'LOGGED OUT' 
    }
    else
    {
       isLoggedIn = 'LOGGED IN'
    }

    //UserLogin Activity
    activityMap = FastMap.newInstance();
    activityMap.put("activity", UtilProperties.getMessage("OSafeAdminUiLabels", "CustomerActivityRegisteredLabel", UtilMisc.toMap(), locale));
    activityMap.put("activityDescription", UtilProperties.getMessage("OSafeAdminUiLabels", "CustomerActivityUserLoginRegistrationInfo", UtilMisc.toMap(), locale));
    activityMap.put("activityDate", userLogin.createdTxStamp);
    activityMap.put("activityLink", "");
    activityMap.put("activityHoverText", UtilProperties.getMessage("OSafeAdminUiLabels", "CustomerActivityUserLoginInfo", UtilMisc.toMap("userLoginId", userLoginId, "isEnabled", isEnabled, "isLoggedIn", isLoggedIn), locale));
    activityList.add(activityMap);
}

communicationEventList = delegator.findByAnd("CommunicationEvent", [partyIdTo : partyId]);
if(UtilValidate.isNotEmpty(userLogins))
{
    for(GenericValue communicationEvent : communicationEventList)
    {
        subject = communicationEvent.subject;
        //CommunicationEvent Activity
        activityMap = FastMap.newInstance();
        activityMap.put("activity", UtilProperties.getMessage("OSafeAdminUiLabels", "CustomerActivityEmailSentLabel", UtilMisc.toMap(), locale));
        activityMap.put("activityDescription", UtilProperties.getMessage("OSafeAdminUiLabels", "CustomerActivityEmailInfo", UtilMisc.toMap("subject", subject), locale));
        activityMap.put("activityDate", communicationEvent.entryDate);
        activityMap.put("activityLink", "");
        activityMap.put("activityHoverText", "");
        activityList.add(activityMap);
    } 
}

shoppingListList = delegator.findByAnd("ShoppingList", [partyId : partyId, shoppingListTypeId : "SLT_SPEC_PURP"]);
if(UtilValidate.isNotEmpty(shoppingListList))
{
    for(GenericValue shoppingList : shoppingListList)
    {
        //ShoppingList Activity
        shoppingListId = shoppingList.shoppingListId;
        activityMap = FastMap.newInstance();
        activityMap.put("activity", UtilProperties.getMessage("OSafeAdminUiLabels", "CustomerActivityAbandonedCartLabel", UtilMisc.toMap(), locale));
        activityMap.put("activityDescription", UtilProperties.getMessage("OSafeAdminUiLabels", "CustomerActivityAbandonedCartInfo", UtilMisc.toMap("shoppingListId", shoppingListId), locale));
        activityMap.put("activityDate", shoppingList.lastUpdatedStamp);
        activityMap.put("activityLink", "");
        activityMap.put("activityHoverText", "");
        activityList.add(activityMap);
    } 
}

custRequestContactUsList = delegator.findByAnd("CustRequest", [fromPartyId : partyId, custRequestTypeId : "RF_CONTACT_US"]);
if(UtilValidate.isNotEmpty(custRequestContactUsList))
{
    for(GenericValue custRequestContactUs : custRequestContactUsList)
    {
        //CustRequest ContactUs Activity
        custRequestId = custRequestContactUs.custRequestId;
        custReqContactUsAttr = delegator.findByPrimaryKey("CustRequestAttribute", [custRequestId : custRequestId, "attrName" : "COMMENT"]);
        if(UtilValidate.isNotEmpty(custReqContactUsAttr) && UtilValidate.isNotEmpty(custReqContactUsAttr.attrValue))
        {
            comments = custReqContactUsAttr.attrValue;
        }
        else
        {
            comments = "";
        } 
        
        activityMap = FastMap.newInstance();
        activityMap.put("activity", UtilProperties.getMessage("OSafeAdminUiLabels", "CustomerActivityContactUsLabel", UtilMisc.toMap(), locale));
        activityMap.put("activityDescription", UtilProperties.getMessage("OSafeAdminUiLabels", "CustomerActivityContactUsInfo", UtilMisc.toMap(), locale));
        activityMap.put("activityDate", custRequestContactUs.custRequestDate);
        activityMap.put("activityLink", "custRequestContactUsDetail?custReqId="+custRequestId);
        activityMap.put("activityHoverText", comments);
        activityList.add(activityMap);
    } 
}

custRequestCatalogList = delegator.findByAnd("CustRequest", [fromPartyId : partyId, custRequestTypeId : "RF_CATALOG"]);
if(UtilValidate.isNotEmpty(custRequestCatalogList))
{
    for(GenericValue custRequestCatalog : custRequestCatalogList)
    {
        //CustRequest Request Catalog Activity
        custRequestId = custRequestCatalog.custRequestId;
        custReqCatalogAttr = delegator.findByPrimaryKey("CustRequestAttribute", [custRequestId : custRequestId, "attrName" : "COMMENT"]);
        if(UtilValidate.isNotEmpty(custReqCatalogAttr) && UtilValidate.isNotEmpty(custReqCatalogAttr.attrValue))
        {
            comments = custReqCatalogAttr.attrValue;
        }
        else
        {
            comments = "";
        }
        activityMap = FastMap.newInstance();
        activityMap.put("activity", UtilProperties.getMessage("OSafeAdminUiLabels", "CustomerActivityRequestCatalogLabel", UtilMisc.toMap(), locale));
        activityMap.put("activityDescription", UtilProperties.getMessage("OSafeAdminUiLabels", "CustomerActivityRequestCatalogInfo", UtilMisc.toMap(), locale));
        activityMap.put("activityDate", custRequestCatalog.custRequestDate);
        activityMap.put("activityLink", "custRequestCatalogDetail?custReqId="+custRequestId);
        activityMap.put("activityHoverText", comments);
        activityList.add(activityMap);
    } 
}

orderHeaderAndRoleList = delegator.findByAnd("OrderHeaderAndRoles", [partyId : partyId, roleTypeId : "PLACING_CUSTOMER"]);
if(UtilValidate.isNotEmpty(orderHeaderAndRoleList))
{
    for(GenericValue orderHeaderAndRole : orderHeaderAndRoleList)
    {
        //Order Placed Activity
        orderId = orderHeaderAndRole.orderId;
        grandTotal = orderHeaderAndRole.grandTotal;
        activityMap = FastMap.newInstance();
        activityMap.put("activity", UtilProperties.getMessage("OSafeAdminUiLabels", "CustomerActivityOrderPlacedLabel", UtilMisc.toMap(), locale));
        activityMap.put("activityDescription", UtilProperties.getMessage("OSafeAdminUiLabels", "CustomerActivityOrderPlacedInfo", UtilMisc.toMap("orderId", orderId, "grandTotal", grandTotal), locale));
        activityMap.put("activityDate", orderHeaderAndRole.entryDate);
        activityMap.put("activityLink", "orderDetail?orderId="+orderId);
        activityMap.put("activityHoverText", "");
        activityList.add(activityMap);
    } 
}
if(UtilValidate.isNotEmpty(userLoginId))
{
    productReviewList = delegator.findByAnd("ProductReview", [userLoginId : userLoginId]);
    if(UtilValidate.isNotEmpty(productReviewList))
    {
        for(GenericValue productReview : productReviewList)
        {
            product = delegator.findOne("Product", [productId : productReview.productId], false);
            productReviewText = productReview.productReview;
            statusId = productReview.statusId;
            productRating = ", " + (productReview.productRating).intValueExact();
            productId = product.productId;
            internalName = "";
            productName = "";
            if(UtilValidate.isNotEmpty(product.internalName))
            {
                internalName = ", " + product.internalName;
            }
            if(UtilValidate.isNotEmpty(product.productName))
            {
                productName = ", " + product.productName;
            }
            activityMap = FastMap.newInstance();
            activityMap.put("activity", UtilProperties.getMessage("OSafeAdminUiLabels", "CustomerActivityRatingPostedLabel", UtilMisc.toMap(), locale));
            activityMap.put("activityDescription", UtilProperties.getMessage("OSafeAdminUiLabels", "CustomerActivityRatingPostedInfo", UtilMisc.toMap("productId", productId, "internalName", internalName, "productName", productName, "productRating", productRating), locale));
            activityMap.put("activityDate", productReview.postedDateTime);
            activityMap.put("activityLink", "reviewDetail?productReviewId="+productReview.productReviewId);
            activityMap.put("activityHoverText", UtilProperties.getMessage("OSafeAdminUiLabels", "CustomerActivityProductRatingInfo", UtilMisc.toMap("productReviewText", productReviewText, "statusId", statusId), locale));
            activityList.add(activityMap);
        }
    }
}

if(UtilValidate.isNotEmpty(partyId))
{
    visitList = delegator.findByAnd("Visit", [partyId : partyId]);
    if(UtilValidate.isNotEmpty(visitList))
    {
        for(GenericValue visit : visitList)
        {
            visitId = visit.visitId;
            initialRequest = visit.initialRequest; 
            activityMap = FastMap.newInstance();
            activityMap.put("activity", UtilProperties.getMessage("OSafeAdminUiLabels", "CustomerActivityVisitLabel", UtilMisc.toMap(), locale));
            activityMap.put("activityDescription", UtilProperties.getMessage("OSafeAdminUiLabels", "CustomerActivityVisitInfo", UtilMisc.toMap("visitId", visitId), locale));
            activityMap.put("activityDate", visit.fromDate);
            activityMap.put("activityLink", "visitDetail?visitId="+visit.visitId);
            activityMap.put("activityHoverText", UtilProperties.getMessage("OSafeAdminUiLabels", "CustomerActivityVisitDetailInfo", UtilMisc.toMap("visitId", visitId, "initialRequest", initialRequest), locale));
            activityList.add(activityMap);
        }
    }
}

context.resultList = UtilMisc.sortMaps(activityList, UtilMisc.toList("activityDate"));
