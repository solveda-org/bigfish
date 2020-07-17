package product;

import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.order.order.OrderReadHelper;
import org.ofbiz.party.contact.ContactHelper;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.UtilDateTime;

userLogin = session.getAttribute("userLogin");
context.userLogin=userLogin;
productReviewId = StringUtils.trimToEmpty(parameters.productReviewId);
if(productReviewId){
    context.productReviewId=productReviewId;
    review = delegator.findOne("ProductReview", [productReviewId : productReviewId], false);
    context.review=review;
    postedInterval = UtilDateTime.getIntervalInDays(UtilDateTime.getDayStart(review.postedDateTime), UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp()));
    context.postedInterval=postedInterval;
}