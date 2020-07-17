package com.osafe.constants;


public class Constants {

    public static final String IMPOERT_XLS_ENTITY_PROPERTY_MAPPING_PREFIX = "import.xls.";
    public static final String DUMP_XML_DIRECTORY_PREFIX = "dumpxml_";
    public static final String DONE_XML_DIRECTORY_PREFIX = "donexml_";
    public static final String[] IMPORT_REMOVE_ENTITIES = {"AgreementProductAppl", "CommunicationEventProduct", "FixedAssetProduct",
                                                           "GoodIdentification", "InventoryItemTempRes", "MrpEvent",
                                                           "OrderSummaryEntry","ShoppingListItem","ShoppingList","OrderAdjustmentBilling",
                                                           "OrderItemBilling", "ShipmentItemBilling", "InvoiceRole",
                                                           "InvoiceContactMech", "InvoiceStatus", "PaymentApplication",
                                                           "ReturnItemBilling", "InvoiceItem", "Invoice",
                                                           "InventoryItemDetail", "ShipmentReceipt", "ReturnItemShipment", "OrderItemShipGrpInvRes", "InventoryItem",
                                                           "ReturnItem", "ReturnStatus", "ReturnAdjustment", "ReturnItemResponse",
                                                           "OrderShipment", "ItemIssuance", "ShipmentPackageContent",
                                                           "ShipmentPackageRouteSeg", "ShipmentRouteSegment", "ShipmentPackage",
                                                           "ShipmentStatus", "ShipmentItem", "Shipment", "ReturnHeader",
                                                           "OrderContent", "OrderHeaderNote", "OrderNotification", "OrderItemChange","OrderItemAssoc",
                                                           "OrderItemShipGroupAssoc","OrderItemAttribute","OrderItemRole","OrderItemPriceInfo","OrderItem","OrderItemShipGroup",
                                                           "OrderAdjustmentTypeAttr","OrderAdjustmentAttribute","OrderAdjustment","OrderContactMech","OrderAttribute",
                                                           "FinAccountTrans","FinAccountAuth","FinAccountStatus","FinAccountRole",
                                                           "Payment", "PaymentGatewayRespMsg", "PaymentGatewayResponse", "PaymentApplication","CommunicationEventOrder",
                                                           "OrderPaymentPreference","FinAccount","OrderRole","OrderStatus","OrderProductPromoCode","ProductPromoUse","OrderHeader",
                                                           "ProductAttribute", "ProductCalculatedInfo",
                                                           "ProductAverageCost", "ProductConfig", "ProductConfigProduct",
                                                           "ProductConfigStats", "ProductCostComponentCalc", "ProductFacilityLocation", "ProductFacility",
                                                           "ProductFeatureApplAttr", "ProductGeo", "ProductGlAccount",
                                                           "ProductMaint", "ProductManufacturingRule", "ProductMeter",
                                                           "ProductOrderItem", "ProductPaymentMethodType", "ProductRole",
                                                           "ProductPromoProduct", "ProductSubscriptionResource", "SupplierProduct",
                                                           "VendorProduct", "WorkEffortGoodStandard", "ProductFeatureIactn",
                                                           "SupplierProductFeature", "ProductCategoryContent", 
                                                           "ProductKeyword", "ProductAssoc", "ProductContent", "ProductReview",
                                                           "ProductPrice", "ProductFeatureGroupAppl", "ProductFeatureCategoryAppl", 
                                                           "ProductFeatureCatGrpAppl", "ProductFeatureGroup", "ProductFeatureAppl",
                                                           "ProductFeatureDataResource", "ProductFeature", "ProductFeatureCategory", "ProductPromoCategory", "ProductCategoryMember", 
                                                           "ProductCategoryRollup","CartAbandonedLine","Product", "ProductCategory"};

    public static final String CATEGORY_ID_DATA_KEY = "productCategoryId";
    public static final String CATEGORY_PARENT_DATA_KEY = "parentCategoryId";
    public static final String CATEGORY_NAME_DATA_KEY = "categoryName";
    public static final String CATEGORY_DESC_DATA_KEY = "description";
    public static final String CATEGORY_LONG_DESC_DATA_KEY = "longDescription";
    public static final String CATEGORY_PLP_IMG_NAME_DATA_KEY = "plpImageName";
    public static final String CATEGORY_PLP_TEXT_DATA_KEY = "plpText";
    public static final String CATEGORY_PDP_TEXT_DATA_KEY = "pdpText";
    public static final String CATEGORY_FROM_DATE_DATA_KEY = "catFromDate";
    public static final String CATEGORY_THRU_DATE_DATA_KEY = "catThruDate";

