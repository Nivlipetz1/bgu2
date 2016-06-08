package Stock.supplierManagement;

import Stock.market.Item;

public class CataloguedItem {
	private Item item;
	private int catalogueNumber;
	private Supplier supplier;
	
	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}
	public CataloguedItem(Item item, int catalogueNumber, Supplier supplier) {
		this.item = item;
		this.catalogueNumber = catalogueNumber;
		this.supplier = supplier;
	}
	public Item getItem() {
		return item;
	}
	public int getCatalogueNumber() {
		return catalogueNumber;
	}
	public Supplier getSupplier() {
		return supplier;
	}
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof CataloguedItem))
			return false;
		return ((CataloguedItem)obj).getCatalogueNumber() == this.getCatalogueNumber()
				&& ((CataloguedItem)obj).getItem().equals(this.getItem());
	}
}
