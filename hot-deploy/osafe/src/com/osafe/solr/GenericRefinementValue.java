package com.osafe.solr;

public class GenericRefinementValue implements Comparable {

    private String scalarCount;
    private String name;
    private String displayName;
    private String supportingText;
    private String displayImage;
    private String refinementURL;
    private String sortMethod;
    private Long sequenceNum;
    private double start;

    public String getScalarCount() {
        return scalarCount;
    }

    public void setScalarCount(String scalarCount) {
        this.scalarCount = scalarCount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getSupportingText() {
        return supportingText;
    }

    public void setSupportingText(String supportingText) {
        this.supportingText = supportingText;
    }

    public String getDisplayImage() {
        return displayImage;
    }

    public void setDisplayImage(String displayImage) {
        this.displayImage = displayImage;
    }

    public String getRefinementURL() {
        return refinementURL;
    }

    public void setRefinementURL(String refinementURL) {
        this.refinementURL = refinementURL;
    }

    public String getSortMethod() {
        return sortMethod;
    }

    public void setSortMethod(String sortMethod) {
        this.sortMethod = sortMethod;
    }

    public double getStart() {
        return start;
    }

    public void setStart(double start) {
        this.start = start;
    }

    public Long getSequenceNum() {
        return sequenceNum;
    }

    public void setSequenceNum(Long sequenceNum) {
        this.sequenceNum = sequenceNum;
    }

    @Override
    public int compareTo(Object value) throws ClassCastException {
        int ret = 0;
        if (!(value instanceof GenericRefinementValue)) {
            throw new ClassCastException("A GenericRefinementValue object expected.");
        }
        double start = ((GenericRefinementValue) value).getStart();
        ret = (int) (this.start - start);
        if ("desc".equalsIgnoreCase(getSortMethod())) {
            ret = ret * -1;
        }
        return ret;
    }

}
