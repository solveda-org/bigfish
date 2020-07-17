package common;

import org.ofbiz.base.util.*;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.entity.*;
import org.ofbiz.product.catalog.*;
import org.ofbiz.product.store.*;
import org.ofbiz.product.category.CategoryWorker;
import org.ofbiz.product.category.CategoryContentWrapper;
import org.ofbiz.product.product.ProductContentWrapper;
import org.ofbiz.product.product.ProductWorker;
import javolution.util.FastList;
import javolution.util.FastSet;
import javolution.util.FastMap;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.order.shoppingcart.*;
import org.ofbiz.webapp.stats.VisitHandler;




import com.osafe.util.Util;

FORMAT_DATE_TIME = Util.getProductStoreParm(request,"FORMAT_DATE_TIME");
decimals=Integer.parseInt("1");
rounding = UtilNumber.getBigDecimalRoundingMode("order.rounding");

productReview = request.getAttribute("productReview");
if(UtilValidate.isNotEmpty(productReview))
{
	//overall rating
	overallRate = 0;
	overallRatePercentage = 0;
	overall = productReview.getBigDecimal("productRating");
	if(UtilValidate.isNotEmpty(overall))
	{
		overallRate = overall.setScale(decimals,rounding);
		overallRatePercentage = ((overallRate / 5) * 100);
	}
	//qaulity rating
	qualityRate = 0;
	qualityRatePercentage = 0;
	quality = productReview.getBigDecimal("qualityRating");
	if(UtilValidate.isNotEmpty(quality))
	{
		qualityRate = quality.setScale(decimals,rounding);
		qualityRatePercentage = ((qualityRate / 5) * 100);
	}
	//effectiveness rating
	effectivenessRate = 0;
	effectivenessRatePercentage = 0;
	effectiveness = productReview.getBigDecimal("effectivenessRating");
	if(UtilValidate.isNotEmpty(effectiveness))
	{
		effectivenessRate = effectiveness.setScale(decimals,rounding);
		effectivenessRatePercentage = ((effectivenessRate / 5) * 100);
	}
	//satisfaction rating
	satisfactionRate = 0;
	satisfactionRatePercentage = 0;
	satisfaction = productReview.getBigDecimal("satisfactionRating");
	if(UtilValidate.isNotEmpty(satisfaction))
	{
		satisfactionRate = satisfaction.setScale(decimals,rounding);
		satisfactionRatePercentage = ((satisfactionRate / 5) * 100);
	}
	//posted date
	postedDate = Util.convertDateTimeFormat(productReview.postedDateTime, FORMAT_DATE_TIME);
	if(UtilValidate.isEmpty(postedDate))
	{
		postedDate = "N/A";
	}
	
	//reviewer nickname
	reviewerNickname = productReview.reviewNickName;
	
	//reviewer title
	reviewerTitle = productReview.reviewTitle;
	
	//reviewer text
	reviewerText = productReview.productReview;
	
	//reviewer location
	reviewerLocation = productReview.reviewLocation;
	
	//reviewer age
	reviewerAge = productReview.reviewAge;
	
	//reviewer gender
	reviewerGender = productReview.reviewGender;
	
	//review response
	reviewResponseText = productReview.reviewResponse;
	
	//review custom 01
	reviewCustom01 = productReview.reviewCustom01;
}

context.FORMAT_DATE_TIME = FORMAT_DATE_TIME;
context.productReview = productReview;
context.overallRate = overallRate;

context.overallRate = overallRate;
context.overallRatePercentage = overallRatePercentage;
context.qualityRate = qualityRate;
context.qualityRatePercentage = qualityRatePercentage;
context.effectivenessRate = effectivenessRate;
context.effectivenessRatePercentage = effectivenessRatePercentage;
context.satisfactionRate = satisfactionRate;
context.satisfactionRatePercentage = satisfactionRatePercentage;
context.postedDate = postedDate;

context.reviewerNickname =reviewerNickname;
context.reviewerTitle = reviewerTitle;
context.reviewerText = reviewerText;
context.reviewText = "";
if(UtilValidate.isNotEmpty(reviewerText))
{
	context.reviewText = Util.getFormattedText(reviewerText);
}
context.reviewerLocation = reviewerLocation;
context.reviewerAge = reviewerAge;
context.reviewerGender = reviewerGender;
context.reviewCustom01 = reviewCustom01;
context.reviewResponseText = reviewResponseText;
context.reviewResponse = "";
if(UtilValidate.isNotEmpty(reviewResponseText))
{
	context.reviewResponse = Util.getFormattedText(reviewResponseText);
}







