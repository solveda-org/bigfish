package order;

import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.order.order.OrderReadHelper;
import org.ofbiz.party.contact.ContactHelper;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.party.contact.ContactMechWorker;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.string.FlexibleStringExpander;
import org.ofbiz.entity.GenericValue;
import javolution.util.FastList;
import javolution.util.FastMap;
import java.math.BigDecimal;
import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.base.util.UtilNumber;
import org.ofbiz.accounting.payment.PaymentWorker;

userLogin = session.getAttribute("userLogin");
orderId = StringUtils.trimToEmpty(parameters.orderId);
context.orderId = orderId;

orderHeader = null;
partyId = null;
orderReadHelper = null;

if (UtilValidate.isNotEmpty(orderId)) 
{
	orderHeader = delegator.findByPrimaryKey("OrderHeader", [orderId : orderId]);
	if (UtilValidate.isNotEmpty(orderHeader)) 
	{
		orderProductStore = orderHeader.getRelatedOne("ProductStore");
		if (UtilValidate.isNotEmpty(orderProductStore.storeName))
		{
			productStoreName = orderProductStore.storeName;
		}
		else
		{
			productStoreName = orderHeader.productStoreId;
		}
		context.productStoreName = productStoreName;
		
	    // note these are overridden in the OrderViewWebSecure.groovy script if run
	    context.hasPermission = true;
	    context.canViewInternalDetails = true;
	
	    orderReadHelper = new OrderReadHelper(orderHeader);
	    
		// get the order type
		orderType = orderHeader.orderTypeId;
		context.orderType = orderType;
		
	    // get the display party
	    displayParty = null;
	    if ("PURCHASE_ORDER".equals(orderType)) 
	    {
	        displayParty = orderReadHelper.getSupplierAgent();
	    } 
	    else 
	    {
	        displayParty = orderReadHelper.getPlacingParty();
	    }
	    if (UtilValidate.isNotEmpty(displayParty)) 
	    {
	        partyId = displayParty.partyId;
	        
	        context.partyId = partyId;
			if(UtilValidate.isNotEmpty(partyId))
			{
				party = delegator.findByPrimaryKey("Party", [partyId : partyId]);
				if(UtilValidate.isNotEmpty(party))
				{
					partyRoles = party.getRelated("PartyRole");
		
					//Get PARTY BILLING,SHIPPING,PRIMARY EMAIL, TELEPHONE LOCATIONS
					partyContactMechPurpose = party.getRelated("PartyContactMechPurpose");
					partyContactMechPurpose = EntityUtil.filterByDate(partyContactMechPurpose,true);
		
					partyBillingLocation = "";
					partyBillingLocations = EntityUtil.filterByAnd(partyContactMechPurpose, UtilMisc.toMap("contactMechPurposeTypeId", "BILLING_LOCATION"));
					partyBillingLocations = EntityUtil.getRelated("PartyContactMech", partyBillingLocations);
					partyBillingLocations = EntityUtil.filterByDate(partyBillingLocations,true);
					partyBillingLocations = EntityUtil.orderBy(partyBillingLocations, UtilMisc.toList("fromDate DESC"));
					if (UtilValidate.isNotEmpty(partyBillingLocations))
					{
						partyBillingLocation = EntityUtil.getFirst(partyBillingLocations);
						context.billingContactMechId = partyBillingLocation.contactMechId;
					}
					
					person = party.getRelated("Person");
					if (UtilValidate.isNotEmpty(person))
					{
						context.billingFirstName = person.firstName?person.firstName:"";
						context.billingLastName = person.lastName?person.lastName:"";
						
					}
				}
				
				//Get PARTY PAYMENT METHODS
				paymentMethodValueMaps = PaymentWorker.getPartyPaymentMethodValueMaps(delegator, partyId, false);
				context.paymentMethodValueMaps = paymentMethodValueMaps;
			}
	    }
		
		orderItems = orderReadHelper.getOrderItems();
		orderAdjustments = orderReadHelper.getAdjustments();
		orderOpenAmount = orderReadHelper.getOrderGrandTotal(orderItems, orderAdjustments);
		orderPaymentPrefList = orderReadHelper.getPaymentPreferences();
		orderPaymentList = FastList.newInstance();
		if (UtilValidate.isNotEmpty(orderPaymentPrefList))
		{
			if (UtilValidate.isNotEmpty(orderPaymentPrefList))
			{
				for (GenericValue orderPaymentPref : orderPaymentPrefList)
				{
					if((orderPaymentPref.statusId).equals("PAYMENT_SETTLED"))
					{
						orderOpenAmount = orderOpenAmount - orderPaymentPref.maxAmount;
					}
					if((orderPaymentPref.statusId).equals("PAYMENT_NOT_RECEIVED"))
					{
						orderPaymentList = orderPaymentPref.getRelated("Payment");
						if (UtilValidate.isNotEmpty(orderPaymentList))
						{
							for (GenericValue orderPayment : orderPaymentList)
							{
								if ((orderPayment.statusId).equals("PMNT_RECEIVED"))
								{
									orderOpenAmount = orderOpenAmount - orderPayment.amount;
								}
							}
						}
					}
				}
			}
		}
		context.orderOpenAmount = orderOpenAmount;
		
		notes = orderHeader.getRelatedOrderBy("OrderHeaderNoteView", ["-noteDateTime"]);
		context.notesCount = notes.size();
	}
}

context.orderHeader = orderHeader;
context.orderReadHelper = orderReadHelper;

