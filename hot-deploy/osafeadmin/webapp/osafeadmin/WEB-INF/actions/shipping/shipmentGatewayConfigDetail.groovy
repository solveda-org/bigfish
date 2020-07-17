package shipping;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilMisc;
shipmentGatewayConfigId=parameters.shipmentGatewayConfigId
if (UtilValidate.isNotEmpty(shipmentGatewayConfigId))
{
   context.shipmentGatewayDhl=delegator.findOne("ShipmentGatewayDhl", UtilMisc.toMap("shipmentGatewayConfigId", shipmentGatewayConfigId), false);
   context.shipmentGatewayFedex=delegator.findOne("ShipmentGatewayFedex", UtilMisc.toMap("shipmentGatewayConfigId", shipmentGatewayConfigId), false);
   context.shipmentGatewayUsps=delegator.findOne("ShipmentGatewayUsps", UtilMisc.toMap("shipmentGatewayConfigId", shipmentGatewayConfigId), false);
   context.shipmentGatewayUps=delegator.findOne("ShipmentGatewayUps", UtilMisc.toMap("shipmentGatewayConfigId", shipmentGatewayConfigId), false);
   context.shipmentGatewayBlueDart=delegator.findOne("ShipmentGatewayBlueDart", UtilMisc.toMap("shipmentGatewayConfigId", shipmentGatewayConfigId), false);
}
