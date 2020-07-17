package com.osafe.solr;

import org.apache.commons.lang.StringUtils;

public class ProductFeatureEncoder {

    public static String decode(String key) {
        return key;
    }

    public static String decodePlus(String key) {
        return StringUtils.replace(key, "_", " ");
    }

}
