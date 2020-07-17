package com.osafe.events;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericPK;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.product.store.ProductStoreWorker;


public class ProductEvents {

    public static final String module = ProductEvents.class.getName();

    private static String stripHTML(String in) {
        final Pattern delimiters = Pattern.compile(" |<[0-9a-zA-Z /%=*]+>+");
        in = delimiters.matcher(in).replaceAll(" ");
        return in;
    }
    public static String createProductReview(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        //LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        GenericValue userLogin = (GenericValue)request.getSession().getAttribute("userLogin");
        GenericValue productStore=null;

        String productStoreId = request.getParameter("productStoreId");

        String productId = null;
        if (request.getParameter("productId") != null)
        {
            productId = request.getParameter("productId");
            productStore=ProductStoreWorker.getProductStore(productStoreId, delegator);
        }
        else {
            String errMsg = "Missing productId";
            request.setAttribute("_ERROR_MESSAGE", errMsg);
            return "error";
        }

        String nickName = null;
        if (request.getParameter("nickName") != null && !request.getParameter("nickName").equals(""))
        {
            nickName = request.getParameter("nickName");
            nickName = stripHTML(nickName);
        } else {
            String errMsg = "Error adding productReview for product " + productId + " with no nickName";
            request.setAttribute("_ERROR_MESSAGE", errMsg);
            return "error";
        }
        String reviewTitle = null;
        if (request.getParameter("reviewTitle") != null && !request.getParameter("reviewTitle").equals(""))
        {
            reviewTitle = request.getParameter("reviewTitle");
            reviewTitle = stripHTML(reviewTitle);
        }
        String productRating = null;
        if (UtilValidate.isNotEmpty(request.getParameter("productRating")))
            productRating = request.getParameter("productRating");
        else 
        {
            /* Do not throw error if a product rating was not given
             * instead default to the highest rating
             */
            /*
             * String errMsg = "Error adding productReview for product " + productId + " with no product rating";
             * request.setAttribute("_ERROR_MESSAGE", errMsg);
             * return "error";
             */
            productRating = "5.0";
        }
        

        String productReview = null;
        if (request.getParameter("productReview") != null)
        {
            productReview = request.getParameter("productReview");
            productReview = stripHTML(productReview);
        } else {
            productReview = null;
        }

        String statusId = "PRR_PENDING";
        if ("Y".equals(productStore.getString("autoApproveReviews")))
        {
            statusId = "PRR_APPROVED";
            
        }

        try
        {
            Map inputMap = new HashMap();
            inputMap.put("productReviewId", delegator.getNextSeqId("ProductReview"));
            inputMap.put("productStoreId", productStoreId);
            inputMap.put("productId", productId);
            inputMap.put("reviewTitle",reviewTitle);
            inputMap.put("reviewNickName", nickName);
            inputMap.put("postedDateTime", UtilDateTime.nowTimestamp());
            inputMap.put("statusId", statusId);
            inputMap.put("productRating", new BigDecimal(Double.parseDouble(productRating)));
            if(productReview != null)
                inputMap.put("productReview", productReview);
            if(UtilValidate.isNotEmpty(userLogin))
            {
                inputMap.put("userLoginId", userLogin.getString("userLoginId"));
                inputMap.put("postedAnonymous", "N");
            }
            else {
                inputMap.put("userLoginId", "anonymous");
                inputMap.put("postedAnonymous", "Y");
            }

            GenericPK entity = delegator.makePK("ProductReview", inputMap);
            GenericValue entityValue = GenericValue.create(entity);
            entityValue.create();
        } catch (Exception e){
            String errMsg = "Error adding productReview for product " + productId + " " + e.toString();
            Debug.logError(errMsg, module);
            request.setAttribute("_ERROR_MESSAGE", errMsg);
            return "error";
        }

        // Calculate average customer rating
        BigDecimal averageRating = org.ofbiz.product.product.ProductWorker.getAverageProductRating(delegator, productId);
        Debug.logInfo("Average product rating is: " + averageRating, module);

        try
        {
            Map inputMap = new HashMap();
            inputMap.put("productId", productId);
            inputMap.put("averageCustomerRating", averageRating);

            GenericPK entity = delegator.makePK("ProductCalculatedInfo", inputMap);
            GenericValue entityValue = GenericValue.create(entity);
            delegator.createOrStore(entityValue);
        } catch (Exception e)
        {
            String errMsg = "Error adding productReviewAverage for product " + productId + " " + e.toString();
            Debug.logError(errMsg, module);
            request.setAttribute("_ERROR_MESSAGE", errMsg);
            return "error";
        }

        return "success";
    }
    
}
