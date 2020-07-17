package com.osafe.thirdparty.firstdata.globalgatewaye4;

import java.math.BigDecimal;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.annotations.*;

/**
 * @author Steve Copous <steve.copous@firstdata.com>
 * <p>INTENDED FOR DEMONSTRATION PURPOSES ONLY.  NOT FOR PRODUCTION USE.</p>
 */
public class LevelThree {

	@Expose private BigDecimal tax_amount;
	@Expose private BigDecimal tax_rate;
	@Expose private BigDecimal alt_tax_amount;
	@Expose private String alt_tax_id;
	@Expose private BigDecimal discount_amount;
	@Expose private BigDecimal duty_amount;
	@Expose private BigDecimal freight_amount;
	@Expose private String ship_from_zip;
	@Expose private ShipToAddress ship_to_address;
	@Expose private ArrayList<LineItem> line_items;
	
	/**
	 * Returns an {@link #ArrayList} of {@link #LineItem}s.  There is a limit of 99 line items for a single transaction.
	 * <p>Example:</p>
	 * <pre>
	 * {@link #LevelThree} levelThree = new LevelThree();
	 * ArrayList<{@link #LineItem}> line_items = levelThree.line_items();
	 * {@link #LineItem} lineItem = new LineItem();
	 * lineItem.description("A line item.");
	 * line_items.add(lineItem);
	 * </pre>
	 * @return {@link LevelThree#line_items}
	 * @see #LineItem
	 */
	public ArrayList<LineItem> line_items() {
		if(line_items == null) {
			line_items = new ArrayList<LineItem>();
		}
		return this.line_items;
	}


	/**
	 * @param tax_amount The amount of tax charged on the line item. 
	 * <p>2 decimal/right justified/zero filled or space filled (50.00)</p>
	 * @return {@link #LevelThree}
	 */
	public LevelThree tax_amount(BigDecimal tax_amount) {
		this.tax_amount = tax_amount;
		return this;
	}


	/**
	 * @param tax_rate The rate of tax charged on the line item.
	 * <p>2 decimal/right justified/zero filled or space filled, (1.00 = 1%)</p>
	 * @return {@link #LevelThree}
	 */
	public LevelThree tax_rate(BigDecimal tax_rate) {
		this.tax_rate = tax_rate;
		return this;
	}


	/**
	 * @param alt_tax_amount Total amount of alternate tax associated with this transaction.
	 * <p>Note: If {@link #alt_tax_amount} is populated, {@link #alt_tax_id} is required</p>
	 * @return {@link #LevelThree}
	 */
	public LevelThree alt_tax_amount(BigDecimal alt_tax_amount) {
		this.alt_tax_amount = alt_tax_amount;
		return this;
	}


	/**
	 * @param alt_tax_id Tax ID number for the alternate tax associated with this transaction.
	 * @return {@link #LevelThree}
	 */
	public LevelThree alt_tax_id(String alt_tax_id) {
		this.alt_tax_id = alt_tax_id;
		return this;
	}


	/**
	 * @param discount_amount Amount of discount applied to the total transaction.
	 * <p>2 decimal/right justified/zero filled or space filled, (20.00)</p>
	 * @return {@link #LevelThree}
	 */
	public LevelThree discount_amount(BigDecimal discount_amount) {
		this.discount_amount = discount_amount;
		return this;
	}


	/**
	 * @param duty_amount Total charges for any import and/or export duties included in this transaction.
	 * <p>2 decimal/right justified/zero filled or space filled, (20.00)</p>
	 * @return {@link #LevelThree}
	 */
	public LevelThree duty_amount(BigDecimal duty_amount) {
		this.duty_amount = duty_amount;
		return this;
	}


	/**
	 * @param freight_amount Total freight or shipping and handling charges.
	 * <p>2 decimal/right justified/zero filled or space filled, (20.00)</p>
	 * @return {@link #LevelThree}
	 */
	public LevelThree freight_amount(BigDecimal freight_amount) {
		this.freight_amount = freight_amount;
		return this;
	}


	/**
	 * @param ship_from_zip The zip/postal code of the location from which the goods were shipped.
	 * @return {@link #LevelThree}
	 */
	public LevelThree ship_from_zip(String ship_from_zip) {
		this.ship_from_zip = ship_from_zip;
		return this;
	}


	/**
	 * @param ship_to_address
	 * @return {@link #LevelThree}
	 * @see #ShipToAddress
	 */
	public LevelThree ship_to_address(ShipToAddress ship_to_address) {
		this.ship_to_address = ship_to_address;
		return this;
	}


	public BigDecimal getTax_amount() {
		return tax_amount;
	}


	public BigDecimal getTax_rate() {
		return tax_rate;
	}


	public BigDecimal getAlt_tax_amount() {
		return alt_tax_amount;
	}


	public String getAlt_tax_id() {
		return alt_tax_id;
	}


	public BigDecimal getDiscount_amount() {
		return discount_amount;
	}


	public BigDecimal getDuty_amount() {
		return duty_amount;
	}


	public BigDecimal getFreight_amount() {
		return freight_amount;
	}


	public String getShip_from_zip() {
		return ship_from_zip;
	}


	public ShipToAddress getShip_to_address() {
		return ship_to_address;
	}


	public ArrayList<LineItem> getLine_items() {
		return line_items;
	}
}
