package Stock.supplierManagement;

import java.util.LinkedList;
import java.util.List;

import Stock.supplyOrder.ItemPricing;

public class QDS {
	private List<ItemDiscountTerm> discountTerms;
	public QDS() {
		discountTerms = new LinkedList<ItemDiscountTerm>();
	}
	public void addDiscount(CataloguedItem ct, int minQuantity, double discount) throws Exception {
		for (ItemDiscountTerm idt : discountTerms) {
			if (idt.getItemPricing().getCatalougeItem().getCatalogueNumber() == ct.getCatalogueNumber()) {
				idt.setDiscountTerm(minQuantity, discount);
				return;
			}
		}
	}
	public void removeDiscount(CataloguedItem ct, int minQuantity, double discount) {
		for (ItemDiscountTerm idt : discountTerms) {
			if (idt.getItemPricing().getCatalougeItem().getCatalogueNumber() == ct.getCatalogueNumber()) {
				idt.removeDiscountTerm();
				return;
			}
		}
	}
	public DiscountTerm getDiscountTerm(CataloguedItem item, int quantity) {
		for (ItemDiscountTerm idt : discountTerms) {
			if (idt.getItemPricing().getCatalougeItem().getCatalogueNumber() == item.getCatalogueNumber())
				return idt.getDiscountTerm();
		}
		return null;
	}
	public boolean removeItem(ItemPricing item) {
		for (ItemDiscountTerm idt : discountTerms) {
			if (idt.getItemPricing().getCatalougeItem().getCatalogueNumber() == 
					item.getCatalougeItem().getCatalogueNumber()) {
				discountTerms.remove(idt);
				return true;
			}
		}
		return false;
	}
}
