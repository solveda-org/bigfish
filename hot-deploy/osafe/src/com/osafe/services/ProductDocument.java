package com.osafe.services;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.beans.Field;

import javolution.util.FastList;
import javolution.util.FastMap;

public class ProductDocument implements Serializable {

    private String id;
    private String productId;
    private String name;
    private String internalName;
    private String description;
    private String sequenceNum;
    private String rowType;
    private String productCategoryId;
    private String topMostProductCategoryId;
    private int categoryLevel;
    private String categoryName;
    private String categoryDescription;
    private String categoryPdpDescription;
    private String categoryImageUrl;
    private String productImageSmallUrl;
    private String productImageSmallAlt;
    private String productImageSmallAltUrl;
    private String productImageMediumUrl;
    private String productImageLargeUrl;

    private String productFeatureGroupId;
    private String productFeatureGroupDescription;
    private Long productFeatureGroupFacetValueMin;
    private Long productFeatureGroupFacetValueMax;
    private Long totalTimesViewed;
    private String productCategoryFacetGroups;

    private BigDecimal listPrice;
    private BigDecimal price;
    private BigDecimal recurrencePrice;
    private BigDecimal customerRating;
    private Double totalQuantityOrdered;
	private List<String> productFeatureTypes;
    private Map<String, List<String>> productFeatures;

    private String productFacilityIds;
    private String introductionDate;
    private String salesDiscontinuationDate;
    private int salesDiscontinuationDateNullFlag;
    private String manufacturerName;
    private String manufacturerIdNo;

    public ProductDocument() {
        super();
        productFeatureTypes = FastList.newInstance();
        productFeatures = FastMap.newInstance();
    }

    public Long getTotalTimesViewed() {
		return totalTimesViewed;
	}

	public void setTotalTimesViewed(Long totalTimesViewed) {
		this.totalTimesViewed = totalTimesViewed;
	}


	public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getTopMostProductCategoryId() {
        return topMostProductCategoryId;
    }

    public void setTopMostProductCategoryId(String topMostProductCategoryId) {
        this.topMostProductCategoryId = topMostProductCategoryId;
    }

    public int getCategoryLevel() {
        return categoryLevel;
    }

    public void setCategoryLevel(int categoryLevel) {
        this.categoryLevel = categoryLevel;
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
        this.categoryDescription = categoryDescription;
    }

    public String getCategoryPdpDescription() {
        return categoryPdpDescription;
    }

    public void setCategoryPdpDescription(String categoryPdpDescription) {
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

    public void setProductImageSmallUrl(String productImageSmallUrl) {
        this.productImageSmallUrl = productImageSmallUrl;
    }

    public String getProductImageSmallAlt() {
        return productImageSmallAlt;
    }

    public void setProductImageSmallAlt(String productImageSmallAlt) {
        this.productImageSmallAlt = productImageSmallAlt;
    }

    public String getProductImageSmallAltUrl() {
        return productImageSmallAltUrl;
    }

    public void setProductImageSmallAltUrl(String productImageSmallAltUrl) {
        this.productImageSmallAltUrl = productImageSmallAltUrl;
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

    public Long getProductFeatureGroupFacetValueMax() {
        return productFeatureGroupFacetValueMax;
    }

    public void setProductFeatureGroupFacetValueMax(Long productFeatureGroupFacetValueMax) {
        this.productFeatureGroupFacetValueMax = productFeatureGroupFacetValueMax;
    }
    
    public Long getProductFeatureGroupFacetValueMin() {
        return productFeatureGroupFacetValueMin;
    }

    public void setProductFeatureGroupFacetValueMin(Long productFeatureGroupFacetValueMin) {
        this.productFeatureGroupFacetValueMin = productFeatureGroupFacetValueMin;
    }
    
    public String getProductCategoryFacetGroups() {
        return productCategoryFacetGroups;
    }

    public void setProductCategoryFacetGroups(String productCategoryFacetGroups) {
        this.productCategoryFacetGroups = productCategoryFacetGroups;
    }

    public BigDecimal getListPrice() {
        return listPrice;
    }

    public void setListPrice(BigDecimal listPrice) {
        this.listPrice = listPrice;
    }
    
    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getRecurrencePrice() {
        return recurrencePrice;
    }

    public void setRecurrencePrice(BigDecimal recurrencePrice) {
        this.recurrencePrice = recurrencePrice;
    }
    public BigDecimal getCustomerRating() {
        return customerRating;
    }

    public void setCustomerRating(BigDecimal customerRating) {
        this.customerRating = customerRating;
    }

    public List<String> getProductFeatureTypes() {
        return productFeatureTypes;
    }

    public void setProductFeatureTypes(List<String> productFeatureTypes) {
        this.productFeatureTypes = productFeatureTypes;
    }

    public void addProductFeature(String productFeatureType, List<String> featureValues) {
        productFeatures.put(productFeatureType, featureValues);
    }

    public Object getProductFeature() {
        return productFeatures;
    }

    public String getRowType() {
        return rowType;
    }

    public void setRowType(String rowType) {
        this.rowType = rowType;
    }


	public Double getTotalQuantityOrdered() {
		return totalQuantityOrdered;
	}

	public void setTotalQuantityOrdered(Double totalQuantityOrdered) {
		this.totalQuantityOrdered = totalQuantityOrdered;
	}
    
    public String getProductFacilityIds() {
        return productFacilityIds;
    }

    public void setProductFacilityIds(String productFacilityIds) {
        this.productFacilityIds = productFacilityIds;
    }
    
    public String getIntroductionDate() {
        return introductionDate;
    }

    public void setIntroductionDate(String introductionDate) {
        this.introductionDate = introductionDate;
    }
    
    public String getSalesDiscontinuationDate() {
        return salesDiscontinuationDate;
    }

    public void setSalesDiscontinuationDate(String salesDiscontinuationDate) {
        this.salesDiscontinuationDate = salesDiscontinuationDate;
    }
    
    public int getSalesDiscontinuationDateNullFlag() {
        return salesDiscontinuationDateNullFlag;
    }

    public void setSalesDiscontinuationDateNullFlag(int salesDiscontinuationDateNullFlag) {
        this.salesDiscontinuationDateNullFlag = salesDiscontinuationDateNullFlag;
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
}
