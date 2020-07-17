package com.osafe.thirdparty.firstdata.globalgatewaye4.serializers;

import java.lang.reflect.Type;

import com.google.gson.*;

/**
 * @author Steve Copous <steve.copous@firstdata.com>
 *
 */
public class BooleanTypeAdapter implements JsonDeserializer<Boolean> {

	/* (non-Javadoc)
	 * @see com.google.gson.JsonDeserializer#deserialize(com.google.gson.JsonElement, java.lang.reflect.Type, com.google.gson.JsonDeserializationContext)
	 */
	public Boolean deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) 
			throws JsonParseException {
        if(json.getAsString().toLowerCase().equals("false")) { return false; }
        if(json.getAsString().toLowerCase().equals("true")) { return true; }
        
		int code = json.getAsInt();
        return code == 0 ? false :
               code == 1 ? true :
               null;
	}
}
