package com.osafe.services;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilNumber;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.order.shoppingcart.ShoppingCart;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.osafe.util.Util;


/**
 * @author rrlakhera
 *
 */
public class StoreCreditServices 
{

    public static final String module = StoreCreditServices.class.getName();
    public static final String paymentMethodTypeId = "FIN_ACCOUNT";
    /**
     * A word on precision: since we're just adding and subtracting, the interim figures should have one more decimal place of precision than the final numbers.
     */
    public static int decimals = UtilNumber.getBigDecimalScale("finaccount.decimals");
    public static int rounding = UtilNumber.getBigDecimalRoundingMode("finaccount.rounding");

    //This method returns the customer's fin account available balance
    public static Map<String, Object> balanceInquire(DispatchContext dctx, Map<String, Object> context)
    {
        Delegator delegator = dctx.getDelegator();
        String productStoreId = (String) context.get("productStoreId");
        String partyId = (String) context.get("partyId");

        String storeCreditMethod = Util.getProductStoreParm(productStoreId, "CHECKOUT_STORE_CREDIT");

        int currencyRounding = rounding;
        String currencyRoundingParam = Util.getProductStoreParm(productStoreId, "CURRENCY_UOM_ROUNDING");
        if (Util.isNumber(currencyRoundingParam))
        {
            currencyRounding = Integer.parseInt(currencyRoundingParam);
        }

        BigDecimal balance = BigDecimal.ZERO;
        Map<String, Object> result = ServiceUtil.returnSuccess();

        try
        {
            if (UtilValidate.isNotEmpty(storeCreditMethod)) 
            {
                //list supported STORE CREDIT METHODS
                if("TEST".equalsIgnoreCase(storeCreditMethod))
                {
                    //test method, 10 whole unit to be available for Store Credit
                    balance = BigDecimal.TEN;
                }
                else if ("TRUE".equalsIgnoreCase(storeCreditMethod))
                {
                    //Get party fin accounts
                    GenericValue finAccount = getPartyFinAccount(delegator, partyId);
                    if (UtilValidate.isNotEmpty(finAccount)) 
                    {
                        balance = finAccount.getBigDecimal("availableBalance");
                    }

                }
                //add code for other methods
            }
        }
        catch (Exception ex)
        {
            Debug.logError("balanceInquire error -> " + ex.getMessage(), module);
        }
        result.put("balance", balance.setScale(decimals, currencyRounding));
        return result;
    }

    //This method creates the store credit payment and set in shopping cart
    public static Map<String, Object> setStoreCreditPayment(DispatchContext dctx, Map<String, Object> context)
    {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        BigDecimal storeCreditAmount = (BigDecimal) context.get("storeCreditAmount");

        ShoppingCart cart = (ShoppingCart) context.get("cart");
        if (cart == null || cart.items().size() <= 0)
        {
            return ServiceUtil.returnError("Shopping cart is empty, cannot proceed with Store Credit Payment");
        }

        int currencyRounding = rounding;
        String productStoreId = cart.getProductStoreId();
        String currencyRoundingParam = Util.getProductStoreParm(productStoreId, "CURRENCY_UOM_ROUNDING");
        if (Util.isNumber(currencyRoundingParam))
        {
            currencyRounding = Integer.parseInt(currencyRoundingParam);
        }

        // Clear the previously added FIN_ACCOUNT payment
        removeStoreCreditPayment(dctx, context);

        String partyId = (String) context.get("partyId");
        if (UtilValidate.isEmpty(partyId))
        {
            partyId = userLogin.getString("partyId");
        }

        String finAccountId = "";
        GenericValue finAccount = getPartyFinAccount(delegator, partyId);
        if (UtilValidate.isNotEmpty(finAccount)) 
        {
            finAccountId = finAccount.getString("finAccountId");
        }


        Timestamp now = UtilDateTime.nowTimestamp();

        List toBeStored = new LinkedList();
        GenericValue newPm = delegator.makeValue("PaymentMethod");
        toBeStored.add(newPm);

        String newPmId = "";
        if (UtilValidate.isEmpty(newPmId))
        {
            try
            {
                newPmId = delegator.getNextSeqId("PaymentMethod");
            }
            catch (IllegalArgumentException e)
            {
                return ServiceUtil.returnError("ERROR: Could not create Store credit payment (id generation failure)");
            }
        }

        newPm.set("partyId", partyId);
        if (UtilValidate.isNotEmpty(finAccountId))
        {
            newPm.set("finAccountId", finAccountId);
        }
        newPm.set("fromDate", (context.get("fromDate") != null ? context.get("fromDate") : now));
        newPm.set("thruDate", context.get("thruDate"));
        newPm.set("description",context.get("description"));

        newPm.set("paymentMethodId", newPmId);
        newPm.set("paymentMethodTypeId", paymentMethodTypeId);

        try
        {
            delegator.storeAll(toBeStored);
        }
        catch (GenericEntityException e)
        {
            Debug.logWarning(e.getMessage(), module);
            return ServiceUtil.returnError("ERROR: Could not create Store credit payment (write failure): " + e.getMessage());
        }

        ShoppingCart.CartPaymentInfo inf = cart.addPaymentAmount(newPmId, storeCreditAmount.setScale(decimals, currencyRounding), true);
        if (UtilValidate.isNotEmpty(finAccountId))
        {
            inf.finAccountId = finAccountId;
        }

        return ServiceUtil.returnSuccess();
    }

