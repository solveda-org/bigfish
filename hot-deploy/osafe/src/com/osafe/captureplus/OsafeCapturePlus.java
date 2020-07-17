package com.osafe.captureplus;

import java.util.Iterator;
import java.util.List;

import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilValidate;

import javolution.util.FastList;

import com.osafe.util.Util;

public class OsafeCapturePlus {
    /** A <code>OsafeCapturePlus</code> instance that represents a usage of capture plus component and setup*/

    public static final String module = OsafeCapturePlus.class.getName();
    public static final OsafeCapturePlus EmptyOsafeCapturePlus = new NullOsafeCapturePlus();

    protected String productStoreId = null;
    protected String pcaCssUrl = null;
    protected String pcaJsUrl = null;

    protected List<String> pcaApiKeyList = FastList.newInstance();
    protected List<String> pcaApiAppNoList = FastList.newInstance();

    private List<OsafeCapturePlusUseInfo> osafeCapturePlusUseInfoList = FastList.newInstance();

    /** don't allow empty constructor */
    protected OsafeCapturePlus() {}

    public OsafeCapturePlus(String productStoreId) {
        setProductStoreId(productStoreId);
        if (UtilValidate.isNotEmpty(getProductStoreId())) {
            setPcaCssUrl(Util.getProductStoreParm(this.productStoreId, "PCA_CSS_URL"));
            setPcaJsUrl(Util.getProductStoreParm(this.productStoreId, "PCA_JS_URL"));
            setPcaApiKeyList(StringUtil.split(Util.getProductStoreParm(this.productStoreId, "PCA_API_KEY"), ","));
            setPcaApiAppNoList(StringUtil.split(Util.getProductStoreParm(this.productStoreId, "PCA_API_APP_NO"), ","));
        }
    }

    public boolean isEmpty() {
        return ((this.productStoreId == null) && (this.productStoreId.length() == 0));
    }

    public boolean isNotEmpty() {
        return !this.isEmpty();
    }

    public void setProductStoreId(String productStoreId) {
        if ((productStoreId == null && this.productStoreId == null) || (productStoreId != null && productStoreId.equals(this.productStoreId))) {
            return;
        }

        if (this.osafeCapturePlusUseInfoList.size() == 0) {
            this.productStoreId = productStoreId;
        } else {
            throw new IllegalArgumentException("Cannot set productStoreId when the capture plus used list is not empty; capture plus used list size is " + this.osafeCapturePlusUseInfoList.size());
        }
    }

    public String getProductStoreId() {
        return this.productStoreId;
    }

    public void setPcaCssUrl(String pcaCssUrl) {
        this.pcaCssUrl = pcaCssUrl;
    }

    public String getPcaCssUrl() {
        return this.pcaCssUrl;
    }

    public void setPcaJsUrl(String pcaJsUrl) {
        this.pcaJsUrl = pcaJsUrl;
    }

    public String getPcaJsUrl() {
        return this.pcaJsUrl;
    }

    public void setPcaApiKeyList(List<String> pcaApiKeyList) {
        this.pcaApiKeyList = pcaApiKeyList;
    }

    public List<String> getPcaApiKeyList() {
        return this.pcaApiKeyList;
    }

    public void setPcaApiAppNoList(List<String> pcaApiAppNoList) {
        this.pcaApiAppNoList = pcaApiAppNoList;
    }

    public List<String> getPcaApiAppNoList() {
        return this.pcaApiAppNoList;
    }

    /** Returns a <code>OsafeCapturePlus</code> instance derived from productstoreid
     * <code>String</code> value.
     *
     * @param productstoreid 
     * @return A <code>OsafeCapturePlus</code> instance
     */
    public static OsafeCapturePlus getInstance(String productstoreid) {
         if (UtilValidate.isEmpty(productstoreid)) {
             return EmptyOsafeCapturePlus;
         } else {
             return new OsafeCapturePlus(productstoreid);
         }
    }

    /** Returns a <code>String</code>
    *
    * @return A <code>String</code> 
    * which contain the value of component id according to field purpose
    */
    public String getComponentId(String fieldPurpose) {
        if (this.isEmpty() || UtilValidate.isEmpty(fieldPurpose)) return null;

        String componentId = "";
        try {
            int keyIndex = 0;
            for (String key : this.pcaApiKeyList) {
                int appNoIndex = 0;
                key = key.trim();
                for (String appNo : this.pcaApiAppNoList) {
                   appNo = appNo.trim();
                   if (keyIndex == appNoIndex) {
                       if (!isUsed(key, appNo)) {
                           componentId = StringUtil.replaceString(key, "-", "").concat(appNo);
                           this.osafeCapturePlusUseInfoList.add(new OsafeCapturePlusUseInfo(key, appNo, componentId, fieldPurpose));
                       }
                   }
                   appNoIndex++;
                }
                if (UtilValidate.isNotEmpty(componentId)) break;;
                keyIndex++;
            }
        } catch (Exception ex) {}
        return componentId;
    }

    private boolean isUsed(String key, String appNo) {
        boolean isUsed = Boolean.FALSE;
        for (OsafeCapturePlusUseInfo osafeCapturePlusUseInfo : this.osafeCapturePlusUseInfoList) {
            if (osafeCapturePlusUseInfo.getPcaApiKey().equalsIgnoreCase(key) && osafeCapturePlusUseInfo.getPcaApiAppNo().equalsIgnoreCase(appNo)) {
                return Boolean.TRUE;
            }
        }
        return isUsed;
    }

    public Iterator<OsafeCapturePlusUseInfo> getOsafeCapturePlusUseInfoIter() {
        return this.osafeCapturePlusUseInfoList.iterator();
    }

    public static class OsafeCapturePlusUseInfo {

        public String pcaApiKey = null;
        public String pcaApiAppNo = null;
        public String assignedComponentId = null;
        public String fieldPurpose = null;

        public OsafeCapturePlusUseInfo(String pcaApiKey, String pcaApiAppNo, String assignedComponentId, String fieldPurpose) {
            this.pcaApiKey = pcaApiKey;
            this.pcaApiAppNo = pcaApiAppNo;
            this.assignedComponentId = assignedComponentId;
            this.fieldPurpose = fieldPurpose;
        }

        public String getPcaApiKey() { return this.pcaApiKey; }
        public String getPcaApiAppNo() { return this.pcaApiAppNo; }
        public String getAssignedComponentId() { return this.assignedComponentId; }
        public String getFieldPurpose() { return this.fieldPurpose; }
    }

    protected static class NullOsafeCapturePlus extends OsafeCapturePlus {
 
        @Override
        public boolean isEmpty() {
            return true;
        }
    }
}
