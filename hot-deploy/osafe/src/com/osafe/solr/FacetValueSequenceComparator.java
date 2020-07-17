package com.osafe.solr;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;

public class FacetValueSequenceComparator implements Comparator {
    private static final String module = FacetValueSequenceComparator.class.getName();
    private Map<String, Integer> sortOrder = FastMap.newInstance();
    private Delegator delegator = null;
    private String productFeatureGroupId = null;
    private String productFeatureGroupSorting = SolrConstants.FACET_SORT_DB_SEQ;
    private boolean useSequenceNum = false;
    private static String SEPARATOR = ";";

    public FacetValueSequenceComparator(Delegator delegator, String productFeatureGroupId) {
        super();
        this.delegator = delegator;
        this.productFeatureGroupId = productFeatureGroupId;
    }

    public void populateSortOrder() {
        sortOrder = getSortOrder();
    }

    public boolean isUseSequenceNum() {
        return useSequenceNum;
    }

    public void setUseSequenceNum(boolean useSequenceNum) {
        this.useSequenceNum = useSequenceNum;
    }

    public String getProductFeatureGroupSorting() {
        return productFeatureGroupSorting;
    }

    public void setProductFeatureGroupSorting(String productFeatureGroupSorting) {
        this.productFeatureGroupSorting = productFeatureGroupSorting;
    }

    @Override
    public int compare(Object o1, Object o2) {

        if (!isUseSequenceNum()) {
            if (SolrConstants.FACET_SORT_DB_SEQ.equals(productFeatureGroupSorting)) {

                // Using the sequence specified in the database
                String name1 = ((GenericRefinementValue) o1).getName();
                String name2 = ((GenericRefinementValue) o2).getName();

                Integer hv1 = null;
                Integer hv2 = null;
                if (sortOrder != null) {
                    hv1 = (Integer) sortOrder.get(name1);
                    hv2 = (Integer) sortOrder.get(name2);
                }

                if (hv1 != null && hv2 != null) {
                    return hv1.compareTo(hv2);
                } else if (hv1 != null) {
                    return -1;
                } else if (hv2 != null) {
                    return 1;
                } else {
                    return name1.compareTo(name2);
                }
            } else if (SolrConstants.FACET_SORT_COUNT.equals(productFeatureGroupSorting)) {
                // Using count of each facet, descending
                Long count1 = Long.valueOf(((GenericRefinementValue) o1).getScalarCount());
                Long count2 = Long.valueOf(((GenericRefinementValue) o2).getScalarCount());
                if (count1 != null && count2 != null) {
                    return count1.compareTo(count2) * -1;
                } else if (count1 != null) {
                    return 1;
                } else if (count2 != null) {
                    return -1;
                } else {
                    return 0;
                }
            } else if (SolrConstants.FACET_SORT_INDEX.equals(productFeatureGroupSorting)) {
                // Using name of each facet
                String name1 = ((GenericRefinementValue) o1).getName();
                String name2 = ((GenericRefinementValue) o2).getName();
                if (name1 != null && name2 != null) {
                    return name1.compareTo(name2);
                } else if (name1 != null) {
                    return -1;
                } else if (name2 != null) {
                    return 1;
                } else {
                    return 0;
                }
            }
        } else {
            Long sequenceNum1 = ((GenericRefinementValue) o1).getSequenceNum();
            Long sequenceNum2 = ((GenericRefinementValue) o2).getSequenceNum();

            if (sequenceNum1 != null && sequenceNum2 != null) {
                return sequenceNum1.compareTo(sequenceNum2);
            } else if (sequenceNum1 != null) {
                return -1;
            } else if (sequenceNum2 != null) {
                return 1;
            }
        }
        return 0;
    }

    private HashMap<String, Integer> getSortOrder() {
        // Find the sequence for the given set of facets

        String[] orderedValues = null;
        if (UtilValidate.isNotEmpty(this.productFeatureGroupId)) {
            try {
                List<GenericValue> productFeatureGroupAppls = delegator.findByAndCache("ProductFeatureGroupAppl", UtilMisc.toMap("productFeatureGroupId", this.productFeatureGroupId));
                productFeatureGroupAppls = EntityUtil.filterByDate(productFeatureGroupAppls);
                productFeatureGroupAppls = EntityUtil.orderBy(productFeatureGroupAppls, UtilMisc.toList("sequenceNum"));
                List<GenericValue> productFeatures = FastList.newInstance();
                GenericValue productFeature = null;

                // Build a list of descriptions in order
                if (UtilValidate.isNotEmpty(productFeatureGroupAppls)) {
                    for (GenericValue productFeatureGroupAppl : productFeatureGroupAppls) {
                        productFeature = productFeatureGroupAppl.getRelatedOneCache("ProductFeature");
                        if (UtilValidate.isNotEmpty(productFeature)) {
                            productFeatures.add(productFeature);
                        }
                    }
                }
                if (UtilValidate.isNotEmpty(productFeatures)) {
                    List fieldListFromEntityList = EntityUtil.getFieldListFromEntityList(productFeatures, "description", false);
                    // Need to replace spaces in decription with underscore symbol
                    // "_"
                    for (int i = 0; i < fieldListFromEntityList.size(); i++) {
                        String val = (String) fieldListFromEntityList.get(i);
                        val = StringUtils.replace(val, " ", "_");
                        fieldListFromEntityList.set(i, val);
                    }
                    orderedValues = (String[]) fieldListFromEntityList.toArray(new String[fieldListFromEntityList.size()]);
                }
            } catch (GenericEntityException e) {
                Debug.logError(e, module);
            }
        }

        if (orderedValues == null)
            return null;
        HashMap<String, Integer> ordHash = new HashMap<String, Integer>();
        for (int i = 0; i < orderedValues.length; i++) {
            if (orderedValues[i] == null || orderedValues[i].trim().equals("")) {
                continue;
            }
            ordHash.put(orderedValues[i].trim(), new Integer(i));
        }
        return ordHash;
    }

}
