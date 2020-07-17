package com.osafe.services;

import java.io.Serializable;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;

public class AddressDocument implements Serializable {

    private String toName=null;
    private String attnName=null;
    private String address1=null;
    private String address2=null;
    private String address3=null;
    private String city=null;
    private String stateProvinceGeoId=null;
    private String postalCode=null;
    private String postalCodeExt=null;
    private String countryGeoId=null;
    private String countyGeoId=null;
    private Double latitude=null;
    private Double longitude=null;

    public AddressDocument() {
        super();
    }
    public String getToName() {
		return toName;
	}
    public String getAttnName() {
		return attnName;
	}
    public String getAddress1() {
		return address1;
	}
    public String getAddress2() {
		return address2;
	}
    public String getAddress3() {
		return address3;
	}
    public String getCity() {
		return city;
	}
    public String getStateProvinceGeoId() {
		return stateProvinceGeoId;
	}
    public String getPostalCode() {
		return postalCode;
	}
    public String getPostalCodeExt() {
		return postalCodeExt;
	}
    public String getCountryGeoId() {
		return countryGeoId;
	}
    public String getCountyGeoId() {
		return countyGeoId;
	}
    public Double getLatitude() {
		return latitude;
	}
    public Double getLongitude() {
		return longitude;
	}
    public void setToName(String toName) {
		this.toName = toName;
	}
    public void setAttnName(String attnName) {
		this.attnName = attnName;
	}
    public void setAddress1(String address1) {
		this.address1 = address1;
	}
    public void setAddress2(String address2) {
		this.address2 = address2;
	}
    public void setAddress3(String address3) {
		this.address3 = address3;
	}
    public void setCity(String city) {
		this.city = city;
	}
    public void setStateProvinceGeoId(String stateProvinceGeoId) {
		this.stateProvinceGeoId = stateProvinceGeoId;
	}
    public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}
    public void setPostalCodeExt(String postalCodeExt) {
		this.postalCodeExt = postalCodeExt;
	}
    public void setCountryGeoId(String countryGeoId) {
		this.countryGeoId = countryGeoId;
	}
    public void setCountyGeoId(String countyGeoId) {
		this.countyGeoId = countyGeoId;
	}
    public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
    public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
}
