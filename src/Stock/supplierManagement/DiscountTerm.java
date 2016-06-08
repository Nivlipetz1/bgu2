package Stock.supplierManagement;

import Stock.supplyOrder.ItemPricing;

public class DiscountTerm {
	private int minQuantity;
	private double discount;
	
	public DiscountTerm(int minQuantity, double discount) throws Exception {
		if (!(0 < discount && discount < 1))
			throw new Exception("discount must always be in (0,1)!");
		
		this.minQuantity = minQuantity;
		this.discount = discount;
	}

	public int getMinQuantity() {
		return minQuantity;
	}

	public double getDiscount() {
		return discount;
	}
}