    public static final String PRODUCT_MASTER_ID_DATA_KEY = "masterProductId";
    public static final String PRODUCT_IS_SELLABLE_DATA_KEY = "isSellable";
    public static final String PRODUCT_ID_DATA_KEY = "productId";
    public static final String PRODUCT_CAT_ID_DATA_KEY = "category_${count}_Id";
    public static final String PRODUCT_CAT_SEQ_NUM_DATA_KEY = "category_${count}_sequenceNum";
    public static final String PRODUCT_CAT_FROM_DATE_DATA_KEY = "category_${count}_fromDate";
    public static final String PRODUCT_CAT_THRU_DATE_DATA_KEY = "category_${count}_thruDate";
    public static final String PRODUCT_CAT_COUNT_DATA_KEY = "productCategoryCount";
    public static final String PRODUCT_INTERNAL_NAME_DATA_KEY = "internalName";
    public static final String PRODUCT_WIDTH_DATA_KEY = "productWidth";
    public static final String PRODUCT_HEIGHT_DATA_KEY = "productHeight";
    public static final String PRODUCT_DEPTH_DATA_KEY = "productDepth";
    public static final String PRODUCT_WEIGHT_DATA_KEY = "productWeight";
    public static final String PRODUCT_RETURN_ABLE_DATA_KEY = "returnable";
    public static final String PRODUCT_CHARGE_SHIP_DATA_KEY = "chargeShipping";
    public static final String PRODUCT_TAX_ABLE_DATA_KEY = "taxable";
    public static final String PRODUCT_INTRO_DATE_DATA_KEY = "introDate";
    public static final String PRODUCT_DISCO_DATE_DATA_KEY = "discoDate";
    public static final String PRODUCT_MANUFACT_PARTY_ID_DATA_KEY = "manufacturerId";
    public static final String PRODUCT_NAME_DATA_KEY = "productName";
    public static final String PRODUCT_SALES_PITCH_DATA_KEY = "salesPitch";
    public static final String PRODUCT_LONG_DESC_DATA_KEY = "longDescription";
    public static final String PRODUCT_SPCL_INS_DATA_KEY = "specialInstructions";
    public static final String PRODUCT_DELIVERY_INFO_DATA_KEY = "deliveryInfo";
    public static final String PRODUCT_DIRECTIONS_DATA_KEY = "directions";
    public static final String PRODUCT_TERMS_COND_DATA_KEY = "termsConditions";
    public static final String PRODUCT_INGREDIENTS_DATA_KEY = "ingredients";
    public static final String PRODUCT_WARNING_DATA_KEY = "warnings";
    public static final String PRODUCT_PLP_LABEL_DATA_KEY = "plpLabel";
    public static final String PRODUCT_PDP_LABEL_DATA_KEY = "pdpLabel";
    public static final String PRODUCT_LIST_PRICE_DATA_KEY = "listPrice";
    public static final String PRODUCT_LIST_PRICE_CUR_DATA_KEY = "listPriceCurrency";
    public static final String PRODUCT_LIST_PRICE_FROM_DATA_KEY = "listPriceFromDate";
    public static final String PRODUCT_LIST_PRICE_THRU_DATA_KEY = "listPriceThruDate";
    public static final String PRODUCT_DEFAULT_PRICE_DATA_KEY = "defaultPrice";
    public static final String PRODUCT_DEFAULT_PRICE_CUR_DATA_KEY = "defaultPriceCurrency";
    public static final String PRODUCT_DEFAULT_PRICE_FROM_DATA_KEY = "defaultPriceFromDate";
    public static final String PRODUCT_DEFAULT_PRICE_THRU_DATA_KEY = "defaultPriceThruDate";
    public static final String PRODUCT_RECURRING_PRICE_DATA_KEY = "recurringPrice";
    public static final String PRODUCT_RECURRING_PRICE_CUR_DATA_KEY = "recurringPriceCurrency";
    public static final String PRODUCT_RECURRING_PRICE_FROM_DATA_KEY = "recurringPriceFromDate";
    public static final String PRODUCT_RECURRING_PRICE_THRU_DATA_KEY = "recurringPriceThruDate";
    public static final String PRODUCT_SLCT_FEAT_TYPE_ID_DATA_KEY = "selectFeature_${count}_type_Id";
    public static final String PRODUCT_SLCT_FEAT_TYPE_DESC_DATA_KEY = "selectFeature_${count}_type_Desc";
    public static final String PRODUCT_SLCT_FEAT_DESC_DATA_KEY = "selectFeature_${count}_description";
    public static final String PRODUCT_SLCT_FEAT_SEQ_NUM_DATA_KEY = "selectFeature_${count}_sequenceNum";
    public static final String PRODUCT_SLCT_FEAT_FROM_DATA_KEY = "selectFeature_${count}_fromDate";
    public static final String PRODUCT_SLCT_FEAT_THRU_DATA_KEY = "selectFeature_${count}_thruDate";
    public static final String PRODUCT_DESC_FEAT_TYPE_ID_DATA_KEY = "descriptiveFeature_${count}_type_Id";
    public static final String PRODUCT_DESC_FEAT_TYPE_DESC_DATA_KEY = "descriptiveFeature_${count}_type_desc";
    public static final String PRODUCT_DESC_FEAT_DESC_DATA_KEY = "descriptiveFeature_${count}_description";
    public static final String PRODUCT_DESC_FEAT_SEQ_NUM_DATA_KEY = "descriptiveFeature_${count}_sequenceNum";
    public static final String PRODUCT_DESC_FEAT_FROM_DATA_KEY = "descriptiveFeature_${count}_fromDate";
    public static final String PRODUCT_DESC_FEAT_THRU_DATA_KEY = "descriptiveFeature_${count}_thruDate";
    public static final String PRODUCT_SKU_DATA_KEY = "goodIdentificationSkuId";
    public static final String PRODUCT_GOOGLE_ID_DATA_KEY = "goodIdentificationGoogleId";
    public static final String PRODUCT_ISBN_DATA_KEY = "goodIdentificationIsbnId";
    public static final String PRODUCT_MANUFACTURER_ID_NO_DATA_KEY = "goodIdentificationManufacturerId";
    public static final String PRODUCT_AVERAGE_RATING_DATA_KEY = "productAverageRating";
    public static final String PRODUCT_REVIEW_SIZE_DATA_KEY = "productReviewSize";
    public static final String PRODUCT_BF_INVENTORY_TOT_DATA_KEY = "bfInventoryTot";
    public static final String PRODUCT_BF_INVENTORY_WHS_DATA_KEY = "bfInventoryWhs";
    public static final String PRODUCT_MULTI_VARIANT_DATA_KEY = "multiVariant";
    public static final String PRODUCT_GIFT_MESSAGE_DATA_KEY = "giftMessage";
    public static final String PRODUCT_QTY_MIN_DATA_KEY = "pdpQtyMin";
    public static final String PRODUCT_QTY_MAX_DATA_KEY = "pdpQtyMax";
    public static final String PRODUCT_QTY_DEFAULT_DATA_KEY = "pdpQtyDefault";
    public static final String PRODUCT_IN_STORE_ONLY_DATA_KEY = "pdpInStoreOnly";
    public static final String PRODUCT_PLP_SWATCH_IMG_DATA_KEY = "plpSwatchImage";
    public static final String PRODUCT_PLP_SWATCH_IMG_THRU_DATA_KEY = "plpSwatchImageThruDate";
    public static final String PRODUCT_PDP_SWATCH_IMG_DATA_KEY = "pdpSwatchImage";
    public static final String PRODUCT_PDP_SWATCH_IMG_THRU_DATA_KEY = "pdpSwatchImageThruDate";
    public static final String PRODUCT_SMALL_IMG_DATA_KEY = "smallImage";
    public static final String PRODUCT_SMALL_IMG_THRU_DATA_KEY = "smallImageThruDate";
    public static final String PRODUCT_SMALL_IMG_ALT_DATA_KEY = "smallImageAlt";
    public static final String PRODUCT_SMALL_IMG_ALT_THRU_DATA_KEY = "smallImageAltThruDate";
    public static final String PRODUCT_THUMB_IMG_DATA_KEY = "thumbImage";
    public static final String PRODUCT_THUMB_IMG_THRU_DATA_KEY = "thumbImageThruDate";
    public static final String PRODUCT_LARGE_IMG_DATA_KEY = "largeImage";
    public static final String PRODUCT_LARGE_IMG_THRU_DATA_KEY = "largeImageThruDate";
    public static final String PRODUCT_DETAIL_IMG_DATA_KEY = "detailImage";
    public static final String PRODUCT_DETAIL_IMG_THRU_DATA_KEY = "detailImageThruDate";
    public static final String PRODUCT_VIDEO_URL_DATA_KEY = "pdpVideoUrl";
    public static final String PRODUCT_VIDEO_URL_THRU_DATA_KEY = "pdpVideoUrlThruDate";
    public static final String PRODUCT_VIDEO_360_URL_DATA_KEY = "pdpVideo360Url";
    public static final String PRODUCT_VIDEO_360_URL_THRU_DATA_KEY = "pdpVideo360UrlThruDate";
    public static final String PRODUCT_ADDNL_IMG_DATA_KEY = "additionalImage_${count}";
    public static final String PRODUCT_ADDNL_IMG_THRU_DATA_KEY = "additionalImageThruDate_${count}";
    public static final String PRODUCT_XTRA_LARGE_IMG_DATA_KEY = "xtraLargeImage_${count}";
    public static final String PRODUCT_XTRA_LARGE_IMG_THRU_DATA_KEY = "xtraLargeImageThruDate_${count}";
    public static final String PRODUCT_XTRA_DETAIL_IMG_DATA_KEY = "xtraDetailImage_${count}";
    public static final String PRODUCT_XTRA_DETAIL_IMG_THRU_DATA_KEY = "xtraDetailImageThruDate_${count}";
    public static final String PRODUCT_ATTACH_URL_DATA_KEY = "productAttachment_${count}";
    public static final String PRODUCT_ATTACH_URL_THRU_DATA_KEY = "productAttachmentThruDate_${count}";
    public static final String PRODUCT_ATTRIBUTES_DATA_KEY = "productAttributes";

