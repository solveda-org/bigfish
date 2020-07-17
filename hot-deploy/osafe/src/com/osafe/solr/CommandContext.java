package com.osafe.solr;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import javolution.util.FastList;
import javolution.util.FastMap;

public class CommandContext {
    private HttpServletRequest request;
    private String requestName;
    private String searchText;
    private String productCategoryId;
    private String topMostProductCategoryId;
    private List<String> filterGroups;
    private String sortParameterName;
    private String sortParameterValue;
    private String numberOfRowsShown;
    private boolean onCategoryList;

    private Map<String, String> filterGroupsDescriptions = FastMap.newInstance();
    private Map<String, String> filterGroupsIds = FastMap.newInstance();
    private Map<String, String> filterGroupsFacetSorts = FastMap.newInstance();

    public CommandContext() {
        super();
        filterGroups = FastList.newInstance();
    }

    public boolean isOnCategoryList() {
        return onCategoryList;
    }

    public void setOnCategoryList(boolean onCategoryList) {
        this.onCategoryList = onCategoryList;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    public String getRequestName() {
        return requestName;
    }

    public void setRequestName(String requestName) {
        this.requestName = requestName;
    }

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
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

    public List<String> getFilterGroups() {
        return filterGroups;
    }

    public void setFilterGroups(List<String> filterGroups) {
        this.filterGroups = filterGroups;
    }

    public void addFilterGroup(String filterGroup) {
        this.filterGroups.add(filterGroup);
    }

    public Map<String, String> getFilterGroupsDescriptions() {
        return filterGroupsDescriptions;
    }

    public void setFilterGroupsDescriptions(Map<String, String> filterGroupsDescriptions) {
        this.filterGroupsDescriptions = filterGroupsDescriptions;
    }

    public Map<String, String> getFilterGroupsIds() {
        return filterGroupsIds;
    }

    public void setFilterGroupsIds(Map<String, String> filterGroupsIds) {
        this.filterGroupsIds = filterGroupsIds;
    }

    public Map<String, String> getFilterGroupsFacetSorts() {
        return filterGroupsFacetSorts;
    }

    public void setFilterGroupsFacetSorts(Map<String, String> filterGroupsFacetSorts) {
        this.filterGroupsFacetSorts = filterGroupsFacetSorts;
    }

    public String getSortParameterName() {
        return sortParameterName;
    }

    public void setSortParameterName(String sortParameterName) {
        this.sortParameterName = sortParameterName;
    }

    public String getSortParameterValue() {
        return sortParameterValue;
    }

    public void setSortParameterValue(String sortParameterValue) {
        this.sortParameterValue = sortParameterValue;
    }

    public String getNumberOfRowsShown() {
        return numberOfRowsShown;
    }

    public void setNumberOfRowsShown(String numberOfRowsShown) {
        this.numberOfRowsShown = numberOfRowsShown;
    }

}
