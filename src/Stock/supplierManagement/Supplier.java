package Stock.supplierManagement;

import java.util.LinkedList;
import java.util.List;

import Stock.market.Item;
import Stock.market.Manufacturer;

public class Supplier {
	private List<Agreement> agreements;
	private List<Manufacturer> representedManufacturers;
	private List<CataloguedItem> suppliableItems;
	private SupplierCard card;

	public Supplier(SupplierCard card) {
		this.agreements = new LinkedList<Agreement>();
		this.card = card;
	}
	public String getSupplierName() {
		return card.getSupplierName();
	}
	public Manufacturer[] getRepresentedManufacturers() {
		Manufacturer[] arr = new Manufacturer[representedManufacturers.size()];
		return representedManufacturers.toArray(arr);
	}
	public CataloguedItem[] getSoldItems() {
		CataloguedItem[] array = new CataloguedItem[suppliableItems.size()];
		return suppliableItems.toArray(array);
	}
	public Agreement[] getAgreements() {
		Agreement[] array = new Agreement[agreements.size()];
		return agreements.toArray(array);
	}
	public void addAgreement(Agreement agr) {
		agreements.add(agr);
	}
	public void removeAgreement(Agreement agr) throws Exception {
		if (agreements.size() == 1)
			throw new Exception("a supplier must be signed on at least one agreement!");
		else agreements.remove(agr);
	}
 	public Agreement getBestAgreement(CataloguedItem item, int quantity) {
 		Agreement bestAgreement = null;
 		double bestPrice = Double.MAX_VALUE;
 		for (Agreement agr : agreements) {
 			try {
				if (agr.isItemIncludedInAgreement(item) && agr.getPrice(item) < bestPrice) {
					bestAgreement = agr;
					bestPrice = agr.getPrice(item);
				}
			} catch (Exception e) {
				// shouldn't happen
				e.printStackTrace();
			}
 		}
 		return bestAgreement;
 	}
	public SupplierCard getCard() {
		return card;
	}
	public void addItem(Item item, int catalogueNumber) {
		if (!representedManufacturers.contains(item.getManufacturer())) {
			representedManufacturers.add(item.getManufacturer());
			item.getManufacturer().addDelegate(this);
		}
		suppliableItems.add(new CataloguedItem(item, catalogueNumber, this));
	}
	public void removeItem(Item item) {
		CataloguedItem _item = null;
		for (CataloguedItem ct : suppliableItems) {
			if (ct.getItem() == item) {
				_item = ct;
				suppliableItems.remove(ct);
			}
		}
		if (_item != null)
			updateManufacturer(_item.getItem().getManufacturer());
	}
	public void removeItem(int catalogueNumber) {
		CataloguedItem _item = null;
		for (CataloguedItem ct : suppliableItems) {
			if (ct.getCatalogueNumber() == catalogueNumber) {
				_item = ct;
				suppliableItems.remove(ct);
			}
		}
		if (_item != null)
			updateManufacturer(_item.getItem().getManufacturer());
	}
	private void updateManufacturer(Manufacturer manufacturer) {
		for (CataloguedItem ct : suppliableItems) {
			if (ct.getItem().getManufacturer() == manufacturer) {
				return;
			}
		}
		manufacturer.removeDelegate(this);
		representedManufacturers.remove(manufacturer);
	}
	public String getAddress() {
		return card.getAddress();
	}

	@Override
	public String toString() {
		return card.toString();
	}
}
