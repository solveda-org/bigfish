package admin;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.*
import java.util.List;
import java.util.Map;

PaymentGatewayConfig = delegator.findByAnd("PaymentGatewayConfig",UtilMisc.toMap());
context.resultList = PaymentGatewayConfig;
