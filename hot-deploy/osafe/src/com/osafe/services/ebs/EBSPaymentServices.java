package com.osafe.services.ebs;

import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ModelService;

/**
 * The Class EBSPaymentServices.
 */
public class EBSPaymentServices {
	
	/**
	 * Ebs auth processor.
	 *
	 * @param dctx the dctx
	 * @param context the context
	 * @return the map
	 */
	public static Map<String, Object> ebsAuthProcessor(DispatchContext dctx,
			Map<String, ? extends Object> context) {

		Map<String, Object> result = FastMap.newInstance();
		result.put("finished", true);
		result.put("authResult", true);
		result.put("serviceTypeEnum", "PRDS_PAY_AUTH");
		result.put("authRefNum", "1234");//GA: TODO need to be changed to real time value
		result.put("processAmount", context.get("processAmount"));

		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}

}