    public static final String PRODUCT_ASSOC_ID_DATA_KEY = "assocProductId";
    public static final String PRODUCT_ASSOC_ID_TO_DATA_KEY = "assocProductIdTo";
    public static final String PRODUCT_ASSOC_TYPE_DATA_KEY = "productAssocTypeId";
    public static final String PRODUCT_ASSOC_FROM_DATA_KEY = "assocFromDate";
    public static final String PRODUCT_ASSOC_THRU_DATA_KEY = "assocThruDate";

    public static final String FACET_GRP_ID_DATA_KEY = "facetGroupId";
    public static final String FACET_GRP_DESC_DATA_KEY = "facetGroupDescription";
    public static final String FACET_GRP_PROD_CAT_ID_DATA_KEY = "facetProductCategoryId";
    public static final String FACET_GRP_SEQ_NUM_DATA_KEY = "facetSequenceNum";
    public static final String FACET_GRP_TOOLTIP_DATA_KEY = "facetTooltip";
    public static final String FACET_GRP_MIN_DATA_KEY = "facetMinDisplay";
    public static final String FACET_GRP_MAX_DATA_KEY = "facetMaxDisplay";
    public static final String FACET_GRP_FROM_DATA_KEY = "facetFromDate";
    public static final String FACET_GRP_THRU_DATA_KEY = "facetThruDate";

