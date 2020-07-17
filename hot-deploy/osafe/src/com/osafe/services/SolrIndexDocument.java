package com.osafe.services;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.beans.Field;

public class SolrIndexDocument {

    @Field
    private String id;
    @Field
    private String rowType;
    @Field
    private String productId;
    @Field
    private String name;
    @Field
    private String internalName;
    @Field
    private String description;
    @Field
    private String sequenceNum;
    @Field
    private String productCategoryId;
    @Field
    private String categoryName;
    @Field
    private String categoryDescription;
    @Field
    private String categoryPdpDescription;
    @Field
    private String categoryImageUrl;
    @Field
    private String productImageSmallUrl;
    @Field
    private String productImageSmallAlt;
    @Field
    private String productImageSmallAltUrl;
    @Field
    private String productImageMediumUrl;
    @Field
    private String productImageLargeUrl;
    @Field
    private String productFeatureGroupId;
    @Field
    private String productFeatureGroupDescription;
    @Field
    private String productFeatureGroupFacetSort;
    @Field
    private String productCategoryFacetGroups;
    @Field
    private Float price;
    @Field
    private Float customerRating;
    @Field
    private String color;
    @Field
    private String size;
    @Field
    private String type;

    public SolrIndexDocument() {
        super();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRowType() {
        return rowType;
    }

    public void setRowType(String rowType) {
        this.rowType = rowType;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInternalName() {
        return internalName;
    }

    public void setInternalName(String internalName) {
        this.internalName = internalName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSequenceNum() {
        return sequenceNum;
    }

    public void setSequenceNum(String sequenceNum) {
        this.sequenceNum = sequenceNum;
    }

    public String getProductCategoryId() {
        if ("productCategory".equals(rowType)) {
            String[] productCategoryIdParts = StringUtils.split(productCategoryId, " ");
            if (productCategoryIdParts.length > 0) {
                return productCategoryIdParts[productCategoryIdParts.length - 1];
            }
        }
        return productCategoryId;
    }

    public void setProductCategoryId(String productCategoryId) {
        this.productCategoryId = productCategoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryDescription() {
        return categoryDescription;
    }

    public void setCategoryDescription(String categoryDescription) {
        this.categoryDescription = productFeatureGroupId;
    }

    public String getCategoryPdpDescription() {
        return categoryPdpDescription;
    }

    public void setcategoryDescription(String categoryPdpDescription) {
        this.categoryPdpDescription = categoryPdpDescription;
    }

    public String getCategoryImageUrl() {
        return categoryImageUrl;
    }

    public void setCategoryImageUrl(String categoryImageUrl) {
        this.categoryImageUrl = categoryImageUrl;
    }

    public String getProductImageSmallUrl() {
        return productImageSmallUrl;
    }

    public void setProductImageSmallAltUrl(String productImageSmallAltUrl) {
        this.productImageSmallAltUrl = productImageSmallAltUrl;
    }

    public String getProductImageSmallAlt() {
    	return productImageSmallAlt;
    }
    
    public void setProductImageSmallAlt(String productImageSmallAlt) {
    	this.productImageSmallAlt=productImageSmallAlt;
    }
    public String getProductImageSmallAltUrl() {
        return productImageSmallAltUrl;
    }

    public void setProductImageSmallUrl(String productImageSmallUrl) {
        this.productImageSmallUrl = productImageSmallUrl;
    }

    public String getProductImageMediumUrl() {
        return productImageMediumUrl;
    }

    public void setProductImageMediumUrl(String productImageMediumUrl) {
        this.productImageMediumUrl = productImageMediumUrl;
    }

    public String getProductImageLargeUrl() {
        return productImageLargeUrl;
    }

    public void setProductImageLargeUrl(String productImageLargeUrl) {
        this.productImageLargeUrl = productImageLargeUrl;
    }

    public String getProductFeatureGroupId() {
        return productFeatureGroupId;
    }

    public void setProductFeatureGroupId(String productFeatureGroupId) {
        this.productFeatureGroupId = productFeatureGroupId;
    }

    public String getProductFeatureGroupDescription() {
        return productFeatureGroupDescription;
    }

    public void setProductFeatureGroupDescription(String productFeatureGroupDescription) {
        this.productFeatureGroupDescription = productFeatureGroupDescription;
    }

    public String getProductFeatureGroupFacetSort() {
        return productFeatureGroupFacetSort;
    }

    public void setProductFeatureGroupFacetSort(String productFeatureGroupFacetSort) {
        this.productFeatureGroupFacetSort = productFeatureGroupFacetSort;
    }

    public String getProductCategoryFacetGroups() {
        return productCategoryFacetGroups;
    }

    public void setProductCategoryFacetGroups(String productCategoryFacetGroups) {
        this.productCategoryFacetGroups = productCategoryFacetGroups;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public Float getCustomerRating() {
        return customerRating;
    }

    public void setCustomerRating(Float customerRating) {
        this.customerRating = customerRating;
    }

    public String getColor() {
        return color;
    }

    @Field("productFeature_COLOR")
    public void setColor(String color) {
        this.color = color;
    }

    public String getSize() {
        return size;
    }

    @Field("productFeature_SIZE")
    public void setSize(String size) {
        this.size = size;
    }

    public String getType() {
        return type;
    }

    @Field("productFeature_TYPE")
    public void setType(String type) {
        this.type = type;
    }

}
