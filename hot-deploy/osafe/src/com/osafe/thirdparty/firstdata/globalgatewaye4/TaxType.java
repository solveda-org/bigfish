package com.osafe.thirdparty.firstdata.globalgatewaye4;

/**
 * @author Steve Copous <steve.copous@firstdata.com>
 * <p>INTENDED FOR DEMONSTRATION PURPOSES ONLY.  NOT FOR PRODUCTION USE.</p>
 */
public class TaxType {
	private String _value_;
	
	protected TaxType(String value) {
		this._value_ = value;
	}
	
	private static final String _UnknownTax = "0";
	private static final String _FederalNationalSalesTax = "1";
	private static final String _StateSalesTax = "2";
	private static final String _CitySalesTax = "3";
	private static final String _LocalSalesTax = "4";
	private static final String _MunicipalSalesTax = "5";
	private static final String _OtherTax = "6";
	private static final String _VAT = "10";
	private static final String _GST = "11";
	private static final String _PST = "12";
	private static final String _HST = "13";
	private static final String _QST = "14";
	private static final String _RoomTax = "20";
	private static final String _OccupancyTax = "21";
	private static final String _EnergyTax = "22";
	
	public static final TaxType UnknownTax = new TaxType(_UnknownTax);
	
	public static final TaxType FederalNationalSalesTax = new TaxType(_FederalNationalSalesTax);
	
	public static final TaxType StateSalesTax = new TaxType(_StateSalesTax);
	
	public static final TaxType CitySalesTax = new TaxType(_CitySalesTax);
	
	public static final TaxType LocalSalesTax = new TaxType(_LocalSalesTax);
	
	public static final TaxType MunicipalSalesTax = new TaxType(_MunicipalSalesTax);
	
	public static final TaxType OtherTax = new TaxType(_OtherTax);
	
	/**
	 * Value Added Tax
	 */
	public static final TaxType VAT = new TaxType(_VAT);
	
	/**
	 * Goods and Services Tax
	 */
	public static final TaxType GST = new TaxType(_GST);
	
	/**
	 * Provincial Sales Tax
	 */
	public static final TaxType PST = new TaxType(_PST);
	
	/**
	 * Harmonized Sales Tax
	 */
	public static final TaxType HST = new TaxType(_HST);
	
	/**
	 * Quebec Sales Tax
	 */
	public static final TaxType QST = new TaxType(_QST);
	
	public static final TaxType RoomTax = new TaxType(_RoomTax);
	
	public static final TaxType OccupancyTax = new TaxType(_OccupancyTax);
	
	public static final TaxType EnergyTax = new TaxType(_EnergyTax);
	
	@Override
	public String toString() {
		return _value_;
	}
}
