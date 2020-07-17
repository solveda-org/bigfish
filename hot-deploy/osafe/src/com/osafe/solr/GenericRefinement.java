package com.osafe.solr;

import java.io.Serializable;
import java.util.Collection;

public class GenericRefinement implements Serializable{

    private String type;
    private String name;
    private String productFeatureTypeId;
    private String productFeatureGroupId;
    private String productFeatureGroupFacetSort;
    private Collection refinementValues;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Collection getRefinementValues() {
        return refinementValues;
    }

    public void setRefinementValues(Collection refinementValues) {
        this.refinementValues = refinementValues;
    }

    public String getProductFeatureTypeId() {
        return productFeatureTypeId;
    }

    public void setProductFeatureTypeId(String productFeatureTypeId) {
        this.productFeatureTypeId = productFeatureTypeId;
    }

    public String getProductFeatureGroupId() {
        return productFeatureGroupId;
    }

    public void setProductFeatureGroupId(String productFeatureGroupId) {
        this.productFeatureGroupId = productFeatureGroupId;
    }

    public String getProductFeatureGroupFacetSort() {
        return productFeatureGroupFacetSort;
    }

    public void setProductFeatureGroupFacetSort(String productFeatureGroupFacetSort) {
        this.productFeatureGroupFacetSort = productFeatureGroupFacetSort;
    }

}
