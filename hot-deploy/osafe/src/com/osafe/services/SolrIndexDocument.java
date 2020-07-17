package com.osafe.services;

import java.util.Date;

import org.apache.solr.client.solrj.beans.Field;
import org.apache.solr.common.SolrDocument;

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
    private Float listPrice;
    @Field
    private Float price;
    @Field
    private Float recurrencePrice;
    @Field
    private Float customerRating;
    @Field
    private String manufacturerName;
    @Field
    private String manufacturerIdNo;
    @Field
    private Date salesDiscontinuationDate;
    @Field
    private int salesDiscontinuationDateNullFlag;
    @Field
    private Date introductionDate;

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

    public Float getListPrice() {
        return listPrice;
    }

    public void setListPrice(Float listPrice) {
        this.listPrice = listPrice;
    }
    
    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public Float getRecurrencePrice() {
        return recurrencePrice;
    }

    public void setRecurrencePrice(Float recurrencePrice) {
        this.recurrencePrice = recurrencePrice;
    }

    public Float getCustomerRating() {
        return customerRating;
    }

    public void setCustomerRating(Float customerRating) {
        this.customerRating = customerRating;
    }

	public void setManufacturerName(String manufacturerName) {
		this.manufacturerName = manufacturerName;
	}

	public String getManufacturerName() {
		return manufacturerName;
	}

	public void setManufacturerIdNo(String manufacturerIdNo) {
		this.manufacturerIdNo = manufacturerIdNo;
	}

	public String getManufacturerIdNo() {
		return manufacturerIdNo;
	}
	
	public Date getSalesDiscontinuationDate() {
        return salesDiscontinuationDate;
    }

    public void setSalesDiscontinuationDate(Date salesDiscontinuationDate) {
        this.salesDiscontinuationDate = salesDiscontinuationDate;
    }
    public int getSalesDiscontinuationDateNullFlag() {
        return salesDiscontinuationDateNullFlag;
    }

    public void setSalesDiscontinuationDateNullFlag(int salesDiscontinuationDateNullFlag) {
        this.salesDiscontinuationDateNullFlag = salesDiscontinuationDateNullFlag;
    }
    public Date getIntroductionDate() {
        return introductionDate;
    }

    public void setIntroductionDate(Date introductionDate) {
        this.introductionDate = introductionDate;
    }
}
