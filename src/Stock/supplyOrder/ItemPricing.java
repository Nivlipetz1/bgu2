package Stock.supplyOrder;

import Stock.supplierManagement.CataloguedItem;

public class ItemPricing {
	private CataloguedItem item;
	private double price;
	public ItemPricing(CataloguedItem item, double price) {
		this.item = item;
		this.price = price;
	}
	public CataloguedItem  getCatalougeItem() {
		return item;
	}
	public double getPrice() {
		return price;
	}
}
