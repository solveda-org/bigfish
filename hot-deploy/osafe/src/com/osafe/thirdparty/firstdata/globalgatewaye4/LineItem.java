package com.osafe.thirdparty.firstdata.globalgatewaye4;

import java.math.BigDecimal;
import com.google.gson.annotations.*;

/**
 * @author Steve Copous <steve.copous@firstdata.com>
 * <p>This object contains the line item data required for {@link #LevelThree} transcations.</p>
 * <p>There is a limit of 99 line items for a single transaction.</p>
 * <pre>
 * {@link #LevelThree} levelThree = new LevelThree();
 * ArrayList<{@link #LineItem}> line_items = levelThree.line_items();
 * {@link #LineItem} lineItem = new LineItem();
 * lineItem.description("A line item.");
 * line_items.add(lineItem);
 * </pre>
 */
public class LineItem {
	@Expose private String commodity_code;
	@Expose private String description;
	@Expose private BigDecimal discount_amount;
	@Expose private Boolean discount_indicator;
	@Expose private Boolean gross_net_indicator;
	@Expose private BigDecimal line_item_total;
	@Expose private String product_code;
	@Expose private String quantity;
	@Expose private BigDecimal tax_amount;
	@Expose private BigDecimal tax_rate;
	@Expose private TaxType tax_type;
	@Expose private BigDecimal unit_cost;
	@Expose private String unit_of_measure;
	
	/**
	 * @param commodity_code The commodity code used to classify the item purchased.
	 * @return {@link #LineItem}
	 */
	public LineItem commodity_code(String commodity_code) {
		this.commodity_code = commodity_code;
		return this;
	}
	
	/**
	 * @param description Item description. 
	 * @return {@link #LineItem}
	 */
	public LineItem description(String description) {
		this.description = description;
		return this;
	}
	
	/**
	 * @param discount_amount The discounted amount for the line item. 
	 * @return {@link #LineItem}
	 */
	public LineItem discount_amount(BigDecimal discount_amount) {
		this.discount_amount = discount_amount;
		return this;
	}
	
	/**
	 * @param discount_indicator <p>{@link #Boolean} indicator for whether a discount is present on the item or not.</p>
	 * <ul>
	 * <li>{@link Boolean#TRUE} - Discounted</li>
	 * <li>{@link Boolean#FALSE} - Not Discounted</li>
	 * </ul> 
	 * @return {@link #LineItem}
	 */
	public LineItem discount_indicator(Boolean discount_indicator) {
		this.discount_indicator = discount_indicator;
		return this;
	}
	
	/**
	 * @param gross_net_indicator <p>{@link #Boolean} Indicates whether tax is included in the total amount or not.</p> 
	 * <ul>
	 * <li>{@link Boolean#TRUE} - Discounted</li>
	 * <li>{@link Boolean#FALSE} - Not Discounted</li>
	 * </ul> 
	 * @return {@link #LineItem}
	 */
	public LineItem gross_net_indicator(Boolean gross_net_indicator) {
		this.gross_net_indicator = gross_net_indicator;
		return this;
	}
	
	/**
	 * @param line_item_total The amount of the item. Normally calculated as price multiplied by quantity.
	 * @return {@link #LineItem}
	 */
	public LineItem line_item_total(BigDecimal line_item_total) {
		this.line_item_total = line_item_total;
		return this;
	}
	
	/**
	 * @param product_code The UPC product code for the line item. 
	 * @return {@link #LineItem}
	 */
	public LineItem product_code(String product_code) {
		this.product_code = product_code;
		return this;
	}
	
	/**
	 * @param quantity Number of units of the item purchased
	 * @return
	 */
	public LineItem quantity(String quantity) {
		this.quantity = quantity;
		return this;
	}
	
	/**
	 * @param tax_amount The amount of tax charged on the line item. 
	 * @return
	 */
	public LineItem tax_amount(BigDecimal tax_amount) {
		this.tax_amount = tax_amount;
		return this;
	}
	
	/**
	 * @param tax_rate The rate of tax charged on the line item.  (1.00 = 1%)
	 * @return
	 */
	public LineItem tax_rate(BigDecimal tax_rate) {
		this.tax_rate = tax_rate;
		return this;
	}
	
	/**
	 * @param tax_type Type of tax being applied.
	 * <pre>
	 * item.tax_type(TaxType.LocalSalesTax);
	 * </pre>
	 * @return
	 * @see #TaxType
	 * @see <a href="https://firstdata.zendesk.com/entries/23395483-Tax-Type">https://firstdata.zendesk.com/entries/23395483-Tax-Type</a>
	 */
	public LineItem tax_type(TaxType tax_type) {
		this.tax_type = tax_type;
		return this;
	}
	
	/**
	 * @param unit_cost The per unit cost of the line item. 
	 * <p>4 decimal/right justified/zero filled or space filled, (100.0000)</p>
	 * @return {@link #LineItem}
	 */
	public LineItem unit_cost(BigDecimal unit_cost) {
		this.unit_cost = unit_cost;
		return this;
	}
	
	/**
	 * @param unit_of_measure The unit of measure, or unit of measure code used for this item
	 * @return {@link #LineItem}
	 * @see <a href="https://firstdata.zendesk.com/entries/23393247-Units-of-Measure">https://firstdata.zendesk.com/entries/23393247-Units-of-Measure</a>
	 */
	public LineItem unit_of_measure(String unit_of_measure) {
		this.unit_of_measure = unit_of_measure;
		return this;
	}

	public String getCommodity_code() {
		return commodity_code;
	}

	public String getDescription() {
		return description;
	}

	public BigDecimal getDiscount_amount() {
		return discount_amount;
	}

	public Boolean getDiscount_indicator() {
		return discount_indicator;
	}

	public Boolean getGross_net_indicator() {
		return gross_net_indicator;
	}

	public BigDecimal getLine_item_total() {
		return line_item_total;
	}

	public String getProduct_code() {
		return product_code;
	}

	public String getQuantity() {
		return quantity;
	}

	public BigDecimal getTax_amount() {
		return tax_amount;
	}

	public BigDecimal getTax_rate() {
		return tax_rate;
	}

	public TaxType getTax_type() {
		return tax_type;
	}

	public BigDecimal getUnit_cost() {
		return unit_cost;
	}

	public String getUnit_of_measure() {
		return unit_of_measure;
	}
	

}
