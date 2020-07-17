package com.osafe.services;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.util.CSVContext;

public class ProductFeatureCellProcessor extends CellProcessorAdaptor {
    private String productFeatureType = null;

    public ProductFeatureCellProcessor(final String productFeatureType) {
        super();
        this.productFeatureType = productFeatureType;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("rawtypes")
    @Override
    public Object execute(final Object value, final CSVContext context) {
        @SuppressWarnings("unchecked")
        Map<String,List<String>> myMap = (Map<String,List<String>>) value;
        if (myMap.containsKey(this.productFeatureType)) {
            return StringUtils.join((List)((Map) value).get(this.productFeatureType)," ");
        } else {
            return "";
        }
    }
}
