package Stock.supplierManagement;

import Stock.supplyOrder.ItemPricing;


public class ItemDiscountTerm {
	public static final double normal_price = 1;

	private DiscountTerm discountTerm;
	private ItemPricing item;

	public ItemDiscountTerm(ItemPricing item, int minQuantity, double discount) throws Exception {
		discountTerm = (discount == 0 ? null : new DiscountTerm(minQuantity, discount));
		this.item = item;
	}
	
	public void removeDiscountTerm() {
		this.discountTerm = null;
	}
	/*
	 * @PRE: 0 < discount && discount < 1
	 */
	public void setDiscountTerm(int minQuantity, double discount) throws Exception {
		this.discountTerm = (discount == 0 ? null : new DiscountTerm(minQuantity, discount));
	}
	
	public ItemPricing getItemPricing() {
		return item;
	}
	public DiscountTerm getDiscountTerm() {
		return discountTerm;
	}
}
