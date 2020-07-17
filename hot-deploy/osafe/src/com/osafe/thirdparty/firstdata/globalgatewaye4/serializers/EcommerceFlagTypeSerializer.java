package com.osafe.thirdparty.firstdata.globalgatewaye4.serializers;

import java.lang.reflect.Type;

import com.osafe.thirdparty.firstdata.globalgatewaye4.EcommerceFlagType;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * @author Steve Copous <steve.copous@firstdata.com>
 *
 */
public class EcommerceFlagTypeSerializer implements JsonSerializer<EcommerceFlagType> {
	/* (non-Javadoc)
	 * @see com.google.gson.JsonSerializer#serialize(java.lang.Object, java.lang.reflect.Type, com.google.gson.JsonSerializationContext)
	 */
	public JsonElement serialize(EcommerceFlagType src, Type typeOfSrc, JsonSerializationContext context) {
		return new JsonPrimitive(src.toString());
	}
}