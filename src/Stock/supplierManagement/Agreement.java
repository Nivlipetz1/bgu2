package Stock.supplierManagement;

import java.util.*;

import Stock.supplyOrder.ItemPricing;

public class Agreement {
	private static final double no_price = -1;
	
	private Supplier signedSupplier;
	private QDS qds;
	private boolean isTransportationFixed;
	private boolean isTransportationBySupplier;
	private List<Date> fixedDays;
	private List<ItemPricing> itemPrices;
	private CataloguedItem cachedItem = null;
	private double chachedPrice = no_price;
	private int agreementId;

	public Agreement(int id, Supplier signedSupplier, QDS qds, boolean isTransportationFixed, boolean isTransportationBySupplier) {
		this.signedSupplier = signedSupplier;
		this.qds = (qds == null) ? new QDS() : qds;
		fixedDays = new LinkedList<Date>();
		itemPrices = new LinkedList<ItemPricing>();
		this.isTransportationFixed = isTransportationFixed;
		this.isTransportationBySupplier = isTransportationBySupplier;
		agreementId = id;
	}
	public int getAgreementId() {
		return agreementId;
	}
	public void setAgreementId(int id) {
		agreementId = id;
	}
	public void addFixedDay(Date date) {
		this.fixedDays.add(date);
		this.isTransportationFixed = true;
	}
	public void makeTransportationBySupplier() {
		isTransportationBySupplier = true;
	}
	public void makeTransportationByCompany() {
		isTransportationBySupplier = false;
	}
	public void makeTransportationByOder() {
		isTransportationFixed = false;
		fixedDays = new LinkedList<Date>();
	}
	public void makeTransportationFixed() {
		isTransportationFixed = true;
	}
	public boolean isTransportationFixed() {
		return isTransportationFixed;
	}
	public boolean isTransportationBySupplier() {
		return isTransportationBySupplier;
	}
	public void addItem(CataloguedItem item, double price) {
		itemPrices.add(new ItemPricing(item, price));
	}
	public void removeItem(int catalogueNumber) {
		for (ItemPricing ip : itemPrices) {
			if (ip.getCatalougeItem().getCatalogueNumber() == catalogueNumber) {
				itemPrices.remove(ip);
				qds.removeItem(ip);
				return;
			}
		}
	}
	public boolean isItemIncludedInAgreement(CataloguedItem item) {
		cachedItem = item;
		for (ItemPricing ip : itemPrices) {
			if (ip.getCatalougeItem().equals(item)) {
				chachedPrice = ip.getPrice();
				return true;
			}
		}
		chachedPrice = no_price;
		return false;
	}
	public double getPrice(CataloguedItem item) throws Exception {
		if (item != cachedItem && !isItemIncludedInAgreement(item))
				throw new Exception("the specified item is not sold to the company in this agreement!");
		else return chachedPrice;
	}
	public void addDiscount(CataloguedItem ct, int minQuantity, double discount) throws Exception {
		qds.addDiscount(ct, minQuantity, discount);
	}
	public QDS getQDS() {
		return qds;
	}
	public Supplier getSignedSupplier() {
		return signedSupplier;
	}
}