    public static final String FACET_VAL_GRP_ID_DATA_KEY = "productFeatureGroupId";
    public static final String FACET_VAL_FEAT_ID_DATA_KEY = "productFeatureId";
    public static final String FACET_VAL_FEAT_TYPE_ID_DATA_KEY = "productFeatureTypeId";
    public static final String FACET_VAL_FEAT_DESC_DATA_KEY = "productFeatureDescription";
    public static final String FACET_VAL_SEQ_NUM_DATA_KEY = "facetValSequenceNum";
    public static final String FACET_VAL_PLP_SWATCH_DATA_KEY = "facetPlpSwatchUrl";
    public static final String FACET_VAL_PDP_SWATCH_DATA_KEY = "facetPdpSwatchUrl";
    public static final String FACET_VAL_FROM_DATA_KEY = "facetValueFromDate";
    public static final String FACET_VAL_THRU_DATA_KEY = "facetValueThruDate";

    public static final String MANUFACTURER_ID_DATA_KEY = "manufacturerId";
    public static final String MANUFACTURER_NAME_DATA_KEY = "manufacturerName";
    public static final String MANUFACTURER_ADDR1_DATA_KEY = "manufacturerAddress1";
    public static final String MANUFACTURER_CITY_DATA_KEY = "manufacturerCity";
    public static final String MANUFACTURER_STATE_DATA_KEY = "manufacturerState";
    public static final String MANUFACTURER_ZIP_DATA_KEY = "manufacturerZip";
    public static final String MANUFACTURER_COUNTRY_DATA_KEY = "manufacturerCountry";
    public static final String MANUFACTURER_SHORT_DESC_DATA_KEY = "manufacturerShortDescription";
    public static final String MANUFACTURER_LONG_DESC_DATA_KEY = "manufacturerLongDescription";
    public static final String MANUFACTURER_IMG_DATA_KEY = "manufacturerImage";
    public static final String MANUFACTURER_IMG_THRU_DATA_KEY = "manufacturerImageThruDate";
    
    public static final String ATTR_NAME_DATA_KEY = "attrName";
    public static final String ATTR_VALUE_DATA_KEY = "attrValue";

}

