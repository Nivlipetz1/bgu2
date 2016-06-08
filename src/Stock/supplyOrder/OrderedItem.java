package Stock.supplyOrder;

import Stock.supplierManagement.Agreement;
import Stock.supplierManagement.CataloguedItem;

public class OrderedItem {
	private CataloguedItem ct;
	private int quantity;
	private double discount, totalOriginalPrice, totalDiscountPrice;
	private Agreement agreement;
	
	public OrderedItem(CataloguedItem ct, int quantity) throws Exception {
		this.ct = ct;
		this.quantity = quantity;
		this.discount = agreement.getQDS().getDiscountTerm(ct, quantity).getDiscount();
		totalOriginalPrice = agreement.getPrice(ct)*quantity;
		totalDiscountPrice = totalOriginalPrice*discount;
	}
	public CataloguedItem getCataloguedItem() {
		return ct;
	}
	public int getQuantity() {
		return quantity;
	}
	public double getDiscount() {
		return discount;
	}
	public double getTotalOriginalPrice() {
		return totalOriginalPrice;
	}
	public double getTotalDiscountPrice() {
		return totalDiscountPrice;
	}
	public int getAgreementId() {
		return agreement.getAgreementId();
	}
}
