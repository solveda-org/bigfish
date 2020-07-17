package com.osafe.feeds.customer;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
@XmlType(propOrder={"country", "address1", "address2","address3", "cityTown", "stateProvience", "zipPostCode", "dayPhone", "eveningPhone"})
public class Address {
	
	private String country;
	private String address1;
	private String address2;
	private String address3;
	private String cityTown;
	private String stateProvience;
	private String zipPostCode;
	private String dayPhone;
	private String eveningPhone;
	
	public String getCountry() {
		return country;
	}
 
	@XmlElement(name="Country")
	public void setCountry(String country) {
		this.country = country;
	}

	public String getAddress1() {
		return address1;
	}
 
	@XmlElement(name="Address1")
	public void setAddress1(String address1) {
		this.address1 = address1;
	}
	
	public String getAddress2() {
		return address2;
	}
 
	@XmlElement(name="Address2")
	public void setAddress2(String address2) {
		this.address2 = address2;
	}
	
	public String getAddress3() {
		return address3;
	}
 
	@XmlElement(name="Address3")
	public void setAddress3(String address3) {
		this.address3 = address3;
	}
	
	public String getCityTown() {
		return cityTown;
	}
 
	@XmlElement(name="CityTown")
	public void setCityTown(String cityTown) {
		this.cityTown = cityTown;
	}
	
	public String getStateProvience() {
		return stateProvience;
	}
 
	@XmlElement(name="StateProvience")
	public void setStateProvience(String stateProvience) {
		this.stateProvience = stateProvience;
	}
	
	public String getZipPostCode() {
		return zipPostCode;
	}
 
	@XmlElement(name="ZipPostCode")
	public void setZipPostCode(String zipPostCode) {
		this.zipPostCode = zipPostCode;
	}
	
	public String getDayPhone() {
		return dayPhone;
	}
 
	@XmlElement(name="DayPhone")
	public void setDayPhone(String dayPhone) {
		this.dayPhone = dayPhone;
	}
	
	public String getEveningPhone() {
		return eveningPhone;
	}
 
	@XmlElement(name="EveningPhone")
	public void setEveningPhone(String eveningPhone) {
		this.eveningPhone = eveningPhone;
	}
}