    //This method removes the store credit payment from shopping cart
    public static Map<String, Object> removeStoreCreditPayment(DispatchContext dctx, Map<String, Object> context)
    {
        ShoppingCart cart = (ShoppingCart) context.get("cart");
        if (cart == null || cart.items().size() <= 0)
        {
            return ServiceUtil.returnError("Shopping cart is empty, cannot proceed remove Store Credit Payment");
        }

        // Clear the previously added FIN_ACCOUNT payment

        List<ShoppingCart.CartPaymentInfo> paymentInfos = cart.getPaymentInfos(true, true, true);
        int index = 0;
        for (ShoppingCart.CartPaymentInfo paymentInfo : paymentInfos)
        {
            if (paymentInfo.paymentMethodTypeId.equals(paymentMethodTypeId))
            {
                cart.clearPayment(index);
            }
            index++;
        }

        return ServiceUtil.returnSuccess();
    }

    //This method returns the total of store credit payment from shopping cart
    public static Map<String, Object> getStoreCreditPaymentTotal(DispatchContext dctx, Map<String, Object> context)
    {
        BigDecimal total = BigDecimal.ZERO;
        ShoppingCart cart = (ShoppingCart) context.get("cart");
        if (cart == null || cart.items().size() <= 0)
        {
            return ServiceUtil.returnError("Shopping cart is empty, cannot proceed ge Store Credit Payment total");
        }

        List<ShoppingCart.CartPaymentInfo> paymentInfos = cart.getPaymentInfos(true, true, true);
        for (ShoppingCart.CartPaymentInfo paymentInfo : paymentInfos)
        {
            if (paymentInfo.paymentMethodTypeId.equals(paymentMethodTypeId) && UtilValidate.isNotEmpty(paymentInfo.amount))
            {
                total = paymentInfo.amount;
            }
        }

        Map<String, Object> result = ServiceUtil.returnSuccess();
        result.put("total", total.setScale(decimals, rounding));
        return result;
    }

    private static GenericValue getPartyFinAccount(Delegator delegator, String partyId)
    {
        GenericValue gv = null;
        try
        {
            List<GenericValue> finAccounts = delegator.findByAndCache("FinAccountAndRole", UtilMisc.toMap("partyId", partyId, "roleTypeId", "OWNER", "statusId", "FNACT_ACTIVE"));
            finAccounts = EntityUtil.filterByDate(finAccounts);
            gv =  EntityUtil.getFirst(finAccounts);
        }
        catch (Exception ex)
        {
            Debug.logError("ERROR: Could not find party fin account: " + ex.getMessage(), module);
        }
        return gv;
    }

}
