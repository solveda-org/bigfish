package com.osafe.thirdparty.firstdata.globalgatewaye4.serializers;

import java.lang.reflect.Type;

import com.osafe.thirdparty.firstdata.globalgatewaye4.ReleaseType;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class ReleaseTypeSerializer implements JsonSerializer<ReleaseType> {

	public JsonElement serialize(ReleaseType src, Type typeOfSrc, JsonSerializationContext context) {
		return new JsonPrimitive(src.toString());
	}
}
