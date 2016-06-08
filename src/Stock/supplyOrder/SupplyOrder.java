package Stock.supplyOrder;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import Stock.supplierManagement.CataloguedItem;
import Stock.supplierManagement.Supplier;
import Stock.supplierManagement.SupplierContact;

public class SupplyOrder {
	private int orderId;
	private Supplier supplier;
	private List<OrderedItem> orderedItems;
	private double finalPrice;
	private Date date;
	
	public SupplyOrder(Supplier supplier) {
		orderedItems = new LinkedList<OrderedItem>();
		this.supplier = supplier;
		date = new Date();
	}
	
	public OrderedItem[] getOrderedItems() {
		OrderedItem[] array = new OrderedItem[orderedItems.size()];
		return orderedItems.toArray(array);
	}
	
	public void addOrder(CataloguedItem ct, int quantity) throws Exception {
		OrderedItem ot = new OrderedItem(ct, quantity);
		orderedItems.add(ot);
		finalPrice += ot.getTotalDiscountPrice();
	}
	
	public void removeOrder(CataloguedItem ct) throws Exception {
		for (OrderedItem ot : orderedItems) {
			if (ot.getCataloguedItem().getCatalogueNumber() == ct.getCatalogueNumber()) {
				orderedItems.remove(ot);
				finalPrice -= ot.getTotalDiscountPrice();
			}
		}
	}
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Supply order from: ");
		sb.append(supplier.getSupplierName());
		sb.append("\n");
		for (OrderedItem ot : orderedItems) {
			sb.append(ot.getCataloguedItem().getCatalogueNumber());
			sb.append("\t");
			sb.append(ot.getCataloguedItem().getItem().getItemName());
			sb.append("\tx");
			sb.append(ot.getQuantity());
			sb.append("\tfound in agreement: " + ot.getAgreementId());
			sb.append("\nPrice: ");
			sb.append(ot.getTotalOriginalPrice());
			sb.append("$\tDiscount: ");
			sb.append(ot.getDiscount());
			sb.append("\tTotal: ");
			sb.append(ot.getTotalDiscountPrice());
			sb.append("$\n");
		}
		sb.append("\n\tTotal Price: ");
		sb.append(finalPrice);
		return sb.toString();
	}

	public int getOrderID()
	{
		return orderId;
	}

	public int getSupID()
	{
		return supplier.getCard().getCardNumber();
	}

	public String getSupName()
	{
		return supplier.getSupplierName();
	}

	public String getSupAddress()
	{
		return supplier.getAddress();
	}

	public Date getDate()
	{
		return this.date;
	}

	public SupplierContact getContact()
	{
		return supplier.getCard().getContacts()[0];
	}
}
