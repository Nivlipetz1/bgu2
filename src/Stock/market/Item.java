package Stock.market;

import java.util.LinkedList;
import java.util.List;

import Stock.supplierManagement.CataloguedItem;
import Stock.supplierManagement.Supplier;

public class Item {
	private int ID;
	private Manufacturer manufacturer;
	private String itemName;
	private List<CataloguedItem> cataloguedItems;
	public void setCataloguedItems(List<CataloguedItem> cataloguedItems) {
		this.cataloguedItems = cataloguedItems;
	}
	private List<Supplier> suppliers;
	public Item(String itemName, Manufacturer manufacturer) {
		this.itemName = itemName;
		this.manufacturer = manufacturer;
		cataloguedItems = new LinkedList<CataloguedItem>();
		suppliers = new LinkedList<Supplier>();
	}
	public Item(int id)
	{
		this.ID = id;
	}
	public Manufacturer getManufacturer() {
		return manufacturer;
	}
	public String getItemName() {
		return itemName;
	}
	public void addCatalougeItem(CataloguedItem ci) {
		cataloguedItems.add(ci);
	}
	public void addSupplier(Supplier supplier, int catalogueNumber) {
		cataloguedItems.add(new CataloguedItem(this, catalogueNumber, supplier));
		suppliers.add(supplier);
	}
	public void removeSupplier(Supplier supplier, int catalogueNumber) {
		for (CataloguedItem ct : cataloguedItems) {
			if (ct.getSupplier().getSupplierName().equals(supplier.getSupplierName()))
				cataloguedItems.remove(ct);
		}
		suppliers.remove(supplier);
	}
	public List<Supplier> getSuppliers() {
		return suppliers;
	}
	public CataloguedItem[] getCataloguedItems() {
		CataloguedItem[] array = new CataloguedItem[cataloguedItems.size()];
		return cataloguedItems.toArray(array);
	}
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Item))
			return false;
		return ((Item)obj).getItemName().equals(getItemName()) 
				&& ((Item)obj).getManufacturer().getManufacturerName().equals(getManufacturer().getManufacturerName());
	}
	public int getID()
	{
		return this.ID;
	}
}