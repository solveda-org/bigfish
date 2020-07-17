package com.osafe.thirdparty.firstdata.globalgatewaye4.serializers;

import java.lang.reflect.Type;

import com.osafe.thirdparty.firstdata.globalgatewaye4.TransactionType;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * @author Steve Copous <steve.copous@firstdata.com>
 *
 */
public class TransactionTypeSerializer implements JsonSerializer<TransactionType> {
	/* (non-Javadoc)
	 * @see com.google.gson.JsonSerializer#serialize(java.lang.Object, java.lang.reflect.Type, com.google.gson.JsonSerializationContext)
	 */
	public JsonElement serialize(TransactionType src, Type typeOfSrc, JsonSerializationContext context) {
		return new JsonPrimitive(src.toString());
	}
}