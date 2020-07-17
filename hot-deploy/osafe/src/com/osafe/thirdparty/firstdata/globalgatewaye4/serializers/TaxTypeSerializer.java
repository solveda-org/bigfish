package com.osafe.thirdparty.firstdata.globalgatewaye4.serializers;

import java.lang.reflect.Type;

import com.osafe.thirdparty.firstdata.globalgatewaye4.TaxType;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * @author Steve Copous <steve.copous@firstdata.com>
 *
 */
public class TaxTypeSerializer implements JsonSerializer<TaxType>, JsonDeserializer<TaxType> {
	/* (non-Javadoc)
	 * @see com.google.gson.JsonSerializer#serialize(java.lang.Object, java.lang.reflect.Type, com.google.gson.JsonSerializationContext)
	 */
	public JsonElement serialize(TaxType src, Type typeOfSrc, JsonSerializationContext context) {
		return new JsonPrimitive(src.toString());
	}

	public TaxType deserialize(JsonElement json, Type typeOfT,
			JsonDeserializationContext context) throws JsonParseException {
		switch(json.getAsInt()) {
		case(0):
			return TaxType.UnknownTax;
		case(1):
			return TaxType.FederalNationalSalesTax;
		case(2):
			return TaxType.StateSalesTax;
		case(3):
			return TaxType.CitySalesTax;
		case(4):
			return TaxType.LocalSalesTax;
		case(5):
			return TaxType.MunicipalSalesTax;
		case(6):
			return TaxType.OtherTax;
		case(10):
			return TaxType.VAT;
		case(11):
			return TaxType.GST;
		case(12):
			return TaxType.PST;
		case(13):
			return TaxType.HST;
		case(14):
			return TaxType.QST;
		case(20):
			return TaxType.RoomTax;
		case(21):
			return TaxType.OccupancyTax;
		case(22):
			return TaxType.EnergyTax;
		default:
			return TaxType.UnknownTax;
		}
	}
}
